package lungsimulator.graphic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import components.CapacitorElm;
import components.CircuitElm;
import components.ExternalVoltageElm;
import components.ResistorElm;
import simulator.CirSim;

/**
 * Show circuit component according to a id-value-unit structure
 */
public class CircuitComponents extends JComponent {
	/**
	 * Serial version UID constant
	 */
	private static final long serialVersionUID = 5354577250667666248L;
	
	/**
	 * Panel with all circuit components
	 */
	private final transient JPanel circuitElements;
	
	/**
	 * Show ventilator value
	 */
	private transient JSpinner ventilator;
	
	/**
	 * Components ids where current have to be shown
	 */
	private final transient List<String> flowIds = new ArrayList<>();
	
	private Map<String, JSpinner> spinnersTime = new HashMap<>();
	
	public CircuitComponents() {
		circuitElements = new JPanel();
		
		TitledBorder cirTitle = BorderFactory.createTitledBorder("Circuit components");
		circuitElements.setBorder(cirTitle);
	}
	
	public void initComponent(final CirSim myCircSim, List<String> timeDependentElements) {
		int yInit = 27;

		for (final CircuitElm element : myCircSim.getElmList()) {
			String elmId = element.getId();
			boolean isTimeDependent = false;
			
			if(timeDependentElements.contains(elmId)) {
				isTimeDependent = true;
			}
			
			// resistance
			if (element instanceof ResistorElm) {
				graphicDesignForElement(elmId, GraphicConstants.UMRES, yInit, element.getValue(), false, isTimeDependent);
				flowIds.add(elmId);
			}

			// capacitor
			if (element instanceof CapacitorElm) {
				graphicDesignForElement(elmId, GraphicConstants.UMCAP, yInit, element.getValue(), false, isTimeDependent);
				flowIds.add(elmId);
			}
			
			if(element instanceof ExternalVoltageElm) {
				graphicDesignForElement(elmId, GraphicConstants.UMGEN, yInit, 0, true, false);
			}

			yInit += 28;
		}
	}
	
	/**
	 * Create graphic design for circuit element
	 * 
	 * @param elementDescr element description
	 * @param elementUnit  unit of measurement
	 * @param elementY     component height
	 * @param value        element value
	 * @param isVentilator true if the element is the ventilator
	 */
	private void graphicDesignForElement(final String elementDescr, final String elementUnit, final int elementY,
			final double value, final boolean isVentilator, final boolean isTimeDependent) {
		JSpinner element = new JSpinner();

		// Element description
		final JLabel elementId = new JLabel(elementDescr);
		elementId.setBounds(GraphicConstants.IDELEMENTX, elementY, GraphicConstants.IDELEMENTWIDTH, GraphicConstants.IDELEMENTHEIGHT);
		circuitElements.add(elementId);

		// Element value
		final SpinnerNumberModel elementModel = new SpinnerNumberModel(0.0, 0.000, 100.0, 0.001);
		element.setModel(elementModel);
		element.setBounds(GraphicConstants.VALELEMENTX, elementY, GraphicConstants.VALELEMENTWIDTH, GraphicConstants.VALELEMENTHEIGHT);
		element.setValue(value);
		circuitElements.add(element);

		if (isTimeDependent) {
			spinnersTime.put(elementDescr, element);
		}

		if (isVentilator) {
			ventilator = element;
		}

		// Element unit of measurement
		final JLabel unit = new JLabel(elementUnit);
		unit.setBounds(GraphicConstants.UMELEMENTX, elementY, GraphicConstants.UMELEMENTWIDTH, GraphicConstants.UMELEMENTHEIGHT);
		circuitElements.add(unit);

	}

}
