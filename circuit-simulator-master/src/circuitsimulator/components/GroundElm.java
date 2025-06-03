package circuitsimulator.components;

import java.awt.Graphics;
import java.util.StringTokenizer;

public class GroundElm extends CircuitElm {
	public GroundElm(int xx, int yy) {
		super(xx, yy);
	}

	public GroundElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
	}

	@Override
	public int getDumpType() {
		return 'g';
	}

	@Override
	public int getPostCount() {
		return 1;
	}

	@Override
	public void setCurrent(int x, double c) {
		current = -c;
	}

	@Override
	public void stamp() {
		sim.stampVoltageSource(0, nodes[0], voltSource, 0);
	}

	@Override
	public double getVoltageDiff() {
		return 0;
	}

	@Override
	public int getVoltageSourceCount() {
		return 1;
	}

	@Override
	public void getInfo(String arr[]) {
		arr[0] = "ground";
		arr[1] = "I = " + getCurrentText(getCurrent());
	}

	@Override
	public boolean hasGroundConnection(int n1) {
		return true;
	}

	@Override
	public int getShortcut() {
		return 'g';
	}

	@Override
	public void doStep() {
		// TODO Auto-generated method stub
		
	}
}
