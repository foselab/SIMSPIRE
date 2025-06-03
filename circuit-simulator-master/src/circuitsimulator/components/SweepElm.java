package circuitsimulator.components;

import java.util.StringTokenizer;

public class SweepElm extends CircuitElm {
	double maxV, maxF, minF, sweepTime, frequency;
	final int FLAG_LOG = 1;
	final int FLAG_BIDIR = 2;

	public SweepElm(int xx, int yy) {
		super(xx, yy);
		minF = 20;
		maxF = 4000;
		maxV = 5;
		sweepTime = .1;
		flags = FLAG_BIDIR;
		reset();
	}

	public SweepElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		minF = new Double(st.nextToken()).doubleValue();
		maxF = new Double(st.nextToken()).doubleValue();
		maxV = new Double(st.nextToken()).doubleValue();
		sweepTime = new Double(st.nextToken()).doubleValue();
		reset();
	}

	@Override
	public int getDumpType() {
		return 170;
	}

	@Override
	public int getPostCount() {
		return 1;
	}

	final int circleSize = 17;

	@Override
	public String dump() {
		return super.dump() + " " + minF + " " + maxF + " " + maxV + " " + sweepTime;
	}

	@Override
	public void setPoints() {
		super.setPoints();
		lead1 = interpPoint(point1, point2, 1 - circleSize / dn);
	}

	@Override
	public void stamp() {
		sim.stampVoltageSource(0, nodes[0], voltSource);
	}

	double fadd, fmul, freqTime, savedTimeStep;
	int dir = 1;

	void setParams() {
		if (frequency < minF || frequency > maxF) {
			frequency = minF;
			freqTime = 0;
			dir = 1;
		}
		if ((flags & FLAG_LOG) == 0) {
			fadd = dir * sim.getTimeStep() * (maxF - minF) / sweepTime;
			fmul = 1;
		} else {
			fadd = 0;
			fmul = Math.pow(maxF / minF, dir * sim.getTimeStep() / sweepTime);
		}
		savedTimeStep = sim.getTimeStep();
	}

	@Override
	public void reset() {
		frequency = minF;
		freqTime = 0;
		dir = 1;
		setParams();
	}

	double v;

	@Override
	public void startIteration() {
		// has timestep been changed?
		if (sim.getTimeStep() != savedTimeStep)
			setParams();
		v = Math.sin(freqTime) * maxV;
		freqTime += frequency * 2 * pi * sim.getTimeStep();
		frequency = frequency * fmul + fadd;
		if (frequency >= maxF && dir == 1) {
			if ((flags & FLAG_BIDIR) != 0) {
				fadd = -fadd;
				fmul = 1 / fmul;
				dir = -1;
			} else
				frequency = minF;
		}
		if (frequency <= minF && dir == -1) {
			fadd = -fadd;
			fmul = 1 / fmul;
			dir = 1;
		}
	}

	@Override
	public void doStep() {
		sim.updateVoltageSource(0, nodes[0], voltSource, v);
	}

	@Override
	public double getVoltageDiff() {
		return volts[0];
	}

	@Override
	public int getVoltageSourceCount() {
		return 1;
	}

	@Override
	public boolean hasGroundConnection(int n1) {
		return true;
	}

	@Override
	public void getInfo(String arr[]) {
		arr[0] = "sweep " + (((flags & FLAG_LOG) == 0) ? "(linear)" : "(log)");
		arr[1] = "I = " + getCurrentDText(getCurrent());
		arr[2] = "V = " + getVoltageText(volts[0]);
		arr[3] = "f = " + getUnitText(frequency, "Hz");
		arr[4] = "range = " + getUnitText(minF, "Hz") + " .. " + getUnitText(maxF, "Hz");
		arr[5] = "time = " + getUnitText(sweepTime, "s");
	}
}
