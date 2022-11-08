package lungsimulator;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

import lungsimulator.components.Archetype;
import lungsimulator.components.Patient;
import lungsimulator.components.SimulatorParams;
import lungsimulator.graphic.GraphicInterface;
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
	 * Manages user interface properties
	 */
	public transient GraphicInterface userInterface = new GraphicInterface();

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
		// while (!wait) {
		//double ntStart = System.currentTimeMillis() / 1000.0;
		//double initialT = ntStart - tStart;
		// wait for step seconds until next resolution
		//if (initialT - lastT >= step) {
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
			//lastT = initialT;
		//}
		// }
	}

	/**
	 * Circuit construction and resolution for each iteration
	 * 
	 * @throws InterruptedException
	 */
	public void simulateCircuit() throws InterruptedException {
		// Create the circuit equivalent to the lung
		final CirSim myCircSim = circuitBuilder.buildCircuitSimulator(patient, archetype);

		// ZMQ settings
		final ZContext context = new ZContext();
		final Socket socket = context.createSocket(SocketType.REQ);
		socket.connect("tcp://localhost:5555");

		final String message = "getPressure";
		double ventilatorValue;
		String replyMessage;

		// moment of time (in seconds) where simulation starts
		final double tStart = System.currentTimeMillis() / 1000.0;
		double lastT = 0;
		final double step = myCircSim.getTimeStep();
		double ntStart;
		double initialT;

		while (userInterface.isWindowOpen()) {
			if (userInterface.getStateOfExecution()) {
				ntStart = System.currentTimeMillis() / 1000.0;
				initialT = ntStart - tStart;
				// wait for step seconds until next resolution
				if (initialT - lastT >= step) {
					// Update ventilator value
					socket.send(message.getBytes(), 0);
					final byte[] reply = socket.recv(0);
					if (reply != null) {
						replyMessage = new String(reply, ZMQ.CHARSET);
						ventilatorValue = Double.parseDouble(replyMessage);
						circuitBuilder.updateVentilatorValue(ventilatorValue);
					}

					// update values for time dependent components
					if (circuitBuilder.isTimeDependentCir()) {
						circuitBuilder.updateCircuitSimulator(archetype, initialT);
					}

					myCircSim.setT(initialT);
					System.out.println("initialT " + initialT);
					myCircSim.analyzeCircuit();
					myCircSim.loopAndContinue(false);
					userInterface.updateShownDataValues(initialT, myCircSim);
					lastT = initialT;
				}

			} else {
				Thread.sleep((long) step);
			}
		}
		socket.close();
		context.close();
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
