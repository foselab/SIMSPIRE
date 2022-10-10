package lungsimulator.components;

import java.util.List;

public class Patient {
	private int schema;
	// list of the circuit's components 
	private List<Element> elementsList;
	// list of formulas that don't represent an Element of the circuit
	private List<Formula> additionalFormulas;

	public Patient(int schema, List<Element> elementsList, List<Formula> additionalFormulas) {
		this.schema = schema;
		this.elementsList = elementsList;
		this.additionalFormulas = additionalFormulas;
	}
	
	public Patient() {}

	public int getSchema() {
		return schema;
	}

	public void setSchema(int schema) {
		this.schema = schema;
	}

	public List<Element> getElementsList() {
		return elementsList;
	}

	public void setElementsList(List<Element> elementsList) {
		this.elementsList = elementsList;
	}

	public List<Formula> getAdditionalFormulas() {
		return additionalFormulas;
	}

	public void setAdditionalFormulas(List<Formula> additionalFormulas) {
		this.additionalFormulas = additionalFormulas;
	}
}
