package components;

import java.awt.Checkbox;
import java.awt.Graphics;
import java.util.StringTokenizer;

import utils.EditInfo;

public class InductorElm extends CircuitElm {
	Inductor ind;
	private double inductance;

	public InductorElm(int xx, int yy) {
		super(xx, yy);
		ind = new Inductor(sim);
		setInductance(1);
		ind.setup(getInductance(), current, flags);
	}

	public InductorElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		ind = new Inductor(sim);
		setInductance(new Double(st.nextToken()).doubleValue());
		current = new Double(st.nextToken()).doubleValue();
		ind.setup(getInductance(), current, flags);
	}

	@Override
	public int getDumpType() {
		return 'l';
	}

	@Override
	public String dump() {
		return super.dump() + " " + getInductance() + " " + current;
	}

	@Override
	public void setPoints() {
		super.setPoints();
		calcLeads(32);
	}

	@Override
	public void reset() {
		current = volts[0] = volts[1] = curcount = 0;
		ind.reset();
	}

	@Override
	public void stamp() {
		ind.stamp(nodes[0], nodes[1]);
	}

	@Override
	public void startIteration() {
		ind.startIteration(volts[0] - volts[1]);
	}

	@Override
	public boolean nonLinear() {
		return ind.nonLinear();
	}

	@Override
	void calculateCurrent() {
		double voltdiff = volts[0] - volts[1];
		current = ind.calculateCurrent(voltdiff);
	}

	@Override
	public void doStep() {
		double voltdiff = volts[0] - volts[1];
		ind.doStep(voltdiff);
	}

	@Override
	public void getInfo(String arr[]) {
		arr[0] = "inductor";
		getBasicInfo(arr);
		arr[3] = "L = " + getUnitText(getInductance(), "H");
		arr[4] = "P = " + getUnitText(getPower(), "W");
	}

	@Override
	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return new EditInfo("Inductance (H)", getInductance(), 0, 0);
		if (n == 1) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.setCheckbox(new Checkbox("Trapezoidal Approximation", ind.isTrapezoidal()));
			return ei;
		}
		return null;
	}

	@Override
	public void setEditValue(int n, EditInfo ei) {
		if (n == 0)
			setInductance(ei.getValue());
		if (n == 1) {
			if (ei.getCheckbox().getState())
				flags &= ~Inductor.FLAG_BACK_EULER;
			else
				flags |= Inductor.FLAG_BACK_EULER;
		}
		ind.setup(getInductance(), current, flags);
	}

	public double getInductance() {
		return inductance;
	}

	public void setInductance(double inductance) {
		this.inductance = inductance;
	}
}
