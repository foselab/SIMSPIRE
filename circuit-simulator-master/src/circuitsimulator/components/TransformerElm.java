package circuitsimulator.components;

import java.awt.Checkbox;
import java.awt.Graphics;
import java.awt.Point;
import java.util.StringTokenizer;

public class TransformerElm extends CircuitElm {
	double inductance, ratio, couplingCoef;
	Point ptEnds[], ptCoil[], ptCore[];
	double current[], curcount[];
	int width;
	public static final int FLAG_BACK_EULER = 2;

	public TransformerElm(int xx, int yy) {
		super(xx, yy);
		inductance = 4;
		ratio = 1;
		width = 32;
		noDiagonal = true;
		couplingCoef = .999;
		current = new double[2];
		curcount = new double[2];
	}

	public TransformerElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		width = max(32, abs(yb - ya));
		inductance = new Double(st.nextToken()).doubleValue();
		ratio = new Double(st.nextToken()).doubleValue();
		current = new double[2];
		curcount = new double[2];
		current[0] = new Double(st.nextToken()).doubleValue();
		current[1] = new Double(st.nextToken()).doubleValue();
		couplingCoef = .999;
		try {
			couplingCoef = new Double(st.nextToken()).doubleValue();
		} catch (Exception e) {
		}
		noDiagonal = true;
	}

	@Override
	public int getDumpType() {
		return 'T';
	}

	@Override
	public String dump() {
		return super.dump() + " " + inductance + " " + ratio + " " + current[0] + " " + current[1] + " " + couplingCoef;
	}

	boolean isTrapezoidal() {
		return (flags & FLAG_BACK_EULER) == 0;
	}

	@Override
	public void setPoints() {
		super.setPoints();
		point2.y = point1.y;
		ptEnds = newPointArray(4);
		ptCoil = newPointArray(4);
		ptCore = newPointArray(4);
		ptEnds[0] = point1;
		ptEnds[1] = point2;
		interpPoint(point1, point2, ptEnds[2], 0, -dsign * width);
		interpPoint(point1, point2, ptEnds[3], 1, -dsign * width);
		double ce = .5 - 12 / dn;
		double cd = .5 - 2 / dn;
		int i;
		for (i = 0; i != 4; i += 2) {
			interpPoint(ptEnds[i], ptEnds[i + 1], ptCoil[i], ce);
			interpPoint(ptEnds[i], ptEnds[i + 1], ptCoil[i + 1], 1 - ce);
			interpPoint(ptEnds[i], ptEnds[i + 1], ptCore[i], cd);
			interpPoint(ptEnds[i], ptEnds[i + 1], ptCore[i + 1], 1 - cd);
		}
	}

	@Override
	public Point getPost(int n) {
		return ptEnds[n];
	}

	@Override
	public int getPostCount() {
		return 4;
	}

	@Override
	public void reset() {
		current[0] = current[1] = volts[0] = volts[1] = volts[2] = volts[3] = curcount[0] = curcount[1] = 0;
	}

	double a1, a2, a3, a4;

	@Override
	public void stamp() {
		// equations for transformer:
		// v1 = L1 di1/dt + M di2/dt
		// v2 = M di1/dt + L2 di2/dt
		// we invert that to get:
		// di1/dt = a1 v1 + a2 v2
		// di2/dt = a3 v1 + a4 v2
		// integrate di1/dt using trapezoidal approx and we get:
		// i1(t2) = i1(t1) + dt/2 (i1(t1) + i1(t2))
		// = i1(t1) + a1 dt/2 v1(t1) + a2 dt/2 v2(t1) +
		// a1 dt/2 v1(t2) + a2 dt/2 v2(t2)
		// the norton equivalent of this for i1 is:
		// a. current source, I = i1(t1) + a1 dt/2 v1(t1) + a2 dt/2 v2(t1)
		// b. resistor, G = a1 dt/2
		// c. current source controlled by voltage v2, G = a2 dt/2
		// and for i2:
		// a. current source, I = i2(t1) + a3 dt/2 v1(t1) + a4 dt/2 v2(t1)
		// b. resistor, G = a3 dt/2
		// c. current source controlled by voltage v2, G = a4 dt/2
		//
		// For backward euler,
		//
		// i1(t2) = i1(t1) + a1 dt v1(t2) + a2 dt v2(t2)
		//
		// So the current source value is just i1(t1) and we use
		// dt instead of dt/2 for the resistor and VCCS.
		//
		// first winding goes from node 0 to 2, second is from 1 to 3
		double l1 = inductance;
		double l2 = inductance * ratio * ratio;
		double m = couplingCoef * Math.sqrt(l1 * l2);
		// build inverted matrix
		double deti = 1 / (l1 * l2 - m * m);
		double ts = isTrapezoidal() ? sim.getTimeStep() / 2 : sim.getTimeStep();
		a1 = l2 * deti * ts; // we multiply dt/2 into a1..a4 here
		a2 = -m * deti * ts;
		a3 = -m * deti * ts;
		a4 = l1 * deti * ts;
		sim.stampConductance(nodes[0], nodes[2], a1);
		sim.stampVCCurrentSource(nodes[0], nodes[2], nodes[1], nodes[3], a2);
		sim.stampVCCurrentSource(nodes[1], nodes[3], nodes[0], nodes[2], a3);
		sim.stampConductance(nodes[1], nodes[3], a4);
		sim.stampRightSide(nodes[0]);
		sim.stampRightSide(nodes[1]);
		sim.stampRightSide(nodes[2]);
		sim.stampRightSide(nodes[3]);
	}

	@Override
	public void startIteration() {
		double voltdiff1 = volts[0] - volts[2];
		double voltdiff2 = volts[1] - volts[3];
		if (isTrapezoidal()) {
			curSourceValue1 = voltdiff1 * a1 + voltdiff2 * a2 + current[0];
			curSourceValue2 = voltdiff1 * a3 + voltdiff2 * a4 + current[1];
		} else {
			curSourceValue1 = current[0];
			curSourceValue2 = current[1];
		}
	}

	double curSourceValue1, curSourceValue2;

	@Override
	public void doStep() {
		sim.stampCurrentSource(nodes[0], nodes[2], curSourceValue1);
		sim.stampCurrentSource(nodes[1], nodes[3], curSourceValue2);
	}

	@Override
	void calculateCurrent() {
		double voltdiff1 = volts[0] - volts[2];
		double voltdiff2 = volts[1] - volts[3];
		current[0] = voltdiff1 * a1 + voltdiff2 * a2 + curSourceValue1;
		current[1] = voltdiff1 * a3 + voltdiff2 * a4 + curSourceValue2;
	}

	@Override
	public void getInfo(String arr[]) {
		arr[0] = "transformer";
		arr[1] = "L = " + getUnitText(inductance, "H");
		arr[2] = "Ratio = 1:" + ratio;
		arr[3] = "Vd1 = " + getVoltageText(volts[0] - volts[2]);
		arr[4] = "Vd2 = " + getVoltageText(volts[1] - volts[3]);
		arr[5] = "I1 = " + getCurrentText(current[0]);
		arr[6] = "I2 = " + getCurrentText(current[1]);
	}

	@Override
	public boolean getConnection(int n1, int n2) {
		if (comparePair(n1, n2, 0, 2))
			return true;
		if (comparePair(n1, n2, 1, 3))
			return true;
		return false;
	}
}
