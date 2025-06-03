package circuitsimulator.components;

import java.awt.Point;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import circuitsimulator.simulator.CirSim;
public class ResistorElm extends CircuitElm {
	
	static private Logger LOGGER = Logger.getLogger(ResistorElm.class.getName());
	
	private double resistance;

	public ResistorElm(int xx, int yy) {
		super(xx, yy); // build a Rectangle xx*yy
		setResistance(100);
	}

	public ResistorElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		this(xa, ya, xb, yb, f, new Double(st.nextToken()).doubleValue());
	}

	public ResistorElm(int xa, int ya, int xb, int yb, int f, double resistance) {
		super(xa, ya, xb, yb, f);
		assert resistance != 0;
		setResistance(resistance);
	}

	@Override
	public int getDumpType() {
		return 'r';
	}

	@Override
	public String dump() {
		return super.dump() + " " + getResistance();
	}

	Point ps3, ps4;

	@Override
	public void setPoints() {
		super.setPoints();
		calcLeads(32);
		ps3 = new Point();
		ps4 = new Point();
	}

	@Override
	void calculateCurrent() {
		LOGGER.log(Level.FINE,this.getClass().getSimpleName() + " - volts[0] = " + volts[0] + ", volts[1] = " + volts[1]);
		current = (volts[0] - volts[1]) / getResistance();
		LOGGER.log(Level.FINE,this.getClass().getSimpleName() + " - current set to " + current);
	}

	@Override
	public void stamp() {
		sim.stampResistor(nodes[0], nodes[1], getResistance());
	}

	@Override
	public void getInfo(String arr[]) {
		arr[0] = "resistor";
		getBasicInfo(arr);
		arr[3] = "R = " + getUnitText(getResistance(), CirSim.getOhmString());
		arr[4] = "P = " + getUnitText(getPower(), "W");
	}

	@Override
	public int getShortcut() {
		return 'r';
	}

	public double getResistance() {
		return resistance;
	}

	public void setResistance(double resistance) {
		this.resistance = resistance;
	}

	@Override
	public void doStep() {
		// TODO Auto-generated method stub

	}
}
