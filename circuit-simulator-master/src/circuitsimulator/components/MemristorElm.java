package circuitsimulator.components;

import java.awt.Point;
import java.util.StringTokenizer;

import circuitsimulator.simulator.CirSim;

public class MemristorElm extends CircuitElm {
	double r_on, r_off, dopeWidth, totalWidth, mobility, resistance;

	public MemristorElm(int xx, int yy) {
		super(xx, yy);
		r_on = 100;
		r_off = 160 * r_on;
		dopeWidth = 0;
		totalWidth = 10e-9; // meters
		mobility = 1e-10; // m^2/sV
		resistance = 100;
	}

	public MemristorElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		r_on = new Double(st.nextToken()).doubleValue();
		r_off = new Double(st.nextToken()).doubleValue();
		dopeWidth = new Double(st.nextToken()).doubleValue();
		totalWidth = new Double(st.nextToken()).doubleValue();
		mobility = new Double(st.nextToken()).doubleValue();
		resistance = 100;
	}

	@Override
	public int getDumpType() {
		return 'm';
	}

	@Override
	public String dump() {
		return super.dump() + " " + r_on + " " + r_off + " " + dopeWidth + " " + totalWidth + " " + mobility;
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
	public boolean nonLinear() {
		return true;
	}

	@Override
	void calculateCurrent() {
		current = (volts[0] - volts[1]) / resistance;
	}

	@Override
	public void reset() {
		dopeWidth = 0;
	}

	@Override
	public void startIteration() {
		double wd = dopeWidth / totalWidth;
		dopeWidth += sim.getTimeStep() * mobility * r_on * current / totalWidth;
		if (dopeWidth < 0)
			dopeWidth = 0;
		if (dopeWidth > totalWidth)
			dopeWidth = totalWidth;
		resistance = r_on * wd + r_off * (1 - wd);
	}

	@Override
	public void stamp() {
		sim.stampNonLinear(nodes[0]);
		sim.stampNonLinear(nodes[1]);
	}

	@Override
	public void doStep() {
		sim.stampResistor(nodes[0], nodes[1], resistance);
	}

	@Override
	public void getInfo(String arr[]) {
		arr[0] = "memristor";
		getBasicInfo(arr);
		arr[3] = "R = " + getUnitText(resistance, CirSim.getOhmString());
		arr[4] = "P = " + getUnitText(getPower(), "W");
	}

	@Override
	public double getScopeValue(int x) {
		return (x == 2) ? resistance : (x == 1) ? getPower() : getVoltageDiff();
	}

	@Override
	public String getScopeUnits(int x) {
		return (x == 2) ? CirSim.getOhmString() : (x == 1) ? "W" : "V";
	}
}
