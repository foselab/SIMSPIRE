package models;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
		
		uploader = new FileUploader(lungSimulator);
        uploader.setVisible(false);
        
		Button saveButton = new Button("OK", event -> {
			saveListener.run();
		});
		
		return new VerticalLayout(title, modelBox, uploader, saveButton);
	}

	private void analyzeChoice(ComponentValueChangeEvent<ComboBox<String>, String> event) throws FileNotFoundException, IOException {
		if (event.getSource() != null) {
			String value = event.getValue();

			if (value.equals("Your own model...")) {
				uploader.setVisible(true);
			} else {
				lungSimulator.initSchema(value.replace("Model of ", ""));
			}
		}
	}

}
