package components;

import java.awt.Point;
import java.awt.Polygon;
import java.util.StringTokenizer;

import simulator.CirSim;

public class SparkGapElm extends CircuitElm {
	double resistance, onresistance, offresistance, breakdown, holdcurrent;
	boolean state;

	public SparkGapElm(int xx, int yy) {
		super(xx, yy);
		offresistance = 1e9;
		onresistance = 1e3;
		breakdown = 1e3;
		holdcurrent = 0.001;
		state = false;
	}

	public SparkGapElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		onresistance = new Double(st.nextToken()).doubleValue();
		offresistance = new Double(st.nextToken()).doubleValue();
		breakdown = new Double(st.nextToken()).doubleValue();
		holdcurrent = new Double(st.nextToken()).doubleValue();
	}

	@Override
	public boolean nonLinear() {
		return true;
	}

	@Override
	public int getDumpType() {
		return 187;
	}

	@Override
	public String dump() {
		return super.dump() + " " + onresistance + " " + offresistance + " " + breakdown + " " + holdcurrent;
	}

	Polygon arrow1, arrow2;

	@Override
	public void setPoints() {
		super.setPoints();
		int dist = 16;
		int alen = 8;
		calcLeads(dist + alen);
		Point p1 = interpPoint(point1, point2, (dn - alen) / (2 * dn));
		arrow1 = calcArrow(point1, p1, alen, alen);
		p1 = interpPoint(point1, point2, (dn + alen) / (2 * dn));
		arrow2 = calcArrow(point2, p1, alen, alen);
	}

	@Override
	void calculateCurrent() {
		double vd = volts[0] - volts[1];
		current = vd / resistance;
	}

	@Override
	public void reset() {
		super.reset();
		state = false;
	}

	@Override
	public void startIteration() {
		if (Math.abs(current) < holdcurrent)
			state = false;
		double vd = volts[0] - volts[1];
		if (Math.abs(vd) > breakdown)
			state = true;
	}

	@Override
	public void doStep() {
		resistance = (state) ? onresistance : offresistance;
		sim.stampResistor(nodes[0], nodes[1], resistance);
	}

	@Override
	public void stamp() {
		sim.stampNonLinear(nodes[0]);
		sim.stampNonLinear(nodes[1]);
	}

	@Override
	public void getInfo(String arr[]) {
		arr[0] = "spark gap";
		getBasicInfo(arr);
		arr[3] = state ? "on" : "off";
		arr[4] = "Ron = " + getUnitText(onresistance, CirSim.getOhmString());
		arr[5] = "Roff = " + getUnitText(offresistance, CirSim.getOhmString());
		arr[6] = "Vbreakdown = " + getUnitText(breakdown, "V");
	}
}
