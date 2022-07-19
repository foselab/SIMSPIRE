package lungsimulator.components;

import java.util.List;

import lungsimulator.utils.Element;
import lungsimulator.utils.Formula;

public class Patient {
	int schema;
	// list of the circuit's components 
	List<Element> elementsList;
	// list of formulas that don't represent an Element of the circuit
	List<Formula> additionalFormulas;

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
