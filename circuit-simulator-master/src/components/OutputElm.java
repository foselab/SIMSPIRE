package components;

import java.awt.Checkbox;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.util.StringTokenizer;

import utils.EditInfo;

public class OutputElm extends CircuitElm {
	final int FLAG_VALUE = 1;

	public OutputElm(int xx, int yy) {
		super(xx, yy);
	}

	public OutputElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
	}

	@Override
	public int getDumpType() {
		return 'O';
	}

	@Override
	public int getPostCount() {
		return 1;
	}

	@Override
	public void setPoints() {
		super.setPoints();
		lead1 = new Point();
	}

	@Override
	public double getVoltageDiff() {
		return volts[0];
	}

	@Override
	public void getInfo(String arr[]) {
		arr[0] = "output";
		arr[1] = "V = " + getVoltageText(volts[0]);
	}

	@Override
	public EditInfo getEditInfo(int n) {
		if (n == 0) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Show Voltage", (flags & FLAG_VALUE) != 0);
			return ei;
		}
		return null;
	}

	@Override
	public void setEditValue(int n, EditInfo ei) {
		if (n == 0)
			flags = (ei.checkbox.getState()) ? (flags | FLAG_VALUE) : (flags & ~FLAG_VALUE);
	}

	@Override
	public void doStep() {
		// TODO Auto-generated method stub
		
	}
}
