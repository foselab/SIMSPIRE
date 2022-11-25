package gui;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		GraphicUserInterface userInterface = new GraphicUserInterface();
		
		userInterface.showSelectModelView();
		
		userInterface.showSimulationView();
	}
}
