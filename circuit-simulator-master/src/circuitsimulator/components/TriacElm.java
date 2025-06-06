package circuitsimulator.components;
// stub implementation of TriacElm, based on SCRElm

import java.awt.Point;
import java.awt.Polygon;
import java.util.StringTokenizer;

// Silicon-Controlled Rectifier
// 3 nodes, 1 internal node
// 0 = anode, 1 = cathode, 2 = gate
// 0, 3 = variable resistor
// 3, 2 = diode
// 2, 1 = 50 ohm resistor

public class TriacElm extends CircuitElm {
	final int anode = 0;
	final int cnode = 1;
	final int gnode = 2;
	final int inode = 3;
	Diode diode;

	public TriacElm(int xx, int yy) {
		super(xx, yy);
		setDefaults();
		setup();
	}

	public TriacElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		setDefaults();
		try {
			lastvac = new Double(st.nextToken()).doubleValue();
			lastvag = new Double(st.nextToken()).doubleValue();
			volts[anode] = 0;
			volts[cnode] = -lastvac;
			volts[gnode] = -lastvag;
			triggerI = new Double(st.nextToken()).doubleValue();
			holdingI = new Double(st.nextToken()).doubleValue();
			cresistance = new Double(st.nextToken()).doubleValue();
		} catch (Exception e) {
		}
		setup();
	}

	void setDefaults() {
		cresistance = 50;
		holdingI = .0082;
		triggerI = .01;
	}

	void setup() {
		diode = new Diode(sim);
		diode.setup(.8, 0);
	}

	@Override
	public boolean nonLinear() {
		return true;
	}

	@Override
	public void reset() {
		volts[anode] = volts[cnode] = volts[gnode] = 0;
		diode.reset();
		lastvag = lastvac = curcount_a = curcount_c = curcount_g = 0;
	}

	@Override
	public int getDumpType() {
		return 183;
	}

	@Override
	public String dump() {
		return super.dump() + " " + (volts[anode] - volts[cnode]) + " " + (volts[anode] - volts[gnode]) + " " + triggerI
				+ " " + holdingI + " " + cresistance;
	}

	double ia, ic, ig, curcount_a, curcount_c, curcount_g;
	double lastvac, lastvag;
	double cresistance, triggerI, holdingI;

	final int hs = 8;
	Polygon poly;
	Point cathode[], gate[];

	@Override
	public void setPoints() {
		super.setPoints();
		int dir = 0;
		if (abs(dx) > abs(dy)) {
			dir = -sign(dx) * sign(dy);
			point2.y = point1.y;
		} else {
			dir = sign(dy) * sign(dx);
			point2.x = point1.x;
		}
		if (dir == 0)
			dir = 1;
		calcLeads(16);
		cathode = newPointArray(2);
		Point pa[] = newPointArray(2);
		interpPoint2(lead1, lead2, pa[0], pa[1], 0, hs);
		interpPoint2(lead1, lead2, cathode[0], cathode[1], 1, hs);

		gate = newPointArray(2);
	}

	@Override
	public Point getPost(int n) {
		return (n == 0) ? point1 : (n == 1) ? point2 : gate[1];
	}

	@Override
	public int getPostCount() {
		return 3;
	}

	@Override
	public int getInternalNodeCount() {
		return 1;
	}

	@Override
	double getPower() {
		return (volts[anode] - volts[gnode]) * ia + (volts[cnode] - volts[gnode]) * ic;
	}

	double aresistance;

	@Override
	public void stamp() {
		sim.stampNonLinear(nodes[anode]);
		sim.stampNonLinear(nodes[cnode]);
		sim.stampNonLinear(nodes[gnode]);
		sim.stampNonLinear(nodes[inode]);
		sim.stampResistor(nodes[gnode], nodes[cnode], cresistance);
		diode.stamp(nodes[inode], nodes[gnode]);
	}

	@Override
	public void doStep() {
		double vac = volts[anode] - volts[cnode]; // typically negative
		double vag = volts[anode] - volts[gnode]; // typically positive
		if (Math.abs(vac - lastvac) > .01 || Math.abs(vag - lastvag) > .01)
			sim.setConverged(false);
		lastvac = vac;
		lastvag = vag;
		diode.doStep(volts[inode] - volts[gnode]);
		double icmult = 1 / triggerI;
		double iamult = 1 / holdingI - icmult;
		// System.out.println(icmult + " " + iamult);
		aresistance = (-icmult * ic + ia * iamult > 1) ? .0105 : 10e5;
		// System.out.println(vac + " " + vag + " " + sim.converged + " " + ic + " " +
		// ia + " " + aresistance + " " + volts[inode] + " " + volts[gnode] + " " +
		// volts[anode]);
		sim.stampResistor(nodes[anode], nodes[inode], aresistance);
	}

	@Override
	public void getInfo(String arr[]) {
		arr[0] = "SCR";
		double vac = volts[anode] - volts[cnode];
		double vag = volts[anode] - volts[gnode];
		double vgc = volts[gnode] - volts[cnode];
		arr[1] = "Ia = " + getCurrentText(ia);
		arr[2] = "Ig = " + getCurrentText(ig);
		arr[3] = "Vac = " + getVoltageText(vac);
		arr[4] = "Vag = " + getVoltageText(vag);
		arr[5] = "Vgc = " + getVoltageText(vgc);
	}

	@Override
	void calculateCurrent() {
		ic = (volts[cnode] - volts[gnode]) / cresistance;
		ia = (volts[anode] - volts[inode]) / aresistance;
		ig = -ic - ia;
	}
}
