package lungsimulator.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
	 * Contains the path of the chosen lung model file
	 */
	private final transient String lungModelPath;

	/**
	 * Contains the path of the chosen archetype file
	 */
	private final transient String archetypePath;

	/**
	 * Contains the path of the demographic data file
	 */
	private final transient String demographicPath;

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
		this.lungModelPath = "config/lung-model-" + modelName + ".yaml";
		this.archetypePath = "config/archetype-" + modelName + ".yaml";
		this.demographicPath = "config/patient-demographic-data.yaml";
	}

	/**
	 * Init file paths according to a custom model
	 * 
	 * @param lungModelName   path to the custom lung model
	 * @param archetypeName   path to the custom archetype
	 * @param demographicName path to the demographic data of the patient
	 */
	public YamlReader(final String lungModelName, final String archetypeName, final String demographicName) {
		this.lungModelPath = lungModelName;
		this.archetypePath = archetypeName;
		this.demographicPath = demographicName;
	}

	/**
	 * Given a YAML file of the patient's model, this method builds a new Patient
	 * object
	 * 
	 * @return patient's model
	 * @throws FileNotFoundException the lung model is not located in the config
	 *                               folder
	 * @throws IOException           the structure of the lung model YAML file is
	 *                               not correct
	 */
	public Patient readPatientModel() throws FileNotFoundException, IOException {

		// Loading the YAML file
		final File file = new File(lungModelPath);
		assert file.exists();

		LOGGER.log(Level.INFO, "Loading patient model...");

		// Instantiating a new ObjectMapper as a YAMLFactory
		final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

		// Mapping the lung model from the YAML file to the Patient class
		final Patient patient = objectMapper.readValue(file, Patient.class);

		LOGGER.log(Level.INFO, "Patient model successfully loaded");

		return patient;
	}

	/**
	 * Given a YAML file of the parameteres of the model, this method builds a new
	 * Archetype object
	 * 
	 * @return patient's archetype
	 * @throws FileNotFoundException the archetype file is not located in the config
	 *                               folder
	 * @throws IOException           the structure of the archetype YAML file is not
	 *                               correct
	 */
	public Archetype readArchetypeParameters() throws FileNotFoundException, IOException {

		// Loading the YAML file
		final File file = new File(archetypePath);
		assert file.exists();

		LOGGER.log(Level.INFO, "Loading archetype parameters...");

		// Instantiating a new ObjectMapper as a YAMLFactory
		final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

		// Mapping the lung model from the YAML file to the Patient class
		final Archetype archetype = objectMapper.readValue(file, Archetype.class);

		LOGGER.log(Level.INFO, "Archetype parameters successfully loaded");

		return archetype;
	}

	/**
	 * Given a YAML file of patient demographic data, this method builds a new
	 * SimulatorParams object
	 * 
	 * @return demographic data of the patient
	 * @throws FileNotFoundException the archetype file is not located in the config
	 *                               folder
	 * @throws IOException           the structure of the archetype YAML file is not
	 *                               correct
	 */
	public SimulatorParams readDemographicData() throws FileNotFoundException, IOException {

		// Loading the YAML file
		final File file = new File(demographicPath);
		assert file.exists();

		LOGGER.log(Level.INFO, "Loading demographic data...");

		// Instantiating a new ObjectMapper as a YAMLFactory
		final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

		// Mapping the lung model from the YAML file to the Patient class
		final SimulatorParams demographicData = objectMapper.readValue(file, SimulatorParams.class);

		LOGGER.log(Level.INFO, "Archetype parameters successfully loaded");

		return demographicData;
	}
}
