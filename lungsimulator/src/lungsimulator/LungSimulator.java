package lungsimulator;

import java.io.FileNotFoundException;
import java.io.IOException;

import lungsimulator.components.Archetype;
import lungsimulator.components.Patient;
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
	private final transient RealTimePlot rtp = new RealTimePlot();

	/**
	 * Patient model description
	 */
	public transient Patient patient;

	/**
	 * Patient health description
	 */
	public transient Archetype archetype;

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
	public LungSimulator() throws FileNotFoundException, IOException {
		final String chosenSchema = userInterface.selectSchema();
		YamlReader yamlReader;

		if (chosenSchema != null) {
			if (chosenSchema.contains("***")) {
				final String[] fileNames = chosenSchema.split("***");
				yamlReader = new YamlReader(fileNames[0], fileNames[1]);
			} else {
				yamlReader = new YamlReader(chosenSchema);
			}
			// Read patient model and patient archetype
			patient = yamlReader.readPatientModel();
			archetype = yamlReader.readArchetypeParameters();

			// Validation
			final Validator validator = new Validator();
			validator.evaluate(patient, archetype);

			// Frame configuration
			userInterface.frameConfig(patient, archetype);
		}
	}

	/**
	 * Circuit construction and resolution for each iteration
	 * 
	 * @throws InterruptedException
	 */
	public void simulateCircuit() throws InterruptedException {
		// Create the circuit equivalent to the lung
		final CirSim myCircSim = rtp.buildCircuitSimulator(patient, archetype);

		double time = 0;

		while (true) {
			if (userInterface.getStateOfExecution()) {
				time += 0.1;

				/*
				 * if(time%2==0) { archetype.getParameters().put("TIME", String.valueOf(time));
				 * myCircSim = rtp.updateCircuitSimulator(patient, archetype); }
				 */

				// Analyze the circuit and simulate a step
				myCircSim.analyzeCircuit();
				myCircSim.loopAndContinue(true);

				Thread.sleep(100);

				userInterface.updateShownDataValues(time, myCircSim);
			} else {
				Thread.sleep(100);
			}
		}

	}

	/**
	 * Launch the application.
	 * 
	 * @throws IOException           the structure of YAML file is not correct: it
	 *                               could be either lung model or archetype (even
	 *                               both)
	 * @throws FileNotFoundException one between the lung model and the archetype
	 *                               file (or both) is not located in config folder
	 * @throws InterruptedException  internal error
	 */
	public static void main(final String[] args) throws FileNotFoundException, IOException, InterruptedException {

		final LungSimulator mySimulator = new LungSimulator();
		mySimulator.simulateCircuit();

	}
}
