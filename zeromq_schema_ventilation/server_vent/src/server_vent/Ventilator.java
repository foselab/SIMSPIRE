package server_vent;

import org.zeromq.ZMQ;

import java.util.Arrays;

import org.zeromq.SocketType;
import org.zeromq.ZContext;

/**
 * simulates a ventilator if it receives a message "getPressure" returns the
 * pressure (10 o 0 every 5 seconds)
 */
public class Ventilator {

	public static void main(String[] args) throws Exception {
		try (ZContext context = new ZContext()) {
			/*
			 * A socket of type REP is used by a service to receive requests from a client and 
			 * send replies to a client. It allows only an alternating sequence of recv(request) and
			 * subsequent send(reply) calls.
			 */
			ZMQ.Socket socket = context.createSocket(SocketType.REP);
			socket.bind("tcp://localhost:5555");
			System.out.println("starting ventilator...");
	
			while (!Thread.currentThread().isInterrupted()) {				
				// If there are no messages available on the specified socket, reply is null (non-blocking mode)
				byte[] reply = socket.recv(ZMQ.DONTWAIT);
				
				if (reply != null) {					
					// Print the message
					String msg = new String(reply, ZMQ.CHARSET);
					System.out.println("Received message: [" + msg + "]");

					//	se mi domanda la pressione, restituisco il valore
					if (msg.equals("getPressure")) {
						// 	per 5 secondi 10, per altri 5 secondi 0					
						long time = System.currentTimeMillis();
						long resto = time%4000;
						String response =  resto < 2000? "10" : "0"; 
						// Send a response
						socket.send(response.getBytes(ZMQ.CHARSET), 0);
					}
				}			
			}
		}	
	}
}