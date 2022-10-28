package lungsimulator.components;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lungsimulator.exceptions.InspireException;

/**
 * Contains all the initial values of variables
 */
public class Archetype {
	/**
	 * Label assigned to the chosen archetype
	 */
	private int schema;
	
	/**
	 * Maps each variable to its initial value
	 */
	private Map<String, String> parameters = new ConcurrentHashMap<>();
	
	/**
	 * Constructor to properly use YAML file
	 */
	public Archetype() {}
	
	/**
	 * Checks there is at least one variable
	 */
	public void validate() {
		if (parameters == null || parameters.isEmpty()) {
			throw new InspireException("Expected some parameters but found 0");
		}
	}

	public int getSchema() {
		return schema;
	}

	public void setSchema(final int schema) {
		this.schema = schema;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(final Map<String, String> parameters) {
		this.parameters = parameters;
	}
}
