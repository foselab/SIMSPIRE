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

int main() {
	mvm::ValvesController v;
	mvm::BreathingMonitor b;
	mvm::Alarms a;

	mvm::StateMachine sm (v, b, a);


	//sm.begin();
	//sm.startVentilation();
	//sm.loop();


/*	mvm::MVMTimerInterface timer_sct;
	bool running = false;
	auto start = std::chrono::system_clock::now();
	MVMStateMachineCore sm;
	//HAL myHAL;
	//sm.set_hal(&myHAL);
	MVMStateMachineOCBs operationCallback(&sm);
	sm.setDefaultSCI_OCB(&operationCallback);
	std::cout << "init state machine" << std::endl;
	sm.set_inspiration_duration_ms(1000);
	sm.set_expiration_duration_ms(2000);
	sm.init();
	std::cout << "setting timer" << std::endl;
	sm.setTimer(&timer_sct);
	std::cout << "entering" << std::endl;
	sm.enter();
	// finished load
	sm.raise_startupEnded();
	sm.runCycle();
	//
	//cout<< "PCV mode? " << std::boolalpha << sm.isStateActive(MVMStateMachine::main_region_PCV) << endl;
	//cout<< "ventilation off? " << std::boolalpha << sm.isStateActive(MVMStateMachine::main_region_PCV_r1_VentilationOff) << endl;
	for (;;) {
		auto end = std::chrono::system_clock::now();
		std::chrono::duration<double> elapsed_seconds = end - start;
		long sec = elapsed_seconds.count();
		// after 2 seconds run it
		if (sec > 2 && sec < 4 && ! running){
			std::cout << "start running" << std::endl;
			running = true;
			sm.raise_startVentilation();
		}
		// after 4 seconds stop it
		if (sec > 4 && running) {
			std::cout << "stopping" << std::endl;
			running = false;
			sm.set_stopVentilation(true);
		}
		sm.runCycle();
		timer_sct.updateActiveTimer(&sm, 1); // 10 milliseconds
		if (sec > 10)
			break;
		//if (sm.getDefaultSCI()->isRaised_finish()){
		//	std::cout << "finish raised" << std::endl;
		//	break;
		//}
	}*/
	return 0;
}
