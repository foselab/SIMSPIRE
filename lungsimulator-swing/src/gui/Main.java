package gui;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Main class
 */
public class Main {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		final GraphicUserInterface userInterface = new GraphicUserInterface();
		
		userInterface.showSelectModelView();
		
		userInterface.showSimulationView();
	}
}
