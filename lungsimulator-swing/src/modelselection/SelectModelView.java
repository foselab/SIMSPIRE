package modelselection;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

/**
 * Manages select model view
 */
public class SelectModelView {
	/**
	 * Available model options
	 */
	final private static String[] MODELS = { "Model of Albanese", "Model of Al-Naggar", "Model of Baker",
			"Model of Jain", "Model of Campbell-Brown", "Your own model..." };

	/**
	 * Selected model
	 */
	private final transient String chosenModel;

	/**
	 * Select model view set up
	 */
	public SelectModelView() {
		final Object choice = JOptionPane.showInputDialog(null, "Select model", "ChooseModel",
				JOptionPane.PLAIN_MESSAGE, null, MODELS, MODELS[0]);

		final String model = String.valueOf(choice);

		if ("Your own model...".equals(model)) {
			chosenModel = model;
		} else {
			chosenModel = model.replace("Model of ", "");
		}
	}

	public String getChosenModel() {
		return chosenModel;
	}

	/**
	 * Get input stream of custom model files
	 * 
	 * @return list of input streams
	 */
	public List<InputStream> getCustomFiles() {
		final ChooseFileWindow fileUploader = new ChooseFileWindow();
		return new ArrayList<InputStream>(Arrays.asList(fileUploader.getPatientStream(),
				fileUploader.getArchetypeStream(), fileUploader.getDemoDataStream()));
	}
}
