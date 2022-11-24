package view;

import java.util.Arrays;
import java.util.List;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;

import charts.RightVerticalLayout;
import data.CircuitComponents;
import data.DemographicComponents;
import lungsimulator.LungSimulator;

public class SimulationView extends Composite<HorizontalLayout> implements HasComponents {
	/**
	 * Backend access
	 */
	private final transient LungSimulator lungSimulator;

	/**
	 * Section where circuit components are displayed
	 */
	private final transient CircuitComponents circuitSection;

	/**
	 * Section where plots are displayed
	 */
	private final transient RightVerticalLayout plotSection;

	/**
	 * Button to start the simulation
	 */
	private final transient Button start;

	/**
	 * Button to stop the simulation
	 */
	private final transient Button stop;

	/**
	 * Flag to manage simulation execution: true when start button is clicked, false
	 * when stop button is pressed
	 */
	private transient boolean flag;

	/**
	 * Builds the simulation view
	 */
	public SimulationView() {
		this.lungSimulator = (LungSimulator) VaadinSession.getCurrent().getAttribute("lungSimulator");
		lungSimulator.simulationSetUp();

		final VerticalLayout leftSide = new VerticalLayout(new H2("INSPIRE"));
		leftSide.getStyle().set("background-color", "#EFEFF0");

		final H4 circuitTitle = new H4("Circuit Components");
		circuitSection = new CircuitComponents(lungSimulator);
		final Div circuitElm = new Div();
		circuitElm.add(circuitTitle, circuitSection);

		leftSide.add(circuitElm);

		start = new Button("Start", e -> simulationManager());
		start.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
		start.setDisableOnClick(true);

		stop = new Button("Stop", e -> {
			flag = false;
			start.setEnabled(true);
		});
		stop.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
		stop.setDisableOnClick(true);
		stop.setVisible(false);

		leftSide.add(new HorizontalLayout(start, stop));

		final H4 demoTitle = new H4("Demographic data");
		leftSide.add(demoTitle);
		leftSide.add(new DemographicComponents(lungSimulator));

		final VerticalLayout rightSide = new VerticalLayout();
		plotSection = new RightVerticalLayout(lungSimulator);
		rightSide.add(plotSection);

		add(leftSide, rightSide);
	}

	/**
	 * Manages the simulation timing logic
	 */
	public void simulationManager() {
		// moment of time (in seconds) where the simulation starts
		double tStart = System.currentTimeMillis() / 1000.0;
		boolean isTimeDependent = lungSimulator.getCircuitBuilder().isTimeDependentCir();

		stop.setVisible(true);
		stop.setEnabled(true);

		flag = true;

		final var userInterface = UI.getCurrent();

		/*
		 * In order to update the user interface during the simulation, without the need
		 * of user interaction, a Server Push must be performed
		 */
		new Thread(() -> {
			/*
			 * A simulation step is defined by three parameters: initialT: the instant of
			 * time where the step begins; stepLength: the length of a step (i.e. 0.1
			 * seconds) lastT: the instant of time where the step ends
			 */
			double lastT = 0;
			double stepLength = 0.1;

			int count = 0;

			while (flag) {
				final double ntStart = System.currentTimeMillis() / 1000.0;
				/*
				 * initialT is given by the difference between the current instant of time
				 * (ntStart) and the instant of time where the entire simulation has begun
				 * (tStart)
				 */
				final double initialT = ntStart - tStart;

				// a step is performed after stepLength seconds
				if (initialT - lastT >= stepLength) {
					count++;
					lungSimulator.miniSimulation(initialT, stepLength);

					/*
					 * Updating the interface is quite burdensome, so to avoid a crash it is updated
					 * every three steps (about every 3*stepLength seconds)
					 */
					if (count == 3) {
						userInterface.access(() -> {
							if(isTimeDependent) {
								circuitSection.updateTimeDependentElms();
							}
							circuitSection
									.updateVentilator(lungSimulator.getCircuitBuilder().getCurrentVentilatorValue());
							plotSection.updateChart(lungSimulator);
						});
						count = 0;
					}
					lastT = initialT;
				}
			}
		}).start();
	}

}
