package components;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.StringTokenizer;

import utils.EditInfo;

public class CurrentElm extends CircuitElm {
	double currentValue;

	public CurrentElm(int xx, int yy) {
		super(xx, yy);
		currentValue = .01;
	}

	public CurrentElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		try {
			currentValue = new Double(st.nextToken()).doubleValue();
		} catch (Exception e) {
			currentValue = .01;
		}
	}

	@Override
	public String dump() {
		return super.dump() + " " + currentValue;
	}

	@Override
	public int getDumpType() {
		return 'i';
	}

	Polygon arrow;
	Point ashaft1, ashaft2, center;

	@Override
	public void setPoints() {
		super.setPoints();
		calcLeads(26);
		ashaft1 = interpPoint(lead1, lead2, .25);
		ashaft2 = interpPoint(lead1, lead2, .6);
		center = interpPoint(lead1, lead2, .5);
		Point p2 = interpPoint(lead1, lead2, .75);
		arrow = calcArrow(center, p2, 4, 4);
	}

	@Override
	public void stamp() {
		current = currentValue;
		sim.stampCurrentSource(nodes[0], nodes[1], current);
	}

	@Override
	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return new EditInfo("Current (A)", currentValue, 0, .1);
		return null;
	}

	@Override
	public void setEditValue(int n, EditInfo ei) {
		currentValue = ei.getValue();
	}

	@Override
	public void getInfo(String arr[]) {
		arr[0] = "current source";
		getBasicInfo(arr);
	}

	@Override
	public double getVoltageDiff() {
		return volts[1] - volts[0];
	}

	@Override
	public void doStep() {
		// TODO Auto-generated method stub
		
	}
}
