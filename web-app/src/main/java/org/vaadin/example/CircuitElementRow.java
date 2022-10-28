package org.vaadin.example;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;

public class CircuitElementRow extends Composite<Component>{
	String id;
	double value;
	String unit;
	
	public CircuitElementRow(String id,	double value, String unit) {
		this.id = id;
		this.value = value;
		this.unit = unit;
	}
	
	public Component initContent() {
		Label elementName = new Label(id);
		elementName.setWidth("250px");
		
		NumberField elementValue = new NumberField();
		elementValue.setHasControls(true);
		elementValue.setMin(0.0);
		elementValue.setMax(50);
		elementValue.setStep(0.01);
		elementValue.setValue(value);
		elementValue.addValueChangeListener(event -> {value = event.getValue();});
		elementValue.setHeight("10px");
		
		Label elementUnit = new Label(unit);
		
		return new HorizontalLayout(elementName, elementValue, elementUnit);
	}
}
