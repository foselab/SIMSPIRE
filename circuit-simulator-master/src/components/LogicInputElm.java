package components;

import java.awt.Polygon;
import java.util.StringTokenizer;

public class LogicInputElm extends SwitchElm {
	final int FLAG_TERNARY = 1;
	final int FLAG_NUMERIC = 2;
	double hiV, loV;
	Polygon arrowPoly;

	public LogicInputElm(int xx, int yy) {
		super(xx, yy, false);
		hiV = 5;
		loV = 0;
		flags = 2; // hausen: default to numeric
	}

	public LogicInputElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
		try {
			hiV = new Double(st.nextToken()).doubleValue();
			loV = new Double(st.nextToken()).doubleValue();
		} catch (Exception e) {
			hiV = 5;
			loV = 0;
		}
		if (isTernary())
			posCount = 3;
	}

	boolean isTernary() {
		return (flags & FLAG_TERNARY) != 0;
	}

	boolean isNumeric() {
		return (flags & (FLAG_TERNARY | FLAG_NUMERIC)) != 0;
	}

	@Override
	public int getDumpType() {
		return 'L';
	}

	@Override
	public String dump() {
		return super.dump() + " " + hiV + " " + loV;
	}

	@Override
	public int getPostCount() {
		return 1;
	}

	@Override
	public void setPoints() {
		super.setPoints();
		lead1 = interpPoint(point1, point2, 1 - 12 / dn);
		arrowPoly = calcArrowReverse(point1, lead1, 8, 8);
	}

	@Override
	public void setCurrent(int vs, double c) {
		current = -c;
	}

	@Override
	public void stamp() {
		double v = (position == 0) ? loV : hiV;
		if (isTernary())
			v = position * 2.5;
		sim.stampVoltageSource(0, nodes[0], voltSource, v);
	}

	@Override
	public int getVoltageSourceCount() {
		return 1;
	}

	@Override
	public double getVoltageDiff() {
		return volts[0];
	}

	@Override
	public void getInfo(String arr[]) {
		arr[0] = "logic input";
		arr[1] = (position == 0) ? "low" : "high";
		if (isNumeric())
			arr[1] = "" + position;
		arr[1] += " (" + getVoltageText(volts[0]) + ")";
		arr[2] = "I = " + getCurrentText(getCurrent());
	}

	@Override
	public boolean hasGroundConnection(int n1) {
		return true;
	}

	@Override
	public int getShortcut() {
		return 'i';
	}
}
