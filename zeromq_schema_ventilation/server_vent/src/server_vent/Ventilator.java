package server_vent;

import org.zeromq.ZMQ;
import org.zeromq.ZContext;

/**
 * simulates a ventilator if it receives a message "getPressure" returns the
 * pressure (10 o 0 every 5 seconds)
 */
public class Ventilator {

	public static void main(String[] args) throws Exception {
		try (ZContext context = new ZContext()) {
			// Socket to talk to clients
			ZMQ.Socket socket = context.createSocket(ZMQ.REP);
			socket.bind("tcp://localhost:5555");
			System.out.println("starting ventilator");
			while (!Thread.currentThread().isInterrupted()) {				
				// do not block if nothing i requested
				byte[] reply = socket.recv(org.zeromq.ZMQ.DONTWAIT);

				if (reply != null) {					
					// Print the message
					String msg = new String(reply, ZMQ.CHARSET);
					System.out.println("Received: [" + msg + "]");

					//	se mi domanda la pressione, restituisco il valore
					if (msg.equals("getPressure")) {
						// 	per 5 secondi 10, per altri 5 secondi 0					
						long time = System.currentTimeMillis();
						long resto = time%10000;
						String response =  resto < 5000? "10" : "0"; 
						// Send a response
						socket.send(response.getBytes(ZMQ.CHARSET), 0);
					}
				}
				// update il ventilatore o altro
				// TODO				
			}
		}	
	}
}