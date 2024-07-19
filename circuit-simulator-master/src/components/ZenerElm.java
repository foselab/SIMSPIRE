package components;

import java.awt.Point;
import java.util.StringTokenizer;

// Zener code contributed by J. Mike Rollins
// http://www.camotruck.net/rollins/simulator.html
public class ZenerElm extends DiodeElm {
	public ZenerElm(int xx, int yy) {
		super(xx, yy);
		zvoltage = default_zvoltage;
		setup();
	}

	public ZenerElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
		zvoltage = new Double(st.nextToken()).doubleValue();
		setup();
	}

	@Override
	void setup() {
		diode.leakage = 5e-6; // 1N4004 is 5.0 uAmp
		super.setup();
	}

	@Override
	public int getDumpType() {
		return 'z';
	}

	@Override
	public String dump() {
		return super.dump() + " " + zvoltage;
	}

	final int hs = 8;
	Point cathode[];
	Point wing[];

	@Override
	public void setPoints() {
		super.setPoints();
		calcLeads(16);
		cathode = newPointArray(2);
		wing = newPointArray(2);
		Point pa[] = newPointArray(2);
		interpPoint2(lead1, lead2, pa[0], pa[1], 0, hs);
		interpPoint2(lead1, lead2, cathode[0], cathode[1], 1, hs);
		interpPoint(cathode[0], cathode[1], wing[0], -0.2, -hs);
		interpPoint(cathode[1], cathode[0], wing[1], -0.2, -hs);
	}

	final double default_zvoltage = 5.6;

	@Override
	public void getInfo(String arr[]) {
		super.getInfo(arr);
		arr[0] = "Zener diode";
		arr[5] = "Vz = " + getVoltageText(zvoltage);
	}

	@Override
	public int getShortcut() {
		return 0;
	}
}
