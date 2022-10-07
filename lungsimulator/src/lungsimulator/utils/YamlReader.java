package lungsimulator.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import lungsimulator.components.Archetype;
import lungsimulator.components.Element;
import lungsimulator.components.Patient;

public class YamlReader {
	// The electrical analogue of lung
	static String lungModelCB = "config/lung-model-CB.yaml";
	static String archetypeParametersCB = "config/archetype-CB.yaml";
	
	// Albanese
	static String lungModelAlbanese = "config/lung-model-Albanese.yaml";
	static String archetypeParametersAlbanese = "config/archetype-Albanese.yaml";
	
	// Baker
	static String lungModelBaker = "config/lung-model-Baker.yaml";
	static String archetypeParametersBaker = "config/archetype-Baker.yaml";
	
	// Jain
	static String lungModelJain = "config/lung-model-Jain.yaml";
	static String archetypeParametersJain = "config/archetype-Jain.yaml";
	
	// Al-Naggar
	static String lungModelAlNaggar = "config/lung-model-AlNaggar.yaml";
	static String archetypeParametersAlNaggar = "config/archetype-AlNaggar.yaml";

	private static final Logger LOGGER = Logger.getLogger(YamlReader.class.getName());
	
	/**
	 * Given a YAML file of the patient's model, this method builds a new Patient object
	 * @return patient's model
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	public Patient readPatientModel() throws FileNotFoundException, IOException, ParseException {

		// Loading the YAML file
		File file = new File(lungModelAlbanese);
		assert file.exists();

		LOGGER.log(Level.INFO, "Loading patient model...");

		// Instantiating a new ObjectMapper as a YAMLFactory
		ObjectMapper om = new ObjectMapper(new YAMLFactory());

		// Mapping the lung model from the YAML file to the Patient class
		Patient patient = om.readValue(file, Patient.class);

		LOGGER.log(Level.INFO, "Patient model successfully loaded");

		return patient;
	}
	
	/**
	 * Given a YAML file of the parameteres of the model, this method builds a new Archetype object
	 * @return patient's archetype
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	public Archetype readArchetypeParameters() throws FileNotFoundException, IOException, ParseException {

		// Loading the YAML file
		File file = new File(archetypeParametersAlbanese);
		assert file.exists();

		LOGGER.log(Level.INFO, "Loading archetype parameters...");

		// Instantiating a new ObjectMapper as a YAMLFactory
		ObjectMapper om = new ObjectMapper(new YAMLFactory());

		// Mapping the lung model from the YAML file to the Patient class
		Archetype archetype = om.readValue(file, Archetype.class);

		LOGGER.log(Level.INFO, "Archetype parameters successfully loaded");

		return archetype;
	}
	
	/**
	 * Checks for missing or not properly built sections of the patient model and its archetype
	 * @param patient
	 * @param archetype
	 * @throws Exception
	 */
	public void validator(Patient patient, Archetype archetype) throws Exception {

		LOGGER.log(Level.INFO, "Init validation process");
		
		if(patient == null || archetype == null) {
			throw new InspireException("At least one model is not properly built");
		}
		
		if(patient.getSchema() != archetype.getSchema()) {
			throw new InspireException("Patient schema and Archetype schema are inconsistent");
		}
		
		if(patient.getElementsList() == null || patient.getElementsList().isEmpty()
				|| (patient.getElementsList() != null && patient.getElementsList().size() < 2)) {
			throw new InspireException("Expected at least 2 components");
		}
		
		if(archetype.getParameters() == null || archetype.getParameters().isEmpty()) {
			throw new InspireException("Expected some parameters but found 0");
		}
		
		if(patient.getElementsList() != null) {
			for(Element e: patient.getElementsList()) {
				if(e.getType() == null || e.getType().isEmpty()) {
					throw new InspireException("Missing element type");
				}
				
				if(e.getX() < 0 || e.getY() < 0 || e.getX1() < 0 || e.getX1() < 0) {
					throw new InspireException("Invalid coordinates");
				}
				
				if(e.getAssociatedFormula() == null) {
					throw new InspireException("Missing formula associated to " + e.getType() + " element");
				}else if(e.getAssociatedFormula() != null && !e.getAssociatedFormula().getIsExternal()){
					if(e.getAssociatedFormula().getFormula() == null || e.getAssociatedFormula().getFormula().isEmpty()) {
						throw new InspireException("Missing formula for element: " + e.getType());
					}
					
					if(e.getAssociatedFormula().getVariables() == null || e.getAssociatedFormula().getVariables().isEmpty()) {
						throw new InspireException("Missing variables for formula: " + e.getAssociatedFormula().getId() + "of element " + e.getType());
					}else {
						for(String var : e.getAssociatedFormula().getVariables()) {
							if (archetype.getParameters().getOrDefault(var, "notFound").equals("notFound")
									&& (!e.getAssociatedFormula().getIsTimeDependent() && !var.equals("TIME"))) {
								throw new InspireException("Missing value for variable: " + var);
							}
							if(var.equals("TIME") && !e.getAssociatedFormula().getIsTimeDependent()) {
								throw new InspireException("Inconsistency error: formula " + e.getAssociatedFormula().getId() + " is not time-dependent, but found var TIME");
							}
						}
					}
				}
				
			}
		}
		
		if(patient.getAdditionalFormulas() != null) {
			// TODO controllo sugli id
		}
		
		LOGGER.log(Level.INFO, "Validation process successfully completed");
	}
}
