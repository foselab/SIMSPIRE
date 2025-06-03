package lungsimulator.components;

import java.util.HashMap;

/**
 * Describes an element of the circuit
 */
public class Element {
	/**
	 * Name or description of the element
	 */
	private String elementName;
	
	/**
	 * The formula used to calculate the element value
	 */
	private Formula associatedFormula;
	
	/**
	 * The class name of the component model in circuit-circuitsimulator.simulator
	 */
	private String type; 
	
	/**
	 * The position of the element in the circuit
	 */
	private HashMap<String, Integer> position;
	
	/**
	 * Coordinate on the x-axis for the first node
	 */
	private int x;
	
	/**
	 * Coordinate on the y-axis for the first node
	 */
	private int y;
	
	/**
	 * Coordinate on the x-axis for the second node
	 */
	private int x1;
	
	/**
	 * Coordinate on the y-axis for the second node
	 */
	private int y1;
	
	/**
	 * Set true to show the pressure on the left node 
	 */
	private boolean showLeft;
	
	/**
	 * Name of the pressure on the left node
	 */
	private String idLeft;
	
	/**
	 * Set true to show the pressure on the right node 
	 */
	private boolean showRight;
	
	/**
	 * Name of the pressure on the left node
	 */
	private String idRight;

	/**
	 * Constructor to properly use YAML file
	 */
	public Element() {
	}
	
	/**
	 * Checks consistency of fields
	 */
	public void validate() {
		//check type
		if (type == null || type.isEmpty()) {
			throw new InspireException("Missing element type");
		}
		
		//check coordinates
		if (x < 0 || y < 0 || x1 < 0 || y1 < 0) {
			throw new InspireException("Invalid coordinates");
		}
		
		//check pressure points
		checkPressurePoints(idLeft, showLeft, "left");
		checkPressurePoints(idRight, showRight, "right");
	}
	
	/**
	 * Checks if there is at least one pressure to show
	 * @return showLeft || showRight
	 */
	public boolean atLeastOnePressurePoint() {
		return showLeft || showRight;
	}
	
	private void checkPressurePoints(final String nodeId, final boolean showNode, final String side) {

		if (showNode && (nodeId == null || nodeId.isEmpty())) {
			throw new InspireException("Missing id for " + side + " node");
		}

		if (!showNode && nodeId != null) {
			throw new InspireException("Inconsistency error: an id for " + side + " node has been set, but show"
					+ side.substring(0, 1).toUpperCase() + side.substring(1) + "is false");
		}
	}

	public String getType() {
		return type;
	}

	public void setType(final String type) {
		this.type = type;
	}

	public int getX() {
		if (position != null && position.containsKey("x1")) {
			return position.get("x1");
		}
		return x;
	}

	public void setX(final int x) {
		position.put("x1", x);
		this.x = x;
	}

	public int getY() {
		if (position != null && position.containsKey("y1")) {
			return position.get("y1");
		}
		return y;
	}

	public void setY(final int y) {
		this.y = y;
		position.put("y1", y);
	}

	public int getX1() {
		if (position != null && position.containsKey("x2")) {
			return position.get("x2");
		}
		return x1;
	}

	public void setX1(final int x1) {
		this.x1 = x1;
		position.put("x2", x1);
	}

	public int getY1() {
		if (position != null && position.containsKey("y2")) {
			return position.get("y2");
		}
		return y1;
	}

	public boolean isShowLeft() {
		return showLeft;
	}

	public void setShowLeft(final boolean showLeft) {
		this.showLeft = showLeft;
	}

	public String getIdLeft() {
		return idLeft;
	}

	public void setIdLeft(final String idLeft) {
		this.idLeft = idLeft;
	}

	public boolean isShowRight() {
		return showRight;
	}

	public void setShowRight(final boolean showRight) {
		this.showRight = showRight;
	}

	public String getIdRight() {
		return idRight;
	}

	public void setIdRight(final String idRight) {
		this.idRight = idRight;
	}

	public void setY1(final int y1) {
		position.put("y2", y1);
		this.y1 = y1;
	}

	public Formula getAssociatedFormula() {
		return associatedFormula;
	}

	public void setAssociatedFormula(final Formula associatedFormula) {
		this.associatedFormula = associatedFormula;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(final String elementName) {
		this.elementName = elementName;
	}

}
