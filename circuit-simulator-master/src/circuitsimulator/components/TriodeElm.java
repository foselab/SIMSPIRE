package circuitsimulator.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.StringTokenizer;

public class TriodeElm extends CircuitElm {
	double mu, kg1;
	double curcountp, curcountc, curcountg, currentp, currentg, currentc;
	final double gridCurrentR = 6000;

	public TriodeElm(int xx, int yy) {
		super(xx, yy);
		mu = 93;
		kg1 = 680;
		setup();
	}

	public TriodeElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		mu = new Double(st.nextToken()).doubleValue();
		kg1 = new Double(st.nextToken()).doubleValue();
		setup();
	}

	void setup() {
		noDiagonal = true;
	}

	@Override
	public boolean nonLinear() {
		return true;
	}

	@Override
	public void reset() {
		volts[0] = volts[1] = volts[2] = 0;
		curcount = 0;
	}

	@Override
	public String dump() {
		return super.dump() + " " + mu + " " + kg1;
	}

	@Override
	public int getDumpType() {
		return 173;
	}

	Point plate[], grid[], cath[], midgrid, midcath;
	int circler;

	@Override
	public void setPoints() {
		super.setPoints();
		plate = newPointArray(4);
		grid = newPointArray(8);
		cath = newPointArray(4);
		grid[0] = point1;
		int nearw = 8;
		interpPoint(point1, point2, plate[1], 1, nearw);
		int farw = 32;
		interpPoint(point1, point2, plate[0], 1, farw);
		int platew = 18;
		interpPoint2(point2, plate[1], plate[2], plate[3], 1, platew);

		circler = 24;
		interpPoint(point1, point2, grid[1], (dn - circler) / dn, 0);
		int i;
		for (i = 0; i != 3; i++) {
			interpPoint(grid[1], point2, grid[2 + i * 2], (i * 3 + 1) / 4.5, 0);
			interpPoint(grid[1], point2, grid[3 + i * 2], (i * 3 + 2) / 4.5, 0);
		}
		midgrid = point2;

		int cathw = 16;
		midcath = interpPoint(point1, point2, 1, -nearw);
		interpPoint2(point2, plate[1], cath[1], cath[2], -1, cathw);
		interpPoint(point2, plate[1], cath[3], -1.2, -cathw);
		interpPoint(point2, plate[1], cath[0], -farw / (double) nearw, cathw);
	}

	@Override
	public Point getPost(int n) {
		return (n == 0) ? plate[0] : (n == 1) ? grid[0] : cath[0];
	}

	@Override
	public int getPostCount() {
		return 3;
	}

	@Override
	double getPower() {
		return (volts[0] - volts[2]) * current;
	}

	double lastv0, lastv1, lastv2;

	@Override
	public void doStep() {
		double vs[] = new double[3];
		vs[0] = volts[0];
		vs[1] = volts[1];
		vs[2] = volts[2];
		if (vs[1] > lastv1 + .5)
			vs[1] = lastv1 + .5;
		if (vs[1] < lastv1 - .5)
			vs[1] = lastv1 - .5;
		if (vs[2] > lastv2 + .5)
			vs[2] = lastv2 + .5;
		if (vs[2] < lastv2 - .5)
			vs[2] = lastv2 - .5;
		int grid = 1;
		int cath = 2;
		int plate = 0;
		double vgk = vs[grid] - vs[cath];
		double vpk = vs[plate] - vs[cath];
		if (Math.abs(lastv0 - vs[0]) > .01 || Math.abs(lastv1 - vs[1]) > .01 || Math.abs(lastv2 - vs[2]) > .01)
			sim.setConverged(false);
		lastv0 = vs[0];
		lastv1 = vs[1];
		lastv2 = vs[2];
		double ids = 0;
		double gm = 0;
		double Gds = 0;
		double ival = vgk + vpk / mu;
		currentg = 0;
		if (vgk > .01) {
			sim.stampResistor(nodes[grid], nodes[cath], gridCurrentR);
			currentg = vgk / gridCurrentR;
		}
		if (ival < 0) {
			// should be all zero, but that causes a singular matrix,
			// so instead we treat it as a large resistor
			Gds = 1e-8;
			ids = vpk * Gds;
		} else {
			ids = Math.pow(ival, 1.5) / kg1;
			double q = 1.5 * Math.sqrt(ival) / kg1;
			// gm = dids/dgk;
			// Gds = dids/dpk;
			Gds = q;
			gm = q / mu;
		}
		currentp = ids;
		currentc = ids + currentg;
		double rs = -ids + Gds * vpk + gm * vgk;
		sim.stampMatrix(nodes[plate], nodes[plate], Gds);
		sim.stampMatrix(nodes[plate], nodes[cath], -Gds - gm);
		sim.stampMatrix(nodes[plate], nodes[grid], gm);

		sim.stampMatrix(nodes[cath], nodes[plate], -Gds);
		sim.stampMatrix(nodes[cath], nodes[cath], Gds + gm);
		sim.stampMatrix(nodes[cath], nodes[grid], -gm);

		sim.stampRightSide(nodes[plate], rs);
		sim.stampRightSide(nodes[cath], -rs);
	}

	@Override
	public void stamp() {
		sim.stampNonLinear(nodes[0]);
		sim.stampNonLinear(nodes[1]);
		sim.stampNonLinear(nodes[2]);
	}

	@Override
	public void getInfo(String arr[]) {
		arr[0] = "triode";
		double vbc = volts[0] - volts[1];
		double vbe = volts[0] - volts[2];
		double vce = volts[1] - volts[2];
		arr[1] = "Vbe = " + getVoltageText(vbe);
		arr[2] = "Vbc = " + getVoltageText(vbc);
		arr[3] = "Vce = " + getVoltageText(vce);
	}

	// grid not connected to other terminals
	@Override
	public boolean getConnection(int n1, int n2) {
		return !(n1 == 1 || n2 == 1);
	}
}
