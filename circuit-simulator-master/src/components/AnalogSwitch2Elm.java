package components;

import java.awt.Graphics;
import java.awt.Point;
import java.util.StringTokenizer;

public class AnalogSwitch2Elm extends AnalogSwitchElm {
	public AnalogSwitch2Elm(int xx, int yy) {
		super(xx, yy);
	}

	public AnalogSwitch2Elm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
	}

	final int openhs = 16;
	Point swposts[], swpoles[], ctlPoint;

	@Override
	public void setPoints() {
		super.setPoints();
		calcLeads(32);
		swposts = newPointArray(2);
		swpoles = newPointArray(2);
		interpPoint2(lead1, lead2, swpoles[0], swpoles[1], 1, openhs);
		interpPoint2(point1, point2, swposts[0], swposts[1], 1, openhs);
		ctlPoint = interpPoint(point1, point2, .5, openhs);
	}

	@Override
	public int getPostCount() {
		return 4;
	}

	@Override
	public Point getPost(int n) {
		return (n == 0) ? point1 : (n == 3) ? ctlPoint : swposts[n - 1];
	}

	@Override
	public int getDumpType() {
		return 160;
	}

	@Override
	void calculateCurrent() {
		if (open)
			current = (volts[0] - volts[2]) / r_on;
		else
			current = (volts[0] - volts[1]) / r_on;
	}

	@Override
	public void stamp() {
		sim.stampNonLinear(nodes[0]);
		sim.stampNonLinear(nodes[1]);
		sim.stampNonLinear(nodes[2]);
	}

	@Override
	public void doStep() {
		open = (volts[3] < 2.5);
		if ((flags & FLAG_INVERT) != 0)
			open = !open;
		if (open) {
			sim.stampResistor(nodes[0], nodes[2], r_on);
			sim.stampResistor(nodes[0], nodes[1], r_off);
		} else {
			sim.stampResistor(nodes[0], nodes[1], r_on);
			sim.stampResistor(nodes[0], nodes[2], r_off);
		}
	}

	@Override
	public boolean getConnection(int n1, int n2) {
		if (n1 == 3 || n2 == 3)
			return false;
		return true;
	}

	@Override
	public void getInfo(String arr[]) {
		arr[0] = "analog switch (SPDT)";
		arr[1] = "I = " + getCurrentDText(getCurrent());
	}
}
