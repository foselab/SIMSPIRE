package lungsimulator.components;

import java.util.List;

public class Formula {
	String id;
	boolean isTimeDependent;
	boolean isExternal;
	String formula;
	List<String> variables;
	
	public Formula(String id, boolean isTimeDependent, boolean isExternal, String formula, List<String> variables) {
		this.id = id;
		this.isTimeDependent = isTimeDependent;
		this.isExternal = isExternal;
		this.formula = formula;
		this.variables = variables;
	}
	
	public Formula() {}
	
	public boolean getIsTimeDependent() {
		return isTimeDependent;
	}
	public void setIsTimeDependent(boolean isTimeDependent) {
		this.isTimeDependent = isTimeDependent;
	}
	
	public boolean getIsExternal() {
		return isExternal;
	}

	public void setIsExternal(boolean isExternal) {
		this.isExternal = isExternal;
	}

	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	public List<String> getVariables() {
		return variables;
	}
	public void setVariables(List<String> variables) {
		this.variables = variables;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
