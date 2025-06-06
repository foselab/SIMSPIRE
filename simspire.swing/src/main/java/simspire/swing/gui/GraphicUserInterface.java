package simspire.swing.gui;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

import lungsimulator.LungSimulator;
import simspire.swing.modelselection.SelectModelView;
import simspire.swing.simulationsection.SimulationView;

/**
 * Manages graphic interface flow
 */
public class GraphicUserInterface {

	/**
	 * Select model view manager
	 */
	private final SelectModelView selectModelView;

	/**
	 * Backend access
	 */
	private final transient LungSimulator lungSimulator;

	/**
	 * Internal logger for errors report
	 */
	private static final Logger LOGGER = Logger.getLogger(GraphicUserInterface.class.getName());

	/**
	 * Init class fields
	 */
	public GraphicUserInterface() {
		lungSimulator = new LungSimulator();
		selectModelView = new SelectModelView();
	}

	/**
	 * Shows model selection view
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void showSelectModelView() throws FileNotFoundException, IOException {
		final String chosenModel = selectModelView.getChosenModel();

		if ("Your own model...".equals(chosenModel)) {
			final List<InputStream> customfileList = selectModelView.getCustomFiles();
			lungSimulator.initCustomPatient(customfileList.get(0));
			lungSimulator.initCustomArchetype(customfileList.get(1));
			lungSimulator.initCustomDemographic(customfileList.get(2));
		} else {
			lungSimulator.initSchema(chosenModel);
		}

		lungSimulator.modelValidation();
		lungSimulator.simulationSetUp();
	}

	/**
	 * Init simulation view
	 */
	public void showSimulationView() {
		final SimulationView simulationView = new SimulationView(lungSimulator);
		final boolean isTimeDependent = lungSimulator.getCircuitBuilder().isTimeDependentCir();

		// moment of time (in seconds) where simulation starts
		final double tStart = System.currentTimeMillis() / 1000.0;
		double lastT = 0;
		final double step = 0.1;
		double ntStart;
		double initialT;

		while (simulationView.isWindowIsOpen()) {
			if (simulationView.getStateOfExecution()) {
				ntStart = System.currentTimeMillis() / 1000.0;
				initialT = ntStart - tStart;
				// wait for step seconds until next resolution
				if (initialT - lastT >= step) {
					// update backend
					lungSimulator.miniSimulation(initialT, step);

					// update frontend
					if (isTimeDependent) {
						simulationView.updateTimeDependentElms();
					}
					simulationView.updateVentilator(lungSimulator.getCircuitBuilder().getCurrentVentValue());
					simulationView.updateCharts(lungSimulator);
					lastT = initialT;
				} else {
					try {
						Thread.sleep((long) step);
					} catch (InterruptedException e) {
						LOGGER.info("Simulation error");
					}
				}
			}
		}
	}

}
