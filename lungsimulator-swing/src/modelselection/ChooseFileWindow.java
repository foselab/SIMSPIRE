package modelselection;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Custom window for file choice
 */
public class ChooseFileWindow extends JFrame{
	
	private InputStream patientStream;
	private InputStream archetypeStream;
	private InputStream demoDataStream;

	public ChooseFileWindow() {
		
		JPanel initPanel = new JPanel();
		
		JFilePicker patientFilePicker = new JFilePicker("Lung model: ", "Browse");
		JFilePicker archetypeFilePicker = new JFilePicker("Archetype: ", "Browse");
		JFilePicker demoDataFilePicker = new JFilePicker("Patient data: ", "Browse");

		// set up layout
		initPanel.setLayout(new GridBagLayout());
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 5, 5);

		// set up components
		patientFilePicker.addFileTypeFilter(".yaml", "YAML files");
		patientFilePicker.setMode(JFilePicker.MODE_OPEN);
		
		archetypeFilePicker.addFileTypeFilter(".yaml", "YAML files");
		archetypeFilePicker.setMode(JFilePicker.MODE_OPEN);
		
		demoDataFilePicker.addFileTypeFilter(".yaml", "YAML files");
		demoDataFilePicker.setMode(JFilePicker.MODE_OPEN);

		// add components to the frame
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
		initPanel.add(archetypeFilePicker, constraints);
		
		constraints.gridx = 0;
		constraints.gridy = 3;
		constraints.weightx = 0.0;
		constraints.gridwidth = 2;
		constraints.fill = GridBagConstraints.NONE;
		initPanel.add(demoDataFilePicker, constraints);

		int output = JOptionPane.showConfirmDialog(null, initPanel, "Upload custom model",
				JOptionPane.OK_CANCEL_OPTION);
		
		if (output == JOptionPane.OK_OPTION) {
			String patientFilePath = patientFilePicker.getSelectedFilePath();
			String archetypeFilePath = archetypeFilePicker.getSelectedFilePath();
			String demoDataFilePath = demoDataFilePicker.getSelectedFilePath();

			if (patientFilePath.equals("") || archetypeFilePath.equals("") || demoDataFilePath.equals("")) {
				JOptionPane.showMessageDialog(this, "Please choose a file to upload!", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}

			try {
				File patientFile = new File(patientFilePath);
				patientStream = new FileInputStream(patientFile);
				
				File archetypeFile = new File(archetypeFilePath);
				archetypeStream = new FileInputStream(archetypeFile);
				
				File demoDataFile = new File(demoDataFilePath);
				demoDataStream = new FileInputStream(demoDataFile);
				
			} catch (Exception ex) {
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
