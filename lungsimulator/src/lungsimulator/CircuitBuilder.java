package lungsimulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.udojava.evalex.Expression;

import components.ACVoltageElm;
import components.CapacitorElm;
import components.CircuitElm;
import components.DCVoltageElm;
import components.ExternalVoltageElm;
import components.ResistorElm;
import lungsimulator.components.Archetype;
import lungsimulator.components.Element;
import lungsimulator.components.Formula;
import lungsimulator.components.Patient;
import simulator.CirSim;

public class CircuitBuilder {

	private boolean timeDependentCir = false;
	private CirSim cirSim = new CirSim();
	private Map<String, Formula> timeDependentElm = new HashMap<>();
	private List<CircuitElm> elements = new ArrayList<>();

	public CircuitBuilder() {
	}

	/**
	 * The method builds a circuit according to the patient's components and
	 * archetype
	 */
	public CirSim buildCircuitSimulator(Patient patient, Archetype archetype) {
		cirSim.setTimeStep(0.1);
		ResistorElm resistance;
		CapacitorElm capacitance;
		ACVoltageElm acVoltage;
		DCVoltageElm dcVoltage;
		ExternalVoltageElm externalVoltage;

		assert patient.getSchema() == archetype.getSchema();

		for (Element element : patient.getElementsList()) {
			String value = resolveFormula(element.getAssociatedFormula(), archetype.getParameters(), "0");

			if (!value.isEmpty()) {
				// resistance
				if (element.getType().equals("ResistorElm")) {
					resistance = new ResistorElm(1, 1);
					resistance.setResistance(Double.parseDouble(value));
					circuitElmSetUp(element, resistance);
				}

				// capacitor
				if (element.getType().equals("CapacitorElm")) {
					capacitance = new CapacitorElm(0, 0);
					capacitance.setCapacitance(Double.parseDouble(value));
					circuitElmSetUp(element, capacitance);
				}
				
				//acVoltage
				if (element.getType().equals("ACVoltageElm")) {
					acVoltage = new ACVoltageElm(1, 1);
					acVoltage.setMaxVoltage(Double.parseDouble(value));
					circuitElmSetUp(element, acVoltage);
				}
				
				//dcVoltage
				if (element.getType().equals("DCVoltageElm")) {
					dcVoltage = new DCVoltageElm(1, 1);
					dcVoltage.setMaxVoltage(Double.parseDouble(value));
					circuitElmSetUp(element, dcVoltage);
				}
			}
			
			//externalVoltage doesn't have a formula
			if (element.getType().equals("ExternalVoltageElm")) {
				externalVoltage = new ExternalVoltageElm(1, 1, 28);
				circuitElmSetUp(element, externalVoltage);
			}
		}

		assert patient.getElementsList().size() == elements.size();

		for (CircuitElm c : elements) {
			c.setPoints();
		}

		cirSim.setElmList(elements);
		CircuitElm.sim = cirSim;

		return cirSim;
	}

	private void circuitElmSetUp(Element element, CircuitElm circuitElm) {
		circuitElm.setId(element.getElementName());
		circuitElm.setX(element.getX());
		circuitElm.setY(element.getY());
		circuitElm.setX2Y2(element.getX1(), element.getY1());

		if (element.isShowLeft()) {
			circuitElm.setIdLeft(element.getIdLeft());
		}

		if (element.isShowRight()) {
			circuitElm.setIdRight(element.getIdRight());
		}

		if (element.getAssociatedFormula().getIsTimeDependent()) {
			timeDependentElm.put(element.getElementName(), element.getAssociatedFormula());
		}
		elements.add(circuitElm);
	}

	/**
	 * Calculate element value
	 * 
	 * @param elementFormula formula description
	 * @param parameters     known values
	 * @param time           time value (it will be assigned only if there is TIME)
	 * @return element value
	 */
	private String resolveFormula(final Formula elementFormula, final Map<String, String> parameters,
			final String time) {
		String value = "";
		// if element has a formula
		if (elementFormula.getFormula() != null) {
			Expression formula = new Expression(elementFormula.getFormula());
			// assign to each variable its value
			for (final String var : elementFormula.getVariables()) {
				if (!"TIME".equals(var)) {
					formula = formula.setVariable(var, parameters.get(var));
				} else {
					formula = formula.setVariable(var, time);
					timeDependentCir = true;
				}
			}
			// resolve
			value = formula.eval().toString();
		}

		return value;
	}

	public CirSim updateCircuitSimulator(Archetype archetype, double time) {
	
		for (CircuitElm circuitElement : cirSim.getElmList()) {
			if(timeDependentElm.containsKey(circuitElement.getId())) {
				String value = resolveFormula(timeDependentElm.get(circuitElement.getId()), archetype.getParameters(), String.valueOf(time));
				
				// resistance
				if (circuitElement instanceof ResistorElm) {
					ResistorElm resistance = (ResistorElm) circuitElement;
					resistance.setResistance(Double.parseDouble(value));
				}

				// capacitor
				if (circuitElement instanceof CapacitorElm) {
					CapacitorElm capacitance = (CapacitorElm) circuitElement;
					capacitance.setCapacitance(Double.parseDouble(value));
				}
				
				//acVoltage
				if (circuitElement instanceof ACVoltageElm) {
					ACVoltageElm acVoltage = (ACVoltageElm) circuitElement;
					acVoltage.setMaxVoltage(Double.parseDouble(value));
				}
				
				//dcVoltage
				if (circuitElement instanceof DCVoltageElm) {
					DCVoltageElm dcVoltage = (DCVoltageElm) circuitElement;
					dcVoltage.setMaxVoltage(Double.parseDouble(value));
				}
			}
		}
		return cirSim;
	}

	public boolean isTimeDependentCir() {
		return timeDependentCir;
	}

	public void setTimeDependentCir(boolean hasTimeDependency) {
		this.timeDependentCir = hasTimeDependency;
	}

}