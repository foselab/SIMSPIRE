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

bool mvm::StateMachine::SMTraceObserver::decreaseRate() {
	if (m_sm->m_asv.rRate - 1 >= 5) {
		m_sm->m_state_machine.setExpiration_duration_asv_ms(m_sm->m_state_machine.getExpiration_duration_asv_ms() + QT_CHANGE_R);
		return true;
	}
	return false;
}

bool mvm::StateMachine::SMTraceObserver::increaseRate() {
	m_sm->m_state_machine.setExpiration_duration_asv_ms(
			m_sm->m_state_machine.getExpiration_duration_asv_ms()
					- QT_CHANGE_R);
}

bool mvm::StateMachine::SMTraceObserver::decreasePressure() {
	return decreasePressure(QT_CHANGE_P);
}

bool mvm::StateMachine::SMTraceObserver::increasePressure() {
	return increasePressure(QT_CHANGE_P);
}

bool mvm::StateMachine::SMTraceObserver::decreasePressure(float qt) {
	if (m_sm->m_asv.targetVTidal >= 4.4 * m_sm->m_state_machine.getIbwASV()) {
		m_sm->m_asv.Pinsp = Pressure(
				m_sm->m_asv.Pinsp.cmH2O() - mvm::Pressure(qt).cmH2O());
		return true;
	}
	return false;
}

bool mvm::StateMachine::SMTraceObserver::increasePressure(float qt) {
	Pressure p = Pressure(m_sm->m_asv.Pinsp.cmH2O() + mvm::Pressure(qt).cmH2O());
	if (p.cmH2O() < 45 && m_sm->m_asv.targetVTidal <= 22 * m_sm->m_state_machine.getIbwASV()) {
		m_sm->m_asv.Pinsp = p;
		return true;
	}
	return false;
}

bool mvm::StateMachine::SMTraceObserver::adaptVolume(float vTidalAvg) {
	// Adapt based on the target values for volume
	if (vTidalAvg > m_sm->m_asv.targetVTidal + 50) {
		return decreasePressure((1 * (vTidalAvg - m_sm->m_asv.targetVTidal)/m_sm->m_asv.targetVTidal));
	} else if (vTidalAvg < m_sm->m_asv.targetVTidal - 50) {
		return increasePressure((1 * (m_sm->m_asv.targetVTidal - vTidalAvg)/m_sm->m_asv.targetVTidal));
	}

	return false;
}

bool mvm::StateMachine::SMTraceObserver::adaptVolume(float vTidalAvg, float m) {
	// Adapt based on the target values for volume and on the angular coefficient of the last two points
	if (vTidalAvg > m_sm->m_asv.targetVTidal + 50) {
		return decreasePressure((1 * (vTidalAvg - m_sm->m_asv.targetVTidal)/m_sm->m_asv.targetVTidal));
	} else if (vTidalAvg < m_sm->m_asv.targetVTidal - 50) {
		return increasePressure((1 * (m_sm->m_asv.targetVTidal - vTidalAvg)/m_sm->m_asv.targetVTidal));
	}
	return false;
}

bool mvm::StateMachine::SMTraceObserver::adaptRate(float rRateAvg, float rc) {
	// Adapt based on the target values for rate
	/*if (rRateAvg > m_sm->m_asv.targetRRate + 0.5) {
		return decreaseRate();
	}
	else if (rRateAvg < m_sm->m_asv.targetRRate - 0.5) {
		// Upper bound
		if (m_sm->m_asv.rRate + 1 <= 60 && m_sm->m_asv.rRate + 1 <= (20 / rc)
				&& m_sm->m_state_machine.getExpiration_duration_asv_ms() - QT_CHANGE_R >= 2 * rc * 1000 * m_sm->m_breathing_monitor.CORRECTION_FACTOR) {
			increaseRate();
			return true;
		}
	}
	return false;*/
	if (rRateAvg > m_sm->m_asv.targetRRate + 0.5) {
		m_sm->m_state_machine.setExpiration_duration_asv_ms(4000*rc);
	}
	else if (rRateAvg < m_sm->m_asv.targetRRate - 0.5) {
		m_sm->m_state_machine.setExpiration_duration_asv_ms(3000*rc);
	}
}

void mvm::StateMachine::SMTraceObserver::updateExpirationTime() {
	// Time corresponding to the expiration duration
	auto current_time = std::chrono::system_clock::now();
	auto duration_in_seconds = std::chrono::duration<double>(
			current_time.time_since_epoch());
	m_sm->m_asv.expirationTimes[m_sm->m_asv.index] = duration_in_seconds.count()
			- m_sm->m_asv.expirationTimes[m_sm->m_asv.index];
	m_sm->m_asv.expirationTimesQueue.push_back(
			m_sm->m_asv.expirationTimes[m_sm->m_asv.index]);
	if (m_sm->m_asv.expirationTimesQueue.size() > 8)
		m_sm->m_asv.expirationTimesQueue.pop_front();
}

void mvm::StateMachine::SMTraceObserver::updateTidalVolume() {
	// Get the real values for tidal volume and respiratory rate
	m_sm->m_breathing_monitor.GetOutputValue(
			mvm::BreathingMonitor::Output::TIDAL_VOLUME,
			&m_sm->m_asv.vTidals[m_sm->m_asv.index]);
	m_sm->m_asv.vTidalsQueue.push_back(m_sm->m_asv.vTidals[m_sm->m_asv.index]);
	if (m_sm->m_asv.vTidalsQueue.size() > 8)
		m_sm->m_asv.vTidalsQueue.pop_front();
}

void mvm::StateMachine::SMTraceObserver::updateRespiratoryRate() {
	m_sm->m_breathing_monitor.GetOutputValue(
			mvm::BreathingMonitor::Output::RESP_RATE,
			&m_sm->m_asv.rRates[m_sm->m_asv.index]);
	m_sm->m_asv.rRatesQueue.push_back(m_sm->m_asv.rRates[m_sm->m_asv.index]);
	if (m_sm->m_asv.rRatesQueue.size() > 8)
		m_sm->m_asv.rRatesQueue.pop_front();
}

void mvm::StateMachine::SMTraceObserver::updateRC(float& realPeep, float& peep,
		float& p_peak) {
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
	m_sm->m_asv.rcSQueue.push_back(m_sm->m_asv.rcS[m_sm->m_asv.index]);
	if (m_sm->m_asv.rcSQueue.size() > 8)
		m_sm->m_asv.rcSQueue.pop_front();

	m_sm->m_asv.csQueue.push_back(
			m_sm->m_asv.vTidals[m_sm->m_asv.index]
					/ (1000 * (p_peak - realPeep)));
	if (m_sm->m_asv.csQueue.size() > 8)
		m_sm->m_asv.csQueue.pop_front();
}

void mvm::StateMachine::SMTraceObserver::refreshASVValues(int n) {
	float vTidalAvg = 0;
	float rRateAvg = 0;
	float cAvg = 0;
	float meanRC = 0;
	double timeAvg = 0;
	float peep = 0;
	float realPeep = 0;
	float p_peak;
	double rc = 0;
	float a = (2 * M_PI * M_PI) / 60;
	float vD = m_sm->m_state_machine.getIbwASV() * 2.2;
	bool useWeightedMean = true;

	// Time corresponding to the expiration duration
	updateExpirationTime();

	// Get the real values for tidal volume and respiratory rate
	updateTidalVolume();
	updateRespiratoryRate();
	// Store the current value of RC
	updateRC(realPeep, peep, p_peak);

	// If the first three cycles (or after 8) has passed, then compute the new values
	if (n == 3 || n >= 8) {
		std::cout << "COMPUTING ASV VALUES" << std::endl;
		// Compute the new values
		if (!useWeightedMean) {
			vTidalAvg = getMean(m_sm->m_asv.vTidals, n);
			rRateAvg = getMean(m_sm->m_asv.rRates, n);
			timeAvg = getMean(m_sm->m_asv.expirationTimes, n);
			meanRC = getMean(m_sm->m_asv.rcS, n);
		} else {
			vTidalAvg = getWeightedMean(m_sm->m_asv.vTidalsQueue);
			rRateAvg = getWeightedMean(m_sm->m_asv.rRatesQueue);
			timeAvg = getWeightedMean(m_sm->m_asv.expirationTimesQueue);
			cAvg = getWeightedMean(m_sm->m_asv.csQueue);
			meanRC = getWeightedMean(m_sm->m_asv.rcSQueue);
		}

		rc = meanRC * m_sm->m_breathing_monitor.CORRECTION_FACTOR;

		// Compute the target values
		m_sm->m_asv.targetRRate = (sqrt(1 + (2 * a * rc * ((m_sm->m_state_machine.getTargetMinuteVentilationASV()
			* m_sm->m_state_machine.getNormalMinuteVentilationASV() / 100) - (m_sm->m_asv.prevF / vD)) / vD)) - 1) / (a * rc);

		m_sm->m_asv.targetVTidal = (m_sm->m_state_machine.getTargetMinuteVentilationASV() * m_sm->m_state_machine.getNormalMinuteVentilationASV() / 100) / m_sm->m_asv.targetRRate;

		m_sm->m_asv.prevF = m_sm->m_asv.targetRRate;

		// Output messages for DEBUG
		std::cout << "AVG V_TIDAL: " << vTidalAvg << std::endl;
		std::cout << "AVG RR: " << rRateAvg << std::endl;
		std::cout << "AVG EXP_TIMES: " << timeAvg << std::endl;
		std::cout << "AVG C: " << cAvg << std::endl;
		std::cout << "RC: " << rc << std::endl;
		std::cout << "TARGET RR: " << m_sm->m_asv.targetRRate << std::endl;
		std::cout << "TARGET VTidal: " << m_sm->m_asv.targetVTidal << std::endl;

		// Adapt based on the target values for rate
		adaptRate(rRateAvg, rc);

		// Compute the angular coefficient
		float m = (((m_sm->m_asv.vTidalsQueue[n-1] - m_sm->m_asv.vTidalsQueue[n-2]) / 100) + ((m_sm->m_asv.vTidalsQueue[n-2] - m_sm->m_asv.vTidalsQueue[n-3]) / 100)) / 2;
//		std::cout << "m: " << m << std::endl;
//
		if ((m <= 0 && m_sm->m_asv.targetVTidal > vTidalAvg) || (m >=0 && m_sm->m_asv.targetVTidal < vTidalAvg))
			adaptVolume(vTidalAvg);

		// Adapt based on the target values for volume
//		m_sm->m_asv.Pinsp = Pressure(vTidalAvg * (rc/cAvg) / (((1/rRateAvg) * 60) - timeAvg));
//		adaptVolume(vTidalAvg);
		// m_sm->m_asv.Pinsp = Pressure(mvm::Pressure((m_sm->m_asv.targetVTidal / (cAvg * 1000)) + peep).cmH2O());
	}

	// Next element
	m_sm->m_asv.index = (m_sm->m_asv.index + 1) % 8;
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
