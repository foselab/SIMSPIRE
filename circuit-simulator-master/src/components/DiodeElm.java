package components;

import java.awt.Point;
import java.awt.Polygon;
import java.util.StringTokenizer;

public class DiodeElm extends CircuitElm {
	Diode diode;
	static final int FLAG_FWDROP = 1;
	final double defaultdrop = .805904783;
	double fwdrop, zvoltage;

	public DiodeElm(int xx, int yy) {
		super(xx, yy);
		diode = new Diode(sim);
		fwdrop = defaultdrop;
		zvoltage = 0;
		setup();
	}

	public DiodeElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		diode = new Diode(sim);
		fwdrop = defaultdrop;
		zvoltage = 0;
		if ((f & FLAG_FWDROP) > 0) {
			try {
				fwdrop = new Double(st.nextToken()).doubleValue();
			} catch (Exception e) {
			}
		}
		setup();
	}

	@Override
	public boolean nonLinear() {
		return true;
	}

	void setup() {
		diode.setup(fwdrop, zvoltage);
	}

	@Override
	public int getDumpType() {
		return 'd';
	}

	@Override
	public String dump() {
		flags |= FLAG_FWDROP;
		return super.dump() + " " + fwdrop;
	}

	final int hs = 8;
	Polygon poly;
	Point cathode[];

	@Override
	public void setPoints() {
		super.setPoints();
		calcLeads(16);
		cathode = newPointArray(2);
		Point pa[] = newPointArray(2);
		interpPoint2(lead1, lead2, pa[0], pa[1], 0, hs);
		interpPoint2(lead1, lead2, cathode[0], cathode[1], 1, hs);
	}
	
	@Override
	public void reset() {
		diode.reset();
		volts[0] = volts[1] = curcount = 0;
	}

	@Override
	public void stamp() {
		diode.stamp(nodes[0], nodes[1]);
	}

	@Override
	public void doStep() {
		diode.doStep(volts[0] - volts[1]);
	}

	@Override
	void calculateCurrent() {
		current = diode.calculateCurrent(volts[0] - volts[1]);
	}

	@Override
	public void getInfo(String arr[]) {
		arr[0] = "diode";
		arr[1] = "I = " + getCurrentText(getCurrent());
		arr[2] = "Vd = " + getVoltageText(getVoltageDiff());
		arr[3] = "P = " + getUnitText(getPower(), "W");
		arr[4] = "Vf = " + getVoltageText(fwdrop);
	}

	@Override
	public int getShortcut() {
		return 'd';
	}
}
