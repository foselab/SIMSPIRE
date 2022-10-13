package lungsimulator.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import lungsimulator.components.Archetype;
import lungsimulator.components.Element;
import lungsimulator.components.Formula;
import lungsimulator.components.Patient;

/**
 * Check lung model and archetype
 */
public class Validator {
	/**
	 * True if at least one pressure point has been highlighted
	 */
	private transient boolean atLeastOnePres;
	/**
	 * Internal logger for errors report
	 */
	private static final Logger LOGGER = Logger.getLogger(Validator.class.getName());
	
	/**
	 * Init validator fields
	 */
	public Validator() {
		atLeastOnePres = false;
	}
	
	/**
	 * Checks for missing or not properly built sections of the patient model and
	 * its archetype
	 * 
	 * @param patient   the chosen patient model
	 * @param archetype the chosen archetype
	 */
	public void evaluate(final Patient patient, final Archetype archetype) {

		LOGGER.log(Level.INFO, "Init validation process");

		if (patient == null || archetype == null) {
			throw new InspireException("At least one model is not properly built");
		}

		if (patient.getSchema() != archetype.getSchema()) {
			throw new InspireException("Patient schema and Archetype schema are inconsistent");
		}

		final List<Element> patientElement = new ArrayList<>(patient.getElementsList());

		if (patientElement == null || patientElement.isEmpty() || patientElement != null && patientElement.size() < 2) {
			throw new InspireException("Expected at least 2 components");
		}

		final Map<String, String> archetypeParams = new ConcurrentHashMap<>(archetype.getParameters());

		if (archetypeParams == null || archetypeParams.isEmpty()) {
			throw new InspireException("Expected some parameters but found 0");
		}

		String elementType;
		Formula elementFormula;

		for (final Element e : patientElement) {
			elementType = e.getType();
			if (elementType == null || elementType.isEmpty()) {
				throw new InspireException("Missing element type");
			}

			if (e.getX() < 0 || e.getY() < 0 || e.getX1() < 0 || e.getX1() < 0) {
				throw new InspireException("Invalid coordinates");
			}

			elementFormula = e.getAssociatedFormula();
			checkElementFormula(archetypeParams, elementFormula, elementType);

			checkPressurePoints(e);

		}

		if (!atLeastOnePres) {
			throw new InspireException("You must set at least one known pressure point");
		}

		LOGGER.log(Level.INFO, "Validation process successfully completed");
	}

	private void checkPressurePoints(final Element element) {
		
		if (element.isShowLeft() && (element.getIdLeft() == null || element.getIdLeft().equals(""))) {
			throw new InspireException("Missing id for left node");
		}

		if (!element.isShowLeft() && element.getIdLeft() != null) {
			throw new InspireException(
					"Inconsistency error: an id for left node has been set, but showLeft is false");
		}

		if (element.isShowRight() && (element.getIdRight() == null || element.getIdRight().equals(""))) {
			throw new InspireException("Missing id for right node");
		}

		if (!element.isShowRight() && element.getIdRight() != null) {
			throw new InspireException(
					"Inconsistency error: an id for right node has been set, but showRight is false");
		}

		if (element.isShowLeft() || element.isShowRight()) {
			atLeastOnePres = true;
		}
	}

	private void checkElementFormula(final Map<String, String> archetypeParams, final Formula elementFormula,
			final String elementType) {
		String formula;
		if (elementFormula == null) {
			throw new InspireException("Missing formula associated to " + elementType + " element");
		} else if (elementFormula != null && !elementFormula.getIsExternal()) {
			formula = elementFormula.getFormula();
			if (formula == null || formula.isEmpty()) {
				throw new InspireException("Missing formula for element: " + elementType);
			}
			checkElementVariables(archetypeParams, elementFormula, elementType);
		}
	}

	private void checkElementVariables(final Map<String, String> archetypeParams, final Formula elementFormula,
			final String elementType) {
		final List<String> elementVariables = new ArrayList<>(elementFormula.getVariables());
		boolean varIsTime;

		if (elementVariables == null || elementVariables.isEmpty()) {
			throw new InspireException(
					"Missing variables for formula: " + elementFormula.getId() + "of element " + elementType);
		} else {
			checkTimeDependency(elementFormula, elementVariables);
			
			for (final String var : elementVariables) {
				varIsTime = "TIME".equals(var);

				if (archetypeParams.getOrDefault(var, null) == null && !varIsTime) {
					throw new InspireException("Missing value for variable: " + var);
				}
			}
		}
	}

	private void checkTimeDependency(final Formula elementFormula, final List<String> elementVariables) {
		final boolean timeIsAVar = elementVariables.contains("TIME");
		final boolean formulaHasTime = elementFormula.getIsTimeDependent();
		
		if (timeIsAVar && !formulaHasTime) {
				throw new InspireException("Inconsistency error: formula " + elementFormula.getId()
						+ " is not time-dependent, but var TIME was found");
		}
		
		if(!timeIsAVar && formulaHasTime) {
			throw new InspireException("Inconsistency error: formula " + elementFormula.getId()
			+ " is time-dependent, but var TIME was not found");
		}
	}
}
