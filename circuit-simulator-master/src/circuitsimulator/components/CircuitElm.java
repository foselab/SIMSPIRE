package circuitsimulator.components;

import java.awt.Point;
import java.text.NumberFormat;

import org.apache.commons.lang3.builder.ToStringBuilder;

import circuitsimulator.simulator.CirSim;

public abstract class CircuitElm {
	private String id;
	private String idLeft;
	private String idRight;
	private double value;
	private String unit;
	public static CirSim sim;

	public static NumberFormat showFormat;
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
	}

	CircuitElm(int xa, int ya, int xb, int yb, int f) {
		setX(xa);
		setY(ya);
		setX2(xb);
		setY2(yb);
		flags = f;
		allocNodes();
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

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}
