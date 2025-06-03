package circuitsimulator.components;

import java.util.StringTokenizer;

public class XorGateElm extends OrGateElm {
	public XorGateElm(int xx, int yy) {
		super(xx, yy);
	}

	public XorGateElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
	}

	@Override
	String getGateName() {
		return "XOR gate";
	}

	@Override
	boolean calcFunction() {
		int i;
		boolean f = false;
		for (i = 0; i != inputCount; i++)
			f ^= getInput(i);
		return f;
	}

	@Override
	public int getDumpType() {
		return 154;
	}

	@Override
	public int getShortcut() {
		return '4';
	}
}
