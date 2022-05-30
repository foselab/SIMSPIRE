/*
 * client_vent2.cpp
 *
 *  Created on: 4 nov 2021
 *      Author: Andrea_PC
 */
#include <iostream>
using namespace std;


#include <string>
#include <iostream>

#include <zmq.hpp>

int main() {
	// initialize the zmq context with a single IO thread
	zmq::context_t context { 1 };

	// construct a REQ (request) socket and connect to interface
	zmq::socket_t socket { context, zmq::socket_type::req };
	socket.connect("tcp://localhost:5555");

	// set up some static data to send
	std::string data { "setPressure " };
	std::string data2 { "getFlow " };

	for (auto request_num = 0; request_num < 10; ++request_num) {
		// send the request message
		std::cout << "Sending setPressure " << request_num << "..." << std::endl;
		socket.send(zmq::buffer(data + std::to_string(request_num)), zmq::send_flags::none);

		// wait for reply from server
		zmq::message_t reply { };
		socket.recv(reply, zmq::recv_flags::none);

		std::cout << "Received " << reply.to_string();
		std::cout << " (" << request_num << ")";
		std::cout << std::endl;

		std::cout << "Sending getFlow " << "..." << std::endl;
		socket.send(zmq::buffer(data2), zmq::send_flags::none);

		// wait for reply from server
		socket.recv(reply, zmq::recv_flags::none);

		std::cout << "Received " << reply.to_string();
		std::cout << " (" << request_num << ")";
		std::cout << std::endl;
	}

	return 0;
}
