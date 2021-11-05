//============================================================================
// Name        : provayakindu.cpp
// Author      : 
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================

#include <src-gen/MVMStateMachineCore.h>
#include <StateMachineOCBs.h>
#include <iostream>
#include <chrono>
#include "MVMTimerInterface.h"
#include "state_machine.h"
#include "ValvesController.h"
#include "BreathingMonitor.h"
#include "Alarms.h"

using namespace std;

#define TOTAL_TIME_MS 110000

int main() {
	mvm::ValvesController valves_controller { };
	mvm::BreathingMonitor breathing_monitor { };
	mvm::Alarms alarms { };
	mvm::StateMachine sm { valves_controller, breathing_monitor, alarms };

	sm.begin();

	// some timers
	AsyncDelay self_testpassed(500, AsyncDelay::MILLIS);
	AsyncDelay run_ventilation(800, AsyncDelay::MILLIS);
	AsyncDelay stop_ventilation(100000, AsyncDelay::MILLIS);
	AsyncDelay finish(TOTAL_TIME_MS, AsyncDelay::MILLIS);
	AsyncDelay cycle(100, AsyncDelay::MILLIS);
	for (;;) {
		if (cycle.isExpired()) {
			std::cout << ".";
			// some commands
			sm.set_self_test_passed(true);
			if (self_testpassed.isExpired()) {
				// pass the self tests
				std::cout << "self test passed" << std::endl;
				sm.set_self_test_passed(true);
				self_testpassed.start(TOTAL_TIME_MS, AsyncDelay::MILLIS);
			}
			if (run_ventilation.isExpired()) {
				std::cout << "starting ventilation in ASV mode" << std::endl;
				sm.set_Mode(MVM_mode::A_SUPPORTED_V);
				sm.startVentilation();
				run_ventilation.start(TOTAL_TIME_MS, AsyncDelay::MILLIS);
			}
			if (stop_ventilation.isExpired()) {
				std::cout << "stopping ventilation" << std::endl;
				sm.set_Stop_PCV_PSV();
				stop_ventilation.start(TOTAL_TIME_MS, AsyncDelay::MILLIS);
			}
			sm.loop();
			cycle.restart();
			if (finish.isExpired())
				break;
		}
	}

	return 0;
}
