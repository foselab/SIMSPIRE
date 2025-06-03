package lungsimulator.utils;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import lungsimulator.components.Archetype;
import lungsimulator.components.Patient;
import lungsimulator.components.SimulatorParams;

public class ResourceReader {
	public static Patient readPatientModel(String modelName) throws IOException {
		InputStream input = ResourceReader.class.getResourceAsStream("/lung-model-" + modelName + ".yaml");
		
		// Instantiating a new ObjectMapper as a YAMLFactory
		final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

		// Mapping the lung model from the YAML file to the Patient class
		final Patient patient = objectMapper.readValue(input, Patient.class);
		
		input.close();
		
		return patient;
	}
	
	public static Archetype readArchetypeModel(String modelName) throws IOException {
		InputStream input = ResourceReader.class.getResourceAsStream("/archetype-" + modelName + ".yaml");
		
		// Instantiating a new ObjectMapper as a YAMLFactory
		final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

		// Mapping the lung model from the YAML file to the Patient class
		final Archetype archetype = objectMapper.readValue(input, Archetype.class);
		
		input.close();
		
		return archetype;
	}
	
	public static SimulatorParams readDemographicModel() throws IOException {
		InputStream input = ResourceReader.class.getResourceAsStream("/patient-demographic-data.yaml");
		
		// Instantiating a new ObjectMapper as a YAMLFactory
		final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

		// Mapping the lung model from the YAML file to the Patient class
		final SimulatorParams demographicData = objectMapper.readValue(input, SimulatorParams.class);
		
		input.close();
		
		return demographicData;
	}
}
