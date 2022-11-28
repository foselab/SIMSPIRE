package simulationsection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import components.CircuitElm;
import lungsimulator.LungSimulator;

/**
 * Show circuit component according to a id-value-unit structure
 */
public class CircuitComponents {

	/**
	 * Show ventilator value
	 */
	private transient CircuitElementRow ventilator;

	private List<String> timeDependentElms;

	private Map<String, CircuitElementRow> components;
	
	private int yInit;

	public CircuitComponents(LungSimulator lungSimulator, JPanel leftPanel) {
		/*TitledBorder cirTitle = BorderFactory.createTitledBorder("Circuit components");
		circElmPanel.setBorder(cirTitle);*/

		components = new HashMap<>();
		
		initComponents(lungSimulator, leftPanel);
	}

	private void initComponents(LungSimulator lungSimulator, JPanel leftPanel) {
		yInit = 27;

		int count = 0;
		int index;

		if (lungSimulator != null) {
			if (lungSimulator.getCircuitBuilder() != null) {
				if (lungSimulator.getCircuitBuilder().isTimeDependentCir()) {
					timeDependentElms = lungSimulator.getCircuitBuilder().getTimeDependentElm();
				}

				index = lungSimulator.getCircuitBuilder().getVentilatorIndex();

				if (lungSimulator.getCircuitBuilder().getElements() != null) {
					for (CircuitElm element : lungSimulator.getCircuitBuilder().getElements()) {
						CircuitElementRow cer = new CircuitElementRow(lungSimulator, leftPanel, element.getId(),
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
		}
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

	public int getyInit() {
		return yInit;
	}

}
