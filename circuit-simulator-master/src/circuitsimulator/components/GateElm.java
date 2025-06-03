package circuitsimulator.components;

import java.awt.Point;
import java.util.StringTokenizer;

public abstract class GateElm extends CircuitElm {
	final int FLAG_SMALL = 1;
	int inputCount = 2;
	boolean lastOutput;

	public GateElm(int xx, int yy) {
		super(xx, yy);
		noDiagonal = true;
		inputCount = 2;
	}

	public GateElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		inputCount = new Integer(st.nextToken()).intValue();
		lastOutput = new Double(st.nextToken()).doubleValue() > 2.5;
		noDiagonal = true;
		setSize((f & FLAG_SMALL) != 0 ? 1 : 2);
	}

	boolean isInverting() {
		return false;
	}

	int gsize, gwidth, gwidth2, gheight, hs2;

	void setSize(int s) {
		gsize = s;
		gwidth = 7 * s;
		gwidth2 = 14 * s;
		gheight = 8 * s;
		flags = (s == 1) ? FLAG_SMALL : 0;
	}

	@Override
	public String dump() {
		return super.dump() + " " + inputCount + " " + volts[inputCount];
	}

	Point inPosts[], inGates[];
	int ww;

	@Override
	public void setPoints() {
		super.setPoints();
		int hs = gheight;
		int i;
		ww = gwidth2; // was 24
		if (ww > dn / 2)
			ww = (int) (dn / 2);
		if (isInverting() && ww + 8 > dn / 2)
			ww = (int) (dn / 2 - 8);
		calcLeads(ww * 2);
		inPosts = new Point[inputCount];
		inGates = new Point[inputCount];
		allocNodes();
		int i0 = -inputCount / 2;
		for (i = 0; i != inputCount; i++, i0++) {
			if (i0 == 0 && (inputCount & 1) == 0)
				i0++;
			inPosts[i] = interpPoint(point1, point2, 0, hs * i0);
			inGates[i] = interpPoint(lead1, lead2, 0, hs * i0);
			volts[i] = (lastOutput ^ isInverting()) ? 5 : 0;
		}
		hs2 = gwidth * (inputCount / 2 + 1);
	}
	
	Point pcircle, linePoints[];

	@Override
	public int getPostCount() {
		return inputCount + 1;
	}

	@Override
	public Point getPost(int n) {
		if (n == inputCount)
			return point2;
		return inPosts[n];
	}

	@Override
	public int getVoltageSourceCount() {
		return 1;
	}

	abstract String getGateName();

	@Override
	public void getInfo(String arr[]) {
		arr[0] = getGateName();
		arr[1] = "Vout = " + getVoltageText(volts[inputCount]);
		arr[2] = "Iout = " + getCurrentText(getCurrent());
	}

	@Override
	public void stamp() {
		sim.stampVoltageSource(0, nodes[inputCount], voltSource);
	}

	boolean getInput(int x) {
		return volts[x] > 2.5;
	}

	abstract boolean calcFunction();

	@Override
	public void doStep() {
		int i;
		boolean f = calcFunction();
		if (isInverting())
			f = !f;
		lastOutput = f;
		double res = f ? 5 : 0;
		sim.updateVoltageSource(0, nodes[inputCount], voltSource, res);
	}

	// there is no current path through the gate inputs, but there
	// is an indirect path through the output to ground.
	@Override
	public boolean getConnection(int n1, int n2) {
		return false;
	}

	@Override
	public boolean hasGroundConnection(int n1) {
		return (n1 == inputCount);
	}
}
