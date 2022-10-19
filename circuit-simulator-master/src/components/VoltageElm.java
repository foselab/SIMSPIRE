package components;

import java.util.List;
import java.util.StringTokenizer;

public class VoltageElm extends CircuitElm {
	static final int FLAG_COS = 2;
	int waveform;
	static final int WF_DC = 0;
	static final int WF_AC = 1;
	static final int WF_SQUARE = 2;
	static final int WF_TRIANGLE = 3;
	static final int WF_SAWTOOTH = 4;
	static final int WF_PULSE = 5;
	static final int WF_VAR = 6;
	
	static final int WF_ZMQ = 28;
	
	double frequency;
	private double maxVoltage;
	private double ventVoltage;
	double freqTimeZero;
	double bias;
	double phaseShift;
	double dutyCycle;
	
	List<String> variables;
	
	VoltageElm(int xx, int yy, int wf) {
		super(xx, yy);
		waveform = wf;
		setMaxVoltage(5);
		frequency = 40;
		dutyCycle = .5;
		reset();
	}

	public VoltageElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		setMaxVoltage(5);
		frequency = 40;
		waveform = WF_DC;
		dutyCycle = .5;
		try {
			waveform = new Integer(st.nextToken()).intValue();
			frequency = new Double(st.nextToken()).doubleValue();
			setMaxVoltage(new Double(st.nextToken()).doubleValue());
			bias = new Double(st.nextToken()).doubleValue();
			phaseShift = new Double(st.nextToken()).doubleValue();
			dutyCycle = new Double(st.nextToken()).doubleValue();
		} catch (Exception e) {
		}
		if ((flags & FLAG_COS) != 0) {
			flags &= ~FLAG_COS;
			phaseShift = pi / 2;
		}
		reset();
	}

	@Override
	public int getDumpType() {
		return 'v';
	}

	@Override
	public String dump() {
		return super.dump() + " " + waveform + " " + frequency + " " + getMaxVoltage() + " " + bias + " " + phaseShift + " "
				+ dutyCycle;
	}
	/*
	 * void setCurrent(double c) { current = c; System.out.print("v current set to "
	 * + c + "\n"); }
	 */

	@Override
	public void reset() {
		freqTimeZero = 0;
		curcount = 0;
	}

	double triangleFunc(double x) {
		if (x < pi)
			return x * (2 / pi) - 1;
		return 1 - (x - pi) * (2 / pi);
	}

	@Override
	public void stamp() {
		System.out.println("nodes[0]: " + nodes[0] + " nodes[1]: " + nodes[1] + " voltSource: " + voltSource);
		if (waveform == WF_DC)
			sim.stampVoltageSource(nodes[0], nodes[1], voltSource, getVoltage());
		else
			sim.stampVoltageSource(nodes[0], nodes[1], voltSource);
	}

	@Override
	public void doStep() {
		if (waveform != WF_DC)
			sim.updateVoltageSource(nodes[0], nodes[1], voltSource, getVoltage());
	}

	double getVoltage() {
		double w = 2 * pi * (sim.getT() - freqTimeZero) * frequency + phaseShift;
		switch (waveform) {
		case WF_DC:
			return getMaxVoltage() + bias;
		case WF_AC:
			return Math.sin(w) * getMaxVoltage() + bias;
		case WF_SQUARE:
			return bias + ((w % (2 * pi) > (2 * pi * dutyCycle)) ? -getMaxVoltage() : getMaxVoltage());
		case WF_TRIANGLE:
			return bias + triangleFunc(w % (2 * pi)) * getMaxVoltage();
		case WF_SAWTOOTH:
			return bias + (w % (2 * pi)) * (getMaxVoltage() / pi) - getMaxVoltage();
		case WF_PULSE:
			return ((w % (2 * pi)) < 1) ? getMaxVoltage() + bias : bias;
		case WF_ZMQ:
			return ventVoltage;
		default:
			return 0;
		}
	}

	final int circleSize = 17;

	@Override
	public void setPoints() {
		super.setPoints();
		calcLeads((waveform == WF_DC || waveform == WF_VAR) ? 8 : circleSize * 2);
	}

	@Override
	public int getVoltageSourceCount() {
		return 1;
	}

	@Override
	public double getPower() {
		return -getVoltageDiff() * current;
	}

	@Override
	public double getVoltageDiff() {
		return volts[1] - volts[0];
	}

	@Override
	public void getInfo(String arr[]) {
		switch (waveform) {
		case WF_DC:
		case WF_VAR:
			arr[0] = "voltage source";
			break;
		case WF_AC:
			arr[0] = "A/C source";
			break;
		case WF_SQUARE:
			arr[0] = "square wave gen";
			break;
		case WF_PULSE:
			arr[0] = "pulse gen";
			break;
		case WF_SAWTOOTH:
			arr[0] = "sawtooth gen";
			break;
		case WF_TRIANGLE:
			arr[0] = "triangle gen";
			break;
		}
		arr[1] = "I = " + getCurrentText(getCurrent());
		arr[2] = ((this instanceof RailElm) ? "V = " : "Vd = ") + getVoltageText(getVoltageDiff());
		if (waveform != WF_DC && waveform != WF_VAR) {
			arr[3] = "f = " + getUnitText(frequency, "Hz");
			arr[4] = "Vmax = " + getVoltageText(getMaxVoltage());
			int i = 5;
			if (bias != 0)
				arr[i++] = "Voff = " + getVoltageText(bias);
			else if (frequency > 500)
				arr[i++] = "wavelength = " + getUnitText(2.9979e8 / frequency, "m");
			arr[i++] = "P = " + getUnitText(getPower(), "W");
		}
	}

	public double getMaxVoltage() {
		return maxVoltage;
	}

	public double getVentVoltage() {
		return ventVoltage;
	}

	public void setVentVoltage(double ventVoltage) {
		this.ventVoltage = ventVoltage;
	}

	public void setMaxVoltage(double maxVoltage) {
		this.maxVoltage = maxVoltage;
	}
}
