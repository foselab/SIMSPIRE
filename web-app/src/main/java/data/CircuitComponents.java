package data;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import components.CircuitElm;
import lungsimulator.LungSimulator;

public class CircuitComponents extends Composite<Component>{
	private LungSimulator lungSimulator;
	VerticalLayout verticalLayout; 
	private int ventilatorIndex;
	CircuitElementRow ventilator;
	
	public CircuitComponents(LungSimulator lungSimulator) {
		this.lungSimulator = lungSimulator;
		verticalLayout = new VerticalLayout();
	}

	protected Component initContent() {
		int count = 0;
		int index;
		if(lungSimulator != null) {
			if(lungSimulator.getCircuitBuilder() != null) {
				index = lungSimulator.getCircuitBuilder().getVentilatorIndex();
				if(lungSimulator.getCircuitBuilder().getElements() != null) {
					for(CircuitElm element: lungSimulator.getCircuitBuilder().getElements()) {
						CircuitElementRow cer = new CircuitElementRow(lungSimulator, element.getId(), element.getValue(), element.getUnit(), index == count, count);
						if(index == count) {
							ventilator = cer;
						}
						count++;
						verticalLayout.add(cer);
					}
				}
			}
		}
		
		return verticalLayout;
	}
	
	public void updateVentilator(double value) {
		ventilator.setVentilator(value);
	}
}
