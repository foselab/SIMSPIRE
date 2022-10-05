package lungsimulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.udojava.evalex.Expression;

import circuits.Circuit;
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
	private static final int SCHEMA_RC = 1;

	// Maximum numebr of data shown in the window
	int maxData = 500;

	// Values
	double rPatValue;
	boolean resistanceChangeEnabled;
	double probabilityChangeResistance;
	double maxVariabilityResistance;
	double cPatValue;
	boolean complianceChangeEnabled;
	double probabilityChangeCompliance;
	double maxVariabilityCompliance;
	double swDropValue;
	boolean dropResistanceChangeEnabled;
	double dropResistanceChangeProbability;
	double maxDropResistanceChange;
	double rDropValue;
	double pressureDropAfterSec;
	private boolean pressureDropEnabled;
	boolean pressureDropEnabledWithProbability;
	double pressureDropMissProbability;
	boolean coughEnabled;
	double coughProbability;

	// Components
	private ResistorElm rPat;
	private CapacitorElm cPat;
	private DCVoltageElm battery;
	private SwitchElm swDrop;
	private ResistorElm rDrop;
	// Simulation params
	SimulatorParams sp;

	public RealTimePlot() {
		this.sp = new SimulatorParams();
	}

	/**
	 * 
	 * @param resistance
	 * @param capacity
	 * @param pMax
	 * @param rDrop
	 * @return the circuit matching the input parameters
	 */
	CirSim buildSimulator(double resistance, double capacity, double pMax, double rDrop) {
		Circuit cir = new Circuit();

		// Resistor
		this.rPat = new ResistorElm(1, 1);
		this.rPat.setX2Y2(1, 0);
		this.rPat.setResistance(resistance);

		// Capacity
		this.setcPat(new CapacitorElm(0, 0));
		this.getcPat().setX2Y2(1, 1);
		this.getcPat().setCapacitance(capacity);

		// Ventilator battery
		this.setBattery(new DCVoltageElm(1, 0));
		this.getBattery().setX2Y2(0, 0);
		this.getBattery().setMaxVoltage(pMax);

		// Switch for pressure drop
		this.setSwDrop(new SwitchElm(1, 1, true));
		this.getSwDrop().setX2Y2(0, 1);

		// Resistor for pressure drop
		this.rDrop = new ResistorElm(0, 0);
		this.rDrop.setX2Y2(0, 1);
		this.rDrop.setResistance(rDrop);

		List<CircuitElm> elements = Arrays.asList(this.rPat, this.getcPat(), this.getBattery(), this.getSwDrop(),
				this.rDrop);
		for (CircuitElm c : elements) {
			c.setPoints();
		}

		CirSim cirSim = new CirSim(cir);
		cirSim.setElmList(elements);
		CircuitElm.sim = cirSim;
		return cirSim;

	}

	/**
	 * The method builds a circuit according to the patient's components and
	 * archetype
	 * 
	 * @param equivalent circuit
	 */
	public CirSim buildCircuitSimulator(Patient patient, Archetype archetype) {
		Circuit cir = new Circuit();
		CirSim cirSim = new CirSim(cir);
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
					resistance.setX(e.getX());
					resistance.setY(e.getY());
					resistance.setX2Y2(e.getX1(), e.getY1());
					if (!e.getAssociatedFormula().getVariables().contains("TIME")) {
						resistance.setResistance(Double.parseDouble(formula.eval().toString()));
					}
					elements.add(resistance);
				}

				// capacitor
				if (e.getType().equals(capacitance.getClass().getSimpleName())) {
					capacitance = new CapacitorElm(0, 0);
					capacitance.setX(e.getX());
					capacitance.setY(e.getY());
					capacitance.setX2Y2(e.getX1(), e.getY1());
					if (!e.getAssociatedFormula().getVariables().contains("TIME")) {
					capacitance.setCapacitance(Double.parseDouble(formula.eval().toString()));
					}
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
		Circuit cir = new Circuit();
		CirSim cirSim = new CirSim(cir);
		cirSim.setTimeStep(0.01);
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

	/**
	 * @return seconds between the object instantiation and the beginning of the
	 *         simulation
	 */
	public double getElapsedSeconds() {
		double nowMillis = System.currentTimeMillis();
		return (nowMillis - this.createdMillis) / 1000.0;
	}

	public void setMaxData(int maxData) {
		this.maxData = maxData;
	}

	public void setrPatValue(double rPatValue) {
		this.rPatValue = rPatValue;
	}

	public void setcPatValue(double cPatValue) {
		this.cPatValue = cPatValue;
	}

	public void setSwDropValue(double swDropValue) {
		this.swDropValue = swDropValue;
	}

	public void setrDropValue(double rDropValue) {
		this.rDropValue = rDropValue;
	}

	public CapacitorElm getcPat() {
		return cPat;
	}

	public void setcPat(CapacitorElm cPat) {
		this.cPat = cPat;
	}

	public DCVoltageElm getBattery() {
		return battery;
	}

	public void setBattery(DCVoltageElm battery) {
		this.battery = battery;
	}

	public SwitchElm getSwDrop() {
		return swDrop;
	}

	public void setSwDrop(SwitchElm swDrop) {
		this.swDrop = swDrop;
	}

	public boolean isPressureDropEnabled() {
		return pressureDropEnabled;
	}

	public void setPressureDropEnabled(boolean pressureDropEnabled) {
		this.pressureDropEnabled = pressureDropEnabled;
	}

	public double getPressureDropAfterSec() {
		return pressureDropAfterSec;
	}

	public void setPressureDropAfterSec(double pressureDropAfterSec) {
		this.pressureDropAfterSec = pressureDropAfterSec;
	}

	public void setProbabilityChangeResistance(double probabilityChangeResistance) {
		this.probabilityChangeResistance = probabilityChangeResistance;
	}

	public void setCoughProbability(double coughProbability) {
		this.coughProbability = coughProbability;
	}

	public void setMaxVariabilityResistance(double maxVariabilityResistance) {
		this.maxVariabilityResistance = maxVariabilityResistance;
	}

	public void setProbabilityChangeCompliance(double probabilityChangeCompliance) {
		this.probabilityChangeCompliance = probabilityChangeCompliance;
	}

	public void setMaxVariabilityCompliance(double maxVariabilityCompliance) {
		this.maxVariabilityCompliance = maxVariabilityCompliance;
	}

	public void setDropResistanceChangeProbability(double dropResistanceChangeProbability) {
		this.dropResistanceChangeProbability = dropResistanceChangeProbability;
	}

	public void setMaxDropResistanceChange(double maxDropResistanceChange) {
		this.maxDropResistanceChange = maxDropResistanceChange;
	}

	public void setPressureDropMissProbability(double pressureDropMissProbability) {
		this.pressureDropMissProbability = pressureDropMissProbability;
	}

	public void setCoughEnabled(Boolean coughEnabled) {
		this.coughEnabled = coughEnabled;
	}

	public void setResistanceChangeEnabled(Boolean resistanceChangeEnabled) {
		this.resistanceChangeEnabled = resistanceChangeEnabled;
	}

	public void setComplianceChangeEnabled(Boolean complianceChangeEnabled) {
		this.complianceChangeEnabled = complianceChangeEnabled;
	}

	public void setDropResistanceChangeEnabled(Boolean dropResistanceChangeEnabled) {
		this.dropResistanceChangeEnabled = dropResistanceChangeEnabled;
	}

	public void setPressureDropEnabledWithProbability(Boolean pressureDropEnabledWithProbability) {
		this.pressureDropEnabledWithProbability = pressureDropEnabledWithProbability;
	}

	public SimulatorParams getSimulatorParams() {
		return sp;
	}

	public int getMaxData() {
		return maxData;
	}

	public ResistorElm getRPat() {
		return rPat;
	}

	public ResistorElm getrDrop() {
		return rDrop;
	}

}