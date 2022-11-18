package lungsimulator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

import lungsimulator.components.Archetype;
import lungsimulator.components.Patient;
import lungsimulator.components.SimulatorParams;
import lungsimulator.utils.Validator;
import lungsimulator.utils.YamlReader;
import simulator.CirSim;

/**
 * It contains the main logic for the lung simulator
 */
public class LungSimulator {
	/**
	 * Reference used to call methods for circuit construction
	 */
	private final transient CircuitBuilder circuitBuilder = new CircuitBuilder();

	/**
	 * Patient model description
	 */
	public transient Patient patient;

	/**
	 * Patient health description
	 */
	public transient Archetype archetype;

	/**
	 * Patient demographic data
	 */
	public transient SimulatorParams demographicData;

	/**
	 * Init the lung simulator by reading the patient model, archetype and
	 * demographic data
	 * 
	 * @throws FileNotFoundException at least one of the required files can't be
	 *                               found
	 * @throws IOException           the structure of at least one YAML file is not
	 *                               correct
	 */
	public void initSchema(String chosenSchema) throws FileNotFoundException, IOException {
		YamlReader yamlReader = new YamlReader(chosenSchema);

		if (chosenSchema != null) {
			// Read patient model and patient archetype
			patient = yamlReader.readPatientModel();
			archetype = yamlReader.readArchetypeParameters();
			demographicData = yamlReader.readDemographicData();
		}
	}

	/**
	 * Init the lung simulator by reading the patient model
	 * 
	 * @throws FileNotFoundException the patient model file can't be found
	 * @throws IOException           the structure of the patient model YAML file is
	 *                               not correct
	 */
	public void initCustomPatient(InputStream input) throws FileNotFoundException, IOException {
		YamlReader yamlReader = new YamlReader("Custom");
		patient = yamlReader.readPatientModel(input);
	}

	/**
	 * Init the lung simulator by reading the patient archetype
	 * 
	 * @throws FileNotFoundException the patient archetype file can't be found
	 * @throws IOException           the structure of the patient archetype YAML
	 *                               file is not correct
	 */
	public void initCustomArchetype(InputStream input) throws FileNotFoundException, IOException {
		YamlReader yamlReader = new YamlReader("Custom");
		archetype = yamlReader.readArchetypeParameters(input);
	}

	/**
	 * Init the lung simulator by reading the patient demographic data
	 * 
	 * @throws FileNotFoundException the patient demographic data file can't be
	 *                               found
	 * @throws IOException           the structure of the patient demographic data
	 *                               YAML file is not correct
	 */
	public void initCustomDemographic(InputStream input) throws FileNotFoundException, IOException {
		YamlReader yamlReader = new YamlReader("Custom");
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

	Socket socket;
	final String message = "getPressure";
	CirSim myCircSim;

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

	public void miniSimulation(double initialT, double timeStep) {
		// Update ventilator value
		socket.send(message.getBytes(), 0);
		final byte[] reply = socket.recv(0);
		if (reply != null) {
			String replyMessage = new String(reply, ZMQ.CHARSET);
			double ventilatorValue = Double.parseDouble(replyMessage);
			circuitBuilder.updateVentilatorValue(ventilatorValue);
		}

		// update values for time dependent components
		if (circuitBuilder.isTimeDependentCir()) {
			circuitBuilder.updateCircuitSimulator(archetype, initialT);
		}

		myCircSim.setTimeStep(timeStep);
		myCircSim.setT(initialT);
		System.out.println("timeStep: " + timeStep + " - initialT " + initialT);
		myCircSim.analyzeCircuit();
		myCircSim.loopAndContinue(false);
		circuitBuilder.updateData(initialT);
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Archetype getArchetype() {
		return archetype;
	}

	public void setArchetype(Archetype archetype) {
		this.archetype = archetype;
	}

	public SimulatorParams getDemographicData() {
		return demographicData;
	}

	public void setDemographicData(SimulatorParams demographicData) {
		this.demographicData = demographicData;
	}

	public CircuitBuilder getCircuitBuilder() {
		return circuitBuilder;
	}
}
