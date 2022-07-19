package components;

import java.awt.Checkbox;
import java.awt.Graphics;
import java.util.StringTokenizer;

import utils.EditInfo;

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
	public void draw(Graphics g) {
		setVoltageColor(g, volts[0]);
		drawThickLine(g, point1, point2);
		doDots(g);
		setBbox(point1, point2, 3);
		if (mustShowCurrent()) {
			String s = getShortUnitText(Math.abs(getCurrent()), "A");
			drawValues(g, s, 4);
		} else if (mustShowVoltage()) {
			String s = getShortUnitText(volts[0], "V");
			drawValues(g, s, 4);
		}
		drawPosts(g);
	}

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
	public EditInfo getEditInfo(int n) {
		if (n == 0) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.setCheckbox(new Checkbox("Show Current", mustShowCurrent()));
			return ei;
		}
		if (n == 1) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.setCheckbox(new Checkbox("Show Voltage", mustShowVoltage()));
			return ei;
		}
		return null;
	}

	@Override
	public void setEditValue(int n, EditInfo ei) {
		if (n == 0) {
			if (ei.getCheckbox().getState())
				flags = FLAG_SHOWCURRENT;
			else
				flags &= ~FLAG_SHOWCURRENT;
		}
		if (n == 1) {
			if (ei.getCheckbox().getState())
				flags = FLAG_SHOWVOLTAGE;
			else
				flags &= ~FLAG_SHOWVOLTAGE;
		}
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
