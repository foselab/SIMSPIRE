package components;

import java.awt.Color;
import java.awt.Graphics;
import java.util.StringTokenizer;

public class SevenSegElm extends ChipElm {
	public SevenSegElm(int xx, int yy) {
		super(xx, yy);
	}

	public SevenSegElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
	}

	@Override
	String getChipName() {
		return "7-segment driver/display";
	}

	Color darkred;

	@Override
	void setupPins() {
		darkred = new Color(30, 0, 0);
		sizeX = 4;
		sizeY = 4;
		pins = new Pin[7];
		pins[0] = new Pin(0, SIDE_W, "a");
		pins[1] = new Pin(1, SIDE_W, "b");
		pins[2] = new Pin(2, SIDE_W, "c");
		pins[3] = new Pin(3, SIDE_W, "d");
		pins[4] = new Pin(1, SIDE_S, "e");
		pins[5] = new Pin(2, SIDE_S, "f");
		pins[6] = new Pin(3, SIDE_S, "g");
	}

	@Override
	public int getPostCount() {
		return 7;
	}

	@Override
	public int getVoltageSourceCount() {
		return 0;
	}

	@Override
	public int getDumpType() {
		return 157;
	}
}
