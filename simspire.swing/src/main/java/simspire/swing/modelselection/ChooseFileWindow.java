package simspire.swing.modelselection;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Custom window for file choice
 */
public class ChooseFileWindow extends JFrame {

	/**
	 * Input stream for patient data
	 */
	private transient InputStream patientStream;

	/**
	 * Input stream for archetype data
	 */
	private transient InputStream archetypeStream;

	/**
	 * Input stream for demographic data
	 */
	private transient InputStream demoDataStream;

	/**
	 * Init the file chooser manager
	 */
	public ChooseFileWindow() {

		final JPanel initPanel = new JPanel();

		final JFilePicker patientFilePicker = new JFilePicker("Lung model: ", "Browse");
		final JFilePicker archFilePicker = new JFilePicker("Archetype: ", "Browse");
		final JFilePicker demoFilePicker = new JFilePicker("Patient data: ", "Browse");

		// set up layout
		initPanel.setLayout(new GridBagLayout());
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 5, 5);

		// set up circuitsimulator.components
		patientFilePicker.addFileTypeFilter(".yaml", "YAML files");
		archFilePicker.addFileTypeFilter(".yaml", "YAML files");
		demoFilePicker.addFileTypeFilter(".yaml", "YAML files");

		// add circuitsimulator.components to the frame
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 0.0;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.NONE;
		initPanel.add(patientFilePicker, constraints);

		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.weightx = 0.0;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.NONE;
		initPanel.add(archFilePicker, constraints);

		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.weightx = 0.0;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.NONE;
		initPanel.add(demoFilePicker, constraints);

		final int output = JOptionPane.showConfirmDialog(null, initPanel, "Upload custom model",
				JOptionPane.OK_CANCEL_OPTION);

		if (output == JOptionPane.OK_OPTION) {
			final String patientFilePath = patientFilePicker.getSelectedFilePath();
			final String archetypeFilePath = archFilePicker.getSelectedFilePath();
			final String demoDataFilePath = demoFilePicker.getSelectedFilePath();

			try {
				patientStream = Files.newInputStream(Paths.get(patientFilePath)); 
				archetypeStream = Files.newInputStream(Paths.get(archetypeFilePath));
				demoDataStream = Files.newInputStream(Paths.get(demoDataFilePath)); 

			} catch (IOException ex) {
				JOptionPane.showMessageDialog(this, "Error executing upload task: " + ex.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public InputStream getPatientStream() {
		return patientStream;
	}

	public InputStream getArchetypeStream() {
		return archetypeStream;
	}

	public InputStream getDemoDataStream() {
		return demoDataStream;
	}
}
