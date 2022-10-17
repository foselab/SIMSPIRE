package components;

import java.awt.Label;
import java.awt.Point;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.StringTokenizer;

import simulator.CirSim;

public class PotElm extends CircuitElm implements AdjustmentListener {
	double position, maxResistance, resistance1, resistance2;
	double current1, current2, current3;
	double curcount1, curcount2, curcount3;
	Scrollbar slider;
	Label label;
	String sliderText;

	public PotElm(int xx, int yy) {
		super(xx, yy);
		setup();
		maxResistance = 1000;
		position = .5;
		sliderText = "Resistance";
	}

	public PotElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		maxResistance = new Double(st.nextToken()).doubleValue();
		position = new Double(st.nextToken()).doubleValue();
		sliderText = st.nextToken();
		while (st.hasMoreTokens())
			sliderText += ' ' + st.nextToken();
	}

	void setup() {
	}

	@Override
	public int getPostCount() {
		return 3;
	}

	@Override
	public int getDumpType() {
		return 174;
	}

	@Override
	public Point getPost(int n) {
		return (n == 0) ? point1 : (n == 1) ? point2 : post3;
	}

	@Override
	public String dump() {
		return super.dump() + " " + maxResistance + " " + position + " " + sliderText;
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		sim.setAnalyzeFlag(true);
		setPoints();
	}

	Point post3, corner2, arrowPoint, midpoint, arrow1, arrow2;
	Point ps3, ps4;
	int bodyLen;

	@Override
	public void setPoints() {
		super.setPoints();
		int offset = 0;
		if (abs(dx) > abs(dy)) {
			dx = sim.snapGrid(dx / 2) * 2;
			point2.x = setX2(point1.x + dx);
			offset = (dx < 0) ? dy : -dy;
			point2.y = point1.y;
		} else {
			dy = sim.snapGrid(dy / 2) * 2;
			point2.y = setY2(point1.y + dy);
			offset = (dy > 0) ? dx : -dx;
			point2.x = point1.x;
		}
		if (offset == 0)
			offset = sim.getGridSize();
		dn = distance(point1, point2);
		int bodyLen = 32;
		calcLeads(bodyLen);
		position = slider.getValue() * .0099 + .005;
		int soff = (int) ((position - .5) * bodyLen);
		// int offset2 = offset - sign(offset)*4;
		post3 = interpPoint(point1, point2, .5, offset);
		corner2 = interpPoint(point1, point2, soff / dn + .5, offset);
		arrowPoint = interpPoint(point1, point2, soff / dn + .5, 8 * sign(offset));
		midpoint = interpPoint(point1, point2, soff / dn + .5);
		arrow1 = new Point();
		arrow2 = new Point();
		double clen = abs(offset) - 8;
		interpPoint2(corner2, arrowPoint, arrow1, arrow2, (clen - 8) / clen, 8);
		ps3 = new Point();
		ps4 = new Point();
	}

	@Override
	void calculateCurrent() {
		current1 = (volts[0] - volts[2]) / resistance1;
		current2 = (volts[1] - volts[2]) / resistance2;
		current3 = -current1 - current2;
	}

	@Override
	public void stamp() {
		resistance1 = maxResistance * position;
		resistance2 = maxResistance * (1 - position);
		sim.stampResistor(nodes[0], nodes[2], resistance1);
		sim.stampResistor(nodes[2], nodes[1], resistance2);
	}

	@Override
	public void getInfo(String arr[]) {
		arr[0] = "potentiometer";
		arr[1] = "Vd = " + getVoltageDText(getVoltageDiff());
		arr[2] = "R1 = " + getUnitText(resistance1, CirSim.getOhmString());
		arr[3] = "R2 = " + getUnitText(resistance2, CirSim.getOhmString());
		arr[4] = "I1 = " + getCurrentDText(current1);
		arr[5] = "I2 = " + getCurrentDText(current2);
	}

	@Override
	public void doStep() {
		// TODO Auto-generated method stub
		
	}
}
