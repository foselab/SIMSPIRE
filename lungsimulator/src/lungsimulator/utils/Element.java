package lungsimulator.utils;

import java.util.List;

public class Element {
	Formula associatedFormula;
	String type; // component's class name
	int x, y, x1, y1; 
	
	public Element(Formula associatedFormula, String type, int x, int y, int x1, int y1) {
		this.associatedFormula = associatedFormula;
		this.type = type;
		this.x = x;
		this.y = y;
		this.x1 = x1;
		this.y1 = y1;
	}
	
	public Element() {}

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
