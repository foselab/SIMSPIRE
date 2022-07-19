package scope;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

import components.CircuitElm;
import components.LogicOutputElm;
import components.MemristorElm;
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
	MemoryImageSource imageSource;
	Image image;
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

	void draw2d(Graphics g) {
		int i;
		if (pixels == null || dpixels == null)
			return;
		int col = (sim.getPrintableCheckItem().getState()) ? 0xFFFFFFFF : 0;
		for (i = 0; i != pixels.length; i++)
			pixels[i] = col;
		for (i = 0; i != getRect().width; i++)
			pixels[i + getRect().width * (getRect().height / 2)] = 0xFF00FF00;
		int ycol = (plotXY) ? 0xFF00FF00 : 0xFFFFFF00;
		for (i = 0; i != getRect().height; i++)
			pixels[getRect().width / 2 + getRect().width * i] = ycol;
		for (i = 0; i != pixels.length; i++) {
			int q = (int) (255 * dpixels[i]);
			if (q > 0)
				pixels[i] = 0xFF000000 | (0x10101 * q);
			dpixels[i] *= .997;
		}
		g.drawImage(image, getRect().x, getRect().y, null);
		getElm();
		g.setColor(CircuitElm.getWhiteColor());
		g.fillOval(getRect().x + draw_ox - 2, getRect().y + draw_oy - 2, 5, 5);
		int yt = getRect().y + 10;
		int x = getRect().x;
		if (text != null && getRect().y + getRect().height > yt + 5) {
			g.drawString(text, x, yt);
			yt += 15;
		}
	}

	public void draw(Graphics g) {
		if (getElm() == null)
			return;
		if (plot2d) {
			draw2d(g);
			return;
		}
		if (pixels == null)
			return;
		int i;
		int col = (sim.getPrintableCheckItem().getState()) ? 0xFFFFFFFF : 0;
		for (i = 0; i != pixels.length; i++)
			pixels[i] = col;
		int x = 0;
		int maxy = (getRect().height - 1) / 2;
		int y = maxy;

		boolean gotI = false;
		boolean gotV = false;
		int minRange = 4;
		double realMaxV = -1e8;
		double realMaxI = -1e8;
		double realMinV = 1e8;
		double realMinI = 1e8;
		int curColor = 0xFFFFFF00;
		int voltColor = (value > 0) ? 0xFFFFFFFF : 0xFF00FF00;
		if (sim.getScopeSelected() == -1 && getElm() == sim.getMouseElm())
			curColor = voltColor = 0xFF00FFFF;
		int ipa = ptr + scopePointCount - getRect().width;
		for (i = 0; i != getRect().width; i++) {
			int ip = (i + ipa) & (scopePointCount - 1);
			while (maxV[ip] > minMaxV)
				minMaxV *= 2;
			while (minV[ip] < -minMaxV)
				minMaxV *= 2;
			while (maxI[ip] > minMaxI)
				minMaxI *= 2;
			while (minI[ip] < -minMaxI)
				minMaxI *= 2;
		}

		double gridStep = 1e-8;
		double gridMax = (showI ? minMaxI : minMaxV);
		while (gridStep * 100 < gridMax)
			gridStep *= 10;
		if (maxy * gridStep / gridMax < .3)
			gridStep = 0;

		int ll;
		boolean sublines = (maxy * gridStep / gridMax > 3);
		for (ll = -100; ll <= 100; ll++) {
			// don't show gridlines if plotting multiple values,
			// or if lines are too close together (except for center line)
			if (ll != 0 && ((showI && showV) || gridStep == 0))
				continue;
			int yl = maxy - (int) (maxy * ll * gridStep / gridMax);
			if (yl < 0 || yl >= getRect().height - 1)
				continue;
			col = ll == 0 ? 0xFF909090 : 0xFF404040;
			if (ll % 10 != 0) {
				col = 0xFF101010;
				if (!sublines)
					continue;
			}
			for (i = 0; i != getRect().width; i++)
				pixels[i + yl * getRect().width] = col;
		}

		gridStep = 1e-15;
		double ts = sim.getTimeStep() * getSpeed();
		while (gridStep < ts * 5)
			gridStep *= 10;
		double tstart = sim.getT() - sim.getTimeStep() * getSpeed() * getRect().width;
		double tx = sim.getT() - (sim.getT() % gridStep);
		int first = 1;
		for (ll = 0;; ll++) {
			double tl = tx - gridStep * ll;
			int gx = (int) ((tl - tstart) / ts);
			if (gx < 0)
				break;
			if (gx >= getRect().width)
				continue;
			if (tl < 0)
				continue;
			col = 0xFF202020;
			first = 0;
			if (((tl + gridStep / 4) % (gridStep * 10)) < gridStep) {
				col = 0xFF909090;
				if (((tl + gridStep / 4) % (gridStep * 100)) < gridStep)
					col = 0xFF4040D0;
			}
			for (i = 0; i < pixels.length; i += getRect().width)
				pixels[i + gx] = col;
		}

		// these two loops are pretty much the same, and should be
		// combined!
		if (value == 0 && showI) {
			int ox = -1, oy = -1;
			int j;
			for (i = 0; i != getRect().width; i++) {
				int ip = (i + ipa) & (scopePointCount - 1);
				int miniy = (int) ((maxy / minMaxI) * minI[ip]);
				int maxiy = (int) ((maxy / minMaxI) * maxI[ip]);
				if (maxI[ip] > realMaxI)
					realMaxI = maxI[ip];
				if (minI[ip] < realMinI)
					realMinI = minI[ip];
				if (miniy <= maxy) {
					if (miniy < -minRange || maxiy > minRange)
						gotI = true;
					if (ox != -1) {
						if (miniy == oy && maxiy == oy)
							continue;
						for (j = ox; j != x + i; j++)
							pixels[j + getRect().width * (y - oy)] = curColor;
						ox = oy = -1;
					}
					if (miniy == maxiy) {
						ox = x + i;
						oy = miniy;
						continue;
					}
					for (j = miniy; j <= maxiy; j++)
						pixels[x + i + getRect().width * (y - j)] = curColor;
				}
			}
			if (ox != -1)
				for (j = ox; j != x + i; j++)
					pixels[j + getRect().width * (y - oy)] = curColor;
		}
		if (value != 0 || showV) {
			int ox = -1, oy = -1, j;
			for (i = 0; i != getRect().width; i++) {
				int ip = (i + ipa) & (scopePointCount - 1);
				int minvy = (int) ((maxy / minMaxV) * minV[ip]);
				int maxvy = (int) ((maxy / minMaxV) * maxV[ip]);
				if (maxV[ip] > realMaxV)
					realMaxV = maxV[ip];
				if (minV[ip] < realMinV)
					realMinV = minV[ip];
				if ((value != 0 || showV) && minvy <= maxy) {
					if (minvy < -minRange || maxvy > minRange)
						gotV = true;
					if (ox != -1) {
						if (minvy == oy && maxvy == oy)
							continue;
						for (j = ox; j != x + i; j++)
							pixels[j + getRect().width * (y - oy)] = voltColor;
						ox = oy = -1;
					}
					if (minvy == maxvy) {
						ox = x + i;
						oy = minvy;
						continue;
					}
					for (j = minvy; j <= maxvy; j++)
						pixels[x + i + getRect().width * (y - j)] = voltColor;
				}
			}
			if (ox != -1)
				for (j = ox; j != x + i; j++)
					pixels[j + getRect().width * (y - oy)] = voltColor;
		}
		double freq = 0;
		if (showFreq) {
			// try to get frequency
			// get average
			double avg = 0;
			for (i = 0; i != getRect().width; i++) {
				int ip = (i + ipa) & (scopePointCount - 1);
				avg += minV[ip] + maxV[ip];
			}
			avg /= i * 2;
			int state = 0;
			double thresh = avg * .05;
			int oi = 0;
			double avperiod = 0;
			int periodct = -1;
			double avperiod2 = 0;
			// count period lengths
			for (i = 0; i != getRect().width; i++) {
				int ip = (i + ipa) & (scopePointCount - 1);
				double q = maxV[ip] - avg;
				int os = state;
				if (q < thresh)
					state = 1;
				else if (q > -thresh)
					state = 2;
				if (state == 2 && os == 1) {
					int pd = i - oi;
					oi = i;
					// short periods can't be counted properly
					if (pd < 12)
						continue;
					// skip first period, it might be too short
					if (periodct >= 0) {
						avperiod += pd;
						avperiod2 += pd * pd;
					}
					periodct++;
				}
			}
			avperiod /= periodct;
			avperiod2 /= periodct;
			double periodstd = Math.sqrt(avperiod2 - avperiod * avperiod);
			freq = 1 / (avperiod * sim.getTimeStep() * getSpeed());
			// don't show freq if standard deviation is too great
			if (periodct < 1 || periodstd > 2)
				freq = 0;
			// System.out.println(freq + " " + periodstd + " " + periodct);
		}
		g.drawImage(image, getRect().x, getRect().y, null);
		getElm();
		g.setColor(CircuitElm.getWhiteColor());
		int yt = getRect().y + 10;
		x += getRect().x;
		if (isShowMax()) {
			if (value != 0) {
				getElm();
				g.drawString(CircuitElm.getUnitText(realMaxV, getElm().getScopeUnits(value)), x, yt);
			} else if (showV) {
				getElm();
				g.drawString(CircuitElm.getVoltageText(realMaxV), x, yt);
			} else if (showI) {
				getElm();
				g.drawString(CircuitElm.getCurrentText(realMaxI), x, yt);
			}
			yt += 15;
		}
		if (isShowMin()) {
			int ym = getRect().y + getRect().height - 5;
			if (value != 0) {
				getElm();
				g.drawString(CircuitElm.getUnitText(realMinV, getElm().getScopeUnits(value)), x, ym);
			} else if (showV) {
				getElm();
				g.drawString(CircuitElm.getVoltageText(realMinV), x, ym);
			} else if (showI) {
				getElm();
				g.drawString(CircuitElm.getCurrentText(realMinI), x, ym);
			}
		}
		if (text != null && getRect().y + getRect().height > yt + 5) {
			g.drawString(text, x, yt);
			yt += 15;
		}
		if (showFreq && freq != 0 && getRect().y + getRect().height > yt + 5) {
			getElm();
			g.drawString(CircuitElm.getUnitText(freq, "Hz"), x, yt);
		}
		if (ptr > 5 && !lockScale) {
			if (!gotI && minMaxI > 1e-4)
				minMaxI /= 2;
			if (!gotV && minMaxV > 1e-4)
				minMaxV /= 2;
		}
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

	public PopupMenu getMenu() {
		if (getElm() == null)
			return null;
		if (getElm() instanceof TransistorElm) {
			sim.getScopeIbMenuItem().setState(value == VAL_IB);
			sim.getScopeIcMenuItem().setState(value == VAL_IC);
			sim.getScopeIeMenuItem().setState(value == VAL_IE);
			sim.getScopeVbeMenuItem().setState(value == VAL_VBE);
			sim.getScopeVbcMenuItem().setState(value == VAL_VBC);
			sim.getScopeVceMenuItem().setState(value == VAL_VCE && ivalue != VAL_IC);
			sim.getScopeVceIcMenuItem().setState(value == VAL_VCE && ivalue == VAL_IC);
			return sim.getTransScopeMenu();
		} else {
			sim.getScopeVMenuItem().setState(showV && value == 0);
			sim.getScopeIMenuItem().setState(showI && value == 0);
			sim.getScopeMaxMenuItem().setState(isShowMax());
			sim.getScopeMinMenuItem().setState(isShowMin());
			sim.getScopeFreqMenuItem().setState(showFreq);
			sim.getScopePowerMenuItem().setState(value == VAL_POWER);
			sim.getScopeVIMenuItem().setState(plot2d && !plotXY);
			sim.getScopeXYMenuItem().setState(plotXY);
			sim.getScopeSelectYMenuItem().setEnabled(plotXY);
			sim.getScopeResistMenuItem().setState(value == VAL_R);
			sim.getScopeResistMenuItem().setEnabled(getElm() instanceof MemristorElm);
			return sim.getScopeMenu();
		}
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
				image = (Image) cstr.newInstance(
						new Object[] { new Integer(w), new Integer(h), new Integer(BufferedImage.TYPE_INT_RGB) });
				Method m = biclass.getMethod("getRaster");
				Object ras = m.invoke(image);
				Object db = rasclass.getMethod("getDataBuffer").invoke(ras);
				pixels = (int[]) dbiclass.getMethod("getData").invoke(db);
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
			imageSource = new MemoryImageSource(w, h, pixels, 0, w);
			imageSource.setAnimated(true);
			imageSource.setFullBufferUpdates(true);
			image = sim.getCv().createImage(imageSource);
		}
		dpixels = new float[w * h];
		draw_ox = draw_oy = -1;
	}

	public void handleMenu(ItemEvent e, Object mi) {
		if (mi == sim.getScopeVMenuItem())
			showVoltage(sim.getScopeVMenuItem().getState());
		if (mi == sim.getScopeIMenuItem())
			showCurrent(sim.getScopeIMenuItem().getState());
		if (mi == sim.getScopeMaxMenuItem())
			showMax(sim.getScopeMaxMenuItem().getState());
		if (mi == sim.getScopeMinMenuItem())
			showMin(sim.getScopeMinMenuItem().getState());
		if (mi == sim.getScopeFreqMenuItem())
			showFreq(sim.getScopeFreqMenuItem().getState());
		if (mi == sim.getScopePowerMenuItem())
			setValue(VAL_POWER);
		if (mi == sim.getScopeIbMenuItem())
			setValue(VAL_IB);
		if (mi == sim.getScopeIcMenuItem())
			setValue(VAL_IC);
		if (mi == sim.getScopeIeMenuItem())
			setValue(VAL_IE);
		if (mi == sim.getScopeVbeMenuItem())
			setValue(VAL_VBE);
		if (mi == sim.getScopeVbcMenuItem())
			setValue(VAL_VBC);
		if (mi == sim.getScopeVceMenuItem())
			setValue(VAL_VCE);
		if (mi == sim.getScopeVceIcMenuItem()) {
			plot2d = true;
			plotXY = false;
			value = VAL_VCE;
			ivalue = VAL_IC;
			resetGraph();
		}

		if (mi == sim.getScopeVIMenuItem()) {
			plot2d = sim.getScopeVIMenuItem().getState();
			plotXY = false;
			resetGraph();
		}
		if (mi == sim.getScopeXYMenuItem()) {
			plotXY = plot2d = sim.getScopeXYMenuItem().getState();
			if (yElm == null)
				selectY();
			resetGraph();
		}
		if (mi == sim.getScopeResistMenuItem())
			setValue(VAL_R);
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
