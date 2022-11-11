package models;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.function.SerializableRunnable;

import lungsimulator.LungSimulator;

public class ChooseModelForm extends Composite<Component> {
	private final LungSimulator lungSimulator;
	private final SerializableRunnable saveListener;

	private ComboBox<String> modelBox = new ComboBox<>("Available models");
	FileUploader uploader;

	// configuration options
	final static String[] models = { "Model of Albanese", "Model of Al-Naggar", "Model of Baker", "Model of Jain",
			"Model of Campbell-Brown", "Your own model..." };

	public ChooseModelForm(LungSimulator lungSimulator, SerializableRunnable saveListener) {
		this.lungSimulator = lungSimulator;
		this.saveListener = saveListener;
	}

	@Override
	protected Component initContent() {
		H3 title = new H3("Select a model");
		modelBox.setItems(models);
		modelBox.setAllowCustomValue(false); // custom values are not allowed
		modelBox.setRequired(true); // there must be a value selected
		modelBox.addValueChangeListener(event -> {
			try {
				analyzeChoice(event);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		TextArea info = new TextArea();
		info.setValue("You can either pick a default model or upload your own model");
		info.setReadOnly(true);
		
		uploader = new FileUploader(lungSimulator);
        uploader.setVisible(false);
        
		Button saveButton = new Button("OK", event -> {
			if(modelBox.getValue() == null) {
				Notification.show("A model must be selected");
			}else {
			saveListener.run();}
		});
		
		return new VerticalLayout(title, modelBox, info, uploader, saveButton);
	}

	private void analyzeChoice(ComponentValueChangeEvent<ComboBox<String>, String> event) throws FileNotFoundException, IOException {
		String value = event.getValue();
		
		if (value != null) {
			if (value.equals("Your own model...")) {
				uploader.setVisible(true);
			} else {
				lungSimulator.initSchema(value.replace("Model of ", ""));
			}
		}else {
			Notification.show("A model must be selected!");
		}
	}

}
