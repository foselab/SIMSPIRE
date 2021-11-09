/*
 * BreathingMonitor.cpp
 *
 *  Created on: 9 nov 2021
 *      Author: Andrea_PC
 */

#include "BreathingMonitor.h"

void mvm::BreathingMonitor::getFlux() {
	// Initialize the Zmq context with a single IO thread
	zmq::context_t context { 1 };
	// Construct a REQ (request) socket and connect to interface
	zmq::socket_t socket { context, zmq::socket_type::req };
	socket.connect("tcp://localhost:5555");
	// Set up the message to be sent for requesting the flow
	std::string data { "getFlux " };
	// send the request message
	socket.send(zmq::buffer(data), zmq::send_flags::none);
	// wait for reply from server
	zmq::message_t reply { };
	socket.recv(reply, zmq::recv_flags::none);
	double value = std::stof(reply.to_string());
	flux = value;

	double time = std::chrono::duration_cast<std::chrono::seconds>(std::chrono::steady_clock::now().time_since_epoch()).count();

	//------------------
	// If it is not the first iteration, then compute the volume with the integral
	//------------------
	if (value < 0) {
		value = std::abs(value);
		if (!first) {
			// The volume is multiplied by 1000 since it is computed in mL, while the flux is expressed in L
			volume += (((oldFlux + value) * (time - oldTime)) / 2.0) * 1000;
		} else {
			first = false;
			volume = 0.0;
		}
		oldFlux = value;
		oldTime = time;
	} else {
		first = true;
	}
}

void mvm::BreathingMonitor::getFluxPeak() {
	// Initialize the Zmq context with a single IO thread
	zmq::context_t context { 1 };
	// Construct a REQ (request) socket and connect to interface
	zmq::socket_t socket { context, zmq::socket_type::req };
	socket.connect("tcp://localhost:5555");
	// Set up the message to be sent for requesting the flow
	std::string data { "getFluxPeak " };
	// send the request message
	socket.send(zmq::buffer(data), zmq::send_flags::none);
	// wait for reply from server
	zmq::message_t reply { };
	socket.recv(reply, zmq::recv_flags::none);
	float value = std::stof(reply.to_string());
	flux_peak = value;
}

void mvm::BreathingMonitor::getPressure() {
	// Initialize the Zmq context with a single IO thread
	zmq::context_t context { 1 };
	// Construct a REQ (request) socket and connect to interface
	zmq::socket_t socket { context, zmq::socket_type::req };
	socket.connect("tcp://localhost:5555");
	// Set up the message to be sent for requesting the flow
	std::string data { "getPressure " };
	// send the request message
	socket.send(zmq::buffer(data), zmq::send_flags::none);
	// wait for reply from server
	zmq::message_t reply { };
	socket.recv(reply, zmq::recv_flags::none);
	pressure_p = std::stof(reply.to_string());
}

void mvm::BreathingMonitor::getRespiratoryRate() {
	// Initialize the Zmq context with a single IO thread
	zmq::context_t context { 1 };
	// Construct a REQ (request) socket and connect to interface
	zmq::socket_t socket { context, zmq::socket_type::req };
	socket.connect("tcp://localhost:5555");
	// Set up the message to be sent for requesting the flow
	std::string data { "getRespiratoryRate " };
	// send the request message
	socket.send(zmq::buffer(data), zmq::send_flags::none);
	// wait for reply from server
	zmq::message_t reply { };
	socket.recv(reply, zmq::recv_flags::none);
	r_rate = std::stof(reply.to_string());
}

mvm::BreathingMonitor::BreathingMonitor() {
	first = true;
	volume = 0;
	oldTime = 0;
	oldFlux = 0;
	pressure_p = 0;
	flux = 0;
	flux_peak = 0;
	r_rate = 0;
}
float mvm::BreathingMonitor::getVolume() {
	return volume;
}
void mvm::BreathingMonitor::setVolume(float volume) {
	this->volume = volume;
}

void mvm::BreathingMonitor::GetOutputValue(Output probe, float *value) {
	switch (probe) {
	case Output::TIDAL_VOLUME:
		(*value) = volume;
		break;
	case Output::PRESSURE_P:
		(*value) = pressure_p;
		break;
	case Output::FLUX:
		(*value) = flux;
		break;
	case Output::PDELTA_1:
		// TODO:
		break;
	case Output::PDELTA_2:
		// TODO:
		break;
	case Output::FLUXPEAK:
		(*value) = flux_peak;
		break;
	case Output::TINSP:
		// TODO:
		break;
	case Output::RESP_RATE:
		(*value) = r_rate;
		break;
	default:
		break;
	}
}
bool mvm::BreathingMonitor::SetConfigurationValue(Config probe, float value) {
	return true;
}
void mvm::BreathingMonitor::GetConfigurationValue(Config probe,
		float *value) const {
}
void mvm::BreathingMonitor::GetOutputValue(Output probe, float *value) const {
}
void mvm::BreathingMonitor::loop() {
	// Read the flux
	getFlux();
	// Read the pressure
	getPressure();
	// Read the peak flux
	getFluxPeak();
	// Read the respiratory rate
	getRespiratoryRate();
}

