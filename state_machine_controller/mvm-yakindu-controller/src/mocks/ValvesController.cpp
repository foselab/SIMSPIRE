/*
 * ValvesController.cpp
 *
 *  Created on: 23 mag 2020
 *      Author: AngeloGargantini
 */

#include "ValvesController.h"
#include <iostream>
#include <zmq.hpp>

using namespace mvm;

void mvm::ValvesController::set_v1(float value) {
	v1_input = value;
	std::cout << std::endl << "in-valve:" << v1_input << " out-valve: "
			<< v2_out << std::endl;

	/*
	 * Send the command to the patient simulator
	 */
	// Initialize the Zmq context with a single IO thread
	zmq::context_t context { 1 };

	// Construct a REQ (request) socket and connect to interface
	zmq::socket_t socket { context, zmq::socket_type::req };
	socket.connect("tcp://localhost:5555");

	// Set up the message to be sent for requesting the flow
	std::string data { "setPressure " };
	std::string response = "";

	// send the request message
	socket.send(zmq::buffer(data + std::to_string(value)),
			zmq::send_flags::none);

	// wait for reply from server
	zmq::message_t reply { };
	socket.recv(reply, zmq::recv_flags::none);

	response = reply.to_string();
}

void mvm::ValvesController::set_v2(float value) {
	v2_out = value;
	std::cout << std::endl << "in-valve:" << v1_input << " out-valve: "
			<< v2_out << std::endl;
}
