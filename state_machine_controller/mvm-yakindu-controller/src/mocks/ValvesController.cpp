/*
 * ValvesController.cpp
 *
 *  Created on: 23 mag 2020
 *      Author: AngeloGargantini
 */

#include "ValvesController.h"
#include <iostream>

using namespace mvm;

void mvm::ValvesController::set_v1(float value)
{
  v1_input = value;
  std::cout << std::endl << "in-valve:" << v1_input << " out-valve: " << v2_out << std::endl;
}

void mvm::ValvesController::set_v2(float value)
{
  v2_out = value;
  std::cout << std::endl << "in-valve:" << v1_input << " out-valve: " << v2_out << std::endl;
}
