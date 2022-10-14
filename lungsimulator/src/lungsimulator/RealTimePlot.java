package lungsimulator;

import java.util.ArrayList;
import java.util.List;

import com.udojava.evalex.Expression;
import components.ACVoltageElm;
import components.CapacitorElm;
import components.CircuitElm;
import components.DCVoltageElm;
import components.ExternalVoltageElm;
import components.ResistorElm;
import components.SwitchElm;
import lungsimulator.components.Archetype;
import lungsimulator.components.Element;
import lungsimulator.components.Patient;
import lungsimulator.components.SimulatorParams;
import simulator.CirSim;

public class RealTimePlot {

	private final long createdMillis = System.currentTimeMillis();

	public RealTimePlot() {}

	/**
	 * The method builds a circuit according to the patient's components and
	 * archetype
	 */
	public CirSim buildCircuitSimulator(Patient patient, Archetype archetype) {
		CirSim cirSim = new CirSim();
		cirSim.setTimeStep(0.1);
		ResistorElm resistance = new ResistorElm(1, 1);
		CapacitorElm capacitance = new CapacitorElm(0, 0);
		ACVoltageElm acVoltage = new ACVoltageElm(1, 1);
		DCVoltageElm dcVoltage = new DCVoltageElm(1, 1);
		ExternalVoltageElm externalVoltage = new ExternalVoltageElm(1, 1, 28);
		List<CircuitElm> elements = new ArrayList<>();

		assert patient.getSchema() == archetype.getSchema();

		for (Element e : patient.getElementsList()) {
			// setting formula's values
			if (e.getAssociatedFormula().getFormula() != null) {
				Expression formula = new Expression(e.getAssociatedFormula().getFormula());
				for (String var : e.getAssociatedFormula().getVariables()) {
					if (!var.equals("TIME")) {
						formula = formula.setVariable(var, archetype.getParameters().get(var));
					}
				}

				// resistance
				if (e.getType().equals(resistance.getClass().getSimpleName())) {
					resistance = new ResistorElm(1, 1);
					resistance.setId(e.getElementName());
					resistance.setX(e.getX());
					resistance.setY(e.getY());
					resistance.setX2Y2(e.getX1(), e.getY1());
					if (!e.getAssociatedFormula().getVariables().contains("TIME")) {
						resistance.setResistance(Double.parseDouble(formula.eval().toString()));
					}
					
					if(e.isShowLeft()) {
						resistance.setIdLeft(e.getIdLeft());
					}
					
					if(e.isShowRight()) {
						resistance.setIdRight(e.getIdRight());
					}
					elements.add(resistance);
				}

				// capacitor
				if (e.getType().equals(capacitance.getClass().getSimpleName())) {
					capacitance = new CapacitorElm(0, 0);
					capacitance.setId(e.getElementName());
					capacitance.setX(e.getX());
					capacitance.setY(e.getY());
					capacitance.setX2Y2(e.getX1(), e.getY1());
					if (!e.getAssociatedFormula().getVariables().contains("TIME")) {
					capacitance.setCapacitance(Double.parseDouble(formula.eval().toString()));
					}
					
					if(e.isShowLeft()) {
						capacitance.setIdLeft(e.getIdLeft());
					}
					
					if(e.isShowRight()) {
						capacitance.setIdRight(e.getIdRight());
					}
					
					elements.add(capacitance);
				}
			}

			// ac voltage
			if (e.getType().equals(acVoltage.getClass().getSimpleName())) {
				acVoltage.setId(e.getElementName());
				acVoltage.setX(e.getX());
				acVoltage.setY(e.getY());
				acVoltage.setX2Y2(e.getX1(), e.getY1());
				if(e.isShowLeft()) {
					acVoltage.setIdLeft(e.getIdLeft());
				}
				
				if(e.isShowRight()) {
					acVoltage.setIdRight(e.getIdRight());
				}
				elements.add(acVoltage);
			}

			// dc voltage
			if (e.getType().equals(dcVoltage.getClass().getSimpleName())) {
				dcVoltage.setId(e.getElementName());
				dcVoltage.setX(e.getX());
				dcVoltage.setY(e.getY());
				dcVoltage.setX2Y2(e.getX1(), e.getY1());
				if(e.isShowLeft()) {
					dcVoltage.setIdLeft(e.getIdLeft());
				}
				
				if(e.isShowRight()) {
					dcVoltage.setIdRight(e.getIdRight());
				}
				elements.add(dcVoltage);

				if (e.getAssociatedFormula().getIsExternal()) {
					cirSim.ventilatorIndex = elements.size() - 1;
				}
			}

			if (e.getType().equals(externalVoltage.getClass().getSimpleName())) {
				externalVoltage.setId(e.getElementName());
				externalVoltage.setX(e.getX());
				externalVoltage.setY(e.getY());
				externalVoltage.setX2Y2(e.getX1(), e.getY1());
				
				if(e.isShowLeft()) {
					externalVoltage.setIdLeft(e.getIdLeft());
				}
				
				if(e.isShowRight()) {
					externalVoltage.setIdRight(e.getIdRight());
				}
				// externalVoltage.setMaxVoltage(10.0);
				elements.add(externalVoltage);
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
	
	public CirSim updateCircuitSimulator(Patient patient, Archetype archetype) {
		CirSim cirSim = new CirSim();
		cirSim.setTimeStep(0.1);
		ResistorElm resistance = new ResistorElm(1, 1);
		CapacitorElm capacitance = new CapacitorElm(0, 0);
		ACVoltageElm acVoltage = new ACVoltageElm(1, 1);
		DCVoltageElm dcVoltage = new DCVoltageElm(1, 1);
		ExternalVoltageElm externalVoltage = new ExternalVoltageElm(1, 1, 28);
		List<CircuitElm> elements = new ArrayList<>();

		assert patient.getSchema() == archetype.getSchema();

		for (Element e : patient.getElementsList()) {
			// setting formula's values
			if (e.getAssociatedFormula().getFormula() != null) {
				Expression formula = new Expression(e.getAssociatedFormula().getFormula());
				for (String var : e.getAssociatedFormula().getVariables()) {
						formula = formula.setVariable(var, archetype.getParameters().get(var));
				}

				// resistance
				if (e.getType().equals(resistance.getClass().getSimpleName())) {
					resistance = new ResistorElm(1, 1);
					resistance.setX(e.getX());
					resistance.setY(e.getY());
					resistance.setX2Y2(e.getX1(), e.getY1());
					resistance.setResistance(Double.parseDouble(formula.eval().toString()));	
					elements.add(resistance);
				}

				// capacitor
				if (e.getType().equals(capacitance.getClass().getSimpleName())) {
					capacitance = new CapacitorElm(0, 0);
					capacitance.setX(e.getX());
					capacitance.setY(e.getY());
					capacitance.setX2Y2(e.getX1(), e.getY1());
					capacitance.setCapacitance(Double.parseDouble(formula.eval().toString()));
					elements.add(capacitance);
				}
			}

			// ac voltage
			if (e.getType().equals(acVoltage.getClass().getSimpleName())) {
				acVoltage.setX(e.getX());
				acVoltage.setY(e.getY());
				acVoltage.setX2Y2(e.getX1(), e.getY1());
				elements.add(acVoltage);
			}

			// dc voltage
			if (e.getType().equals(dcVoltage.getClass().getSimpleName())) {
				dcVoltage.setX(e.getX());
				dcVoltage.setY(e.getY());
				dcVoltage.setX2Y2(e.getX1(), e.getY1());
				elements.add(dcVoltage);

				if (e.getAssociatedFormula().getIsExternal()) {
					cirSim.ventilatorIndex = elements.size() - 1;
				}
			}

			if (e.getType().equals(externalVoltage.getClass().getSimpleName())) {
				externalVoltage.setX(e.getX());
				externalVoltage.setY(e.getY());
				externalVoltage.setX2Y2(e.getX1(), e.getY1());
				elements.add(externalVoltage);
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

	/**
	 * @return seconds between the object instantiation and the beginning of the
	 *         simulation
	 */
	public double getElapsedSeconds() {
		double nowMillis = System.currentTimeMillis();
		return (nowMillis - this.createdMillis) / 1000.0;
	}


}