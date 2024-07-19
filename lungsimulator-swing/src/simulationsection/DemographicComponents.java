package simulationsection;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lungsimulator.LungSimulator;
import lungsimulator.components.SimulatorParams;
import utils.GraphicConstants;

/**
 * Manages demographic components
 */
public class DemographicComponents {

	/**
	 * Demographic components constructor
	 * 
	 * @param lungSimulator backend access
	 * @param yInitPar      initial coordinate for components allocation
	 * @param leftPanel     panel where components have to be added
	 */
	public DemographicComponents(final LungSimulator lungSimulator, final int yInitPar, final JPanel leftPanel) {
		int yInit = yInitPar + 102;

		final SimulatorParams demographicData = lungSimulator.getDemographicData();

		final JLabel elementId = new JLabel("Patient demographic data");
		elementId.setBounds(GraphicConstants.IDELEMENTX, yInit, 200, GraphicConstants.IDELEMENTHEIGHT);
		leftPanel.add(elementId);

		yInit += 28;

		// gender set up
		final JLabel gender = new JLabel("Gender ");
		gender.setBounds(GraphicConstants.IDELEMENTX, yInit, 200, GraphicConstants.IDELEMENTHEIGHT);
		leftPanel.add(gender);

		final String[] genders = { "Male", "Female" };
		final JComboBox<String> gendersBox = new JComboBox<>(genders);
		gendersBox.setBounds(GraphicConstants.IDELEMENTX + 80, yInit, 100, 20);
		if ("MALE".equalsIgnoreCase(demographicData.getGender())) {
			gendersBox.setSelectedItem("Male");
		} else {
			gendersBox.setSelectedItem("Female");
		}

		gendersBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(final ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					final String item = (String) event.getItem();
					lungSimulator.getDemographicData().setGender(item);
				}
			}
		});

		leftPanel.add(gendersBox);

		yInit += 28;

		final JLabel age = new JLabel("Age (years)");
		age.setBounds(GraphicConstants.IDELEMENTX, yInit, 200, GraphicConstants.IDELEMENTHEIGHT);
		leftPanel.add(age);

		final SpinnerNumberModel ageModel = new SpinnerNumberModel(demographicData.getAge(), 18, 126, 1);
		final JSpinner ageElm = new JSpinner(ageModel);
		ageElm.setBounds(GraphicConstants.IDELEMENTX + 80, yInit, GraphicConstants.VALELEMENTWIDTH,
				GraphicConstants.VALELEMENTHEIGHT);
		ageElm.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent event) {
				final JSpinner spinner = (JSpinner) event.getSource();
				final int newvalue = (int) spinner.getValue();
				final int oldValue = (int) spinner.getPreviousValue();
				if (newvalue >= 18 && newvalue <= 126) {
					if (oldValue != newvalue) {
						lungSimulator.getDemographicData().setAge(newvalue);
					}
				} else {
					JOptionPane.showMessageDialog(null, "The value must be between 18 and 126");
					ageElm.setValue(oldValue);
				}
			}
		});
		leftPanel.add(ageElm);

		yInit += 28;

		final JLabel height = new JLabel("Height (m)");
		height.setBounds(GraphicConstants.IDELEMENTX, yInit, 200, GraphicConstants.IDELEMENTHEIGHT);
		leftPanel.add(height);

		final SpinnerNumberModel heightModel = new SpinnerNumberModel(demographicData.getHeight(), 0.55, 2.60, 0.01);
		final JSpinner heightElm = new JSpinner(heightModel);
		heightElm.setBounds(GraphicConstants.IDELEMENTX + 80, yInit, GraphicConstants.VALELEMENTWIDTH,
				GraphicConstants.VALELEMENTHEIGHT);
		leftPanel.add(heightElm);

		yInit += 28;

		final JLabel weight = new JLabel("Weigth (kg)");
		weight.setBounds(GraphicConstants.IDELEMENTX, yInit, 200, GraphicConstants.IDELEMENTHEIGHT);
		leftPanel.add(weight);

		final SpinnerNumberModel weightModel = new SpinnerNumberModel(demographicData.getWeight(), 25, 600, 0.1);
		final JSpinner weightElm = new JSpinner(weightModel);
		weightElm.setBounds(GraphicConstants.IDELEMENTX + 80, yInit, GraphicConstants.VALELEMENTWIDTH,
				GraphicConstants.VALELEMENTHEIGHT);
		weightElm.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent event) {
				final JSpinner spinner = (JSpinner) event.getSource();
				final double newvalue = (double) spinner.getValue();
				final double oldValue = (double) spinner.getPreviousValue();
				if (newvalue >= 25 && newvalue <= 600) {
					if (oldValue != newvalue) {
						lungSimulator.getDemographicData().setWeight(newvalue);
					}
				} else {
					JOptionPane.showMessageDialog(null, "The height value must be between 0.55 and 2.60");
					weightElm.setValue(oldValue);
				}
			}
		});
		leftPanel.add(weightElm);

		yInit += 28;

		final JLabel ibw = new JLabel("Ibw (kg)");
		ibw.setBounds(GraphicConstants.IDELEMENTX, yInit, 200, GraphicConstants.IDELEMENTHEIGHT);
		leftPanel.add(ibw);

		final JLabel ibwValue = new JLabel(String.valueOf(demographicData.getIbw()));
		ibwValue.setBounds(GraphicConstants.IDELEMENTX + 80, yInit, GraphicConstants.VALELEMENTWIDTH,
				GraphicConstants.VALELEMENTHEIGHT);
		leftPanel.add(ibwValue);

		heightElm.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent event) {
				final JSpinner spinner = (JSpinner) event.getSource();
				final double newvalue = (double) spinner.getValue();
				final double oldValue = (double) spinner.getPreviousValue();
				if (newvalue >= 0.55 && newvalue <= 2.60) {
					if (oldValue != newvalue) {
						lungSimulator.getDemographicData().setHeight(newvalue);
						ibwValue.setText(String.valueOf(lungSimulator.getDemographicData().getIbw()));
					}
				} else {
					JOptionPane.showMessageDialog(null, "The height value must be between 0.55 and 2.60");
					heightElm.setValue(oldValue);
				}
			}
		});
	}

}
