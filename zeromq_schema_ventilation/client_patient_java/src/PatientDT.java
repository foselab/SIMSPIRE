import org.zeromq.ZMQ;
import org.zeromq.SocketType;
import org.zeromq.ZContext;


/** 
 * rappresenta il paziente come DT con il circuito
 * 
 * chiede continuamente la pressione al ventilatore e procede con il calcolo dei suoi parametri
 */
public class PatientDT
{
    public static void main(String[] args)
    {
        try (ZContext context = new ZContext()) {
            System.out.println("Connecting to ventilator");

      		//  Socket to talk to server
            ZMQ.Socket socket = context.createSocket(SocketType.REQ);
            socket.connect("tcp://localhost:5555");

            for (int requestNbr = 0; requestNbr != 100; requestNbr++) {
            	// messaggio per avere la pressione settata dal ventilatore
                String request = "getPressure";
                System.out.println("Sending getPressure " + requestNbr);
                
                socket.send(request.getBytes(ZMQ.CHARSET), 0);
                byte[] reply = socket.recv(0);
                System.out.println(
                    "Received " + new String(reply, ZMQ.CHARSET) + " " + requestNbr
                );
                // setta la pressione nel circuito
                // TODO
                // calcola tutti i parametri
                // TODO
                // calcolo del flusso se viene richiesto dal ventilatore
                // TODO
                // pausa per simulare il ritardo nella risoluzione del circuito
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}