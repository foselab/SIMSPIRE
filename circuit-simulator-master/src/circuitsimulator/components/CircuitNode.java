package circuitsimulator.components;
import java.util.Vector;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class CircuitNode {
	private int x;
	private int y;
	private Vector<CircuitNodeLink> links;
	private boolean internal;

	public CircuitNode() {
		setLinks(new Vector<CircuitNodeLink>());
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

	public int setY(int y) {
		this.y = y;
		return y;
	}

	public Vector<CircuitNodeLink> getLinks() {
		return links;
	}

	public void setLinks(Vector<CircuitNodeLink> links) {
		this.links = links;
	}

	public boolean isInternal() {
		return internal;
	}

	public void setInternal(boolean internal) {
		this.internal = internal;
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
