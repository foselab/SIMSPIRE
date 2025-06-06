package circuitsimulator.components;

import java.util.StringTokenizer;

public class WireElm extends ResistorElm {
	public static boolean ideal = false;
	private static final double defaultResistance = 1E-06;

	public WireElm(int xx, int yy) {
		super(xx, yy);
		setResistance(defaultResistance);
	}

	public WireElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, defaultResistance);
	}

	static final int FLAG_SHOWCURRENT = 1;
	static final int FLAG_SHOWVOLTAGE = 2;

	@Override
	void calculateCurrent() {
		if (!ideal) {
			super.calculateCurrent();
		}
	}

	@Override
	public void stamp() {
		if (ideal) {
			sim.stampVoltageSource(nodes[0], nodes[1], voltSource, 0);
		} else {
			sim.stampResistor(nodes[0], nodes[1], getResistance());
		}
	}

	boolean mustShowCurrent() {
		return (flags & FLAG_SHOWCURRENT) != 0;
	}

	boolean mustShowVoltage() {
		return (flags & FLAG_SHOWVOLTAGE) != 0;
	}

	@Override
	public int getVoltageSourceCount() {
		if (ideal) {
			return 1;
		} else {
			return super.getVoltageSourceCount();
		}
	}

	@Override
	public void getInfo(String arr[]) {
		arr[0] = "wire";
		arr[1] = "I = " + getCurrentDText(getCurrent());
		arr[2] = "V = " + getVoltageText(volts[0]);
	}

	@Override
	double getPower() {
		if (ideal) {
			return 0;
		} else {
			return super.getPower();
		}
	}

	@Override
	public double getVoltageDiff() {
		if (ideal) {
			return volts[0];
		} else {
			return super.getVoltageDiff();
		}
	}

	@Override
	public boolean isWire() {
		return ideal;
	}

	@Override
	public int getShortcut() {
		return 'w';
	}

	@Override
	public int getDumpType() {
		return 'w';
	}

	@Override
	public String dump() {
		int t = getDumpType();
		return (t < 127 ? ((char) t) + " " : t + " ") + getX() + " " + getY() + " " + getX2() + " " + getY2() + " " + flags;
	}
}
