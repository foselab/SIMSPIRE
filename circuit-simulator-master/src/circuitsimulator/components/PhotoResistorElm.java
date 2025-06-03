package circuitsimulator.components;
// stub PhotoResistorElm based on SparkGapElm

import java.awt.Label;
import java.awt.Point;
import java.awt.Scrollbar;
import java.util.StringTokenizer;

import circuitsimulator.simulator.CirSim;

public class PhotoResistorElm extends CircuitElm {
	double minresistance, maxresistance;
	double resistance;
	Scrollbar slider;
	Label label;

	public PhotoResistorElm(int xx, int yy) {
		super(xx, yy);
		maxresistance = 1e9;
		minresistance = 1e3;
	}

	public PhotoResistorElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		minresistance = new Double(st.nextToken()).doubleValue();
		maxresistance = new Double(st.nextToken()).doubleValue();
	}

	@Override
	public boolean nonLinear() {
		return true;
	}

	@Override
	public int getDumpType() {
		return 186;
	}

	@Override
	public String dump() {
		return super.dump() + " " + minresistance + " " + maxresistance;
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
		double vd = volts[0] - volts[1];
		current = vd / resistance;
	}

	@Override
	public void startIteration() {
		double vd = volts[0] - volts[1];
		// FIXME set resistance as appropriate, using slider.getValue()
		resistance = minresistance;
		// System.out.print(this + " res current set to " + current + "\n");
	}

	@Override
	public void doStep() {
		sim.stampResistor(nodes[0], nodes[1], resistance);
	}

	@Override
	public void stamp() {
		sim.stampNonLinear(nodes[0]);
		sim.stampNonLinear(nodes[1]);
	}

	@Override
	public void getInfo(String arr[]) {
		// FIXME
		arr[0] = "spark gap";
		getBasicInfo(arr);
		arr[3] = "R = " + getUnitText(resistance, CirSim.getOhmString());
		arr[4] = "Ron = " + getUnitText(minresistance, CirSim.getOhmString());
		arr[5] = "Roff = " + getUnitText(maxresistance, CirSim.getOhmString());
	}
}
