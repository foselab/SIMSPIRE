package components;
// stub implementation of DiacElm, based on SparkGapElm

import java.awt.Point;
import java.util.StringTokenizer;

import simulator.CirSim;

public class DiacElm extends CircuitElm {
	double onresistance, offresistance, breakdown, holdcurrent;
	boolean state;

	public DiacElm(int xx, int yy) {
		super(xx, yy);
		// FIXME need to adjust defaults to make sense for diac
		offresistance = 1e9;
		onresistance = 1e3;
		breakdown = 1e3;
		holdcurrent = 0.001;
		state = false;
	}

	public DiacElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		onresistance = new Double(st.nextToken()).doubleValue();
		offresistance = new Double(st.nextToken()).doubleValue();
		breakdown = new Double(st.nextToken()).doubleValue();
		holdcurrent = new Double(st.nextToken()).doubleValue();
	}

	@Override
	public boolean nonLinear() {
		return true;
	}

	@Override
	public int getDumpType() {
		return 185;
	}

	@Override
	public String dump() {
		return super.dump() + " " + onresistance + " " + offresistance + " " + breakdown + " " + holdcurrent;
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
		if (state)
			current = vd / onresistance;
		else
			current = vd / offresistance;
	}

	@Override
	public void startIteration() {
		double vd = volts[0] - volts[1];
		if (Math.abs(current) < holdcurrent)
			state = false;
		if (Math.abs(vd) > breakdown)
			state = true;
		// System.out.print(this + " res current set to " + current + "\n");
	}

	@Override
	public void doStep() {
		if (state)
			sim.stampResistor(nodes[0], nodes[1], onresistance);
		else
			sim.stampResistor(nodes[0], nodes[1], offresistance);
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
		arr[3] = state ? "on" : "off";
		arr[4] = "Ron = " + getUnitText(onresistance, CirSim.getOhmString());
		arr[5] = "Roff = " + getUnitText(offresistance, CirSim.getOhmString());
		arr[6] = "Vbrkdn = " + getUnitText(breakdown, "V");
		arr[7] = "Ihold = " + getUnitText(holdcurrent, "A");
	}
}
