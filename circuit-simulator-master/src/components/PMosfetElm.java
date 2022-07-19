package components;

public class PMosfetElm extends MosfetElm {
	public PMosfetElm(int xx, int yy) {
		super(xx, yy, true);
	}

	@Override
	public Class getDumpClass() {
		return MosfetElm.class;
	}
}
