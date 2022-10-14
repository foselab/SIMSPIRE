package components;

import java.awt.Choice;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;
import java.util.StringTokenizer;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

import utils.EditInfo;

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
	
	//TODO nuove waveform
	static final int WF_FORMULA = 23;
	static final int WF_ZMQ = 28;
	
	ZContext context;
	Socket socket;
	
	double frequency;
	private double maxVoltage;
	double freqTimeZero;
	double bias;
	double phaseShift;
	double dutyCycle;
	
	List<String> variables;
	
	VoltageElm(int xx, int yy, int wf) {
		super(xx, yy);
		waveform = wf;
		
		//Aggiungo inizializzazione ZMQ se necessario
		if(wf == WF_ZMQ) {
			context = new ZContext();
			socket = context.createSocket(SocketType.REQ);
			socket.connect("tcp://localhost:5555");
		}
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
		case WF_FORMULA:
			return -1; //TODO calcolo della formula all'istante t
		case WF_ZMQ:
			// chiamo zmq
			String message = "getPressure";
			socket.send(message.getBytes(), 0);
			byte[] reply = socket.recv(0);
			if (reply != null) {
				String msg = new String(reply, ZMQ.CHARSET);
				System.out.println("[ZMQ Received]: " + msg);
				return Double.parseDouble(msg);
			}
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

	void drawWaveform(Graphics g, Point center) {
		g.setColor(needsHighlight() ? getSelectColor() : Color.gray);
		int xc = center.x;
		int yc = center.y;
		drawThickCircle(g, xc, yc, circleSize);
		int wl = 8;
		adjustBbox(xc - circleSize, yc - circleSize, xc + circleSize, yc + circleSize);
		int xc2;
		switch (waveform) {
		case WF_DC: {
			break;
		}
		case WF_SQUARE:
			xc2 = (int) (wl * 2 * dutyCycle - wl + xc);
			xc2 = max(xc - wl + 3, min(xc + wl - 3, xc2));
			drawThickLine(g, xc - wl, yc - wl, xc - wl, yc);
			drawThickLine(g, xc - wl, yc - wl, xc2, yc - wl);
			drawThickLine(g, xc2, yc - wl, xc2, yc + wl);
			drawThickLine(g, xc + wl, yc + wl, xc2, yc + wl);
			drawThickLine(g, xc + wl, yc, xc + wl, yc + wl);
			break;
		case WF_PULSE:
			yc += wl / 2;
			drawThickLine(g, xc - wl, yc - wl, xc - wl, yc);
			drawThickLine(g, xc - wl, yc - wl, xc - wl / 2, yc - wl);
			drawThickLine(g, xc - wl / 2, yc - wl, xc - wl / 2, yc);
			drawThickLine(g, xc - wl / 2, yc, xc + wl, yc);
			break;
		case WF_SAWTOOTH:
			drawThickLine(g, xc, yc - wl, xc - wl, yc);
			drawThickLine(g, xc, yc - wl, xc, yc + wl);
			drawThickLine(g, xc, yc + wl, xc + wl, yc);
			break;
		case WF_TRIANGLE: {
			int xl = 5;
			drawThickLine(g, xc - xl * 2, yc, xc - xl, yc - wl);
			drawThickLine(g, xc - xl, yc - wl, xc, yc);
			drawThickLine(g, xc, yc, xc + xl, yc + wl);
			drawThickLine(g, xc + xl, yc + wl, xc + xl * 2, yc);
			break;
		}
		case WF_AC: {
			int i;
			int xl = 10;
			int ox = -1, oy = -1;
			for (i = -xl; i <= xl; i++) {
				int yy = yc + (int) (.95 * Math.sin(i * pi / xl) * wl);
				if (ox != -1)
					drawThickLine(g, ox, oy, xc + i, yy);
				ox = xc + i;
				oy = yy;
			}
			break;
		}
		}
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

	@Override
	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return new EditInfo(waveform == WF_DC ? "Voltage" : "Max Voltage", getMaxVoltage(), -20, 20);
		if (n == 1) {
			EditInfo ei = new EditInfo("Waveform", waveform, -1, -1);
			ei.setChoice(new Choice());
			ei.getChoice().add("D/C");
			ei.getChoice().add("A/C");
			ei.getChoice().add("Square Wave");
			ei.getChoice().add("Triangle");
			ei.getChoice().add("Sawtooth");
			ei.getChoice().add("Pulse");
			ei.getChoice().select(waveform);
			return ei;
		}
		if (waveform == WF_DC)
			return null;
		if (n == 2)
			return new EditInfo("Frequency (Hz)", frequency, 4, 500);
		if (n == 3)
			return new EditInfo("DC Offset (V)", bias, -20, 20);
		if (n == 4)
			return new EditInfo("Phase Offset (degrees)", phaseShift * 180 / pi, -180, 180).setDimensionless();
		if (n == 5 && waveform == WF_SQUARE)
			return new EditInfo("Duty Cycle", dutyCycle * 100, 0, 100).setDimensionless();
		return null;
	}

	@Override
	public void setEditValue(int n, EditInfo ei) {
		if (n == 0)
			setMaxVoltage(ei.getValue());
		if (n == 3)
			bias = ei.getValue();
		if (n == 2) {
			// adjust time zero to maintain continuity ind the waveform
			// even though the frequency has changed.
			double oldfreq = frequency;
			frequency = ei.getValue();
			double maxfreq = 1 / (8 * sim.getTimeStep());
			if (frequency > maxfreq)
				frequency = maxfreq;
			double adj = frequency - oldfreq;
			freqTimeZero = sim.getT() - oldfreq * (sim.getT() - freqTimeZero) / frequency;
		}
		if (n == 1) {
			int ow = waveform;
			waveform = ei.getChoice().getSelectedIndex();
			if (waveform == WF_DC && ow != WF_DC) {
				ei.setNewDialog(true);
				bias = 0;
			} else if (waveform != WF_DC && ow == WF_DC) {
				ei.setNewDialog(true);
			}
			if ((waveform == WF_SQUARE || ow == WF_SQUARE) && waveform != ow)
				ei.setNewDialog(true);
			setPoints();
		}
		if (n == 4)
			phaseShift = ei.getValue() * pi / 180;
		if (n == 5)
			dutyCycle = ei.getValue() * .01;
	}

	public double getMaxVoltage() {
		return maxVoltage;
	}

	public void setMaxVoltage(double maxVoltage) {
		this.maxVoltage = maxVoltage;
	}
}
