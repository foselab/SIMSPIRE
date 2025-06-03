package circuitsimulator.components;

import java.awt.Point;
import java.util.StringTokenizer;

public class SwitchElm extends CircuitElm {
	private boolean momentary;
	// position 0 == closed, position 1 == open
	int position, posCount;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public SwitchElm(int xx, int yy) {
		super(xx, yy);
		setMomentary(false);
		position = 0;
		posCount = 2;
	}

	public SwitchElm(int xx, int yy, boolean mm) {
		super(xx, yy);
		position = (mm) ? 1 : 0;
		setMomentary(mm);
		posCount = 2;
	}

	public SwitchElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		String str = st.nextToken();
		if (str.compareTo("true") == 0)
			position = (this instanceof LogicInputElm) ? 0 : 1;
		else if (str.compareTo("false") == 0)
			position = (this instanceof LogicInputElm) ? 1 : 0;
		else
			position = new Integer(str).intValue();
		setMomentary(new Boolean(st.nextToken()).booleanValue());
		posCount = 2;
	}

	@Override
	public int getDumpType() {
		return 's';
	}

	@Override
	public String dump() {
		return super.dump() + " " + position + " " + isMomentary();
	}

	Point ps, ps2;

	@Override
	public void setPoints() {
		super.setPoints();
		calcLeads(32);
		ps = new Point();
		ps2 = new Point();
	}

	@Override
	void calculateCurrent() {
		if (position == 1)
			current = 0;
	}

	@Override
	public void stamp() {
		if (position == 0)
			sim.stampVoltageSource(nodes[0], nodes[1], voltSource, 0);
	}

	@Override
	public int getVoltageSourceCount() {
		return (position == 1) ? 0 : 1;
	}

	public void mouseUp() {
		if (isMomentary())
			toggle();
	}

	public void toggle() {
		position++;
		if (position >= posCount)
			position = 0;
	}

	@Override
	public void getInfo(String arr[]) {
		arr[0] = (isMomentary()) ? "push switch (SPST)" : "switch (SPST)";
		if (position == 1) {
			arr[1] = "open";
			arr[2] = "Vd = " + getVoltageDText(getVoltageDiff());
		} else {
			arr[1] = "closed";
			arr[2] = "V = " + getVoltageText(volts[0]);
			arr[3] = "I = " + getCurrentDText(getCurrent());
		}
	}

	@Override
	public boolean getConnection(int n1, int n2) {
		return position == 0;
	}

	@Override
	public boolean isWire() {
		return true;
	}

	@Override
	public int getShortcut() {
		return 's';
	}

	public boolean isMomentary() {
		return momentary;
	}

	public void setMomentary(boolean momentary) {
		this.momentary = momentary;
	}

	@Override
	public void doStep() {
		// TODO Auto-generated method stub
		
	}
}
