package components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.StringTokenizer;

import utils.EditInfo;

public class LEDElm extends DiodeElm {
	double colorR, colorG, colorB;

	public LEDElm(int xx, int yy) {
		super(xx, yy);
		fwdrop = 2.1024259;
		setup();
		colorR = 1;
		colorG = colorB = 0;
	}

	public LEDElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
		if ((f & FLAG_FWDROP) == 0)
			fwdrop = 2.1024259;
		setup();
		colorR = new Double(st.nextToken()).doubleValue();
		colorG = new Double(st.nextToken()).doubleValue();
		colorB = new Double(st.nextToken()).doubleValue();
	}

	@Override
	public int getDumpType() {
		return 162;
	}

	@Override
	public String dump() {
		return super.dump() + " " + colorR + " " + colorG + " " + colorB;
	}

	Point ledLead1, ledLead2, ledCenter;

	@Override
	public void setPoints() {
		super.setPoints();
		int cr = 12;
		ledLead1 = interpPoint(point1, point2, .5 - cr / dn);
		ledLead2 = interpPoint(point1, point2, .5 + cr / dn);
		ledCenter = interpPoint(point1, point2, .5);
	}

	@Override
	public void getInfo(String arr[]) {
		super.getInfo(arr);
		arr[0] = "LED";
	}

	@Override
	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return super.getEditInfo(n);
		if (n == 1)
			return new EditInfo("Red Value (0-1)", colorR, 0, 1).setDimensionless();
		if (n == 2)
			return new EditInfo("Green Value (0-1)", colorG, 0, 1).setDimensionless();
		if (n == 3)
			return new EditInfo("Blue Value (0-1)", colorB, 0, 1).setDimensionless();
		return null;
	}

	@Override
	public void setEditValue(int n, EditInfo ei) {
		if (n == 0)
			super.setEditValue(0, ei);
		if (n == 1)
			colorR = ei.getValue();
		if (n == 2)
			colorG = ei.getValue();
		if (n == 3)
			colorB = ei.getValue();
	}

	@Override
	public int getShortcut() {
		return 'l';
	}
}
