package components;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.StringTokenizer;

public class JfetElm extends MosfetElm {
	JfetElm(int xx, int yy, boolean pnpflag) {
		super(xx, yy, pnpflag);
		noDiagonal = true;
	}

	public JfetElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
		noDiagonal = true;
	}

	Polygon gatePoly;
	Polygon arrowPoly;
	Point gatePt;

	@Override
	public void setPoints() {
		super.setPoints();

		// find the coordinates of the various points we need to draw
		// the JFET.
		int hs2 = hs * dsign;
		src = newPointArray(3);
		drn = newPointArray(3);
		interpPoint2(point1, point2, src[0], drn[0], 1, hs2);
		interpPoint2(point1, point2, src[1], drn[1], 1, hs2 / 2);
		interpPoint2(point1, point2, src[2], drn[2], 1 - 10 / dn, hs2 / 2);

		gatePt = interpPoint(point1, point2, 1 - 14 / dn);

		Point ra[] = newPointArray(4);
		interpPoint2(point1, point2, ra[0], ra[1], 1 - 13 / dn, hs);
		interpPoint2(point1, point2, ra[2], ra[3], 1 - 10 / dn, hs);
		gatePoly = createPolygon(ra[0], ra[1], ra[3], ra[2]);
		if (pnp == -1) {
			Point x = interpPoint(gatePt, point1, 18 / dn);
			arrowPoly = calcArrow(gatePt, x, 8, 3);
		} else
			arrowPoly = calcArrow(point1, gatePt, 8, 3);
	}

	@Override
	public int getDumpType() {
		return 'j';
	}

	// these values are taken from Hayes+Horowitz p155
	@Override
	double getDefaultThreshold() {
		return -4;
	}

	@Override
	double getBeta() {
		return .00125;
	}

	@Override
	public void getInfo(String arr[]) {
		getFetInfo(arr, "JFET");
	}
}
