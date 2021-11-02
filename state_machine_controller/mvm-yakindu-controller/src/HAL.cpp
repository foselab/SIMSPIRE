/*
 * HAL.cpp
 *
 *  Created on: 7 mag 2020
 *      Author: AngeloGargantini
 */
#include <HAL.h>
#include <iostream>

HAL::HAL() {
	// TODO Auto-generated constructor stub

}

HAL::~HAL() {
	// TODO Auto-generated destructor stub
}

void HAL::startExpiration(){
	std::cout<< "starting expiration - out open" << std::endl;
}

void HAL::startInspiration(){
	std::cout<< "starting inspiration - in open" << std::endl;
}

void HAL::endExpiration(){
	std::cout<< "end expiration - out close" << std::endl;
}

void HAL::endInspiration(){
	std::cout<< "end inspiration - in close" << std::endl;
}

double HAL::getPressure(){
	std::cout<< "reading pressure " << std::endl;
	return 20;
}

bool HAL::flowDrop(){
	return true;
}
