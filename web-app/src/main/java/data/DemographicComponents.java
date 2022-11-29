package data;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import lungsimulator.LungSimulator;
import lungsimulator.components.SimulatorParams;

/**
 * Manages demographic data of the simulation view
 */
public class DemographicComponents extends Composite<VerticalLayout> implements HasComponents {

	/**
	 * Init demographic data section
	 * 
	 * @param lungSimulator backend access
	 */
	public DemographicComponents(final LungSimulator lungSimulator) {
		final DemographicEventManager dem = new DemographicEventManager(lungSimulator);

		final SimulatorParams demographicData = lungSimulator.getDemographicData();
		if (lungSimulator != null && demographicData != null) {
			// gender set up
			dem.setGender(demographicData.getGender());
			add(new HorizontalLayout(demographicElementRow("Gender"), dem.getGender()));

			// age set up
			dem.setAge(18, 125, 1, demographicData.getAge());
			add(new HorizontalLayout(demographicElementRow("Age (years)"), dem.getAge()));

			// height set up
			dem.setHeight(0.55, 2.60, 0.01, demographicData.getHeight());
			add(new HorizontalLayout(demographicElementRow("Height (m)"), dem.getHeight()));

			// weight set up
			dem.setWeight(25, 600, 0.1, demographicData.getWeight());
			add(new HorizontalLayout(demographicElementRow("Weight (Kg)"), dem.getWeight()));

			// ibw set up
			dem.setIbw(demographicData.getIbw());
			add(new HorizontalLayout(demographicElementRow("Ibw (Kg)"), dem.getIbw()));
		}
	}

	private Label demographicElementRow(final String idLabel) {
		final Label label = new Label(idLabel);
		label.setWidth("140px");
		return label;
	}
}
