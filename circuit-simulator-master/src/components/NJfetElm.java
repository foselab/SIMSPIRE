package components;

public class NJfetElm extends JfetElm {
	public NJfetElm(int xx, int yy) {
		super(xx, yy, false);
	}

	@Override
	public Class getDumpClass() {
		return JfetElm.class;
	}
}
