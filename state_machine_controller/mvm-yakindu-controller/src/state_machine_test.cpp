/*
 * state_machine_test.cpp
 *
 *  Created on: 4 nov 2021
 *      Author: Andrea_PC
 */

#include <iostream>
#include "state_machine.h"

using namespace std;

int main() {
	mvm::StateMachine sm;

	sm.begin();
	sm.startVentilation();
	sm.loop();

}
