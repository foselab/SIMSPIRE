package circuitsimulator.components;

import java.awt.Label;
import java.awt.Scrollbar;
import java.util.StringTokenizer;

public class VarRailElm extends RailElm {
	Scrollbar slider;
	Label label;
	String sliderText;

	public VarRailElm(int xx, int yy) {
		super(xx, yy, WF_VAR);
		sliderText = "Voltage";
		frequency = getMaxVoltage();
	}

	public VarRailElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
		sliderText = st.nextToken();
		while (st.hasMoreTokens())
			sliderText += ' ' + st.nextToken();
	}

	@Override
	public String dump() {
		return super.dump() + " " + sliderText;
	}

	@Override
	public int getDumpType() {
		return 172;
	}

	@Override
	double getVoltage() {
		frequency = slider.getValue() * (getMaxVoltage() - bias) / 100. + bias;
		return frequency;
	}

	@Override
	public int getShortcut() {
		return 0;
	}
}
