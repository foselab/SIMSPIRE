
#ifndef MVM_MVMSTATEMACHINEOCBS_H
#define MVM_MVMSTATEMACHINEOCBS_H

#include <src-gen/MVMStateMachineCore.h>
#include <cstdint>

namespace mvm {

class StateMachine;

class MVMStateMachineOCBs : public MVMStateMachineCore::OperationCallback
{
 private:
  StateMachine* m_sm;

  // min flux peak for drop flow transition
  static constexpr int min_flux_peak = 10;

  // common method for PCV/PSV
  sc_boolean dropPAW_ITS(float ITS);

 public:
  MVMStateMachineOCBs(MVMStateMachineOCBs const&) = delete;
  MVMStateMachineOCBs& operator=(MVMStateMachineOCBs const&) = delete;

  MVMStateMachineOCBs(StateMachine* state_machine)
      : m_sm{state_machine}
  {
  }

  // general commands

  void closeInputValve() override;

  void openInputValve(MVM_PIO) override;

  void closeOutputValve() override;

  void openOutputValve() override;

  // general

  sc_boolean pawGTMaxPinsp() override;

  // ASV
  sc_boolean flowDropASV() override;
  sc_boolean dropPAW_ITS_ASV() override;

  // PCV

  sc_boolean dropPAW_ITS_PCV() override;

  // PSV

  sc_boolean flowDropPSV() override;

  sc_boolean dropPAW_ITS_PSV() override;

  int32_t min_exp_time_psv() override;

  // APNEA ALARM called when PSV -> PCV due to apnea
  void apneaAlarm() override;

  // called when stop is performed
  void finish() override;

  // called when ventilation is starting
  void start() override;

  // called before any inspiration to signal if it is initiated by the patient (b = true);
  // spontaneous breath
  void autoBreath(sc_boolean b) override;

};

} // namespace mvm

#endif
