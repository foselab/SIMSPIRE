package lungsimulator.graphic;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;

/**
 * Custom window for file choice
 */
public class ChooseFileWindow extends JComponent {
	/**
	 * Serial version UID constant
	 */
	private static final long serialVersionUID = -8783191093656773534L;

	/**
	 * Text field for custom patient model path
	 */
	private final transient JTextField patientPath;
	
	/**
	 * Text field for custom archetype file path
	 */
	private final transient JTextField archetypePath;
	
	/**
	 * Text field for custom demographic data path
	 */
	private final transient JTextField demographicPath;

	/**
	 * User choice (OK button or cancel button)
	 */
	private final transient int output;

	/**
	 * Choose file window layout set up
	 */
	public ChooseFileWindow() {
		super();
		patientPath = new JTextField(35);
		archetypePath = new JTextField(35);
		demographicPath = new JTextField(35);
		
		JFileChooser filechooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

		final JPanel initPanel = new JPanel();
		//initPanel.setLayout(new BoxLayout(initPanel, BoxLayout.Y_AXIS));
		initPanel.add(Box.createVerticalStrut(15));

		initPanel.add(new JLabel("Insert your lung model file name (e.g. myLungModel.yaml)"));
		JButton chooseFile = new JButton("Choose...");
		initPanel.add(chooseFile);
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

	/**
	 * Checks input and return the result
	 * @return a string with all file names separated by comma
	 */
	public String getResult() {
		String result;
		if (output == JOptionPane.OK_OPTION) {
			result = checkField(patientPath.getText()) + "," + checkField(archetypePath.getText()) + ","
					+ checkField(demographicPath.getText());
		}else {
			result = "";
		}
		
		return result;
	}

	private String checkField(final String text) {
		String correctText = text;
		
		if(!text.contains("config/")) {
			correctText = "config/" + text;
		}
		
		if(!text.contains(".yaml")) {
			correctText += ".yaml";
		}
		
		return correctText;
	}
}
