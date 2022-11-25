package gui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import lungsimulator.LungSimulator;
import modelselection.SelectModelView;

public class GraphicUserInterface {

	SelectModelView selectModelView;
	LungSimulator lungSimulator;

	public GraphicUserInterface() {
		lungSimulator = new LungSimulator();
		selectModelView = new SelectModelView();
	}

	public void showSelectModelView() throws FileNotFoundException, IOException {
		String chosenModel = selectModelView.getChosenModel();

		if ("Your own model...".equals(chosenModel)) {
			List<InputStream> customfileList = selectModelView.getCustomFiles();
			lungSimulator.initCustomPatient(customfileList.get(0));
			lungSimulator.initCustomArchetype(customfileList.get(1));
			lungSimulator.initCustomDemographic(customfileList.get(2));
		} else {
			lungSimulator.initSchema(chosenModel);
		}

		lungSimulator.modelValidation();
		lungSimulator.simulationSetUp();
	}

	public void showSimulationView() {
		//init simulationview
		
		// moment of time (in seconds) where simulation starts
		final double tStart = System.currentTimeMillis() / 1000.0;
		double lastT = 0;
		final double step = 0.1;
		double ntStart;
		double initialT;
		
		while (isWindowOpen()) {
			if (getStateOfExecution()) {
				ntStart = System.currentTimeMillis() / 1000.0;
				initialT = ntStart - tStart;
				// wait for step seconds until next resolution
				if (initialT - lastT >= step) {
					lungSimulator.miniSimulation(initialT, step);
					//update gui
				} else {
					try {
						Thread.sleep((long) step);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	private boolean getStateOfExecution() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isWindowOpen() {
		// TODO Auto-generated method stub
		return false;
	}

}
