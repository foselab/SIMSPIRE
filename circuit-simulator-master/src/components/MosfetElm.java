package components;

import java.awt.Point;
import java.awt.Polygon;
import java.util.StringTokenizer;

public class MosfetElm extends CircuitElm {
	int pnp;
	int FLAG_PNP = 1;
	int FLAG_SHOWVT = 2;
	int FLAG_DIGITAL = 4;
	double vt;

	MosfetElm(int xx, int yy, boolean pnpflag) {
		super(xx, yy);
		pnp = (pnpflag) ? -1 : 1;
		flags = (pnpflag) ? FLAG_PNP : 0;
		noDiagonal = true;
		vt = getDefaultThreshold();
	}

	public MosfetElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		pnp = ((f & FLAG_PNP) != 0) ? -1 : 1;
		noDiagonal = true;
		vt = getDefaultThreshold();
		try {
			vt = new Double(st.nextToken()).doubleValue();
		} catch (Exception e) {
		}
	}

	double getDefaultThreshold() {
		return 1.5;
	}

	double getBeta() {
		return .02;
	}

	@Override
	public boolean nonLinear() {
		return true;
	}

	boolean drawDigital() {
		return (flags & FLAG_DIGITAL) != 0;
	}

	@Override
	public void reset() {
		lastv1 = lastv2 = volts[0] = volts[1] = volts[2] = curcount = 0;
	}

	@Override
	public String dump() {
		return super.dump() + " " + vt;
	}

	@Override
	public int getDumpType() {
		return 'f';
	}

	final int hs = 16;

	@Override
	public Point getPost(int n) {
		return (n == 0) ? point1 : (n == 1) ? src[0] : drn[0];
	}

	@Override
	public double getCurrent() {
		return ids;
	}

	@Override
	double getPower() {
		return ids * (volts[2] - volts[1]);
	}

	@Override
	public int getPostCount() {
		return 3;
	}

	int pcircler;
	Point src[], drn[], gate[], pcircle;
	Polygon arrowPoly;

	@Override
	public void setPoints() {
		super.setPoints();

		// find the coordinates of the various points we need to draw
		// the MOSFET.
		int hs2 = hs * dsign;
		src = newPointArray(3);
		drn = newPointArray(3);
		interpPoint2(point1, point2, src[0], drn[0], 1, -hs2);
		interpPoint2(point1, point2, src[1], drn[1], 1 - 22 / dn, -hs2);
		interpPoint2(point1, point2, src[2], drn[2], 1 - 22 / dn, -hs2 * 4 / 3);

		gate = newPointArray(3);
		interpPoint2(point1, point2, gate[0], gate[2], 1 - 28 / dn, hs2 / 2); // was 1-20/dn
		interpPoint(gate[0], gate[2], gate[1], .5);

		if (!drawDigital()) {
		} else if (pnp == -1) {
			interpPoint(point1, point2, gate[1], 1 - 36 / dn);
			int dist = (dsign < 0) ? 32 : 31;
			pcircle = interpPoint(point1, point2, 1 - dist / dn);
			pcircler = 3;
		}
	}

	double lastv1, lastv2;
	double ids;
	int mode = 0;
	double gm = 0;

	@Override
	public void stamp() {
		sim.stampNonLinear(nodes[1]);
		sim.stampNonLinear(nodes[2]);
	}

	@Override
	public void doStep() {
		double vs[] = new double[3];
		vs[0] = volts[0];
		vs[1] = volts[1];
		vs[2] = volts[2];
		if (vs[1] > lastv1 + .5)
			vs[1] = lastv1 + .5;
		if (vs[1] < lastv1 - .5)
			vs[1] = lastv1 - .5;
		if (vs[2] > lastv2 + .5)
			vs[2] = lastv2 + .5;
		if (vs[2] < lastv2 - .5)
			vs[2] = lastv2 - .5;
		int source = 1;
		int drain = 2;
		if (pnp * vs[1] > pnp * vs[2]) {
			source = 2;
			drain = 1;
		}
		int gate = 0;
		double vgs = vs[gate] - vs[source];
		double vds = vs[drain] - vs[source];
		if (Math.abs(lastv1 - vs[1]) > .01 || Math.abs(lastv2 - vs[2]) > .01)
			sim.setConverged(false);
		lastv1 = vs[1];
		lastv2 = vs[2];
		double realvgs = vgs;
		double realvds = vds;
		vgs *= pnp;
		vds *= pnp;
		ids = 0;
		gm = 0;
		double Gds = 0;
		double beta = getBeta();
		if (vgs > .5 && this instanceof JfetElm) {
			sim.stop("JFET is reverse biased!", this);
			return;
		}
		if (vgs < vt) {
			// should be all zero, but that causes a singular matrix,
			// so instead we treat it as a large resistor
			Gds = 1e-8;
			ids = vds * Gds;
			mode = 0;
		} else if (vds < vgs - vt) {
			// linear
			ids = beta * ((vgs - vt) * vds - vds * vds * .5);
			gm = beta * vds;
			Gds = beta * (vgs - vds - vt);
			mode = 1;
		} else {
			// saturation; Gds = 0
			gm = beta * (vgs - vt);
			// use very small Gds to avoid nonconvergence
			Gds = 1e-8;
			ids = .5 * beta * (vgs - vt) * (vgs - vt) + (vds - (vgs - vt)) * Gds;
			mode = 2;
		}
		double rs = -pnp * ids + Gds * realvds + gm * realvgs;
		// System.out.println("M " + vds + " " + vgs + " " + ids + " " + gm + " "+ Gds +
		// " " + volts[0] + " " + volts[1] + " " + volts[2] + " " + source + " " + rs +
		// " " + this);
		sim.stampMatrix(nodes[drain], nodes[drain], Gds);
		sim.stampMatrix(nodes[drain], nodes[source], -Gds - gm);
		sim.stampMatrix(nodes[drain], nodes[gate], gm);

		sim.stampMatrix(nodes[source], nodes[drain], -Gds);
		sim.stampMatrix(nodes[source], nodes[source], Gds + gm);
		sim.stampMatrix(nodes[source], nodes[gate], -gm);

		sim.stampRightSide(nodes[drain], rs);
		sim.stampRightSide(nodes[source], -rs);
		if (source == 2 && pnp == 1 || source == 1 && pnp == -1)
			ids = -ids;
	}

	void getFetInfo(String arr[], String n) {
		arr[0] = ((pnp == -1) ? "p-" : "n-") + n;
		arr[0] += " (Vt = " + getVoltageText(pnp * vt) + ")";
		arr[1] = ((pnp == 1) ? "Ids = " : "Isd = ") + getCurrentText(ids);
		arr[2] = "Vgs = " + getVoltageText(volts[0] - volts[pnp == -1 ? 2 : 1]);
		arr[3] = ((pnp == 1) ? "Vds = " : "Vsd = ") + getVoltageText(volts[2] - volts[1]);
		arr[4] = (mode == 0) ? "off" : (mode == 1) ? "linear" : "saturation";
		arr[5] = "gm = " + getUnitText(gm, "A/V");
	}

	@Override
	public void getInfo(String arr[]) {
		getFetInfo(arr, "MOSFET");
	}

	@Override
	public boolean canViewInScope() {
		return true;
	}

	@Override
	public double getVoltageDiff() {
		return volts[2] - volts[1];
	}

	@Override
	public boolean getConnection(int n1, int n2) {
		return !(n1 == 0 || n2 == 0);
	}
}
