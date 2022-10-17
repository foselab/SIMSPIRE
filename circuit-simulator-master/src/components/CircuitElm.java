package components;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.text.NumberFormat;

import org.apache.commons.lang3.builder.ToStringBuilder;

import simulator.CirSim;

public abstract class CircuitElm {
	private String id;
	private String idLeft;
	private String idRight;
	public static CirSim sim;

	public static NumberFormat showFormat, shortFormat, noCommaFormat;
	static final double pi = 3.14159265358979323846;

	private int x;
	private int y;
	private int x2;
	private int y2;
	int flags;
	int nodes[];
	int voltSource;
	int dx, dy, dsign;
	double dn, dpx1, dpy1;
	Point point1, point2, lead1, lead2;
	double volts[];
	double current, curcount;
	Rectangle boundingBox;
	boolean noDiagonal;
	public boolean selected;

	public int getDumpType() {
		return 0;
	}

	public Class getDumpClass() {
		return getClass();
	}

	int getDefaultFlags() {
		return 0;
	}

	CircuitElm(int xx, int yy) {
		setX(setX2(xx)); // x = x2 = xx
		setY(setY2(yy)); // y = y2 = yy
		flags = getDefaultFlags(); // 0
		allocNodes(); // initialize nodes and volts as a list of 2 elements
		initBoundingBox();
	}

	CircuitElm(int xa, int ya, int xb, int yb, int f) {
		setX(xa);
		setY(ya);
		setX2(xb);
		setY2(yb);
		flags = f;
		allocNodes();
		initBoundingBox();
	}

	void initBoundingBox() {
		boundingBox = new Rectangle();
		boundingBox.setBounds(min(getX(), getX2()), min(getY(), getY2()), abs(getX2() - getX()) + 1,
				abs(getY2() - getY()) + 1);
	}

	void allocNodes() {
		nodes = new int[getPostCount() + getInternalNodeCount()];
		volts = new double[getPostCount() + getInternalNodeCount()];
	}

	public String dump() {
		int t = getDumpType();
		return (t < 127 ? ((char) t) + " " : t + " ") + getX() + " " + getY() + " " + getX2() + " " + getY2() + " "
				+ flags;
	}

	public void reset() {
		int i;
		for (i = 0; i != getPostCount() + getInternalNodeCount(); i++)
			volts[i] = 0;
		curcount = 0;
	}

	public void setCurrent(int x, double c) {
		current = c;
	}

	public double getCurrent() {
		return current;
	}

	abstract public void doStep();

	public void startIteration() {
	}

	public void setNodeVoltage(int n, double c) {
		volts[n] = c;
		calculateCurrent();
	}

	void calculateCurrent() {
	}

	public void setPoints() {
		dx = getX2() - getX();
		dy = getY2() - getY();
		dn = Math.sqrt(dx * dx + dy * dy);
		dpx1 = dy / dn;
		dpy1 = -dx / dn;
		dsign = (dy == 0) ? sign(dx) : sign(dy);
		point1 = new Point(getX(), getY());
		point2 = new Point(getX2(), getY2());
	}

	void calcLeads(int len) {
		if (dn < len || len == 0) {
			lead1 = point1;
			lead2 = point2;
			return;
		}
		lead1 = interpPoint(point1, point2, (dn - len) / (2 * dn));
		lead2 = interpPoint(point1, point2, (dn + len) / (2 * dn));
	}

	Point interpPoint(Point a, Point b, double f) {
		Point p = new Point();
		interpPoint(a, b, p, f);
		return p;
	}

	void interpPoint(Point a, Point b, Point c, double f) {
		/*
		 * double q = (a.x*(1-f)+b.x*f+.48); System.out.println(q + " " + (int) q);
		 */
		c.x = (int) Math.floor(a.x * (1 - f) + b.x * f + .48);
		c.y = (int) Math.floor(a.y * (1 - f) + b.y * f + .48);
	}

	void interpPoint(Point a, Point b, Point c, double f, double g) {
		int gx = b.y - a.y;
		int gy = a.x - b.x;
		g /= Math.sqrt(gx * gx + gy * gy);
		c.x = (int) Math.floor(a.x * (1 - f) + b.x * f + g * gx + .48);
		c.y = (int) Math.floor(a.y * (1 - f) + b.y * f + g * gy + .48);
	}

	Point interpPoint(Point a, Point b, double f, double g) {
		Point p = new Point();
		interpPoint(a, b, p, f, g);
		return p;
	}

	void interpPoint2(Point a, Point b, Point c, Point d, double f, double g) {
		int xpd = b.x - a.x;
		int ypd = b.y - a.y;
		int gx = b.y - a.y;
		int gy = a.x - b.x;
		g /= Math.sqrt(gx * gx + gy * gy);
		c.x = (int) Math.floor(a.x * (1 - f) + b.x * f + g * gx + .48);
		c.y = (int) Math.floor(a.y * (1 - f) + b.y * f + g * gy + .48);
		d.x = (int) Math.floor(a.x * (1 - f) + b.x * f - g * gx + .48);
		d.y = (int) Math.floor(a.y * (1 - f) + b.y * f - g * gy + .48);
	}

	Point[] newPointArray(int n) {
		Point a[] = new Point[n];
		while (n > 0)
			a[--n] = new Point();
		return a;
	}

	Polygon calcArrow(Point a, Point b, double al, double aw) {
		Polygon poly = new Polygon();
		Point p1 = new Point();
		Point p2 = new Point();
		int adx = b.x - a.x;
		int ady = b.y - a.y;
		double l = Math.sqrt(adx * adx + ady * ady);
		poly.addPoint(b.x, b.y);
		interpPoint2(a, b, p1, p2, 1 - al / l, aw);
		poly.addPoint(p1.x, p1.y);
		poly.addPoint(p2.x, p2.y);
		return poly;
	}

	Polygon calcArrowReverse(Point a, Point b, double al, double aw) {
		Polygon poly = new Polygon();
		Point p1 = new Point();
		Point p2 = new Point();
		double adx = b.x - a.x;
		double ady = b.y - a.y;
		double l = Math.sqrt(adx * adx + ady * ady);
		if (l > 0) {
			adx /= l;
			ady /= l;
			double bdx = -ady; // orthogonal unit vector
			double bdy = adx; //
			poly.addPoint((int) Math.round(b.x + 1 - adx * al), (int) Math.round(b.y + 1 - ady * al));
			poly.addPoint((int) Math.round(b.x + 1 - bdx * al), (int) Math.round(b.y + 1 - bdy * aw));
			poly.addPoint((int) Math.round(b.x + 1 + bdx * al), (int) Math.round(b.y + 1 + bdy * aw));
		}
		return poly;
	}

	Polygon createPolygon(Point a, Point b, Point c) {
		Polygon p = new Polygon();
		p.addPoint(a.x, a.y);
		p.addPoint(b.x, b.y);
		p.addPoint(c.x, c.y);
		return p;
	}

	Polygon createPolygon(Point a, Point b, Point c, Point d) {
		Polygon p = new Polygon();
		p.addPoint(a.x, a.y);
		p.addPoint(b.x, b.y);
		p.addPoint(c.x, c.y);
		p.addPoint(d.x, d.y);
		return p;
	}

	Polygon createPolygon(Point a[]) {
		Polygon p = new Polygon();
		int i;
		for (i = 0; i != a.length; i++)
			p.addPoint(a[i].x, a[i].y);
		return p;
	}

	public void drag(int xx, int yy) {
		xx = sim.snapGrid(xx);
		yy = sim.snapGrid(yy);
		if (noDiagonal) {
			if (Math.abs(getX() - xx) < Math.abs(getY() - yy)) {
				xx = getX();
			} else {
				yy = getY();
			}
		}
		setX2(xx);
		setY2(yy);
		setPoints();
	}

	public void stamp() {
	}

	/**
	 * @return 0
	 */
	public int getVoltageSourceCount() {
		return 0;
	}

	/**
	 * @return 0
	 */
	public int getInternalNodeCount() {
		return 0;
	}

	public void setNode(int p, int n) {
		nodes[p] = n;
	}

	public void setVoltageSource(int n, int v) {
		voltSource = v;
	}

	int getVoltageSource() {
		return voltSource;
	}

	public double getVoltageDiff() {
		return volts[0] - volts[1];
	}
	
	public double getVoltZero() {
		return volts[0];
	}
	
	public double getVoltOne() {
		return volts[1];
	}

	public boolean nonLinear() {
		return false;
	}

	/**
	 * @return 2
	 */
	public int getPostCount() {
		return 2;
	}

	public int getNode(int n) {
		return nodes[n];
	}

	/**
	 * If n = 0 returns point1 of the element; If n = 1 returns point2 of the
	 * element; If n > 1 returns null;
	 * 
	 * @param n
	 * @return
	 */
	public Point getPost(int n) {
		return (n == 0) ? point1 : (n == 1) ? point2 : null;
	}

	void setBbox(int x1, int y1, int x2, int y2) {
		if (x1 > x2) {
			int q = x1;
			x1 = x2;
			x2 = q;
		}
		if (y1 > y2) {
			int q = y1;
			y1 = y2;
			y2 = q;
		}
		boundingBox.setBounds(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
	}

	void setBbox(Point p1, Point p2, double w) {
		setBbox(p1.x, p1.y, p2.x, p2.y);
		int gx = p2.y - p1.y;
		int gy = p1.x - p2.x;
		int dpx = (int) (dpx1 * w);
		int dpy = (int) (dpy1 * w);
		adjustBbox(p1.x + dpx, p1.y + dpy, p1.x - dpx, p1.y - dpy);
	}

	void adjustBbox(int x1, int y1, int x2, int y2) {
		if (x1 > x2) {
			int q = x1;
			x1 = x2;
			x2 = q;
		}
		if (y1 > y2) {
			int q = y1;
			y1 = y2;
			y2 = q;
		}
		x1 = min(boundingBox.x, x1);
		y1 = min(boundingBox.y, y1);
		x2 = max(boundingBox.x + boundingBox.width - 1, x2);
		y2 = max(boundingBox.y + boundingBox.height - 1, y2);
		boundingBox.setBounds(x1, y1, x2 - x1, y2 - y1);
	}

	public boolean isCenteredText() {
		return false;
	}

	static String getVoltageDText(double v) {
		return getUnitText(Math.abs(v), "V");
	}

	public static String getVoltageText(double v) {
		return getUnitText(v, "V");
	}

	public static String getUnitText(double v, String u) {
		double va = Math.abs(v);
		if (va < 1e-14)
			return "0 " + u;
		if (va < 1e-9)
			return showFormat.format(v * 1e12) + " p" + u;
		if (va < 1e-6)
			return showFormat.format(v * 1e9) + " n" + u;
		if (va < 1e-3)
			return showFormat.format(v * 1e6) + " " + CirSim.getMuString() + u;
		if (va < 1)
			return showFormat.format(v * 1e3) + " m" + u;
		if (va < 1e3)
			return showFormat.format(v) + " " + u;
		if (va < 1e6)
			return showFormat.format(v * 1e-3) + " k" + u;
		if (va < 1e9)
			return showFormat.format(v * 1e-6) + " M" + u;
		return showFormat.format(v * 1e-9) + " G" + u;
	}

	public static String getCurrentText(double i) {
		return getUnitText(i, "A");
	}

	static String getCurrentDText(double i) {
		return getUnitText(Math.abs(i), "A");
	}

	public void getInfo(String arr[]) {
	}

	int getBasicInfo(String arr[]) {
		arr[1] = "I = " + getCurrentDText(getCurrent());
		arr[2] = "Vd = " + getVoltageDText(getVoltageDiff());
		return 3;
	}

	double getPower() {
		return getVoltageDiff() * current;
	}

	public double getScopeValue(int x) {
		return (x == 1) ? getPower() : getVoltageDiff();
	}

	public String getScopeUnits(int x) {
		return (x == 1) ? "W" : "V";
	}

	public boolean getConnection(int n1, int n2) {
		return true;
	}

	/**
	 * @return false
	 */
	public boolean hasGroundConnection(int n1) {
		return false;
	}

	public boolean isWire() {
		return false;
	}

	public boolean canViewInScope() {
		return getPostCount() <= 2;
	}

	boolean comparePair(int x1, int x2, int y1, int y2) {
		return ((x1 == y1 && x2 == y2) || (x1 == y2 && x2 == y1));
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean x) {
		selected = x;
	}

	public static int abs(int x) {
		return x < 0 ? -x : x;
	}

	public static int sign(int x) {
		return (x < 0) ? -1 : (x == 0) ? 0 : 1;
	}

	static int min(int a, int b) {
		return (a < b) ? a : b;
	}

	static int max(int a, int b) {
		return (a > b) ? a : b;
	}

	static double distance(Point p1, Point p2) {
		double x = p1.x - p2.x;
		double y = p1.y - p2.y;
		return Math.sqrt(x * x + y * y);
	}

	public Rectangle getBoundingBox() {
		return boundingBox;
	}
	
	public int getShortcut() {
		return 0;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getX2() {
		return x2;
	}

	public int setX2(int x2) {
		this.x2 = x2;
		return x2;
	}

	public void setX2Y2(int x2, int y2) {
		this.x2 = x2;
		this.y2 = y2;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getY2() {
		return y2;
	}

	public int setY2(int y2) {
		this.y2 = y2;
		return y2;
	}

	public String getId() {
		return id;
	}

	public String getIdLeft() {
		return idLeft;
	}

	public void setIdLeft(String idLeft) {
		this.idLeft = idLeft;
	}

	public String getIdRight() {
		return idRight;
	}

	public void setIdRight(String idRight) {
		this.idRight = idRight;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String toString() {
		return new ToStringBuilder(this).append("current", current).append("\nnodes", nodes).append("\npoint1", point1)
				.append("\npoint2", point2).append("\nvolts", volts).append("\nvoltSource", voltSource).toString();
	}
}
