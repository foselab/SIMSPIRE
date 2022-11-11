package data;

import java.util.ArrayList;
import java.util.Arrays;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextFieldVariant;

import lungsimulator.LungSimulator;

/**
 * Demographic data set up and event manager
 */
public class DemographicEventManager {
	/**
	 * Backend access
	 */
	final private transient LungSimulator lungSimulator;

	/**
	 * Gender options
	 */
	private ComboBox<String> gender;

	/**
	 * Age field
	 */
	private transient IntegerField age;

	/**
	 * Height field
	 */
	private transient NumberField height;

	/**
	 * Weight field
	 */
	private transient NumberField weight;

	/**
	 * Ibw field
	 */
	private transient NumberField ibw;

	/**
	 * Set backend access
	 * 
	 * @param lungSimulator backend access
	 */
	public DemographicEventManager(final LungSimulator lungSimulator) {
		this.lungSimulator = lungSimulator;
	}

	public void setGender(final String selectedValue) {
		gender = new ComboBox<>();
		gender.setAllowCustomValue(false); // custom values are not allowed
		gender.setRequired(true); // there must be a value selected
		gender.setItems(new ArrayList<>(Arrays.asList("Male", "Female")));
		gender.setValue(selectedValue.equalsIgnoreCase("female") ? "Female" : "Male");
		gender.setWidth("130px");
		gender.addValueChangeListener(event -> {
			final String newValue = event.getValue();
			final String oldValue = event.getOldValue();

			// switch gender
			if (newValue != null) {
				if (!newValue.equalsIgnoreCase(oldValue)) {
					lungSimulator.getDemographicData().setGender(event.getValue());
				}
			} else {
				Notification.show("Please pick a valid option for gender field");
				gender.setValue(oldValue);
			}
		});
	}

	/**
	 * Age field set up
	 * @param min minimum value for age field
	 * @param max maximum value for age field
	 * @param step step for age field
	 * @param value current value for age field
	 */
	public void setAge(final int min, final int max, final int step, final int value) {
		age = new IntegerField();
		age.setHasControls(true);
		age.setMin(min);
		age.setMax(max);
		age.setStep(step);
		age.setValue(value);
		age.addValueChangeListener(event -> {
			final int newValue = event.getValue();
			final int oldValue = event.getOldValue();
			if (newValue >= min && newValue <= max) {
				if (oldValue != newValue) {
					lungSimulator.getDemographicData().setAge(newValue);
				}
			} else {
				Notification.show("The age value must be between " + min + " and " + max);
				age.setValue(oldValue);
			}
		});
	}

	/**
	 * Height field set up
	 * @param min minimum value for height field
	 * @param max maximum value for height field
	 * @param step step for height field
	 * @param value current value for height field
	 */
	public void setHeight(final double min, final double max, final double step, final double value) {
		height = new NumberField();
		height.setHasControls(true);
		height.setMin(min);
		height.setMax(max);
		height.setStep(step);
		height.setValue(value);
		height.addValueChangeListener(event -> {
			final double newValue = event.getValue();
			final double oldValue = event.getOldValue();
			if (newValue >= min && newValue <= max) {
				if (oldValue != newValue) {
					lungSimulator.getDemographicData().setHeight(event.getValue());
					ibw.setValue(lungSimulator.getDemographicData().getIbw());
				}
			} else {
				Notification.show("The height value must be between " + min + " and " + max);
				height.setValue(oldValue);
			}
		});
	}

	/**
	 * Weight field set up
	 * @param min minimum value for weight field
	 * @param max maximum value for weight field
	 * @param step step for weight field
	 * @param value current value for weight field
	 */
	public void setWeight(final double min, final double max, final double step, final double value) {
		weight = new NumberField();
		weight.setHasControls(true);
		weight.setMin(min);
		weight.setMax(max);
		weight.setStep(step);
		weight.setValue(value);
		weight.addValueChangeListener(event -> {
			final double newValue = event.getValue();
			final double oldValue = event.getOldValue();
			if (newValue >= min && newValue <= max) {
				if (oldValue != newValue) {
					lungSimulator.getDemographicData().setWeight(event.getValue());
				}
			} else {
				Notification.show("The weight value must be between " + min + " and " + max);
				weight.setValue(oldValue);
			}
		});
	}

	/**
	 * Ibw field set up
	 * @param value	current value for ibw field
	 */
	public void setIbw(final double value) {
		ibw = new NumberField();
		ibw.setHasControls(false);
		ibw.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);
		ibw.setValue(value);
		ibw.setReadOnly(true);
	}

	public ComboBox<String> getGender() {
		return gender;
	}

	public IntegerField getAge() {
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
