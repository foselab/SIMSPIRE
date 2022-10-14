package lungsimulator.components;

import java.util.List;

import lungsimulator.exceptions.InspireException;

/**
 * Describes all the attributes of a patient model
 */
public class Patient {
	/**
	 * Label assigned to the patient model
	 */
	private int schema;
	
	/**
	 *  List of all circuit components
	 */
	private List<Element> elementsList;
	
	/**
	 * Constructor to properly use YAML file
	 */
	public Patient() {}
	
	/**
	 * Checks there are at least two elements
	 */
	public void validate() {
		if (elementsList == null || elementsList.isEmpty() || elementsList != null && elementsList.size() < 2) {
			throw new InspireException("Expected at least 2 components");
		}
	}

	public int getSchema() {
		return schema;
	}

	public void setSchema(final int schema) {
		this.schema = schema;
	}

	public List<Element> getElementsList() {
		return elementsList;
	}

	public void setElementsList(final List<Element> elementsList) {
		this.elementsList = elementsList;
	}
}
