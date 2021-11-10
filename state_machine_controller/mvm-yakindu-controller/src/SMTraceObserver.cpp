/*
 * MVMSTTraceObserver.cpp
 *
 * register the callbacks when exiting/entering states
 *      Author: AngeloGargantini
 */

#include <src-gen/MVMStateMachineCore.h>
#include <src-gen/sc_tracing.h>
#include <cmath>
#include <iostream>
#include "state_machine.h"
#include "ASVUtility.h"

constexpr auto PCV_Inspiration =
		MVMStateMachineCore::MVMStateMachineCoreStates::main_region_PCV_r1_Inspiration;
constexpr auto PSV_Inspiration =
		MVMStateMachineCore::MVMStateMachineCoreStates::main_region_PSV_r1_Inspiration;
constexpr auto PCV_Expiration =
		MVMStateMachineCore::MVMStateMachineCoreStates::main_region_PCV_r1_Expiration;
constexpr auto PSV_Expiration =
		MVMStateMachineCore::MVMStateMachineCoreStates::main_region_PSV_r1_Expiration;
constexpr auto ASV_InitialExpiration =
		MVMStateMachineCore::MVMStateMachineCoreStates::main_region_ASV_r1_InitialExpiration;
constexpr auto ASV_Expiration =
		MVMStateMachineCore::MVMStateMachineCoreStates::main_region_ASV_r1_Expiration;
constexpr auto ASV_InitialInspiration =
		MVMStateMachineCore::MVMStateMachineCoreStates::main_region_ASV_r1_InitialInspiration;
constexpr auto ASV_Inspiration =
		MVMStateMachineCore::MVMStateMachineCoreStates::main_region_ASV_r1_Inspiration;

void mvm::StateMachine::SMTraceObserver::stateEntered(
		MVMStateMachineCore::MVMStateMachineCoreStates state) {

	// On start of a new breathing cycle
	// VERY IMPORTANT: When PSV this event must be called after triggering
	if (state == PCV_Inspiration || state == PSV_Inspiration) {
		m_sm->m_breathing_monitor.TransitionNewCycle_Event_cb();
		//m_sm->m_valves_controller.TransitionNewCycle_Event_cb();
		m_sm->m_valves_controller.breath(true);
		m_sm->m_alarms.TransitionNewCycle_Event_cb();
		// do not return, nested states
	}

	// On passing from Inhale to Exhale
	// when entering exhale - after pause or RM
	if (state == PCV_Expiration || state == PSV_Expiration) {
		m_sm->m_breathing_monitor.TransitionInhaleExhale_Event_cb();
		//m_sm->m_valves_controller.TransitionInhaleExhale_Event_cb();
		m_sm->m_valves_controller.breath(false);
		m_sm->m_alarms.TransitionInhaleExhale_Event_cb();
	}

	// When entering in Expiration State for ASV, store the current time
	if (state == ASV_Expiration || state == ASV_InitialExpiration) {
		auto current_time = std::chrono::system_clock::now();
		auto duration_in_seconds = std::chrono::duration<double>(
				current_time.time_since_epoch());
		m_sm->m_asv.expirationTimes[m_sm->m_asv.index] =
				duration_in_seconds.count();
	}
}

void mvm::StateMachine::SMTraceObserver::adaptVolume(float vTidalAvg) {
	int QT_CHANGE = 1;
	// Adapt based on the target values for volume
	if (vTidalAvg > m_sm->m_asv.targetVTidal + 5) {
		// Lower limit
		if (m_sm->m_asv.targetVTidal >= 4.4 * m_sm->m_state_machine.getIbwASV()) {
			m_sm->m_asv.Pinsp = Pressure(
					m_sm->m_asv.Pinsp.cmH2O() - mvm::Pressure(QT_CHANGE).cmH2O());
		}
	} else if (vTidalAvg < m_sm->m_asv.targetVTidal - 5) {
		Pressure p = Pressure(
				m_sm->m_asv.Pinsp.cmH2O() + mvm::Pressure(QT_CHANGE).cmH2O());
		// Upper limit
		if (p.cmH2O() < 45
				&& m_sm->m_asv.targetVTidal <= 22 * m_sm->m_state_machine.getIbwASV()) {
			m_sm->m_asv.Pinsp = Pressure(
					m_sm->m_asv.Pinsp.cmH2O() + mvm::Pressure(QT_CHANGE).cmH2O());
		}
	}
}

void mvm::StateMachine::SMTraceObserver::adaptRate(float rRateAvg, float rc) {
	int QT_CHANGE = 500;
	// Adapt based on the target values for rate
	if (rRateAvg > m_sm->m_asv.targetRRate + 0.5) {
		// Lower bound
		if (m_sm->m_asv.rRate - 1 >= 5)
			m_sm->m_state_machine.setExpiration_duration_asv_ms(
					m_sm->m_state_machine.getExpiration_duration_asv_ms()
							+ QT_CHANGE);
	} else if (rRateAvg < m_sm->m_asv.targetRRate - 0.5) {
		// Upper bound
		if (m_sm->m_asv.rRate + 1 <= 60 && m_sm->m_asv.rRate + 1 <= (20 / rc)
				&& m_sm->m_state_machine.getExpiration_duration_asv_ms()
						- QT_CHANGE >= 2 * rc * 1000 * m_sm->m_breathing_monitor.CORRECTION_FACTOR)
			m_sm->m_state_machine.setExpiration_duration_asv_ms(
					m_sm->m_state_machine.getExpiration_duration_asv_ms()
							- QT_CHANGE);
	}
}

void mvm::StateMachine::SMTraceObserver::refreshASVValues(int n) {
	float vTidalAvg = 0;
	float rRateAvg = 0;
	float meanRC = 0;
	double timeAvg = 0;
	float peep = 0;
	float realPeep = 0;
	float p_peak;
	double rc = 0;
	float a = (2 * M_PI * M_PI) / 60;
	float vD = m_sm->m_state_machine.getIbwASV() * 2.2;

	// Time corresponding to the expiration duration
	auto current_time = std::chrono::system_clock::now();
	auto duration_in_seconds = std::chrono::duration<double>(
			current_time.time_since_epoch());
	m_sm->m_asv.expirationTimes[m_sm->m_asv.index] = duration_in_seconds.count()
			- m_sm->m_asv.expirationTimes[m_sm->m_asv.index];

	// Get the real values for tidal volume and respiratory rate
	m_sm->m_breathing_monitor.GetOutputValue(
			mvm::BreathingMonitor::Output::TIDAL_VOLUME,
			&m_sm->m_asv.vTidals[m_sm->m_asv.index]);
	m_sm->m_breathing_monitor.GetOutputValue(
			mvm::BreathingMonitor::Output::RESP_RATE,
			&m_sm->m_asv.rRates[m_sm->m_asv.index]);
	// Store the current value of RC
	m_sm->m_breathing_monitor.GetOutputValue(
			mvm::BreathingMonitor::Output::PEEP, &realPeep);
	m_sm->m_breathing_monitor.GetOutputValue(
			mvm::BreathingMonitor::Output::PRESSURE_P, &peep);
	m_sm->m_breathing_monitor.GetOutputValue(
			mvm::BreathingMonitor::Output::FLAT_TOP_P, &p_peak);
	m_sm->m_asv.rcS[m_sm->m_asv.index] =
			-m_sm->m_asv.expirationTimes[m_sm->m_asv.index]
					/ log((peep - realPeep) / (p_peak - realPeep));
	// Next element
	m_sm->m_asv.index = (m_sm->m_asv.index + 1) % 8;

	if (n == 3 || n >= 8) {
		std::cout << "COMPUTING ASV VALUES" << std::endl;
		// Compute the new values
		vTidalAvg = getMean(m_sm->m_asv.vTidals, n);
		rRateAvg = getMean(m_sm->m_asv.rRates, n);
		timeAvg = getMean(m_sm->m_asv.expirationTimes, n);

		std::cout << "CURRENT V_TIDAL AVG: " << vTidalAvg << std::endl;
		std::cout << "CURRENT RR AVG: " << rRateAvg << std::endl;
		std::cout << "CURRENT EXP_TIMES AVG: " << timeAvg << std::endl;

		// Compute the RC value: an RC Circuit has a discharge time of 5 * R * C
		//rc = (timeAvg / 5);
		meanRC = getMean(m_sm->m_asv.rcS, n);
		rc = meanRC * m_sm->m_breathing_monitor.CORRECTION_FACTOR;
		std::cout << "RC: " << rc << std::endl;

		// Compute the target values
		m_sm->m_asv.targetRRate =
				(sqrt(
						1
								+ (2 * a * rc
										* ((m_sm->m_state_machine.getTargetMinuteVentilationASV()
												* m_sm->m_state_machine.getNormalMinuteVentilationASV()
												/ 100)
												- (m_sm->m_asv.prevF / vD)) / vD))
						- 1) / (a * rc);

		std::cout << "TARGET RR: " << m_sm->m_asv.targetRRate << std::endl;

		m_sm->m_asv.targetVTidal =
				(m_sm->m_state_machine.getTargetMinuteVentilationASV()
						* m_sm->m_state_machine.getNormalMinuteVentilationASV()
						/ 100) / m_sm->m_asv.targetRRate;

		std::cout << "TARGET VTidal: " << m_sm->m_asv.targetVTidal << std::endl;

		m_sm->m_asv.prevF = rRateAvg;

		// Adapt based on the target values for rate
		adaptRate(rRateAvg, rc);

		// Adapt based on the target values for volume
		adaptVolume(vTidalAvg);
	}
}

void mvm::StateMachine::SMTraceObserver::stateExited(
		MVMStateMachineCore::MVMStateMachineCoreStates state) {
	// When respiratory cycle finish
	if (state == PCV_Expiration || state == PSV_Expiration) {
		m_sm->m_breathing_monitor.TransitionEndCycle_Event_cb();
		//m_sm->m_valves_controller.TransitionEndCycle_Event_cb();
		m_sm->m_alarms.TransitionEndCycle_Event_cb();
	}

	// When in ASV, if the expiration ends, reads the new values and refresh the params
	if (state == ASV_InitialExpiration || state == ASV_Expiration) {
		refreshASVValues(m_sm->m_state_machine.getNumCycle());
	}

	// Refresh the number of cycles if needed
	if (state == ASV_Inspiration && m_sm->m_state_machine.getNumCycle() < 8) {
		m_sm->m_state_machine.setNumCycle(
				m_sm->m_state_machine.getNumCycle() + 1);
	}
}
