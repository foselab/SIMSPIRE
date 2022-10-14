package components;

import java.awt.Checkbox;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.StringTokenizer;

import utils.EditInfo;

public class ProbeElm extends CircuitElm {
	static final int FLAG_SHOWVOLTAGE = 1;

	public ProbeElm(int xx, int yy) {
		super(xx, yy);
	}

	public ProbeElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
	}

	@Override
	public int getDumpType() {
		return 'p';
	}

	Point center;

	@Override
	public void setPoints() {
		super.setPoints();
		// swap points so that we subtract higher from lower
		if (point2.y < point1.y) {
			Point x = point1;
			point1 = point2;
			point2 = x;
		}
		center = interpPoint(point1, point2, .5);
	}

	boolean mustShowVoltage() {
		return (flags & FLAG_SHOWVOLTAGE) != 0;
	}

	@Override
	public void getInfo(String arr[]) {
		arr[0] = "scope probe";
		arr[1] = "Vd = " + getVoltageText(getVoltageDiff());
	}

	@Override
	public boolean getConnection(int n1, int n2) {
		return false;
	}

	@Override
	public EditInfo getEditInfo(int n) {
		if (n == 0) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Show Voltage", mustShowVoltage());
			return ei;
		}
		return null;
	}

	@Override
	public void setEditValue(int n, EditInfo ei) {
		if (n == 0) {
			if (ei.checkbox.getState())
				flags = FLAG_SHOWVOLTAGE;
			else
				flags &= ~FLAG_SHOWVOLTAGE;
		}
	}

	@Override
	public void doStep() {
		// TODO Auto-generated method stub
		
	}
}
