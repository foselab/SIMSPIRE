/*
 * MVMSTTraceObserver.cpp
 *
 * register the callbacks when exiting/entering states
 *      Author: AngeloGargantini
 */

#include <src-gen/MVMStateMachineCore.h>
#include <src-gen/sc_tracing.h>
#include "state_machine.h"
#include "ASVUtility.h"

constexpr auto PCV_Inspiration = MVMStateMachineCore::
    MVMStateMachineCoreStates::main_region_PCV_r1_Inspiration;
constexpr auto PSV_Inspiration = MVMStateMachineCore::
    MVMStateMachineCoreStates::main_region_PSV_r1_Inspiration;
constexpr auto PCV_Expiration = MVMStateMachineCore::MVMStateMachineCoreStates::
    main_region_PCV_r1_Expiration;
constexpr auto PSV_Expiration = MVMStateMachineCore::MVMStateMachineCoreStates::
    main_region_PSV_r1_Expiration;
constexpr auto ASV_InitialExpiration = MVMStateMachineCore::MVMStateMachineCoreStates::
    main_region_ASV_r1_InitialExpiration;
constexpr auto ASV_Expiration = MVMStateMachineCore::MVMStateMachineCoreStates::
    main_region_ASV_r1_Expiration;
constexpr auto ASV_InitialInspiration = MVMStateMachineCore::MVMStateMachineCoreStates::
    main_region_ASV_r1_InitialInspiration;
constexpr auto ASV_Inspiration = MVMStateMachineCore::MVMStateMachineCoreStates::
    main_region_ASV_r1_Inspiration;

void mvm::StateMachine::SMTraceObserver::stateEntered(
    MVMStateMachineCore::MVMStateMachineCoreStates state)
{
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
}

void mvm::StateMachine::SMTraceObserver::refreshASVValues() {
	// TODO: How to measure the time?
	m_sm->m_asv.expirationTimes[m_sm->m_asv.index] = 0;
	// Get the real values for tidal volume and respiratory rate
	m_breathing_monitor.GetOutputValue(
			mvm::BreathingMonitor::Output::TIDAL_VOLUME,
			&m_sm->m_asv.vTidals[m_sm->m_asv.index]);
	m_breathing_monitor.GetOutputValue(mvm::BreathingMonitor::Output::RESP_RATE,
			&m_sm->m_asv.rRates[m_sm->m_asv.index]);
	m_sm->m_asv.index = (m_sm->m_asv.index + 1) % 8;

	// Compute the new value?
	if (m_sm->m_state_machine.getNumCycle() == 3) {
		// Initial parameters computation
	} else {
		if (m_sm->m_state_machine.getNumCycle() > 8) {
			// Compute the parameters with the last 8 values

		}
	}
}

void mvm::StateMachine::SMTraceObserver::stateExited(
    MVMStateMachineCore::MVMStateMachineCoreStates state)
{

  // When respiratory cycle finish
  if (state == PCV_Expiration || state == PSV_Expiration) {
    m_sm->m_breathing_monitor.TransitionEndCycle_Event_cb();
    //m_sm->m_valves_controller.TransitionEndCycle_Event_cb();
    m_sm->m_alarms.TransitionEndCycle_Event_cb();
  }

  // When in ASV, if the expiration ends, reads the new values and refresh the params
  if (state == ASV_InitialExpiration || state == ASV_Expiration) {
	  refreshASVValues();
  }
}
