package circuitsimulator.components;

import java.util.StringTokenizer;

public class PhaseCompElm extends ChipElm {
	public PhaseCompElm(int xx, int yy) {
		super(xx, yy);
	}

	public PhaseCompElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
	}

	@Override
	String getChipName() {
		return "phase comparator";
	}

	@Override
	void setupPins() {
		sizeX = 2;
		sizeY = 2;
		pins = new Pin[3];
		pins[0] = new Pin(0, SIDE_W, "I1");
		pins[1] = new Pin(1, SIDE_W, "I2");
		pins[2] = new Pin(0, SIDE_E, "O");
		pins[2].output = true;
	}

	@Override
	public boolean nonLinear() {
		return true;
	}

	@Override
	public void stamp() {
		int vn = sim.getNodeList().size() + pins[2].voltSource;
		sim.stampNonLinear(vn);
		sim.stampNonLinear(0);
		sim.stampNonLinear(nodes[2]);
	}

	boolean ff1, ff2;

	@Override
	public void doStep() {
		boolean v1 = volts[0] > 2.5;
		boolean v2 = volts[1] > 2.5;
		if (v1 && !pins[0].value)
			ff1 = true;
		if (v2 && !pins[1].value)
			ff2 = true;
		if (ff1 && ff2)
			ff1 = ff2 = false;
		double out = (ff1) ? 5 : (ff2) ? 0 : -1;
		// System.out.println(out + " " + v1 + " " + v2);
		if (out != -1)
			sim.stampVoltageSource(0, nodes[2], pins[2].voltSource, out);
		else {
			// tie current through output pin to 0
			int vn = sim.getNodeList().size() + pins[2].voltSource;
			sim.stampMatrix(vn, vn, 1);
		}
		pins[0].value = v1;
		pins[1].value = v2;
	}

	@Override
	public int getPostCount() {
		return 3;
	}

	@Override
	public int getVoltageSourceCount() {
		return 1;
	}

	@Override
	public int getDumpType() {
		return 161;
	}
}
