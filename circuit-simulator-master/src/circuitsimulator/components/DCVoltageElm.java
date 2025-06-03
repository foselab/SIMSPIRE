package circuitsimulator.components;

public class DCVoltageElm extends VoltageElm {
	public DCVoltageElm(int xx, int yy) {
		super(xx, yy, WF_DC);
	}

	@Override
	public Class getDumpClass() {
		return VoltageElm.class;
	}

	@Override
	public int getShortcut() {
		return 'v';
	}
}
