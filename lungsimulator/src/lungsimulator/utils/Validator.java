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
import lungsimulator.components.SimulatorParams;
import lungsimulator.exceptions.InspireException;

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
	 * Checks for missing or not properly built sections of the patient model,
	 * its archetype and its demographic data
	 * 
	 * @param patient   the chosen patient model
	 * @param archetype the chosen archetype
	 * @param demographicData demographic data of the patient
	 */
	public void evaluate(final Patient patient, final Archetype archetype, final SimulatorParams demographicData) {

		LOGGER.log(Level.INFO, "Init validation process");

		// objects are not null and schema id is equal for both objects
		checkConsistency(patient, archetype, demographicData);

		// check there are minimum elements required for a proper implementation
		patient.validate();
		archetype.validate();
		// check demographic data are legit
		demographicData.validate();

		final List<Element> patientElement = new ArrayList<>(patient.getElementsList());
		final Map<String, String> archetypeParams = new ConcurrentHashMap<>(archetype.getParameters());

		for (final Element e : patientElement) {
			// check element
			e.validate();

			// check formula
			checkElementFormula(archetypeParams, e.getAssociatedFormula(), e.getType(), e.getElementName());

			// once one pressure point is found, there is no need to keep checking
			if (!atLeastOnePres) {
				atLeastOnePres = e.atLeastOnePressurePoint();
			}
		}

		if (!atLeastOnePres) {
			throw new InspireException("You must set at least one known pressure point");
		}

		LOGGER.log(Level.INFO, "Validation process successfully completed");
	}

	private void checkConsistency(final Patient patient, final Archetype archetype, SimulatorParams demographicData) {
		if (patient == null) {
			throw new InspireException("Lung model file is not properly built");
		}
		
		if(archetype == null) {
			throw new InspireException("Archetype file is not properly built");
		}
		
		if(demographicData == null) {
			throw new InspireException("Demographic data file is not properly built");
		}

		if (patient.getSchema() != archetype.getSchema()) {
			throw new InspireException("Patient schema and Archetype schema are inconsistent");
		}
	}

	private void checkElementFormula(final Map<String, String> archetypeParams, final Formula elementFormula,
			final String elementType, final String elementName) {
		String formula;
		if (elementFormula == null) {
			throw new InspireException("Missing formula associated to " + elementType + " element");
		} else if (elementFormula != null && !elementFormula.getIsExternal()) {
			formula = elementFormula.getFormula();
			if (formula == null || formula.isEmpty()) {
				throw new InspireException("Missing formula for element: " + elementType);
			}
			checkElementVariables(archetypeParams, elementFormula, elementType, elementName);
		}
	}

	private void checkElementVariables(final Map<String, String> archetypeParams, final Formula elementFormula,
			final String elementType, final String elementName) {
		final List<String> elementVariables = new ArrayList<>(elementFormula.getVariables());
		boolean varIsTime;

		if (elementVariables == null || elementVariables.isEmpty()) {
			throw new InspireException(
					"Missing variables for formula: " + elementName + "of element " + elementType);
		} else {
			checkTimeDependency(elementFormula, elementVariables, elementName);

			for (final String var : elementVariables) {
				varIsTime = "TIME".equals(var);

				if (archetypeParams.getOrDefault(var, null) == null && !varIsTime) {
					throw new InspireException("Missing value for variable: " + var);
				}
			}
		}
	}

	private void checkTimeDependency(final Formula elementFormula, final List<String> elementVariables, final String elementName) {
		final boolean timeIsAVar = elementVariables.contains("TIME");
		final boolean formulaHasTime = elementFormula.getIsTimeDependent();

		if (timeIsAVar && !formulaHasTime) {
			throw new InspireException("Inconsistency error: formula " + elementName
					+ " is not time-dependent, but var TIME was found");
		}

		if (!timeIsAVar && formulaHasTime) {
			throw new InspireException("Inconsistency error: formula " + elementName
					+ " is time-dependent, but var TIME was not found");
		}
	}
}
