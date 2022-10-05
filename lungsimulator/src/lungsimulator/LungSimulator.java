package lungsimulator;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.json.simple.parser.ParseException;
import lungsimulator.components.Archetype;
import lungsimulator.components.Patient;
import lungsimulator.utils.YamlReader;
import simulator.CirSim;

public class LungSimulator {
	// Define the new plotter
	RealTimePlot rtp = new RealTimePlot();
	
	public Patient patient;
	public Archetype archetype;
	public GraphicInterface userInterface = new GraphicInterface();
	
	/**
	 * Init the lung simulator by reading and validating the patient model and
	 * archetype and set the frame configuration
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * @throws Exception
	 */
	public LungSimulator() throws FileNotFoundException, IOException, ParseException, Exception {
		// Read patient model and patient archetype
		YamlReader yamlReader = new YamlReader();
		patient = yamlReader.readPatientModel();
		archetype = yamlReader.readArchetypeParameters();

		// Validation
		yamlReader.validator(patient, archetype);

		// Frame configuration
		userInterface.frameConfig(patient, archetype);
	}
	
	public void simulateCircuit() throws InterruptedException {
		// Create the circuit equivalent to the lung
		CirSim myCircSim = rtp.buildCircuitSimulator(patient, archetype);
		myCircSim.setTimeStep(0.01);

		double time;

		while (true) {
			time = rtp.getElapsedSeconds();
			
			if(time%2==0) {
			archetype.getParameters().put("TIME", String.valueOf(time));
			myCircSim = rtp.updateCircuitSimulator(patient, archetype);
			}
			
			// Analyze the circuit and simulate a step
			myCircSim.analyzeCircuit();
			myCircSim.loopAndContinue(true);

			/* After having analyzed the circuit, then check if a new cycle has started
			lastCycleTime = rtp.getElapsedSeconds() - startCycleTime;
			startCycleTime = rtp.getElapsedSeconds();*/

			//Thread.sleep(10);
			
			userInterface.updateShownDataValues(time, myCircSim);

		}

	}

	/**
	 * Launch the application.
	 * 
	 * @throws ParseException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 */
	public static void main(String[] args)
			throws FileNotFoundException, IOException, ParseException, InterruptedException, Exception {

		LungSimulator mySimulator = new LungSimulator();
		mySimulator.simulateCircuit();
	}
}
