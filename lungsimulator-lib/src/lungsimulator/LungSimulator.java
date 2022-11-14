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
	 * Init the lung simulator by reading and validating the patient model and
	 * archetype and set the frame configuration
	 * 
	 * @throws FileNotFoundException one between the lung model and the archetype
	 *                               file (or both) is not located in config folder
	 * @throws IOException           the structure of YAML file is not correct: it
	 *                               could be either lung model or archetype (even
	 *                               both)
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

	public void initCustomPatient(InputStream input) throws FileNotFoundException, IOException {
		YamlReader yamlReader = new YamlReader("Custom");
		patient = yamlReader.readPatientModel(input);
	}

	public void initCustomArchetype(InputStream input) throws FileNotFoundException, IOException {
		YamlReader yamlReader = new YamlReader("Custom");
		archetype = yamlReader.readArchetypeParameters(input);
	}

	public void initCustomDemographic(InputStream input) throws FileNotFoundException, IOException {
		YamlReader yamlReader = new YamlReader("Custom");
		demographicData = yamlReader.readDemographicData(input);
	}

	public void modelValidation() {
		// Validation
		final Validator validator = new Validator();
		validator.evaluate(patient, archetype, demographicData);
	}

	double tStart;
	double lastT;
	double step;
	Socket socket;
	final String message = "getPressure";
	CirSim myCircSim;

	public void mini() {
		myCircSim = circuitBuilder.buildCircuitSimulator(patient, archetype);

		// ZMQ settings
		final ZContext context = new ZContext();
		socket = context.createSocket(SocketType.REQ);
		socket.connect("tcp://localhost:5555");

		// moment of time (in seconds) where simulation starts
		tStart = System.currentTimeMillis() / 1000.0;
		lastT = 0;
		step = myCircSim.getTimeStep();
	}

	public void miniSimulation(double initialT) {
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
				// circuitBuilder.updateCircuitSimulator(archetype, initialT);
			}

			myCircSim.setT(initialT);
			System.out.println("initialT " + initialT);
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
