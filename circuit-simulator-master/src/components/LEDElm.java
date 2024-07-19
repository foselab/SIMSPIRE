package components;

import java.awt.Point;
import java.util.StringTokenizer;


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
	public int getShortcut() {
		return 'l';
	}
}
