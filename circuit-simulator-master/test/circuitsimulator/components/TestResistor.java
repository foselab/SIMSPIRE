package circuitsimulator.components;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import circuitsimulator.components.CapacitorElm;
import circuitsimulator.components.CircuitElm;
import circuitsimulator.components.DCVoltageElm;
import circuitsimulator.components.ResistorElm;
import circuitsimulator.simulator.CirSim;

public class TestResistor {

	@Test
	public void test1() {
	
		
		// R
		ResistorElm R = new ResistorElm(1,1);
		R.setX2Y2(1,0);
		R.setResistance(10);

		CapacitorElm cap = new CapacitorElm(0,0);
		cap.setX2Y2(1, 1);
		cap.setCapacitance(0.0020);

		
		DCVoltageElm battery = new DCVoltageElm(1,0);
		battery.setX2Y2(0,0);
		battery.setMaxVoltage(20);
		
				
		List<CircuitElm> elements = Arrays.asList(R,cap,battery);		
//		List<CircuitElm> elements = Arrays.asList(R,battery);
		for(CircuitElm c:elements) {
			c.setPoints();
		}
		
		CirSim cirSim  = new CirSim();				
		cirSim.setElmList(elements);		
		CircuitElm.sim = cirSim;
		cirSim.setTimeStep(0.001);
		
		cirSim.analyzeCircuit();

		//cirSim.runCircuit();
		
		for (int i = 1 ; i < 100; i++) {
			cirSim.loopAndContinue(false);
			System.out.print("R:current" + R.getCurrent());
			System.out.print(" voltage " + R.getVoltageDiff());
			System.out.print("C: current" + cap.getCurrent());
			System.out.println(" voltage " + cap.getVoltageDiff());
		}
		System.out.println("**********");
		battery.setMaxVoltage(5);
		cirSim.analyzeCircuit();
		for (int i = 1 ; i < 100; i++) {
			cirSim.loopAndContinue(false);
			System.out.print("R:current" + R.getCurrent());
			System.out.print(" voltage " + R.getVoltageDiff());
			System.out.print("C: current" + cap.getCurrent());
			System.out.println(" voltage " + cap.getVoltageDiff());
		}
		

	}

}