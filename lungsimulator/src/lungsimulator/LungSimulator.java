package lungsimulator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

import circuitsimulator.simulator.CirSim;
import lungsimulator.components.Archetype;
import lungsimulator.components.Patient;
import lungsimulator.components.SimulatorParams;
import lungsimulator.yamlreaders.YamlReader;

/**
 * It contains the main logic for the lung circuitsimulator.simulator
 */
public class LungSimulator {
	/**
	 * Reference used to call methods for circuit construction
	 */
	private final transient CircuitBuilder circuitBuilder;

	/**
	 * Patient model description
	 */
	private transient Patient patient;

	/**
	 * Patient health description
	 */
	private transient Archetype archetype;

	/**
	 * Patient demographic data
	 */
	private transient SimulatorParams demographicData;

	/**
	 * Socket for ZMQ communication
	 */
	private transient Socket socket;

	/**
	 * Message for the ZMQ communication
	 */
	private final static String MESSAGE = "getPressure";

	/**
	 * Circuit
	 */
	private transient CirSim myCircSim;

	/**
	 * Internal logger for info report
	 */
	private static final Logger LOGGER = Logger.getLogger(LungSimulator.class.getName());

	/**
	 * Init fields of the class
	 */
	public LungSimulator() {
		circuitBuilder = new CircuitBuilder();
	}

	/**
	 * Init the lung circuitsimulator.simulator by reading the patient model, archetype and
	 * demographic data
	 * 
	 * @throws FileNotFoundException at least one of the required files can't be
	 *                               found
	 * @throws IOException           the structure of at least one YAML file is not
	 *                               correct
	 */
	public void initSchema(final String chosenSchema) throws FileNotFoundException, IOException {
		final YamlReader yamlReader = new YamlReader(chosenSchema);

		if (chosenSchema != null) {
			// Read patient model and patient archetype
			patient = yamlReader.readPatientModel();
			archetype = yamlReader.readArchetypeParameters();
			demographicData = yamlReader.readDemographicData();
		}
	}

	/**
	 * Init the lung circuitsimulator.simulator by reading the patient model
	 * 
	 * @throws FileNotFoundException the patient model file can't be found
	 * @throws IOException           the structure of the patient model YAML file is
	 *                               not correct
	 */
	public void initCustomPatient(final InputStream input) throws FileNotFoundException, IOException {
		final YamlReader yamlReader = new YamlReader("Custom");
		patient = yamlReader.readPatientModel(input);
	}

	/**
	 * Init the lung circuitsimulator.simulator by reading the patient archetype
	 * 
	 * @throws FileNotFoundException the patient archetype file can't be found
	 * @throws IOException           the structure of the patient archetype YAML
	 *                               file is not correct
	 */
	public void initCustomArchetype(final InputStream input) throws FileNotFoundException, IOException {
		final YamlReader yamlReader = new YamlReader("Custom");
		archetype = yamlReader.readArchetypeParameters(input);
	}

	/**
	 * Init the lung circuitsimulator.simulator by reading the patient demographic data
	 * 
	 * @throws FileNotFoundException the patient demographic data file can't be
	 *                               found
	 * @throws IOException           the structure of the patient demographic data
	 *                               YAML file is not correct
	 */
	public void initCustomDemographic(final InputStream input) throws FileNotFoundException, IOException {
		final YamlReader yamlReader = new YamlReader("Custom");
		demographicData = yamlReader.readDemographicData(input);
	}

	/**
	 * Validation of the chosen model
	 */
	public void modelValidation() {
		// Validation
		final Validator validator = new Validator();
		validator.evaluate(patient, archetype, demographicData);
	}

	/**
	 * Init the circuit and the connection to the ventilator
	 */
	public void simulationSetUp() {
		myCircSim = circuitBuilder.buildCircuitSimulator(patient, archetype);

		// ZMQ settings
		final ZContext context = new ZContext();
		socket = context.createSocket(SocketType.REQ);
		socket.connect("tcp://localhost:5555");
	}

	/**
	 * Executes a simulation step
	 * 
	 * @param initialT moment of time at which the step begins
	 * @param timeStep range between two step execution
	 */
	public void miniSimulation(final double initialT, final double timeStep) {
		// Update ventilator value
		socket.send(MESSAGE.getBytes(), 0);
		final byte[] reply = socket.recv(0);
		if (reply != null) {
			final String replyMessage = new String(reply, ZMQ.CHARSET);
			final double ventilatorValue = Double.parseDouble(replyMessage);
			circuitBuilder.updateVentilatorValue(ventilatorValue);
		}

		// update values for time dependent circuitsimulator.components
		if (circuitBuilder.isTimeDependentCir()) {
			circuitBuilder.updateCircuitSimulator(archetype, initialT);
		}

		myCircSim.setTimeStep(timeStep);
		myCircSim.setT(initialT);

		if (LOGGER.isLoggable(Level.INFO)) {
			LOGGER.log(Level.INFO, "timeStep: " + timeStep + " - initialT " + initialT);
		}

		myCircSim.analyzeCircuit();
		myCircSim.loopAndContinue(false);
		circuitBuilder.updateData(initialT);
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(final Patient patient) {
		this.patient = patient;
	}

	public Archetype getArchetype() {
		return archetype;
	}

	public void setArchetype(final Archetype archetype) {
		this.archetype = archetype;
	}

	public SimulatorParams getDemographicData() {
		return demographicData;
	}

	public void setDemographicData(final SimulatorParams demographicData) {
		this.demographicData = demographicData;
	}

	public CircuitBuilder getCircuitBuilder() {
		return circuitBuilder;
	}
}
