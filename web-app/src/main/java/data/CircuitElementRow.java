package data;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextFieldVariant;

import lungsimulator.LungSimulator;

public class CircuitElementRow extends Composite<Component> {
	String id;
	double value;
	String unit;
	boolean isVentilator;
	NumberField ventilator;
	int posNumber;
	LungSimulator lungSimulator;
	
	public CircuitElementRow(LungSimulator lungSimulator, String id, double value, String unit, boolean isVentilator, int posNumber) {
		this.lungSimulator = lungSimulator;
		this.id = id;
		this.value = value;
		this.unit = unit;
		this.isVentilator = isVentilator;
		this.posNumber = posNumber;
	}

	public Component initContent() {
		Label elementName = new Label(id);
		elementName.setWidth("250px");

		NumberField elementValue = new NumberField();
		elementValue.setMin(0.0);
		elementValue.setMax(50);
		elementValue.setStep(0.01);
		elementValue.setValue(value);
		if (isVentilator) {
			elementValue.setHasControls(false);
			elementValue.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER);
			elementValue.setReadOnly(true);
			ventilator = elementValue;
		} else {
			elementValue.setHasControls(true);
			elementValue.addValueChangeListener(event -> {
				value = event.getValue();
				lungSimulator.getCircuitBuilder().updateElementValue(value, posNumber);
				Notification.show("Element updated");
			});
		}
		elementValue.setHeight("10px");

		Label elementUnit = new Label(unit);

		return new HorizontalLayout(elementName, elementValue, elementUnit);
	}

	public double getVentilator() {
		return ventilator == null ? -1 : ventilator.getValue();
	}

	public void setVentilator(double value) {
		if (ventilator != null) {
			ventilator.setValue(value);
		}
	}
}
