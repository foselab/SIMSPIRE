package org.vaadin.example;

import java.util.ArrayList;
import java.util.Arrays;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import lungsimulator.LungSimulator;
import lungsimulator.components.SimulatorParams;

public class DemographicComponents extends Composite<Component> {
	private LungSimulator lungSimulator;
	VerticalLayout verticalLayout;
	DemographicEventManager dem;

	public DemographicComponents(LungSimulator lungSimulator) {
		this.lungSimulator = lungSimulator;
		verticalLayout = new VerticalLayout();
		dem = new DemographicEventManager(lungSimulator);
	}

	protected Component initContent() {
		SimulatorParams demographicData = lungSimulator.getDemographicData();
		if (lungSimulator != null && demographicData != null) {
			// gender set up
			dem.setGender(demographicData.getGender());
			verticalLayout.add(new HorizontalLayout(demographicElementRow("Gender"), dem.getGender()));

			// age set up
			dem.setAge(18, 125, 1.0, demographicData.getAge());
			verticalLayout.add(new HorizontalLayout(demographicElementRow("Age (years)"), dem.getAge()));

			// height set up
			dem.setHeight(0.55, 2.60, 0.01, demographicData.getHeight());
			verticalLayout.add(new HorizontalLayout(demographicElementRow("Height (m)"), dem.getHeight()));

			// weight set up
			dem.setWeight(25, 600, 0.1, demographicData.getWeight());
			verticalLayout.add(new HorizontalLayout(demographicElementRow("Weight (Kg)"), dem.getWeight()));

			// ibw set up
			dem.setIbw(demographicData.getIbw());
			verticalLayout.add(new HorizontalLayout(demographicElementRow("Ibw (Kg)"), dem.getIbw()));
		}

		return verticalLayout;
	}

	private Label demographicElementRow(String id) {
		Label label = new Label(id);
		label.setWidth("140px");
		return label;
	}
}
