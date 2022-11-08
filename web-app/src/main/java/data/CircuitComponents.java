package data;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import components.CircuitElm;
import lungsimulator.LungSimulator;

public class CircuitComponents extends Composite<Component>{
	private LungSimulator lungSimulator;
	VerticalLayout vl = new VerticalLayout();
	private int ventilatorIndex;
	private double currentVentilatorValue;
	
	public CircuitComponents(LungSimulator lungSimulator) {
		this.lungSimulator = lungSimulator;
	}

	protected Component initContent() {
		int count = 0;
		int index;
		if(lungSimulator != null) {
			if(lungSimulator.getCircuitBuilder() != null) {
				index = lungSimulator.getCircuitBuilder().getVentilatorIndex();
				if(lungSimulator.getCircuitBuilder().getElements() != null) {
					for(CircuitElm element: lungSimulator.getCircuitBuilder().getElements()) {
						CircuitElementRow cer = new CircuitElementRow(element.getId(), element.getValue(), element.getUnit(), index == count);
						if(index == count) {
							ventilatorIndex = count;
						}
						count++;
						vl.add(cer);
					}
				}
			}
		}
		
		return vl;
	}
}
