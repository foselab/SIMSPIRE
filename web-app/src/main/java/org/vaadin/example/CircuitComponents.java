package org.vaadin.example;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import components.CircuitElm;
import lungsimulator.LungSimulator;

public class CircuitComponents extends Composite<Component>{
	private LungSimulator lungSimulator;
	VerticalLayout vl = new VerticalLayout();
	
	public CircuitComponents(LungSimulator lungSimulator) {
		this.lungSimulator = lungSimulator;
	}

	protected Component initContent() {
		if(lungSimulator != null) {
			if(lungSimulator.getCircuitBuilder() != null) {
				if(lungSimulator.getCircuitBuilder().getElements() != null) {
					for(CircuitElm element: lungSimulator.getCircuitBuilder().getElements()) {
						CircuitElementRow cer = new CircuitElementRow(element.getId(), element.getValue(), "loading");
						vl.add(cer);
					}
				}
			}
		}
		
		return vl;
	}
}
