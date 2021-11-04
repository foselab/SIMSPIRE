package server_vent;

import org.zeromq.ZMQ;
import org.zeromq.ZContext;

public class HelloWorldServer {
	static int n;
	
	public static void main(String[] args) throws Exception {
		try (ZContext context = new ZContext()) {
			// Socket to talk to clients
			ZMQ.Socket socket = context.createSocket(ZMQ.REP);
			socket.bind("tcp://localhost:5555");

			while (!Thread.currentThread().isInterrupted()) {
				// Block until a message is received
				byte[] reply = socket.recv(0);

				// Print the message
				String msg = new String(reply, ZMQ.CHARSET);
				System.out.println("Received: [" + msg + "]");

				// Send a response
				String response;
				if (msg.split(" ")[0].equals("getFlow")) {
					if (n < 5)
						response = "LOW";
					else
						response = "HIGH";
				} else {
					n = Integer.parseInt(msg.split(" ")[1]);
					response = "ACK";
				}
				
				socket.send(response.getBytes(ZMQ.CHARSET), 0);
			}
		}
	}
}