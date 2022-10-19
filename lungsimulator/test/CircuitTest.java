import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

import components.CapacitorElm;
import components.CircuitElm;
import components.DCVoltageElm;
import components.ResistorElm;
import simulator.CirSim;

public class CircuitTest {

	private CirSim cirSim;
	private DCVoltageElm battery;

	@BeforeClass
	static public void extracted() {
		Logger.getLogger(CirSim.class.getName()).setLevel(Level.OFF);
		Logger.getLogger(ResistorElm.class.getName()).setLevel(Level.OFF);
		Logger.getLogger(CapacitorElm.class.getName()).setLevel(Level.OFF);
	}

	@Test
	public void test() throws InterruptedException {		
		buildCircuit();		
		// in secondi
		double delta = 0.02;
		
		cirSim.setTimeStep(delta);

		// voglio stampare la corrente per 5 sec
		final long init_millisec = System.currentTimeMillis();
		long last_millisec = init_millisec;
		while (System.currentTimeMillis() - init_millisec < 1000) {
			// stop for delta millisecs
			while(System.currentTimeMillis() - last_millisec < delta * 1000);
			System.out.println("time " + (System.currentTimeMillis() - init_millisec) + " battery I: "+ battery.getCurrent());
			cirSim.analyzeCircuit();
			cirSim.loopAndContinue(false);
			last_millisec = System.currentTimeMillis();
		}

	}

	@Test
	public void testTimerTask() throws InterruptedException {		
		buildCircuit();		
		// in secondi
		double delta = 0.02;		
		cirSim.setTimeStep(delta);
		Timer timer = new Timer();
		final long init_millisec = System.currentTimeMillis();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
    			System.out.println("time " + (System.currentTimeMillis() - init_millisec) + " battery I: "+ battery.getCurrent());
    			cirSim.analyzeCircuit();
    			cirSim.loopAndContinue(false);            	
            }
        }, 0, (long)(delta*1000));
	}

	
	private void buildCircuit() {
		// R
		ResistorElm R = new ResistorElm(1, 1);
		R.setX2Y2(1, 0);
		R.setResistance(10);

		CapacitorElm cap = new CapacitorElm(0, 0);
		cap.setX2Y2(1, 1);
		cap.setCapacitance(0.0020);

		battery = new DCVoltageElm(1, 0);
		battery.setX2Y2(0, 0);
		battery.setMaxVoltage(20);

		List<CircuitElm> elements = Arrays.asList(R, cap, battery);
//		List<CircuitElm> elements = Arrays.asList(R,battery);
		for (CircuitElm c : elements) {
			c.setPoints();
		}

		cirSim = new CirSim();
		cirSim.setElmList(elements);
		CircuitElm.sim = cirSim;
	}

}
