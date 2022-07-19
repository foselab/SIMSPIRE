package components;

public class SquareRailElm extends RailElm {
	public SquareRailElm(int xx, int yy) {
		super(xx, yy, WF_SQUARE);
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
