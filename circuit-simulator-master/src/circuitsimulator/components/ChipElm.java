package circuitsimulator.components;

import java.awt.Point;
import java.util.StringTokenizer;

public abstract class ChipElm extends CircuitElm {
	int csize, cspc, cspc2;
	int bits;
	final int FLAG_SMALL = 1;
	final int FLAG_FLIP_X = 1024;
	final int FLAG_FLIP_Y = 2048;

	public ChipElm(int xx, int yy) {
		super(xx, yy);
		if (needsBits())
			bits = (this instanceof DecadeElm) ? 10 : 4;
		noDiagonal = true;
		setupPins();
	}

	public ChipElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		if (needsBits())
			bits = new Integer(st.nextToken()).intValue();
		noDiagonal = true;
		setupPins();
		setSize((f & FLAG_SMALL) != 0 ? 1 : 2);
		int i;
		for (i = 0; i != getPostCount(); i++) {
			if (pins[i].state) {
				volts[i] = new Double(st.nextToken()).doubleValue();
				pins[i].value = volts[i] > 2.5;
			}
		}
	}

	boolean needsBits() {
		return false;
	}

	void setSize(int s) {
		csize = s;
		cspc = 8 * s;
		cspc2 = cspc * 2;
		flags &= ~FLAG_SMALL;
		flags |= (s == 1) ? FLAG_SMALL : 0;
	}

	abstract void setupPins();

	int rectPointsX[], rectPointsY[];
	int clockPointsX[], clockPointsY[];
	Pin pins[];
	int sizeX, sizeY;
	boolean lastClock;

	@Override
	public void setPoints() {
		int x0 = getX() + cspc2;
		int y0 = getY();
		int xr = x0 - cspc;
		int yr = y0 - cspc;
		int xs = sizeX * cspc2;
		int ys = sizeY * cspc2;
		rectPointsX = new int[] { xr, xr + xs, xr + xs, xr };
		rectPointsY = new int[] { yr, yr, yr + ys, yr + ys };
		int i;
		for (i = 0; i != getPostCount(); i++) {
			Pin p = pins[i];
			switch (p.side) {
			case SIDE_N:
				p.setPoint(x0, y0, 1, 0, 0, -1, 0, 0);
				break;
			case SIDE_S:
				p.setPoint(x0, y0, 1, 0, 0, 1, 0, ys - cspc2);
				break;
			case SIDE_W:
				p.setPoint(x0, y0, 0, 1, -1, 0, 0, 0);
				break;
			case SIDE_E:
				p.setPoint(x0, y0, 0, 1, 1, 0, xs - cspc2, 0);
				break;
			}
		}
	}

	@Override
	public Point getPost(int n) {
		return pins[n].post;
	}

	@Override
	public abstract int getVoltageSourceCount(); // output count

	@Override
	public void setVoltageSource(int j, int vs) {
		int i;
		for (i = 0; i != getPostCount(); i++) {
			Pin p = pins[i];
			if (p.output && j-- == 0) {
				p.voltSource = vs;
				return;
			}
		}
		System.out.println("setVoltageSource failed for " + this);
	}

	@Override
	public void stamp() {
		int i;
		for (i = 0; i != getPostCount(); i++) {
			Pin p = pins[i];
			if (p.output)
				sim.stampVoltageSource(0, nodes[i], p.voltSource);
		}
	}

	void execute() {
	}

	@Override
	public void doStep() {
		int i;
		for (i = 0; i != getPostCount(); i++) {
			Pin p = pins[i];
			if (!p.output)
				p.value = volts[i] > 2.5;
		}
		execute();
		for (i = 0; i != getPostCount(); i++) {
			Pin p = pins[i];
			if (p.output)
				sim.updateVoltageSource(0, nodes[i], p.voltSource, p.value ? 5 : 0);
		}
	}

	@Override
	public void reset() {
		int i;
		for (i = 0; i != getPostCount(); i++) {
			pins[i].value = false;
			pins[i].curcount = 0;
			volts[i] = 0;
		}
		lastClock = false;
	}

	@Override
	public String dump() {
		String s = super.dump();
		if (needsBits())
			s += " " + bits;
		int i;
		for (i = 0; i != getPostCount(); i++) {
			if (pins[i].state)
				s += " " + volts[i];
		}
		return s;
	}

	@Override
	public void getInfo(String arr[]) {
		arr[0] = getChipName();
		int i, a = 1;
		for (i = 0; i != getPostCount(); i++) {
			Pin p = pins[i];
			if (arr[a] != null)
				arr[a] += "; ";
			else
				arr[a] = "";
			String t = p.text;
			if (p.lineOver)
				t += '\'';
			if (p.clock)
				t = "Clk";
			arr[a] += t + " = " + getVoltageText(volts[i]);
			if (i % 2 == 1)
				a++;
		}
	}

	@Override
	public void setCurrent(int x, double c) {
		int i;
		for (i = 0; i != getPostCount(); i++)
			if (pins[i].output && pins[i].voltSource == x)
				pins[i].current = c;
	}

	String getChipName() {
		return "chip";
	}

	@Override
	public boolean getConnection(int n1, int n2) {
		return false;
	}

	@Override
	public boolean hasGroundConnection(int n1) {
		return pins[n1].output;
	}

	final int SIDE_N = 0;
	final int SIDE_S = 1;
	final int SIDE_W = 2;
	final int SIDE_E = 3;

	class Pin {
		Pin(int p, int s, String t) {
			pos = p;
			side = s;
			text = t;
		}

		Point post, stub;
		Point textloc;
		int pos, side, voltSource, bubbleX, bubbleY;
		String text;
		boolean lineOver, bubble, clock, output, value, state;
		double curcount, current;

		void setPoint(int px, int py, int dx, int dy, int dax, int day, int sx, int sy) {
			if ((flags & FLAG_FLIP_X) != 0) {
				dx = -dx;
				dax = -dax;
				px += cspc2 * (sizeX - 1);
				sx = -sx;
			}
			if ((flags & FLAG_FLIP_Y) != 0) {
				dy = -dy;
				day = -day;
				py += cspc2 * (sizeY - 1);
				sy = -sy;
			}
			int xa = px + cspc2 * dx * pos + sx;
			int ya = py + cspc2 * dy * pos + sy;
			post = new Point(xa + dax * cspc2, ya + day * cspc2);
			stub = new Point(xa + dax * cspc, ya + day * cspc);
			textloc = new Point(xa, ya);
			if (bubble) {
				bubbleX = xa + dax * 10 * csize;
				bubbleY = ya + day * 10 * csize;
			}
			if (clock) {
				clockPointsX = new int[3];
				clockPointsY = new int[3];
				clockPointsX[0] = xa + dax * cspc - dx * cspc / 2;
				clockPointsY[0] = ya + day * cspc - dy * cspc / 2;
				clockPointsX[1] = xa;
				clockPointsY[1] = ya;
				clockPointsX[2] = xa + dax * cspc + dx * cspc / 2;
				clockPointsY[2] = ya + day * cspc + dy * cspc / 2;
			}
		}
	}
}
