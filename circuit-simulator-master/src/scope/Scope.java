package scope;
import java.awt.Rectangle;
import java.lang.reflect.Constructor;
import java.util.StringTokenizer;

import components.CircuitElm;
import components.LogicOutputElm;
import components.OutputElm;
import components.ProbeElm;
import components.TransistorElm;
import simulator.CirSim;

public class Scope {
	final int FLAG_YELM = 32;
	public static final int VAL_POWER = 1;
	public static final int VAL_IB = 1;
	public static final int VAL_IC = 2;
	public static final int VAL_IE = 3;
	public static final int VAL_VBE = 4;
	public static final int VAL_VBC = 5;
	public static final int VAL_VCE = 6;
	public static final int VAL_R = 2;
	double minV[], maxV[], minMaxV;
	double minI[], maxI[], minMaxI;
	int scopePointCount = 128;
	int ptr, ctr;
	private int speed;
	private int position;
	int value, ivalue;
	String text;
	private Rectangle rect;
	boolean showI, showV;
	private boolean showMax;
	private boolean showMin;
	boolean showFreq;
	boolean lockScale;
	boolean plot2d;
	boolean plotXY;
	private CircuitElm elm;
	CircuitElm xElm;
	CircuitElm yElm;
	int pixels[];
	int draw_ox, draw_oy;
	float dpixels[];
	CirSim sim;

	public Scope(CirSim s) {
		setRect(new Rectangle());
		reset();
		sim = s;
	}

	void showCurrent(boolean b) {
		showI = b;
		value = ivalue = 0;
	}

	void showVoltage(boolean b) {
		showV = b;
		value = ivalue = 0;
	}

	void showMax(boolean b) {
		setShowMax(b);
	}

	void showMin(boolean b) {
		setShowMin(b);
	}

	void showFreq(boolean b) {
		showFreq = b;
	}

	void setLockScale(boolean b) {
		lockScale = b;
	}

	public void resetGraph() {
		scopePointCount = 1;
		while (scopePointCount <= getRect().width)
			scopePointCount *= 2;
		minV = new double[scopePointCount];
		maxV = new double[scopePointCount];
		minI = new double[scopePointCount];
		maxI = new double[scopePointCount];
		ptr = ctr = 0;
		allocImage();
	}

	boolean active() {
		return getElm() != null;
	}

	void reset() {
		resetGraph();
		minMaxV = 5;
		minMaxI = .1;
		setSpeed(64);
		showI = showV = setShowMax(true);
		showFreq = lockScale = setShowMin(false);
		plot2d = false;
		// no showI for Output
		if (getElm() != null && (getElm() instanceof OutputElm || getElm() instanceof LogicOutputElm || getElm() instanceof ProbeElm))
			showI = false;
		value = ivalue = 0;
		if (getElm() instanceof TransistorElm)
			value = VAL_VCE;
	}

	public void setRect(Rectangle r) {
		rect = r;
		resetGraph();
	}

	int getWidth() {
		return getRect().width;
	}

	public int rightEdge() {
		return getRect().x + getRect().width;
	}

	public void setElm(CircuitElm ce) {
		elm = ce;
		reset();
	}

	public void timeStep() {
		if (getElm() == null)
			return;
		double v = getElm().getScopeValue(value);
		if (v < minV[ptr])
			minV[ptr] = v;
		if (v > maxV[ptr])
			maxV[ptr] = v;
		double i = 0;
		if (value == 0 || ivalue != 0) {
			i = (ivalue == 0) ? getElm().getCurrent() : getElm().getScopeValue(ivalue);
			if (i < minI[ptr])
				minI[ptr] = i;
			if (i > maxI[ptr])
				maxI[ptr] = i;
		}

		if (plot2d && dpixels != null) {
			boolean newscale = false;
			while (v > minMaxV || v < -minMaxV) {
				minMaxV *= 2;
				newscale = true;
			}
			double yval = i;
			if (plotXY)
				yval = (yElm == null) ? 0 : yElm.getVoltageDiff();
			while (yval > minMaxI || yval < -minMaxI) {
				minMaxI *= 2;
				newscale = true;
			}
			if (newscale)
				clear2dView();
			double xa = v / minMaxV;
			double ya = yval / minMaxI;
			int x = (int) (getRect().width * (1 + xa) * .499);
			int y = (int) (getRect().height * (1 - ya) * .499);
			drawTo(x, y);
		} else {
			ctr++;
			if (ctr >= getSpeed()) {
				ptr = (ptr + 1) & (scopePointCount - 1);
				minV[ptr] = maxV[ptr] = v;
				minI[ptr] = maxI[ptr] = i;
				ctr = 0;
			}
		}
	}

	void drawTo(int x2, int y2) {
		if (draw_ox == -1) {
			draw_ox = x2;
			draw_oy = y2;
		}
		// need to draw a line from x1,y1 to x2,y2
		if (draw_ox == x2 && draw_oy == y2) {
			dpixels[x2 + getRect().width * y2] = 1;
		} else if (CircuitElm.abs(y2 - draw_oy) > CircuitElm.abs(x2 - draw_ox)) {
			// y difference is greater, so we step along y's
			// from min to max y and calculate x for each step
			double sgn = CircuitElm.sign(y2 - draw_oy);
			int x, y;
			for (y = draw_oy; y != y2 + sgn; y += sgn) {
				x = draw_ox + (x2 - draw_ox) * (y - draw_oy) / (y2 - draw_oy);
				dpixels[x + getRect().width * y] = 1;
			}
		} else {
			// x difference is greater, so we step along x's
			// from min to max x and calculate y for each step
			double sgn = CircuitElm.sign(x2 - draw_ox);
			int x, y;
			for (x = draw_ox; x != x2 + sgn; x += sgn) {
				y = draw_oy + (y2 - draw_oy) * (x - draw_ox) / (x2 - draw_ox);
				dpixels[x + getRect().width * y] = 1;
			}
		}
		draw_ox = x2;
		draw_oy = y2;
	}

	void clear2dView() {
		int i;
		for (i = 0; i != dpixels.length; i++)
			dpixels[i] = 0;
		draw_ox = draw_oy = -1;
	}

	public void adjustScale(double x) {
		minMaxV *= x;
		minMaxI *= x;
	}

	public void speedUp() {
		if (getSpeed() > 1) {
			setSpeed(getSpeed() / 2);
			resetGraph();
		}
	}

	public void slowDown() {
		setSpeed(getSpeed() * 2);
		resetGraph();
	}

	void setValue(int x) {
		reset();
		value = x;
	}

	public String dump() {
		if (getElm() == null)
			return null;
		int flags = (showI ? 1 : 0) | (showV ? 2 : 0) | (isShowMax() ? 0 : 4) | // showMax used to be always on
				(showFreq ? 8 : 0) | (lockScale ? 16 : 0) | (plot2d ? 64 : 0) | (plotXY ? 128 : 0)
				| (isShowMin() ? 256 : 0);
		flags |= FLAG_YELM; // yelm present
		int eno = sim.locateElm(getElm());
		if (eno < 0)
			return null;
		int yno = yElm == null ? -1 : sim.locateElm(yElm);
		String x = "o " + eno + " " + getSpeed() + " " + value + " " + flags + " " + minMaxV + " " + minMaxI + " " + getPosition()
				+ " " + yno;
		if (text != null)
			x += " " + text;
		return x;
	}

	public void undump(StringTokenizer st) {
		reset();
		int e = new Integer(st.nextToken()).intValue();
		if (e == -1)
			return;
		setElm(sim.getElm(e));
		setSpeed(new Integer(st.nextToken()).intValue());
		value = new Integer(st.nextToken()).intValue();
		int flags = new Integer(st.nextToken()).intValue();
		minMaxV = new Double(st.nextToken()).doubleValue();
		minMaxI = new Double(st.nextToken()).doubleValue();
		if (minMaxV == 0)
			minMaxV = .5;
		if (minMaxI == 0)
			minMaxI = 1;
		text = null;
		yElm = null;
		try {
			setPosition(new Integer(st.nextToken()).intValue());
			int ye = -1;
			if ((flags & FLAG_YELM) != 0) {
				ye = new Integer(st.nextToken()).intValue();
				if (ye != -1)
					yElm = sim.getElm(ye);
			}
			while (st.hasMoreTokens()) {
				if (text == null)
					text = st.nextToken();
				else
					text += " " + st.nextToken();
			}
		} catch (Exception ee) {
		}
		showI = (flags & 1) != 0;
		showV = (flags & 2) != 0;
		setShowMax((flags & 4) == 0);
		showFreq = (flags & 8) != 0;
		lockScale = (flags & 16) != 0;
		plot2d = (flags & 64) != 0;
		plotXY = (flags & 128) != 0;
		setShowMin((flags & 256) != 0);
	}

	void allocImage() {
		pixels = null;
		int w = getRect().width;
		int h = getRect().height;
		if (w == 0 || h == 0)
			return;
		if (sim.isUseBufferedImage()) {
			try {
				/*
				 * simulate the following code using reflection: dbimage = new
				 * BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB); DataBuffer db =
				 * (DataBuffer)(((BufferedImage)dbimage). getRaster().getDataBuffer());
				 * DataBufferInt dbi = (DataBufferInt) db; pixels = dbi.getData();
				 */
				Class biclass = Class.forName("java.awt.image.BufferedImage");
				Class dbiclass = Class.forName("java.awt.image.DataBufferInt");
				Class rasclass = Class.forName("java.awt.image.Raster");
				Constructor cstr = biclass.getConstructor(new Class[] { int.class, int.class, int.class });
			} catch (Exception ee) {
				// ee.printStackTrace();
				System.out.println("BufferedImage failed");
			}
		}
		if (pixels == null) {
			pixels = new int[w * h];
			int i;
			for (i = 0; i != w * h; i++)
				pixels[i] = 0xFF000000;
		}
		dpixels = new float[w * h];
		draw_ox = draw_oy = -1;
	}

	public void select() {
		sim.setMouseElm(getElm());
		if (plotXY) {
			sim.setPlotXElm(getElm());
			sim.setPlotYElm(yElm);
		}
	}

	public void selectY() {
		int e = yElm == null ? -1 : sim.locateElm(yElm);
		int firstE = e;
		while (true) {
			for (e++; e < sim.getElmList().size(); e++) {
				CircuitElm ce = sim.getElm(e);
				if ((ce instanceof OutputElm || ce instanceof ProbeElm) && ce != getElm()) {
					yElm = ce;
					return;
				}
			}
			if (firstE == -1)
				return;
			e = firstE = -1;
		}
	}

	public CircuitElm getElm() {
		return elm;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public Rectangle getRect() {
		return rect;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public boolean isShowMin() {
		return showMin;
	}

	public boolean setShowMin(boolean showMin) {
		this.showMin = showMin;
		return showMin;
	}

	public boolean isShowMax() {
		return showMax;
	}

	public boolean setShowMax(boolean showMax) {
		this.showMax = showMax;
		return showMax;
	}
}
