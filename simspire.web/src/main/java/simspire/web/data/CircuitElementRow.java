package simspire.web.data;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import lungsimulator.LungSimulator;

/**
 * Creates a custom component where the name, value and unit of measure of a
 * circuit element are shown
 */
public class CircuitElementRow extends Composite<HorizontalLayout> implements HasComponents {
	/**
	 * For serialization purpose
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Reference to the read only field where the ventilator's value is stored
	 */
	private NumberField ventilator;

	/**
	 * Location of element value
	 */
	private final transient NumberField elementValue;

	/**
	 * Minimum value for a circuit element
	 */
	final static private double MIN = 0.0;

	/**
	 * Maximum value for a circuit element
	 */
	final static private double MAX = 50.0;

	/**
	 * Index of element in elementList
	 */
	private final transient int posNumber;

	/**
	 * backend access
	 */
	private final transient LungSimulator lungSimulator;

	/**
	 * Creates a custom component where the name, value and unit of measure of a
	 * circuit element are shown
	 * 
	 * @param lungSimulator backend access
	 * @param elmId         name of the circuit element
	 * @param value         current value of the circuit element
	 * @param unit          unit of measure for the circuit element
	 * @param isVentilator  true if the circuit element is the ventilator
	 * @param posNumber     position in the list where the circuit element is stored
	 */
	public CircuitElementRow(final LungSimulator lungSimulator, final String elmId, final double value,
			final String unit, final boolean isVentilator, final int posNumber) {
		this.lungSimulator = lungSimulator;
		this.posNumber = posNumber;
		ventilator = new NumberField();

		final Label elementName = new Label(elmId);
		elementName.setWidth("250px");

		elementValue = new NumberField();
		elementValue.setMin(MIN);
		elementValue.setMax(MAX);
		elementValue.setStep(0.000_001);
		elementValue.setValue(value);
		if (isVentilator) {
			elementValue.setHasControls(false);
			elementValue.addThemeVariants(TextFieldVariant.LUMO_ALIGN_CENTER); // text is centered
			elementValue.setReadOnly(true); // the user cannot modify the value
			ventilator = elementValue;
		} else {
			elementValue.setHasControls(true);
			elementValue.addValueChangeListener(event -> {
				final double newvalue = event.getValue();
				final double oldValue = event.getOldValue();
				if (newvalue >= MIN && newvalue <= MAX) {
					if (oldValue != newvalue) {
						lungSimulator.getCircuitBuilder().updateElementValue(newvalue, posNumber);
					}
				} else {
					Notification.show("The value must be between 0.0 and 50.0");
					elementValue.setValue(oldValue);
				}
			});
		}
		elementValue.setHeight("10px");

		final Label elementUnit = new Label(unit);

		add(elementName, elementValue, elementUnit);
	}

	public double getVentilator() {
		return ventilator == null ? -1 : ventilator.getValue();
	}

	public void setVentilator(final double value) {
		if (ventilator != null) {
			ventilator.setValue(value);
		}
	}

	/**
	 * Updates element value
	 */
	public void updateElmValue() {
		elementValue.setValue(lungSimulator.getCircuitBuilder().getElementValue(posNumber));
	}
}
