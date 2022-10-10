package lungsimulator.components;

public class Element {
	private Formula associatedFormula;
	private String type; // component's class name
	private int x, y, x1, y1;
	private boolean showLeft;
	private String idLeft;
	private boolean showRight;
	private String idRight;

	public Element(Formula associatedFormula, String type, int x, int y, int x1, int y1, boolean showLeft,
			String idLeft, boolean showRight, String idRight) {
		this.associatedFormula = associatedFormula;
		this.type = type;
		this.x = x;
		this.y = y;
		this.x1 = x1;
		this.y1 = y1;
		this.showLeft = showLeft;
		this.idLeft = idLeft;
		this.showRight = showRight;
		this.idRight = idRight;
	}

	public Element() {
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getX1() {
		return x1;
	}

	public void setX1(int x1) {
		this.x1 = x1;
	}

	public int getY1() {
		return y1;
	}

	public boolean isShowLeft() {
		return showLeft;
	}

	public void setShowLeft(boolean showLeft) {
		this.showLeft = showLeft;
	}

	public String getIdLeft() {
		return idLeft;
	}

	public void setIdLeft(String idLeft) {
		this.idLeft = idLeft;
	}

	public boolean isShowRight() {
		return showRight;
	}

	public void setShowRight(boolean showRight) {
		this.showRight = showRight;
	}

	public String getIdRight() {
		return idRight;
	}

	public void setIdRight(String idRight) {
		this.idRight = idRight;
	}

	public void setY1(int y1) {
		this.y1 = y1;
	}

	public Formula getAssociatedFormula() {
		return associatedFormula;
	}

	public void setAssociatedFormula(Formula associatedFormula) {
		this.associatedFormula = associatedFormula;
	}

}
