package components;

import java.util.StringTokenizer;

public class DFlipFlopElm extends ChipElm {
	final int FLAG_RESET = 2;

	boolean hasReset() {
		return (flags & FLAG_RESET) != 0;
	}

	public DFlipFlopElm(int xx, int yy) {
		super(xx, yy);
	}

	public DFlipFlopElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
		pins[2].value = !pins[1].value;
	}

	@Override
	String getChipName() {
		return "D flip-flop";
	}

	@Override
	void setupPins() {
		sizeX = 2;
		sizeY = 3;
		pins = new Pin[getPostCount()];
		pins[0] = new Pin(0, SIDE_W, "D");
		pins[1] = new Pin(0, SIDE_E, "Q");
		pins[1].output = pins[1].state = true;
		pins[2] = new Pin(2, SIDE_E, "Q");
		pins[2].output = true;
		pins[2].lineOver = true;
		pins[3] = new Pin(1, SIDE_W, "");
		pins[3].clock = true;
		if (hasReset())
			pins[4] = new Pin(2, SIDE_W, "R");
	}

	@Override
	public int getPostCount() {
		return hasReset() ? 5 : 4;
	}

	@Override
	public int getVoltageSourceCount() {
		return 2;
	}

	@Override
	void execute() {
		if (pins[3].value && !lastClock) {
			pins[1].value = pins[0].value;
			pins[2].value = !pins[0].value;
		}
		if (pins.length > 4 && pins[4].value) {
			pins[1].value = false;
			pins[2].value = true;
		}
		lastClock = pins[3].value;
	}

	@Override
	public int getDumpType() {
		return 155;
	}
}
