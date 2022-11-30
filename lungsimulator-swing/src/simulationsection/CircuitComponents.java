package simulationsection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import components.CircuitElm;
import lungsimulator.CircuitBuilder;
import lungsimulator.LungSimulator;

/**
 * Show circuit component according to a id-value-unit structure
 */
public class CircuitComponents {

	/**
	 * Show ventilator value
	 */
	private transient CircuitElementRow ventilator;

	/**
	 * Ids of time dependent elements
	 */
	private transient List<String> timeDependentElms;

	/**
	 * Link between component's name and its graphic spinner
	 */
	private final transient Map<String, CircuitElementRow> components;

	/**
	 * Coordinate helper
	 */
	private transient int yInit;

	/**
	 * Init circuit components section
	 * 
	 * @param lungSimulator backend access
	 * @param leftPanel     panel where circuit components have to be shown
	 */
	public CircuitComponents(final LungSimulator lungSimulator, final JPanel leftPanel) {
		components = new HashMap<>();

		initComponents(lungSimulator, leftPanel);
	}

	private void initComponents(final LungSimulator lungSimulator, final JPanel leftPanel) {
		yInit = 27;

		int count = 0;
		final CircuitBuilder circuitBuilder = lungSimulator.getCircuitBuilder();

		if (circuitBuilder.isTimeDependentCir()) {
			timeDependentElms = circuitBuilder.getTimeDependentElm();
		}

		final int index = circuitBuilder.getVentilatorIndex();

		if (circuitBuilder.getElements() != null) {
			for (final CircuitElm element : circuitBuilder.getElements()) {
				final CircuitElementRow cer = new CircuitElementRow(lungSimulator, leftPanel, element.getId(),
						element.getValue(), element.getUnit(), index == count, count, yInit);

				if (index == count) {
					ventilator = cer;
				}

				count++;

				components.put(element.getId(), cer);
				yInit += 28;
			}
		}

	}

	/**
	 * Updates ventilator value
	 * @param value new ventilator value
	 */
	public void updateVentilator(final double value) {
		ventilator.setVentilator(value);
	}

	/**
	 * Updates time dependent elements
	 */
	public void updateTimeDependentElms() {
		for (final Map.Entry<String, CircuitElementRow> entry : components.entrySet()) {
			if (timeDependentElms.contains(entry.getKey())) {
				entry.getValue().updateElmValue();
			}
		}

	}

	public int getyInit() {
		return yInit;
	}

}
