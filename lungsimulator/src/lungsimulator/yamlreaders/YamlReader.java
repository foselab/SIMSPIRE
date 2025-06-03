package lungsimulator.yamlreaders;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import lungsimulator.components.Archetype;
import lungsimulator.components.Patient;
import lungsimulator.components.SimulatorParams;


/**
 * Read, translate and validate lung model and archetype files
 */
public class YamlReader {
	/**
	 * Contains the name of the chosen model
	 */
	private final transient String modelName;

	/**
	 * Internal logger for errors report
	 */
	private static final Logger LOGGER = Logger.getLogger(YamlReader.class.getName());

	/**
	 * Init file paths according to a built-in model
	 * 
	 * @param modelName the name of the chosen model
	 */
	public YamlReader(final String modelName) {
		this.modelName = modelName;
	}

	/**
	 * Given the name of the chosen model, this method builds a new Patient object
	 * 
	 * @return patient's model
	 * @throws FileNotFoundException the lung model is not located in the config
	 *                               folder
	 * @throws IOException           the structure of the lung model YAML file is
	 *                               not correct
	 */
	public Patient readPatientModel() throws FileNotFoundException, IOException {

		LOGGER.log(Level.INFO, "Loading patient model...");

		final Patient patient = ResourceReader.readPatientModel(modelName);

		LOGGER.log(Level.INFO, "Patient model successfully loaded");

		return patient;
	}

	/**
	 * Given a custom patient model, this method builds a new Patient object
	 * 
	 * @return patient's model
	 * @throws FileNotFoundException the lung model is not located in the config
	 *                               folder
	 * @throws IOException           the structure of the lung model YAML file is
	 *                               not correct
	 */
	public Patient readPatientModel(final InputStream input) throws FileNotFoundException, IOException {

		LOGGER.log(Level.INFO, "Loading patient model...");

		// Instantiating a new ObjectMapper as a YAMLFactory
		final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

		// Mapping the lung model from the YAML file to the Patient class
		final Patient patient = objectMapper.readValue(input, Patient.class);

		LOGGER.log(Level.INFO, "Patient model successfully loaded");

		return patient;
	}

	/**
	 * Given the name of the chosen model, this method builds a new Archetype object
	 * 
	 * @return patient's archetype
	 * @throws FileNotFoundException the archetype file is not located in the config
	 *                               folder
	 * @throws IOException           the structure of the archetype YAML file is not
	 *                               correct
	 */
	public Archetype readArchetypeParameters() throws FileNotFoundException, IOException {

		LOGGER.log(Level.INFO, "Loading archetype parameters...");

		final Archetype archetype = ResourceReader.readArchetypeModel(modelName);

		LOGGER.log(Level.INFO, "Archetype parameters successfully loaded");

		return archetype;
	}

	/**
	 * Given custom parameteres of the model, this method builds a new Archetype
	 * object
	 * 
	 * @return patient's archetype
	 * @throws FileNotFoundException the archetype file is not located in the config
	 *                               folder
	 * @throws IOException           the structure of the archetype YAML file is not
	 *                               correct
	 */
	public Archetype readArchetypeParameters(final InputStream input) throws FileNotFoundException, IOException {

		LOGGER.log(Level.INFO, "Loading archetype parameters...");

		// Instantiating a new ObjectMapper as a YAMLFactory
		final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

		// Mapping the lung model from the YAML file to the Patient class
		final Archetype archetype = objectMapper.readValue(input, Archetype.class);

		LOGGER.log(Level.INFO, "Archetype parameters successfully loaded");

		return archetype;
	}

	/**
	 * Given the patient demographic data, this method builds a new SimulatorParams
	 * object
	 * 
	 * @return demographic data of the patient
	 * @throws FileNotFoundException the archetype file is not located in the config
	 *                               folder
	 * @throws IOException           the structure of the archetype YAML file is not
	 *                               correct
	 */
	public SimulatorParams readDemographicData() throws FileNotFoundException, IOException {

		LOGGER.log(Level.INFO, "Loading demographic data...");

		final SimulatorParams demographicData = ResourceReader.readDemographicModel();

		LOGGER.log(Level.INFO, "Archetype parameters successfully loaded");

		return demographicData;
	}

	/**
	 * Given custom patient demographic data, this method builds a new
	 * SimulatorParams object
	 * 
	 * @return demographic data of the patient
	 * @throws FileNotFoundException the archetype file is not located in the config
	 *                               folder
	 * @throws IOException           the structure of the archetype YAML file is not
	 *                               correct
	 */
	public SimulatorParams readDemographicData(final InputStream input) throws FileNotFoundException, IOException {

		LOGGER.log(Level.INFO, "Loading demographic data...");

		// Instantiating a new ObjectMapper as a YAMLFactory
		final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

		// Mapping the lung model from the YAML file to the Patient class
		final SimulatorParams demographicData = objectMapper.readValue(input, SimulatorParams.class);

		LOGGER.log(Level.INFO, "Demographic data successfully loaded");

		return demographicData;
	}
}
