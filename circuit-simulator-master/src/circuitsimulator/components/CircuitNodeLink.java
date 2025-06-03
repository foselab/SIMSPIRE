package circuitsimulator.components;
public class CircuitNodeLink {
	private int num;
	private CircuitElm elm;
	public CircuitElm getElm() {
		return elm;
	}
	public void setElm(CircuitElm elm) {
		this.elm = elm;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	
	public String toString() {
		return "num: " + num + ", elm: " + elm;
	}
}
