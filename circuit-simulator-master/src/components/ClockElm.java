package components;

public class ClockElm extends RailElm {
	public ClockElm(int xx, int yy) {
		super(xx, yy, WF_SQUARE);
		setMaxVoltage(2.5);
		bias = 2.5;
		frequency = 100;
		flags |= FLAG_CLOCK;
	}

	@Override
	public Class getDumpClass() {
		return RailElm.class;
	}

	@Override
	public int getShortcut() {
		return 0;
	}
}
