package lungsimulator.components;

import java.util.HashMap;

public class Archetype {
	int schema;
	HashMap<String, String> parameters;
	
	public Archetype(int schema, HashMap<String, String> parameters) {
		this.schema = schema;
		this.parameters = parameters;
	}
	
	public Archetype() {}

	public int getSchema() {
		return schema;
	}

	public void setSchema(int schema) {
		this.schema = schema;
	}

	public HashMap<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(HashMap<String, String> parameters) {
		this.parameters = parameters;
	}
	
	

}
