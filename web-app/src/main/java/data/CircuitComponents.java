package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import components.CircuitElm;
import lungsimulator.LungSimulator;

public class CircuitComponents extends Composite<Component>{
	private LungSimulator lungSimulator;
	VerticalLayout verticalLayout; 
	CircuitElementRow ventilator;
	List<String> timeDependentElms;
	Map<String, CircuitElementRow> components;
	
	public CircuitComponents(LungSimulator lungSimulator) {
		this.lungSimulator = lungSimulator;
		components = new HashMap<>();
		verticalLayout = new VerticalLayout();
	}

	protected Component initContent() {
		int count = 0;
		int index;
		if(lungSimulator != null) {
			if(lungSimulator.getCircuitBuilder() != null) {
				
				if(lungSimulator.getCircuitBuilder().isTimeDependentCir()) {
					timeDependentElms = lungSimulator.getCircuitBuilder().getTimeDependentElm();
				}
				
				index = lungSimulator.getCircuitBuilder().getVentilatorIndex();
				if(lungSimulator.getCircuitBuilder().getElements() != null) {
					for(CircuitElm element: lungSimulator.getCircuitBuilder().getElements()) {
						CircuitElementRow cer = new CircuitElementRow(lungSimulator, element.getId(), element.getValue(), element.getUnit(), index == count, count);
						if(index == count) {
							ventilator = cer;
						}
						count++;
						components.put(element.getId(), cer);
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

	public void updateTimeDependentElms() {
		for(Map.Entry<String, CircuitElementRow> entry: components.entrySet()) {
			if(timeDependentElms.contains(entry.getKey())) {
				entry.getValue().updateElmValue();
			}
		}
		
	}
}
