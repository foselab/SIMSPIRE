package simspire.swing.modelselection;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Manages file choices
 */
public class JFilePicker extends JPanel {

	/**
	 * File path
	 */
	private final transient JTextField textField;

	/**
	 * Component for file selection
	 */
	private final transient JFileChooser fileChooser;

	/**
	 * Set up file picker
	 * @param textFieldLabel label text
	 * @param buttonLabel button text
	 */
	public JFilePicker(final String textFieldLabel, final String buttonLabel) {

		fileChooser = new JFileChooser();

		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		// creates the GUI
		final JLabel label = new JLabel(textFieldLabel);

		textField = new JTextField(30);
		final JButton button = new JButton(buttonLabel);

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				buttonActionPerformed();
			}
		});

		add(label);
		add(textField);
		add(button);

	}

	private void buttonActionPerformed() {
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
		}
	}

	/**
	 * Set file restrictions
	 * @param extension accepted file extensions
	 * @param description file extensions description
	 */
	public void addFileTypeFilter(final String extension, final String description) {
		final FileTypeFilter filter = new FileTypeFilter(extension, description);
		fileChooser.addChoosableFileFilter(filter);
	}

	/**
	 * Get selected file path
	 * @return file path seleted
	 */
	public String getSelectedFilePath() {
		return textField.getText();
	}

	public JFileChooser getFileChooser() {
		return this.fileChooser;
	}
}
