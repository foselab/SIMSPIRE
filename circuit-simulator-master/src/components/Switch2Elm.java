package components;

import java.awt.Point;
import java.util.StringTokenizer;

public class Switch2Elm extends SwitchElm {
	int link;
	static final int FLAG_CENTER_OFF = 1;

	public Switch2Elm(int xx, int yy) {
		super(xx, yy, false);
		noDiagonal = true;
	}

	Switch2Elm(int xx, int yy, boolean mm) {
		super(xx, yy, mm);
		noDiagonal = true;
	}

	public Switch2Elm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
		link = new Integer(st.nextToken()).intValue();
		noDiagonal = true;
	}

	@Override
	public int getDumpType() {
		return 'S';
	}

	@Override
	public String dump() {
		return super.dump() + " " + link;
	}

	final int openhs = 16;
	Point swposts[], swpoles[];

	@Override
	public void setPoints() {
		super.setPoints();
		calcLeads(32);
		swposts = newPointArray(2);
		swpoles = newPointArray(3);
		interpPoint2(lead1, lead2, swpoles[0], swpoles[1], 1, openhs);
		swpoles[2] = lead2;
		interpPoint2(point1, point2, swposts[0], swposts[1], 1, openhs);
		posCount = hasCenterOff() ? 3 : 2;
	}

	@Override
	public Point getPost(int n) {
		return (n == 0) ? point1 : swposts[n - 1];
	}

	@Override
	public int getPostCount() {
		return 3;
	}

	@Override
	void calculateCurrent() {
		if (position == 2)
			current = 0;
	}

	@Override
	public void stamp() {
		if (position == 2) // in center?
			return;
		sim.stampVoltageSource(nodes[0], nodes[position + 1], voltSource, 0);
	}

	@Override
	public int getVoltageSourceCount() {
		return (position == 2) ? 0 : 1;
	}

	@Override
	public void toggle() {
		super.toggle();
		if (link != 0) {
			int i;
			for (i = 0; i != sim.getElmList().size(); i++) {
				Object o = sim.getElmList().get(i);
				if (o instanceof Switch2Elm) {
					Switch2Elm s2 = (Switch2Elm) o;
					if (s2.link == link)
						s2.position = position;
				}
			}
		}
	}

	@Override
	public boolean getConnection(int n1, int n2) {
		if (position == 2)
			return false;
		return comparePair(n1, n2, 0, 1 + position);
	}

	@Override
	public void getInfo(String arr[]) {
		arr[0] = (link == 0) ? "switch (SPDT)" : "switch (DPDT)";
		arr[1] = "I = " + getCurrentDText(getCurrent());
	}

	boolean hasCenterOff() {
		return (flags & FLAG_CENTER_OFF) != 0;
	}

	@Override
	public int getShortcut() {
		return 'S';
	}
}
