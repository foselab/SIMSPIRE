package circuitsimulator.components;

import java.util.StringTokenizer;

public class DACElm extends ChipElm {
	public DACElm(int xx, int yy) {
		super(xx, yy);
	}

	public DACElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
	}

	@Override
	String getChipName() {
		return "DAC";
	}

	@Override
	boolean needsBits() {
		return true;
	}

	@Override
	void setupPins() {
		sizeX = 2;
		sizeY = bits > 2 ? bits : 2;
		pins = new Pin[getPostCount()];
		int i;
		for (i = 0; i != bits; i++)
			pins[i] = new Pin(bits - 1 - i, SIDE_W, "D" + i);
		pins[bits] = new Pin(0, SIDE_E, "O");
		pins[bits].output = true;
		pins[bits + 1] = new Pin(sizeY - 1, SIDE_E, "V+");
		allocNodes();
	}

	@Override
	public void doStep() {
		int ival = 0;
		int i;
		for (i = 0; i != bits; i++)
			if (volts[i] > 2.5)
				ival |= 1 << i;
		int ivalmax = (1 << bits) - 1;
		double v = ival * volts[bits + 1] / ivalmax;
		sim.updateVoltageSource(0, nodes[bits], pins[bits].voltSource, v);
	}

	@Override
	public int getVoltageSourceCount() {
		return 1;
	}

	@Override
	public int getPostCount() {
		return bits + 2;
	}

	@Override
	public int getDumpType() {
		return 166;
	}
}
