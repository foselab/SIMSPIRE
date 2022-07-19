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
import lungsimulator.components.Patient;

public class YamlReader {
	// RC circuit
	static String LUNG_MODEL_RC = "config/lung-model-RC.yaml";
	static String ARCHETYPE_PARAMETERS_RC = "config/archetype-RC.yaml";
	
	// The electrical analogue of lung
	static String LUNG_MODEL_CB = "config/lung-model-CB.yaml";
	static String ARCHETYPE_PARAMETERS_CB = "config/archetype-CB.yaml";
	
	// Rideout
	static String LUNG_MODEL_RS = "config/lung-model-RS.yaml";
	static String ARCHETYPE_PARAMETERS_RS = "config/archetype-RS.yaml";
	
	// A model based approach
	static String LUNG_MODEL_MB = "config/lung-model-MB.yaml";
	static String ARCHETYPE_PARAMETERS_MB = "config/archetype-MB.yaml";

	private static final Logger logger = Logger.getLogger(YamlReader.class.getName());
	
	/**
	 * Given a YAML file of the patient's model, this method builds a new Patient object
	 * @return patient's model
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 */
	public Patient readPatientModel() throws FileNotFoundException, IOException, ParseException {

		// Loading the YAML file
		File file = new File(LUNG_MODEL_CB);
		assert file.exists();

		logger.log(Level.INFO, "Loading patient model...");

		// Instantiating a new ObjectMapper as a YAMLFactory
		ObjectMapper om = new ObjectMapper(new YAMLFactory());

		// Mapping the lung model from the YAML file to the Patient class
		Patient patient = om.readValue(file, Patient.class);

		logger.log(Level.INFO, "Patient model successfully loaded");

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
		File file = new File(ARCHETYPE_PARAMETERS_CB);
		assert file.exists();

		logger.log(Level.INFO, "Loading archetype parameters...");

		// Instantiating a new ObjectMapper as a YAMLFactory
		ObjectMapper om = new ObjectMapper(new YAMLFactory());

		// Mapping the lung model from the YAML file to the Patient class
		Archetype archetype = om.readValue(file, Archetype.class);

		logger.log(Level.INFO, "Archetype parameters successfully loaded");

		return archetype;
	}
	
	/**
	 * Checks for missing or not properly built sections of the patient model and its archetype
	 * @param patient
	 * @param archetype
	 * @throws Exception
	 */
	public void validator(Patient patient, Archetype archetype) throws Exception {
		//TODO check correttezza intervalli dei parametri

		logger.log(Level.INFO, "Init validation process");
		
		if(patient == null || archetype == null) {
			throw new Exception("At least one model is not properly built");
		}
		
		if(patient.getSchema() != archetype.getSchema()) {
			throw new Exception("Patient schema and Archetype schema are inconsistent");
		}
		
		if(patient.getElementsList() == null || patient.getElementsList().isEmpty()
				|| (patient.getElementsList() != null && patient.getElementsList().size() < 2)) {
			throw new Exception("Expected at least 2 components");
		}
		
		if(archetype.getParameters() == null || archetype.getParameters().isEmpty()) {
			throw new Exception("Expected some parameters but found 0");
		}
		
		if(patient.getElementsList() != null) {
			for(Element e: patient.getElementsList()) {
				if(e.getType() == null || e.getType().isEmpty()) {
					throw new Exception("Missing element type");
				}
				
				if(e.getX() < 0 || e.getY() < 0 || e.getX1() < 0 || e.getX1() < 0) {
					throw new Exception("Invalid coordinates");
				}
				
				if(e.getAssociatedFormula() == null) {
					throw new Exception("Missing formula associated to " + e.getType() + " element");
				}else if(e.getAssociatedFormula() != null && !e.getAssociatedFormula().getIsExternal()){
					if(e.getAssociatedFormula().getFormula() == null || e.getAssociatedFormula().getFormula().isEmpty()) {
						throw new Exception("Missing formula for element: " + e.getType());
					}
					
					if(e.getAssociatedFormula().getVariables() == null || e.getAssociatedFormula().getVariables().isEmpty()) {
						throw new Exception("Missing variables for formula: " + e.getAssociatedFormula().getId() + "of element " + e.getType());
					}else {
						for(String var : e.getAssociatedFormula().getVariables()) {
							if (archetype.getParameters().getOrDefault(var, "notFound").equals("notFound")
									&& (!e.getAssociatedFormula().getIsTimeDependent() && !var.equals("TIME"))) {
								throw new Exception("Missing value for variable: " + var);
							}
							if(var.equals("TIME") && !e.getAssociatedFormula().getIsTimeDependent()) {
								throw new Exception("Inconsistency error: formula " + e.getAssociatedFormula().getId() + " is not time-dependent, but found var TIME");
							}
						}
					}
				}
				
			}
		}
		
		if(patient.getAdditionalFormulas() != null) {
			// TODO controllo sugli id
		}
		
		logger.log(Level.INFO, "Validation process successfully completed");
	}
}
