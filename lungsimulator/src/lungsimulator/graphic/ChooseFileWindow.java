package lungsimulator.graphic;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ChooseFileWindow extends JComponent {
	JTextField patientPath;
	JTextField archetypePath;
	JTextField demographicPath;

	JPanel initPanel;

	int output;

	public ChooseFileWindow() {
		patientPath = new JTextField(35);
		archetypePath = new JTextField(35);
		demographicPath = new JTextField(35);

		initPanel = new JPanel();
		initPanel.setLayout(new BoxLayout(initPanel, BoxLayout.Y_AXIS));
		initPanel.add(new JLabel("NOTE: All files must be in config folder!"));
		initPanel.add(Box.createVerticalStrut(15));

		initPanel.add(new JLabel("Insert your lung model file name (e.g. myLungModel.yaml)"));
		initPanel.add(patientPath);
		initPanel.add(Box.createVerticalStrut(15));

		initPanel.add(new JLabel("Insert your archetype file name (e.g. myArcModel.yaml)"));
		initPanel.add(archetypePath);
		initPanel.add(Box.createVerticalStrut(15));

		initPanel.add(new JLabel("Insert your demographic data file name (e.g. myDemModel.yaml)"));
		initPanel.add(demographicPath);

		output = JOptionPane.showConfirmDialog(null, initPanel, "Please enter the path of your files: ",
				JOptionPane.OK_CANCEL_OPTION);
	}

	public String getResult() {
		String result = "";
		if (output == JOptionPane.OK_OPTION) {
			result = checkField(patientPath.getText()) + "," + checkField(archetypePath.getText()) + ","
					+ checkField(demographicPath.getText());
		}
		return result;
	}

	private String checkField(String text) {
		if(!text.contains("config/")) {
			text = "config/" + text;
		}
		
		if(!text.contains(".yaml")) {
			text = text + ".yaml";
		}
		return text;
	}
}
