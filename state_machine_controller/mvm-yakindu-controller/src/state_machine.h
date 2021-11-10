// SPDX-FileCopyrightText: 2019-2020 INFN
// SPDX-FileCopyrightText: 2019-2020 University of Bergamo
//
// SPDX-FileContributor: Angelo Gargantini
// SPDX-FileContributor: Silvia Bonfanti
// SPDX-FileContributor: Francesco Giacomini
//
// SPDX-License-Identifier: MIT

#ifndef MVM_STATE_MACHINE_H
#define MVM_STATE_MACHINE_H

#include "Alarms.h"
#include "BreathingMonitor.h"
#include "MVMTimerInterface.h"
#include "StateMachineOCBs.h"
#include "ValvesController.h"
#include "pressure.h"
#include <AsyncDelay.h>
#include <src-gen/MVMStateMachineCore.h>
#include <src-gen/sc_tracing.h>
#include <limits>
#include <iostream>
#include <cmath>

namespace mvm {

// parameter settings: default, min and max
template<typename T>
struct ParameterSettings {
	T default_val;
	T min;
	T max;
};

template<typename T>
constexpr T invalid_value() {
	return std::numeric_limits<T>::min();
}

template<typename T>
constexpr bool is_invalid_value(T const &t) {
	return t == invalid_value<T>();
}

template<>
constexpr Pressure invalid_value() {
	return Pressure::millibar(invalid_value<float>());
}

template<typename T>
constexpr bool checkParams(ParameterSettings<T> const &p) {
	return (p.default_val == invalid_value<T>() && p.min <= p.max)
			|| (p.min <= p.default_val && p.default_val <= p.max);
}

// val is assigned to a only if it is between min and max
template<class T>
inline bool check_assign(T &a, T val, ParameterSettings<T> const &ps) {
	if (val >= ps.min && val <= ps.max) {
		a = val;
		return true;
	} else {
		return false;
	}
}

// PER.30
constexpr ParameterSettings<int> param_RR_PCV { 12, 4, 50 };
static_assert(checkParams(param_RR_PCV), "parameters bounds inconsistency");
// PER.31
constexpr ParameterSettings<float> param_I_E_Ratio_PCV { .5f, .25f, 1.0f };
static_assert(checkParams(param_I_E_Ratio_PCV),
		"parameters bounds inconsistency");
// PER.32
// Target inspiratory pressure (Pinsp_PCV) = 15 cm H2O
constexpr ParameterSettings<Pressure> param_Pinsp_PCV { 15_cmH2O, 2_cmH2O,
		50_cmH2O };
static_assert(checkParams(param_Pinsp_PCV), "parameters bounds inconsistency");
// PER.33
// sec^2 TODO: check that seconds are used and not milliseconds
// common for every ITS
constexpr Pressure def_its = 20_cmH2O;
constexpr Pressure min_its = 5_cmH2O;
constexpr Pressure max_its = 200_cmH2O;
// Inhale trigger sensitivity (ITSPCV) = 20 cm H2O/sec2
constexpr ParameterSettings<Pressure> param_ITS_PCV { def_its, min_its, max_its };
static_assert(checkParams(param_ITS_PCV), "parameters bounds inconsistency");

// Target inspiratory pressure (Pinsp_PSV) = 15 cm H2O
// PER.40
constexpr ParameterSettings<Pressure> param_Pinsp_PSV { 15_cmH2O, 2_cmH2O,
		50_cmH2O };
static_assert(checkParams(param_Pinsp_PSV), "parameters bounds inconsistency");
// Inhale trigger sensitivity (ITSPSV) = 20 cm H2O/sec2
// PER 41
constexpr ParameterSettings<Pressure> param_ITS_PSV { def_its, min_its, max_its };
static_assert(checkParams(param_ITS_PSV), "parameters bounds inconsistency");
// Expiratory Trigger Sensitivity in PSV as percentage
// PER 42
constexpr ParameterSettings<int> param_ets_perc_PSV { 30, 1, 60 };
static_assert(checkParams(param_ets_perc_PSV),
		"parameters bounds inconsistency");

// APNEA BACKUP
// Apnealag ref PER.43 MILLISECONDS
constexpr ParameterSettings<int> param_APLag { 30000, 10000, 60000 };
static_assert(checkParams(param_APLag), "parameters bounds inconsistency");
//
// Respiratory Rate (RRAP) ref PER.44
constexpr ParameterSettings<int> param_RR_AP { invalid_value<int>(), 4, 50 };
static_assert(checkParams(param_RR_AP), "parameters bounds inconsistency");
// target Inspiratory Pressure: the set pressure to supply the patient
// during the inspiratory phase of the breathing cycle in case of apnea
// backup. ref PER.46
constexpr ParameterSettings<Pressure> param_Pinsp_AP {
		invalid_value<Pressure>(), 2_cmH2O, 50_cmH2O };
static_assert(checkParams(param_Pinsp_AP), "parameters bounds inconsistency");
// max P_insp PER.50
constexpr ParameterSettings<Pressure> param_max_P_insp { 40_cmH2O, 0_cmH2O,
		60_cmH2O };
static_assert(checkParams(param_max_P_insp), "parameters bounds inconsistency");

// Recruitment Maneuver
// pressure PER.11.1
constexpr ParameterSettings<Pressure> param_Pressure_RM { 20_cmH2O, 0_cmH2O,
		50_cmH2O };
static_assert(checkParams(param_Pressure_RM),
		"parameters bounds inconsistency");
// time RM PER 11.2
constexpr ParameterSettings<int> param_time_RM_millisec { 10000, 1000, 30000 };
static_assert(checkParams(param_time_RM_millisec),
		"parameters bounds inconsistency");

// LeakCompansation
// pressure - taken from min PEEP (alamars) PER.52
constexpr ParameterSettings<Pressure> param_Pressure_LC { 5_cmH2O, 0_cmH2O,
		20_cmH2O };
static_assert(checkParams(param_Pressure_LC),
		"parameters bounds inconsistency");

class StateMachine {
	// using nested class for trace observer
	/** trace observer to call methods when some states are exited or entered*/
	class SMTraceObserver: public sc::trace::TraceObserver<MVMStateMachineCore::MVMStateMachineCoreStates> {
		StateMachine *m_sm;

		void adaptVolume(float vTidalAvg);

		void adaptRate(float rRateAvg, float rc);

	public:
		SMTraceObserver(StateMachine *state_machine) :
				m_sm { state_machine } {
		}

		void refreshASVValues(int n);

		void stateEntered(MVMStateMachineCore::MVMStateMachineCoreStates state) override;

		void stateExited(MVMStateMachineCore::MVMStateMachineCoreStates state) override;

	};
	// members
	MVMStateMachineCore m_state_machine { };
	MVMTimerInterface m_timers { };
	AsyncDelay m_stop_watch { };

	ValvesController &m_valves_controller;
	BreathingMonitor &m_breathing_monitor;
	Alarms &m_alarms;

	// call backs from the state machine
	MVMStateMachineOCBs m_operation_cbs;

	StateMachine::SMTraceObserver m_traceobserver;

	static constexpr float m_1min = 60000.f;

	struct PCVData {
		// Respiratory Rate (RRPCV) = 12 b/min
		int RR = param_RR_PCV.default_val;
		// I:E Ratio (I:EPCV) = 1:2
		float I_E_Ratio = param_I_E_Ratio_PCV.default_val;
		// Target inspiratory pressure (Pinsp_PCV) mbar
		Pressure Pinsp = param_Pinsp_PCV.default_val;
		// Inhale trigger sensitivity (ITSPCV), mbar/sec2
		Pressure ITS = param_ITS_PCV.default_val;
	} m_pcv;
	// PSV Data
	struct PSVData {
		// Target inspiratory pressure (Pinsp_PSV) mbar
		Pressure Pinsp = param_Pinsp_PSV.default_val;
		// Inhale trigger sensitivity (ITSPSV) mbar/sec2
		Pressure ITS = param_ITS_PSV.default_val;
		// Expiratory Trigger Sensitivity in PSV as percentage
		int ets_perc = param_ets_perc_PSV.default_val;
	} m_psv;
	// ASV Data
	struct ASVData {
		double expirationTimes[8];
		float vTidals[8];
		float targetVTidal;
		float rRates[8];
		double rcS[8];
		float targetRRate;
		int index = 0;
		float prevF = 10;
		Pressure Pinsp = param_Pinsp_PSV.default_val;
		float rRate = 0;
	} m_asv;
	// APNEA BACKUP
	struct APData {
		// Respiratory Rate (RRAP) ref PER.44
		int RR = param_RR_AP.default_val;
		// ratio of Inspiratory time to Expiratory time in apnea backup mode
		// ref PER.45
		static constexpr float I_E = 0.5f;
		// target Inspiratory Pressure: the set pressure to supply the patient
		// during the inspiratory phase of the breathing cycle in case of apnea
		// backup. ref PER.46
		Pressure Pinsp = param_Pinsp_AP.default_val;
		// NOTE: the apnea lag is in the generated machine
	} m_ap;
	// max P_insp PER.50 mbar
	Pressure m_max_P_insp = param_max_P_insp.default_val;
	// others are never used
	//
	struct RMData {
		// pressure
		Pressure pressure = param_Pressure_RM.default_val;
	} m_rm;
	// leak compensation
	struct LCData {
		// pressure
		Pressure pressure = param_Pressure_LC.default_val;
		// enabled?
		bool enabled = false;
	} m_leak_comp;

	// method to set the inspiration/expiration time for PCV when RR or I_E_Ratio
	// are changed
	void m_setInsExpPCVTime() {
		// compute duration for a cycle
		float cycle_duration_ms = m_1min / m_pcv.RR;
		// expiration time (as integer int32_t as it is needed)
		int32_t expiration_time = static_cast<int32_t>(cycle_duration_ms
				/ (1.f + m_pcv.I_E_Ratio));
		//
		int32_t inspiration_time = static_cast<int32_t>(cycle_duration_ms)
				- expiration_time;
		m_state_machine.setExpiration_duration_ms(expiration_time);
		m_state_machine.setInspiration_duration_ms(inspiration_time);
	}
	// shortcut to know if the machine is in PSV/PCV super states
	bool isVentilating() const {
		return m_state_machine.isStateActive(
				MVMStateMachineCore::MVMStateMachineCoreStates::main_region_PCV)
				|| m_state_machine.isStateActive(
						MVMStateMachineCore::MVMStateMachineCoreStates::main_region_PSV);
	}

	// set the pressure to the invalve V1 and also to the alarm
	void set_ptarget(float pressure) {
		m_valves_controller.set_v1(pressure);
		m_alarms.SetConfigurationValue(Alarms::Config::PRESSURE_SETPOINT,
				pressure);
	}

public:
	StateMachine(ValvesController &valves_controller,
			BreathingMonitor &breathing_monitor, Alarms &alarms) :
			m_valves_controller { valves_controller }, m_breathing_monitor {
					breathing_monitor }, m_alarms { alarms }, m_operation_cbs(
					this), m_traceobserver(this) {
		m_state_machine.setOperationCallback(&m_operation_cbs);
		m_state_machine.setTimerService(&m_timers);
		m_state_machine.setTraceObserver(&m_traceobserver);
		m_setInsExpPCVTime();
	}
	// all the functions that must be registered to the commander
	bool set_RR_PCV(int val) {
		// PER.30
		bool res = check_assign(m_pcv.RR, val, param_RR_PCV);
		if (res) {
			m_setInsExpPCVTime();
			leave_ap_backup_mode();
		}
		return res;
	}
	int get_RR_PCV() const {
		return m_pcv.RR;
	}

	bool set_I_E_PCV(float val) {
		// PER.31
		bool res = check_assign(m_pcv.I_E_Ratio, val, param_I_E_Ratio_PCV);
		if (res) {
			m_setInsExpPCVTime();
			leave_ap_backup_mode();
		}
		return res;
	}
	float get_I_E_PCV() const {
		return m_pcv.I_E_Ratio;
	}
	bool set_Pinsp_PCV(Pressure val) {
		// PER.32
		bool res = check_assign(m_pcv.Pinsp, val, param_Pinsp_PCV);
		if (res) {
			leave_ap_backup_mode();
		}
		return res;
	}
	Pressure get_Pinsp_PCV() const {
		return m_pcv.Pinsp;
	}

	bool set_ITS_PCV(Pressure val) {
		// PER.33
		bool res = check_assign(m_pcv.ITS, val, param_ITS_PCV);
		if (res) {
			leave_ap_backup_mode();
		}
		return res;
	}

	Pressure get_ITS_PCV() const {
		return m_pcv.ITS;
	}
	// ASV



	// PSV
	// P_insp when in PSV mode
	bool set_Pinsp_PSV(Pressure val) {
		// PER.40
		return check_assign(m_psv.Pinsp, val, param_Pinsp_PSV);
	}
	Pressure get_Pinsp_PSV() const {
		return m_psv.Pinsp;
	}
	// Inhale Trigger Sensitivity: the drop in pressure that triggers a new
	// inspiration
	bool set_ITS_PSV(Pressure val) { // PER.41
		return check_assign(m_psv.ITS, val, param_ITS_PSV);
	}

	Pressure get_ITS_PSV() const { // PER.41
		return m_psv.ITS;
	}
	// trigger sensitivity
	// Expiratory Trigger Sensitivity: percentage of peak flow at which MVM
	// triggers expiration as percentage
	// PER.42
	bool set_ETS_perc(int ets_perc) {
		return check_assign(m_psv.ets_perc, ets_perc, param_ets_perc_PSV);
	}
	int get_ETS_perc() const {
		return m_psv.ets_perc;
	}

	// apnea lag
	// set the apnea lag in seconds
	bool set_ApnealagSec(int apnea_lag_sec) {
		return set_ApnealagMilliSec(apnea_lag_sec * 1000);
	}
	// apnealag in milliseconds
	bool set_ApnealagMilliSec(int apnea_lag_millisec) {
		bool set = false;
		int apnea_lag_tobeset;
		// PER. 43
		bool res = check_assign(apnea_lag_tobeset, apnea_lag_millisec,
				param_APLag);
		if (res) {
			// the state machine uses milliseconds
			m_state_machine.setApnealag(apnea_lag_tobeset);
			set = true;
		}
		return set;
	}
	// in milliseconds
	int get_ApnealagMilliSec() const {
		return m_state_machine.getApnealag();
	}

	// set the respiratory ratio for the apnea backup
	bool set_RR_AP(int rr_ap) {
		return check_assign(m_ap.RR, rr_ap, param_RR_AP);
	}
	int get_RR_AP() const {
		return m_ap.RR;
	}
	// P insp when apnea backup
	bool set_Pinsp_AP(Pressure p_insp_ap) {
		return check_assign(m_ap.Pinsp, p_insp_ap, param_Pinsp_AP);
	}
	Pressure get_Pinsp_AP() const {
		return m_ap.Pinsp;
	}
	// set the max Pinsp (useful for example coughing)
	bool set_Max_Pinsp(Pressure val) {
		// PER.50
		return check_assign(m_max_P_insp, val, param_max_P_insp);
	}
	Pressure get_Max_Pinsp() const {
		return m_max_P_insp;
	}

	// set the mode P_CONTROLLED_V, P_SUPPORTED_V
	// if already ventilating change the mode
	bool set_Mode(MVM_mode mode) {
		// if running in ventilation check that change is allowed
		if (mode == MVM_mode::P_SUPPORTED_V
				&& m_state_machine.getMode() == MVM_mode::P_CONTROLLED_V
				&& isVentilating()) {
			// check that apnea parameters are set
			if (is_invalid_value(m_ap.RR) || is_invalid_value(m_ap.Pinsp)) {
				return false;
			}
		}
		m_state_machine.setMode(mode);
		return true;
	}
	MVM_mode get_Mode() const {
		return m_state_machine.getMode();
	}
	// apnea backup flag: true iff the ventilation is running in PCV because of
	// an apnea backup with the apnea backup parameters
	bool get_apnea_backup_mode() const {
		return m_state_machine.getApnea_backup_mode();
	}

	bool leave_ap_backup_mode() {
		m_state_machine.setApnea_backup_mode(false);
		return true;
	}

	// start the ventilation in the right mode
	// to start ventilation in PSV, apnea data must be set
	bool startVentilation() {
		if (m_state_machine.getMode() == MVM_mode::P_SUPPORTED_V) {
			// check that apnea parameters are set
			if (is_invalid_value(m_ap.RR) || is_invalid_value(m_ap.Pinsp))
				return false;
		}
		m_state_machine.raiseStartVentilation();
		return true;
	}

	// return the status of the ventilation
	// 0-> not running (stopped, not ventilating)
	// 1-> running
	enum RunStatus {
		STOPPED, RUNNING
	};

	// return the status of the ventilation
	RunStatus getVentilationRunning() const {
		return isVentilating() ? RUNNING : STOPPED;
	}

	// this will stop the ventilation at the end of the next expiration
	bool set_Stop_PCV_PSV() {
		m_state_machine.setStopVentilation(true);
		return true;
	}

	bool set_Expiratory_Pause_on() {
		m_state_machine.setExp_pause(true);
		return true;
	}

	bool set_Expiratory_Pause_off() {
		m_state_machine.setExp_pause(false);
		return true;
	}

	bool set_Inspiratory_Pause_on() {
		m_state_machine.setIns_pause(true);
		return true;
	}

	bool set_Inspiratory_Pause_off() {
		m_state_machine.setIns_pause(false);
		return true;
	}
	// self test passed: needed to go into ventilation off
	bool set_self_test_passed(bool v) {
		if (v) {
			m_state_machine.raiseSelfTestPassed();
		} else if (m_state_machine.isStateActive(
				MVMStateMachineCore::MVMStateMachineCoreStates::main_region_SelfTest)) {
			// poweroff the machine
			m_state_machine.raisePoweroff();
		}
		return true;
	}

	bool set_Recruitment_Maneuver_on() {
		m_state_machine.setRm_request(true);
		return true;
	}

	bool set_Recruitment_Maneuver_off() {
		m_state_machine.setRm_request(false);
		return true;
	}
	// set the RM pressure
	bool set_Pressure_Recruitment_Maneuver(Pressure val) {
		return check_assign(m_rm.pressure, val, param_Pressure_RM);
	}
	Pressure get_Pressure_Recruitment_Maneuver() const {
		return m_rm.pressure;
	}
	// set the RM time in seconds
	bool set_Maximum_RM_timeSec(int val) {
		return set_Maximum_RM_timeMillisec(val * 1000);
	}
	// set the RM time in milliseconds
	bool set_Maximum_RM_timeMillisec(int val) {
		int m_time_msec;
		bool res = check_assign(m_time_msec, val, param_time_RM_millisec);
		if (res) {
			m_state_machine.setMax_rm_time(val);
		}
		return res;
	}
	int get_Maximum_RM_timeMillisec() const {
		return m_state_machine.getMax_rm_time();
	}
	//
	bool set_enable_leak_compensation(bool val) {
		m_leak_comp.enabled = val;
		return true;
	}
	bool get_enable_leak_compensation() const {
		return m_leak_comp.enabled;
	}
	//
	bool set_Pressure_LeakCompensation(Pressure val) {
		return check_assign(m_leak_comp.pressure, val, param_Pressure_LC);
	}
	Pressure get_Pressure_LeakCompensation() const {
		return m_leak_comp.pressure;
	}

	void begin() {
		// set default values (it should be useless)
		m_state_machine.setApnealag(param_APLag.default_val);
		m_state_machine.setMax_rm_time(param_time_RM_millisec.default_val);
		//
		m_state_machine.enter();
		m_state_machine.raiseStartupEnded();
	}
	void loop() {
		auto const elapsed = m_stop_watch.getDuration();
		m_stop_watch.restart();
		m_timers.updateTimers(&m_state_machine, elapsed);
		m_state_machine.runCycle();
		m_breathing_monitor.loop();
	}
	// OCBsmust have access to the internals of the state machine
	friend class MVMStateMachineOCBs;
};

} // namespace mvm

#endif
