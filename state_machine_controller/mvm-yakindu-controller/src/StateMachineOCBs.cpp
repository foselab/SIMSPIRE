// SPDX-FileCopyrightText: 2019-2020 INFN
// SPDX-FileCopyrightText: 2019-2020 University of Bergamo
//
// SPDX-FileContributor: Angelo Gargantini
// SPDX-FileContributor: Silvia Bonfanti
// SPDX-FileContributor: Francesco Giacomini
//
// SPDX-License-Identifier: MIT

#include "Alarms.h"
#include "ValvesController.h"
#include "state_machine.h"
#include <StateMachineOCBs.h>
#include "Serial.h"
// clang-format off
//#define DEBUG_PSV Serial2
#ifdef DEBUG_PSV
#define DBG(x, ...) DEBUG_PSV.print(x, ##__VA_ARGS__)
#define DBGN(x, ...) DEBUG_PSV.println(x, ##__VA_ARGS__)
#else
#define DBG(x, ...) do { if (0) Serial.print(x, ##__VA_ARGS__); } while(0)
#define DBGN(x, ...) do { if (0) Serial.println(x, ##__VA_ARGS__); } while(0)
#endif
// clang-format on

namespace mvm {
constexpr auto valve_close = 0;
constexpr auto valve_open = 1;

// very useful for testing the machine in a PURE PCV
// pawGTMaxPinsp
constexpr auto enable_pawGTMaxPinsp = true;
// dropPAW_ITS_PCV
constexpr auto enable_dropPAW_ITS_PCV = true;

constexpr int32_t deadtime_max = 2000;
constexpr int32_t deadtime_min = 400;

constexpr int32_t inspiratory_time_divider = 2;

// VALVES
void MVMStateMachineOCBs::closeInputValve()
{
/*  if (p == MVM_PIC::ZERO || !m_sm->m_leak_comp.enabled) {
    m_sm->set_ptarget(valve_close);
  } else {
    // assert p == MVM_PIC::SMART_PEEP && m_sm->m_leak_comp.enabled
    m_sm->set_ptarget(m_sm->m_leak_comp.pressure.millibar());
  }*/

  m_sm->set_ptarget(valve_close);
}

void MVMStateMachineOCBs::openInputValve(MVM_PIO p)
{
  // open input valve only if not stopped (or stopping)
  // if stop and openInputValve arrive at the same time, openInputValve has the
  // precedence
  // but it can be ignored
  if (!m_sm->m_state_machine.getStopVentilation()) {
    // in PCV?
    switch (p) {
    case MVM_PIO::PCV:
      m_sm->set_ptarget(m_sm->m_pcv.Pinsp.millibar());
      break;
    case MVM_PIO::PSV:
      m_sm->set_ptarget(m_sm->m_psv.Pinsp.millibar());
      break;
    case MVM_PIO::RM:
      m_sm->set_ptarget(m_sm->m_rm.pressure.millibar());
      break;
    case MVM_PIO::ASV:
          m_sm->set_ptarget(m_sm->m_asv.Pinsp.millibar());
          break;
    default:
      // assert(false)
      break;
    }
  }
}

void MVMStateMachineOCBs::closeOutputValve()
{
  // close valve only if not stopped (or stopping)
  if (!m_sm->m_state_machine.getStopVentilation()) {
      m_sm->m_valves_controller.set_v2(valve_close);
  }
}

void MVMStateMachineOCBs::openOutputValve()
{
  m_sm->m_valves_controller.set_v2(valve_open);
}

sc_boolean MVMStateMachineOCBs::pawGTMaxPinsp()
{
  bool val_return = false;
  if (enable_pawGTMaxPinsp) {
    // @Andrea: BreathingMonitorStatus.PPatient > 1.xxx * Pset
    // transition from INSP PCV to expiration PCV
    // for example https://github.com/MechanicalVentilatorMilano/MVM/issues/1
    // FUN.60
    float pPatient = 0.f;
    m_sm->m_breathing_monitor.GetOutputValue(
        mvm::BreathingMonitor::Output::PRESSURE_P, &pPatient);
    // compare pPatient
    // if P_insp > max_P_insp this will be often be true
    // consider pPatient > 1.1 * P_insp;
    val_return = pPatient > m_sm->m_max_P_insp.millibar();
  }
  return val_return;
}

// drop of PAW to trigger change form Exp to Insp.
// ITS in mbar/sec2
sc_boolean MVMStateMachineOCBs::dropPAW_ITS(float ITS)
{
  // Trigger on a negative second derivative of the pressure going below a
  // threshold given by the ITS parameter. Inhibit the trigger if the
  // first derivative of the pressure is not between -1 and 0 to be sure
  // that the trigger is accepted only after that the exhalation phase
  // ended.
  float delta1 = 0.f;
  float delta2 = 0.f;
  float pressure = 0.f;
  m_sm->m_breathing_monitor.GetOutputValue(
      mvm::BreathingMonitor::Output::PRESSURE_P, &pressure);
  m_sm->m_breathing_monitor.GetOutputValue(
      mvm::BreathingMonitor::Output::PDELTA_1, &delta1);
  m_sm->m_breathing_monitor.GetOutputValue(
      mvm::BreathingMonitor::Output::PDELTA_2, &delta2);
  DBG("ITS: P ");
  DBG(pressure);
  DBG(" D1 ");
  DBG(delta1);
  DBG(" D2 ");
  DBGN(delta2);
  auto const threshold = -1.f * ITS;
  bool const p2_trig = delta2 < threshold;
  bool const p1_trig = delta1 > -1.f && delta1 < 0.f;
  return p2_trig && p1_trig;
}

// ASV
sc_boolean MVMStateMachineOCBs::flowDropASV()
{
  float fluxpeak, flux;
  m_sm->m_breathing_monitor.GetOutputValue(
      mvm::BreathingMonitor::Output::FLUXPEAK, &fluxpeak);
  m_sm->m_breathing_monitor.GetOutputValue(mvm::BreathingMonitor::Output::FLUX,
                                           &flux);
  // as percentage of the flux peak

  flux = flux > 0 ? flux : fluxpeak;
  auto const trigger = flux < m_sm->m_psv.ets_perc / 100.0 * fluxpeak;

  DBG("DROP: FP ");
  DBG(fluxpeak);
  DBG(" F ");
  DBG(flux);
  DBG(" M ");
  DBG(min_flux_peak);
  DBG(" T ");
  DBGN(trigger);
  return trigger;
}

sc_boolean MVMStateMachineOCBs::dropPAW_ITS_ASV()
{
  return dropPAW_ITS(m_sm->m_psv.ITS.millibar());
}


// PCV
sc_boolean MVMStateMachineOCBs::dropPAW_ITS_PCV()
{
  // questa serve se PAW si abbassa durante l'expiration di PCV nella trigger
  // window cosi' da far partire un nuovo ciclo.
  // Nota che la trigger window non parte subito quando si entra di
  // Expiratory, aspettiamo gia' 700 ms come nel vecchio FM.
  return enable_dropPAW_ITS_PCV && dropPAW_ITS(m_sm->m_pcv.ITS.millibar());
}

sc_boolean MVMStateMachineOCBs::dropPAW_ITS_PSV()
{
  return dropPAW_ITS(m_sm->m_psv.ITS.millibar());
}

// PSV
sc_boolean MVMStateMachineOCBs::flowDropPSV()
{
  float fluxpeak, flux;
  m_sm->m_breathing_monitor.GetOutputValue(
      mvm::BreathingMonitor::Output::FLUXPEAK, &fluxpeak);
  m_sm->m_breathing_monitor.GetOutputValue(mvm::BreathingMonitor::Output::FLUX,
                                           &flux);
  // as percentage of the flux peak

  flux = flux > 0 ? flux : fluxpeak;
  auto const trigger = flux < m_sm->m_psv.ets_perc / 100.0 * fluxpeak;

  DBG("DROP: FP ");
  DBG(fluxpeak);
  DBG(" F ");
  DBG(flux);
  DBG(" M ");
  DBG(min_flux_peak);
  DBG(" T ");
  DBGN(trigger);
  return trigger;
}

int32_t MVMStateMachineOCBs::min_exp_time_psv()
{
  // take the current Resp Rate
  float last_inspiratory_time_Sec;
  // VAL_TINSP - last in seconds
  // new enum added by Andrea on request
  m_sm->m_breathing_monitor.GetOutputValue(mvm::BreathingMonitor::Output::TINSP,
                                           &last_inspiratory_time_Sec);
  int32_t deadtime =
      static_cast<int32_t>(last_inspiratory_time_Sec * 1000 / inspiratory_time_divider);
  return static_cast<int32_t>(
      std::min<int32_t>(deadtime_max, std::max<int32_t>(deadtime_min, deadtime)));
}

void MVMStateMachineOCBs::apneaAlarm()
{
  // set the PCV prameters from the settings for apnea
  m_sm->set_RR_PCV(m_sm->m_ap.RR);
  m_sm->set_Pinsp_PCV(m_sm->m_ap.Pinsp);
  m_sm->set_I_E_PCV(m_sm->m_ap.I_E);
  // ITS_PCV has no AP value setting
  // raise the alarm
  m_sm->m_alarms.TriggerAlarm(mvm::ALARM_ENUM::ALARM_APNEA);
  // set the apnea backup mode
  m_sm->m_state_machine.setApnea_backup_mode(true);
}

// automatically called by the state machine when it goes from VentilatioOff to
// Inspiration
void MVMStateMachineOCBs::start()
{
  // call the methods to alarms and so on to signal start ventilation
  m_sm->m_alarms.SetConfigurationValue(mvm::Alarms::Config::VENTILATOR_RUN, 1);
  m_sm->m_valves_controller.running(true);
}
// automatically called by the state machine when exits ventilation and it goes
// into VentilationOff
void MVMStateMachineOCBs::finish()
{
  // call the methods to alarms and so on to signal stop ventilation
  m_sm->m_alarms.SetConfigurationValue(mvm::Alarms::Config::VENTILATOR_RUN, 0);
  m_sm->m_valves_controller.running(false);
  m_sm->m_breathing_monitor.TransitionEndCycle_Event_cb();
}

// called before any inspiration to signal if it is initiated by the patient (b = true);
void MVMStateMachineOCBs::autoBreath(sc_boolean b){
  m_sm->m_valves_controller.spontaneus(b);
}


} // namespace mvm
