package simulationsection;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lungsimulator.LungSimulator;
import utils.GraphicConstants;

public class CircuitElementRow {

	/**
	 * Reference to the read only field where the ventilator's value is stored
	 */
	private JLabel ventilator;

	/**
	 * Location of element value
	 */
	private JSpinner elementValue;

	/**
	 * Minimum value for a circuit element
	 */
	final static private double MIN = 0.0;

	/**
	 * Maximum value for a circuit element
	 */
	final static private double MAX = 50.0;

	private int posNumber;

	LungSimulator lungSimulator;

	/**
	 * Creates a custom component where the name, value and unit of measure of a
	 * circuit element are shown
	 * 
	 * @param lungSimulator backend access
	 * @param id            name of the circuit element
	 * @param value         current value of the circuit element
	 * @param unit          unit of measure for the circuit element
	 * @param isVentilator  true if the circuit element is the ventilator
	 * @param posNumber     position in the list where the circuit element is stored
	 */
	public CircuitElementRow(final LungSimulator lungSimulator, final JPanel circElmPanel, final String elmId,
			final double value, final String unit, final boolean isVentilator, final int posNumber,
			final int elementY) {
		this.lungSimulator = lungSimulator;
		this.posNumber = posNumber;

		final JLabel elementName = new JLabel(elmId);
		elementName.setBounds(GraphicConstants.IDELEMENTX, elementY, GraphicConstants.IDELEMENTWIDTH,
				GraphicConstants.IDELEMENTHEIGHT);
		circElmPanel.add(elementName);

		if (isVentilator) {
			ventilator = new JLabel(String.valueOf(value));
			ventilator.setBounds(GraphicConstants.VALELEMENTX, elementY, GraphicConstants.VALELEMENTWIDTH,
					GraphicConstants.VALELEMENTHEIGHT);
			circElmPanel.add(ventilator);
		} else {
			elementValue = new JSpinner();
			final SpinnerNumberModel elementModel = new SpinnerNumberModel(value, MIN, MAX, 0.001);
			elementValue.setModel(elementModel);
			elementValue.setBounds(GraphicConstants.VALELEMENTX, elementY, GraphicConstants.VALELEMENTWIDTH,
					GraphicConstants.VALELEMENTHEIGHT);

			elementValue.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {
					JSpinner spinner = (JSpinner) e.getSource();
					final double newvalue = (double) spinner.getValue();
					final double oldValue = (double) spinner.getPreviousValue();
					if (newvalue >= MIN && newvalue <= MAX) {
						if (oldValue != newvalue) {
							lungSimulator.getCircuitBuilder().updateElementValue(newvalue, posNumber);
						}
					} else {
						JOptionPane.showMessageDialog(circElmPanel, "The value must be between 0.0 and 50.0");
						elementValue.setValue(oldValue);
					}
				}
			});

			circElmPanel.add(elementValue);
		}

		final JLabel elementUnit = new JLabel(unit);
		elementUnit.setBounds(GraphicConstants.UMELEMENTX, elementY, GraphicConstants.UMELEMENTWIDTH, GraphicConstants.UMELEMENTHEIGHT);
		circElmPanel.add(elementUnit);
	}

	public double getVentilator() {
		return ventilator == null ? -1 : Double.parseDouble(ventilator.getText());
	}

	public void setVentilator(final double value) {
		if (ventilator != null) {
			ventilator.setText(String.valueOf(value));
		}
	}

	public void updateElmValue() {
		elementValue.setValue(lungSimulator.getCircuitBuilder().getElementValue(posNumber));
	}

}
