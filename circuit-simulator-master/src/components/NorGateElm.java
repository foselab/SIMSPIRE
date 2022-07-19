package components;

import java.util.StringTokenizer;

public class NorGateElm extends OrGateElm {
	public NorGateElm(int xx, int yy) {
		super(xx, yy);
	}

	public NorGateElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
	}

	@Override
	String getGateName() {
		return "NOR gate";
	}

	@Override
	boolean isInverting() {
		return true;
	}

	@Override
	public int getDumpType() {
		return 153;
	}

	@Override
	public int getShortcut() {
		return '#';
	}
}
