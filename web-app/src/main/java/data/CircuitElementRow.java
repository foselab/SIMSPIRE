package data;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextFieldVariant;

public class CircuitElementRow extends Composite<Component> {
	String id;
	double value;
	String unit;
	boolean isVentilator;
	NumberField ventilator;

	public CircuitElementRow(String id, double value, String unit, boolean isVentilator) {
		this.id = id;
		this.value = value;
		this.unit = unit;
		this.isVentilator = isVentilator;
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
