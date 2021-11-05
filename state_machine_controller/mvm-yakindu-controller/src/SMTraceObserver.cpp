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

void mvm::StateMachine::SMTraceObserver::refreshASVValues(int n) {
	float vTidalAvg = 0;
	float rRateAvg = 0;
	float timeAvg = 0;
	float rc = 0;
	float a = 2 * M_PI * M_PI / 60;
	float vD = m_sm->m_state_machine.getIbwASV() * 2.2;

	// Time corresponding to the exiration duration
	auto current_time = std::chrono::system_clock::now();
	auto duration_in_seconds = std::chrono::duration<double>(
			current_time.time_since_epoch());
	m_sm->m_asv.expirationTimes[m_sm->m_asv.index] = duration_in_seconds.count()
			- m_sm->m_asv.expirationTimes[m_sm->m_asv.index];

	// Get the real values for tidal volume and respiratory rate
	m_sm->m_breathing_monitor.GetOutputValue(
			mvm::BreathingMonitor::Output::TIDAL_VOLUME,
			&m_sm->m_asv.vTidals[m_sm->m_asv.index]);
	m_sm->m_breathing_monitor.GetOutputValue(mvm::BreathingMonitor::Output::RESP_RATE,
			&m_sm->m_asv.rRates[m_sm->m_asv.index]);
	m_sm->m_asv.index = (m_sm->m_asv.index + 1) % 8;

	// Compute the new values
	vTidalAvg = getMean(m_sm->m_asv.vTidals, n);
	rRateAvg = getMean(m_sm->m_asv.rRates, n);
	timeAvg = getMean(m_sm->m_asv.expirationTimes, n);

	// Compute the RC value: an RC Circuit has a discharge time of 5 * R * C
	rc = timeAvg / 5;

	// Compute the target values
	m_sm->m_asv.targetRRate = (sqrt(1 + 2 * a * rc * (m_sm->m_state_machine.getTargetMinuteVentilationASV() - m_sm->m_asv.prevF / vD) / vD) - 1) / (a * rc);
	m_sm->m_asv.targetVTidal =
			m_sm->m_state_machine.getTargetMinuteVentilationASV()
					/ m_sm->m_asv.targetRRate;
	m_sm->m_asv.prevF = m_sm->m_asv.targetRRate;

	// Adapt based on the target values
	if (rRateAvg > m_sm->m_asv.targetRRate)
		m_sm->m_asv.Pinsp = Pressure(
				m_sm->m_asv.Pinsp.millibar() - mvm::Pressure(2).millibar());
	else if (rRateAvg < m_sm->m_asv.targetRRate)
		m_sm->m_asv.Pinsp = Pressure(
				m_sm->m_asv.Pinsp.millibar() + mvm::Pressure(2).millibar());
}

void mvm::StateMachine::SMTraceObserver::stateExited(
		MVMStateMachineCore::MVMStateMachineCoreStates state) {
	std::cout << "STATE EXITED" << std::endl;

	// When respiratory cycle finish
	if (state == PCV_Expiration || state == PSV_Expiration) {
		m_sm->m_breathing_monitor.TransitionEndCycle_Event_cb();
		//m_sm->m_valves_controller.TransitionEndCycle_Event_cb();
		m_sm->m_alarms.TransitionEndCycle_Event_cb();
	}

	// When in ASV, if the expiration ends, reads the new values and refresh the params
	if (state == ASV_InitialExpiration
			&& m_sm->m_state_machine.getNumCycle() == 3) {
		refreshASVValues(3);
	}
	if (state == ASV_Expiration && m_sm->m_state_machine.getNumCycle() >= 8) {
		refreshASVValues(8);
	}

	// Refresh the number of cycles if needed
	if (state == ASV_Inspiration && m_sm->m_state_machine.getNumCycle() < 8) {
		m_sm->m_state_machine.setNumCycle(
				m_sm->m_state_machine.getNumCycle() + 1);
	}
}
