package simspire.web.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import circuitsimulator.components.CircuitElm;
import lungsimulator.CircuitBuilder;
import lungsimulator.LungSimulator;

/**
 * Manages the circuit circuitsimulator.components section of the simulation simspire.web.view
 */
public class CircuitComponents extends Composite<VerticalLayout> implements HasComponents {
	/**
	 * Reference to the ventilator value shown in the simulation simspire.web.view
	 */
	private transient CircuitElementRow ventilator;

	/**
	 * List of time dependent elements
	 */
	private transient List<String> timeDependentElms;

	/**
	 * List of circuit elements
	 */
	private final transient Map<String, CircuitElementRow> components;

	/**
	 * Init the circuit circuitsimulator.components section
	 * 
	 * @param lungSimulator backend access
	 */
	public CircuitComponents(final LungSimulator lungSimulator) {
		components = new HashMap<>();

		final CircuitBuilder circuitBuilder = lungSimulator.getCircuitBuilder();

		if (circuitBuilder.isTimeDependentCir()) {
			timeDependentElms = circuitBuilder.getTimeDependentElm();
		}

		final int index = circuitBuilder.getVentilatorIndex();

		final List<CircuitElm> circuitElements = circuitBuilder.getElements();
		CircuitElementRow cer;
		
		for (int i = 0; i < circuitElements.size(); i++) {
			final CircuitElm element = circuitElements.get(i);
			
			cer = new CircuitElementRow(lungSimulator, element.getId(), element.getValue(),
					element.getUnit(), index == i, i);
			
			if (index == i) {
				ventilator = cer;
			}

			components.put(element.getId(), cer);
			add(cer);
		}
	}

	/**
	 * Updates ventilator value
	 * 
	 * @param value ventilator value
	 */
	public void updateVentilator(final double value) {
		ventilator.setVentilator(value);
	}

	/**
	 * Update time dependent elements
	 */
	public void updateTimeDependentElms() {
		for (final Map.Entry<String, CircuitElementRow> entry : components.entrySet()) {
			if (timeDependentElms.contains(entry.getKey())) {
				entry.getValue().updateElmValue();
			}
		}

	}
}
