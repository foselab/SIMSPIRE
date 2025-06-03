package circuitsimulator.components;

public class PushSwitchElm extends SwitchElm {
	public PushSwitchElm(int xx, int yy) {
		super(xx, yy, true);
	}

	@Override
	public Class getDumpClass() {
		return SwitchElm.class;
	}

	@Override
	public int getShortcut() {
		return 0;
	}
}
