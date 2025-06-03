package simspire.web.models;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.function.SerializableRunnable;

import lungsimulator.LungSimulator;

/**
 * Custom component for model selection
 */
public class ChooseModelForm extends Composite<VerticalLayout> implements HasComponents {
	/**
	 * Backend access
	 */
	private final transient LungSimulator lungSimulator;

	/**
	 * Component for uploading custom model files
	 */
	private final transient FileUploader uploader;

	/**
	 * List of available simspire.web.models
	 */
	final static private String[] MODELS = { "Model of Albanese", "Model of Al-Naggar", "Model of Baker",
			"Model of Jain", "Model of Campbell-Brown", "Your own model..." };

	/**
	 * Builds the form for model selection
	 * 
	 * @param lungSimulator backend access
	 * @param saveListener  instructions that have to be executed after model
	 *                      selection
	 */
	public ChooseModelForm(final LungSimulator lungSimulator, final SerializableRunnable saveListener) {
		this.lungSimulator = lungSimulator;

		final H3 title = new H3("Select a model");
		final ComboBox<String> modelBox = new ComboBox<>("Available simspire.web.models");
		modelBox.setItems(MODELS);
		modelBox.setAllowCustomValue(false); // custom values are not allowed
		modelBox.setRequired(true); // there must be a value selected
		modelBox.addValueChangeListener(event -> {
			try {
				analyzeChoice(event.getValue());
			} catch (FileNotFoundException e) {
				Notification.show("File not found");
			} catch (IOException e) {
				Notification.show("Incorrect file structure");
			}
		});

		final TextArea info = new TextArea();
		info.setValue("You can either pick a default model or upload your own model");
		info.setReadOnly(true);

		uploader = new FileUploader(lungSimulator);
		uploader.setVisible(false);

		final Button saveButton = new Button("OK", event -> {
			if (modelBox.getValue() == null) {
				Notification.show("A model must be selected");
			} else {
				saveListener.run();
			}
		});

		add(title, modelBox, info, uploader, saveButton);
	}

	private void analyzeChoice(final String value) throws FileNotFoundException, IOException {
		if (value != null) {
			if ("Your own model...".equals(value)) {
				uploader.setVisible(true);
			} else {
				uploader.setVisible(false);
				lungSimulator.initSchema(value.replace("Model of ", ""));
			}
		} else {
			Notification.show("A model must be selected");
		}
	}

}
