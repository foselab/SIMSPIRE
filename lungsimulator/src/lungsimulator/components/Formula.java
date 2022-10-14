package lungsimulator.components;

import java.util.List;

/**
 * Describes the properties of the formula used to calculate the element's value
 */
public class Formula {
	/**
	 * True if the formula has a time variable
	 */
	private boolean isTimeDependent;
	
	/**
	 * True if the value is not in the archetype
	 */
	private boolean isExternal;
	
	/**
	 * The formula to calculate the element's value
	 */
	private String formula;
	
	/**
	 * Variables associated to the formula
	 */
	private List<String> variables;
	
	/**
	 * Constructor to properly use YAML file
	 */
	public Formula() {}
	
	public boolean getIsTimeDependent() {
		return isTimeDependent;
	}
	public void setIsTimeDependent(final boolean isTimeDependent) {
		this.isTimeDependent = isTimeDependent;
	}
	
	public boolean getIsExternal() {
		return isExternal;
	}

	public void setIsExternal(final boolean isExternal) {
		this.isExternal = isExternal;
	}

	public String getFormula() {
		return formula;
	}
	public void setFormula(final String formula) {
		this.formula = formula;
	}
	public List<String> getVariables() {
		return variables;
	}
	public void setVariables(final List<String> variables) {
		this.variables = variables;
	}

}
