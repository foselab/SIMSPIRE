package data;

import java.util.ArrayList;
import java.util.Arrays;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;

import lungsimulator.LungSimulator;

public class DemographicEventManager {
	private LungSimulator lungSimulator;
	private ComboBox<String> gender;
	private NumberField age; 
	private NumberField height;
	private NumberField weight;
	private NumberField ibw;

	public DemographicEventManager(LungSimulator lungSimulator) {
		this.lungSimulator = lungSimulator;
	}

	public void setGender(String selectedValue) {
		gender = new ComboBox<>();
		gender.setItems(new ArrayList<>(Arrays.asList("Male", "Female")));
		gender.setValue(selectedValue.equalsIgnoreCase("female") ? "Female" : "Male");
		gender.setWidth("130px");
		gender.addValueChangeListener(event -> {
			// switch gender
			lungSimulator.getDemographicData().setGender(event.getValue());
		});
	}
	
	public void setAge(double min, double max, double step, double value) {
		age = new NumberField();
		age.setHasControls(true);
		age.setMin(min);
		age.setMax(max);
		age.setStep(step);
		age.setValue(value);
		age.addValueChangeListener(event -> {
			double val = event.getValue();
			lungSimulator.getDemographicData().setAge((int) val);
		});
	}
	
	public void setHeight(double min, double max, double step, double value) {
		height = new NumberField();
		height.setHasControls(true);
		height.setMin(min);
		height.setMax(max);
		height.setStep(step);
		height.setValue(value);
		height.addValueChangeListener(event -> {
			lungSimulator.getDemographicData().setHeight(event.getValue());
			ibw.setValue(lungSimulator.getDemographicData().getIbw());
		});
	}
	
	public void setWeight(double min, double max, double step, double value) {
		weight = new NumberField();
		weight.setHasControls(true);
		weight.setMin(min);
		weight.setMax(max);
		weight.setStep(step);
		weight.setValue(value);
		weight.addValueChangeListener(event -> {
			lungSimulator.getDemographicData().setWeight(event.getValue());
		});
	}

	public void setIbw(double value) {
		ibw = new NumberField();
		ibw.setHasControls(false);
		ibw.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);
		ibw.setValue(value);
		ibw.setReadOnly(true);
	}
	
	public ComboBox<String> getGender() {
		return gender;
	}

	public NumberField getAge() {
		return age;
	}

	public NumberField getHeight() {
		return height;
	}

	public NumberField getWeight() {
		return weight;
	}

	public NumberField getIbw() {
		return ibw;
	}
}
