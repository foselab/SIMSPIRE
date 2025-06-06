package circuitsimulator.components;

import java.awt.Point;
import java.util.StringTokenizer;

import circuitsimulator.simulator.CirSim;

public class TransLineElm extends CircuitElm {
	double delay, imped;
	double voltageL[], voltageR[];
	int lenSteps, ptr, width;

	public TransLineElm(int xx, int yy) {
		super(xx, yy);
		delay = 1000 * sim.getTimeStep();
		imped = 75;
		noDiagonal = true;
		reset();
	}

	public TransLineElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		delay = new Double(st.nextToken()).doubleValue();
		imped = new Double(st.nextToken()).doubleValue();
		width = new Integer(st.nextToken()).intValue();
		// next slot is for resistance (losses), which is not implemented
		st.nextToken();
		noDiagonal = true;
		reset();
	}

	@Override
	public int getDumpType() {
		return 171;
	}

	@Override
	public int getPostCount() {
		return 4;
	}

	@Override
	public int getInternalNodeCount() {
		return 2;
	}

	@Override
	public String dump() {
		return super.dump() + " " + delay + " " + imped + " " + width + " " + 0.;
	}

	Point posts[], inner[];

	@Override
	public void reset() {
		if (sim.getTimeStep() == 0)
			return;
		lenSteps = (int) (delay / sim.getTimeStep());
		System.out.println(lenSteps + " steps");
		if (lenSteps > 100000)
			voltageL = voltageR = null;
		else {
			voltageL = new double[lenSteps];
			voltageR = new double[lenSteps];
		}
		ptr = 0;
		super.reset();
	}

	@Override
	public void setPoints() {
		super.setPoints();
		int ds = (dy == 0) ? sign(dx) : -sign(dy);
		Point p3 = interpPoint(point1, point2, 0, -width * ds);
		Point p4 = interpPoint(point1, point2, 1, -width * ds);

		// we number the posts like this because we want the lower-numbered
		// points to be on the bottom, so that if some of them are unconnected
		// (which is often true) then the bottom ones will get automatically
		// attached to ground.
		posts = new Point[] { p3, p4, point1, point2 };
	}

	int voltSource1, voltSource2;
	double current1, current2, curCount1, curCount2;

	@Override
	public void setVoltageSource(int n, int v) {
		if (n == 0)
			voltSource1 = v;
		else
			voltSource2 = v;
	}

	@Override
	public void setCurrent(int v, double c) {
		if (v == voltSource1)
			current1 = c;
		else
			current2 = c;
	}

	@Override
	public void stamp() {
		sim.stampVoltageSource(nodes[4], nodes[0], voltSource1);
		sim.stampVoltageSource(nodes[5], nodes[1], voltSource2);
		sim.stampResistor(nodes[2], nodes[4], imped);
		sim.stampResistor(nodes[3], nodes[5], imped);
	}

	@Override
	public void startIteration() {
		// calculate voltages, currents sent over wire
		if (voltageL == null) {
			sim.stop("Transmission line delay too large!", this);
			return;
		}
		voltageL[ptr] = volts[2] - volts[0] + volts[2] - volts[4];
		voltageR[ptr] = volts[3] - volts[1] + volts[3] - volts[5];
		// System.out.println(volts[2] + " " + volts[0] + " " + (volts[2]-volts[0]) + "
		// " + (imped*current1) + " " + voltageL[ptr]);
		/*
		 * System.out.println("sending fwd  " + currentL[ptr] + " " + current1);
		 * System.out.println("sending back " + currentR[ptr] + " " + current2);
		 */
		// System.out.println("sending back " + voltageR[ptr]);
		ptr = (ptr + 1) % lenSteps;
	}

	@Override
	public void doStep() {
		if (voltageL == null) {
			sim.stop("Transmission line delay too large!", this);
			return;
		}
		sim.updateVoltageSource(nodes[4], nodes[0], voltSource1, -voltageR[ptr]);
		sim.updateVoltageSource(nodes[5], nodes[1], voltSource2, -voltageL[ptr]);
		if (Math.abs(volts[0]) > 1e-5 || Math.abs(volts[1]) > 1e-5) {
			sim.stop("Need to ground transmission line!", this);
			return;
		}
	}

	@Override
	public Point getPost(int n) {
		return posts[n];
	}

	// double getVoltageDiff() { return volts[0]; }
	@Override
	public int getVoltageSourceCount() {
		return 2;
	}

	@Override
	public boolean hasGroundConnection(int n1) {
		return false;
	}

	@Override
	public boolean getConnection(int n1, int n2) {
		return false;
		/*
		 * if (comparePair(n1, n2, 0, 1)) return true; if (comparePair(n1, n2, 2, 3))
		 * return true; return false;
		 */
	}

	@Override
	public void getInfo(String arr[]) {
		arr[0] = "transmission line";
		arr[1] = getUnitText(imped, CirSim.getOhmString());
		arr[2] = "length = " + getUnitText(2.9979e8 * delay, "m");
		arr[3] = "delay = " + getUnitText(delay, "s");
	}
}
