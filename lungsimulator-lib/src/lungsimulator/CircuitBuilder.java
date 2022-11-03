package lungsimulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import lungsimulator.graphic.GraphicConstants;
import lungsimulator.utils.Utils;
import simulator.CirSim;

/**
 * Converts elements in patient model to their equivalent circuit elements
 */
public class CircuitBuilder {

	/**
	 * True if the model has at least one time dependent component
	 */
	private boolean timeDependentCir;

	/**
	 * The circuit
	 */
	private final transient CirSim cirSim;

	/**
	 * Contains every time dependent component and its formula
	 */
	private final transient Map<String, Formula> timeDependentElm;

	/**
	 * The elements of the circuit
	 */
	private final transient List<CircuitElm> elements;

	/**
	 * Index of ventilator in cirSim elements list
	 */
	private transient int ventilatorIndex;

	private List<String> flowIds = new ArrayList<>();

	private String[] timeline;
	private double[][] initdataPressure;
	private double[][] initdataVentilatorPressure;
	private double[][] initdataFlow;
	private List<String> pressureIds = new ArrayList<>();
	private Map<String, String> pressureCoord = new LinkedHashMap<>();
	
	/**
	 * Unit of measure for resistance element
	 */
	public static final String UMRES = "cmH2O/L/s";
	
	/**
	 * Unit of measure for capacitor element
	 */
	public static final String UMCAP = "L/cmH2O";
	
	/**
	 * Unit of measure for voltage element
	 */
	public static final String UMGEN = "cmH2O";

	/**
	 * Init class fields
	 */
	public CircuitBuilder() {
		timeDependentCir = false;
		cirSim = new CirSim();
		timeDependentElm = new HashMap<>();
		elements = new ArrayList<>();
	}

	/**
	 * The method builds a circuit according to the patient's components and
	 * archetype
	 * 
	 * @param patient   patient model
	 * @param archetype archetype parameters
	 * @return the circuit
	 */
	public CirSim buildCircuitSimulator(final Patient patient, final Archetype archetype) {
		cirSim.setTimeStep(0.1);
		ResistorElm resistance;
		CapacitorElm capacitance;
		ACVoltageElm acVoltage;
		DCVoltageElm dcVoltage;
		ExternalVoltageElm externalVoltage;

		for (final Element element : patient.getElementsList()) {
			final String value = resolveFormula(element.getAssociatedFormula(), archetype.getParameters(), "0");

			if (!value.isEmpty()) {
				// resistance
				if ("ResistorElm".equals(element.getType())) {
					resistance = new ResistorElm(1, 1);
					resistance.setResistance(Double.parseDouble(value));
					resistance.setValue(Double.parseDouble(value));
					resistance.setUnit(UMRES);
					circuitElmSetUp(element, resistance);
					flowIds.add(element.getElementName());
				}

				// capacitor
				if ("CapacitorElm".equals(element.getType())) {
					capacitance = new CapacitorElm(0, 0);
					capacitance.setCapacitance(Double.parseDouble(value));
					capacitance.setValue(Double.parseDouble(value));
					capacitance.setUnit(UMCAP);
					circuitElmSetUp(element, capacitance);
					flowIds.add(element.getElementName());
				}

				// acVoltage
				if ("ACVoltageElm".equals(element.getType())) {
					acVoltage = new ACVoltageElm(1, 1);
					acVoltage.setMaxVoltage(Double.parseDouble(value));
					acVoltage.setValue(Double.parseDouble(value));
					acVoltage.setUnit(UMGEN);
					circuitElmSetUp(element, acVoltage);
					flowIds.add(element.getElementName());
				}

				// dcVoltage
				if ("DCVoltageElm".equals(element.getType())) {
					dcVoltage = new DCVoltageElm(1, 1);
					dcVoltage.setMaxVoltage(Double.parseDouble(value));
					dcVoltage.setValue(Double.parseDouble(value));
					dcVoltage.setUnit(UMGEN);
					circuitElmSetUp(element, dcVoltage);
					flowIds.add(element.getElementName());
				}
			}

			// externalVoltage doesn't have a formula
			if ("ExternalVoltageElm".equals(element.getType())) {
				externalVoltage = new ExternalVoltageElm(1, 1, 28);
				externalVoltage.setUnit(UMGEN);
				circuitElmSetUp(element, externalVoltage);
				ventilatorIndex = elements.size() - 1;
			}
		}

		dataInit();

		for (final CircuitElm circuitElm : elements) {
			circuitElm.setPoints();
		}

		cirSim.setElmList(elements);
		CircuitElm.sim = cirSim;

		return cirSim;
	}

	private void dataInit() {
		pressureIds = new ArrayList<>(pressureCoord.keySet());
		// Init the arrays for the data
		initdataPressure = new double[pressureCoord.size()][GraphicConstants.MAXDATA];
		// One row for time values and one row for pressure values
		initdataVentilatorPressure = new double[2][GraphicConstants.MAXDATA];
		// One row for time values and one row for each element of the circuit
		initdataFlow = new double[flowIds.size()][GraphicConstants.MAXDATA];

		timeline = new String[GraphicConstants.MAXDATA];

		// Fill initData with zeros
		Utils.initVectors(GraphicConstants.MAXDATA, timeline, initdataPressure, initdataVentilatorPressure,
				initdataFlow);

	}

	public void updateData(double time) {
		// Shift data in vectors
		Utils.shiftData(GraphicConstants.MAXDATA, timeline, initdataPressure, initdataVentilatorPressure, initdataFlow);

		// Update time
		timeline[GraphicConstants.MAXDATA - 1] = String.valueOf(time);

		int count = 0;
		int countPressure = 0;

		for (final CircuitElm cir : elements) {
			/*
			 * Questione tempo dipendenza
			if (spinnersTime.containsKey(cir.getId())) {
				updateSpinners(cir);
			}*/

			if (cir.getIdLeft() != null && pressureCoord.containsKey(cir.getIdLeft())
					&& pressureCoord.get(cir.getIdLeft()).equals("left")) {
				initdataPressure[countPressure][GraphicConstants.MAXDATA - 1] = cir.getVoltZero();
				countPressure++;
			}

			if (cir.getIdRight() != null && pressureCoord.containsKey(cir.getIdRight())
					&& pressureCoord.get(cir.getIdRight()).equals("right")) {
				initdataPressure[countPressure][GraphicConstants.MAXDATA - 1] = cir.getVoltOne();
				countPressure++;
			}

			if (cir.getClass().getSimpleName().equals("ExternalVoltageElm")) {
				initdataVentilatorPressure[1][GraphicConstants.MAXDATA - 1] = cir.getVoltageDiff();
				//ventilator.setValue(cir.getVoltageDiff());
			} else {
				initdataFlow[count][GraphicConstants.MAXDATA - 1] = cir.getCurrent();
				count++;
			}
		}
	}

	private void circuitElmSetUp(final Element element, final CircuitElm circuitElm) {
		circuitElm.setId(element.getElementName());
		circuitElm.setX(element.getX());
		circuitElm.setY(element.getY());
		circuitElm.setX2Y2(element.getX1(), element.getY1());

		if (element.isShowLeft()) {
			circuitElm.setIdLeft(element.getIdLeft());
			pressureCoord.put(element.getIdLeft(), "left");
		}

		if (element.isShowRight()) {
			circuitElm.setIdRight(element.getIdRight());
			pressureCoord.put(element.getIdRight(), "right");
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

	/**
	 * Update components values
	 * 
	 * @param archetype chosen archetype
	 * @param time      new time for variable TIME
	 */
	public void updateCircuitSimulator(final Archetype archetype, final double time) {

		for (final CircuitElm circuitElement : cirSim.getElmList()) {
			if (timeDependentElm.containsKey(circuitElement.getId())) {
				final String value = resolveFormula(timeDependentElm.get(circuitElement.getId()),
						archetype.getParameters(), String.valueOf(time));

				// resistance
				if (circuitElement instanceof ResistorElm) {
					final ResistorElm resistance = (ResistorElm) circuitElement;
					resistance.setResistance(Double.parseDouble(value));
				}

				// capacitor
				if (circuitElement instanceof CapacitorElm) {
					final CapacitorElm capacitance = (CapacitorElm) circuitElement;
					capacitance.setCapacitance(Double.parseDouble(value));
				}

				// acVoltage
				if (circuitElement instanceof ACVoltageElm) {
					final ACVoltageElm acVoltage = (ACVoltageElm) circuitElement;
					acVoltage.setMaxVoltage(Double.parseDouble(value));
				}

				// dcVoltage
				if (circuitElement instanceof DCVoltageElm) {
					final DCVoltageElm dcVoltage = (DCVoltageElm) circuitElement;
					dcVoltage.setMaxVoltage(Double.parseDouble(value));
				}
			}
		}
	}

	/**
	 * update the value of ventilator element
	 * 
	 * @param ventilatorValue new ventilator value
	 */
	public void updateVentilatorValue(final double ventilatorValue) {
		final ExternalVoltageElm ventilator = (ExternalVoltageElm) cirSim.getElmList().get(ventilatorIndex);
		ventilator.setVentVoltage(ventilatorValue);
	}

	public boolean isTimeDependentCir() {
		return timeDependentCir;
	}

	public void setTimeDependentCir(final boolean hasTimeDependency) {
		this.timeDependentCir = hasTimeDependency;
	}

	public List<String> getTimeDependentElm() {
		return new ArrayList<String>(timeDependentElm.keySet());
	}

	public List<CircuitElm> getElements() {
		return elements;
	}

	public List<String> getFlowIds() {
		return flowIds;
	}

	public void setFlowIds(List<String> flowIds) {
		this.flowIds = flowIds;
	}

	public double[][] getInitdataPressure() {
		return initdataPressure;
	}

	public void setInitdataPressure(double[][] initdataPressure) {
		this.initdataPressure = initdataPressure;
	}

	public double[][] getInitdataVentilatorPressure() {
		return initdataVentilatorPressure;
	}

	public void setInitdataVentilatorPressure(double[][] initdataVentilatorPressure) {
		this.initdataVentilatorPressure = initdataVentilatorPressure;
	}

	public double[][] getInitdataFlow() {
		return initdataFlow;
	}

	public void setInitdataFlow(double[][] initdataFlow) {
		this.initdataFlow = initdataFlow;
	}

	public List<String> getPressureIds() {
		return pressureIds;
	}

	public void setPressureIds(List<String> pressureIds) {
		this.pressureIds = pressureIds;
	}

	public Map<String, String> getPressureCoord() {
		return pressureCoord;
	}

	public void setPressureCoord(Map<String, String> pressureCoord) {
		this.pressureCoord = pressureCoord;
	}

	public String[] getTimeline() {
		return timeline;
	}

	public void setTimeline(String[] timeline) {
		this.timeline = timeline;
	}

}