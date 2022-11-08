package view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;

import charts.RightVerticalLayout;
import data.CircuitComponents;
import data.DemographicComponents;
import lungsimulator.LungSimulator;

public class SimulationView extends Composite<Component> {
	LungSimulator lungSimulator;
	VerticalLayout vertical;
	VerticalLayout plots;
	CircuitComponents cc;
	RightVerticalLayout rvl;
	Button start;
	Button stop;
	boolean flag;

	public SimulationView() {
		vertical = new VerticalLayout(new H2("INSPIRE"));
		this.lungSimulator = (LungSimulator) VaadinSession.getCurrent().getAttribute("lungSimulator");
		lungSimulator.mini();
		H4 circuitTitle = new H4("Circuit Components");
		vertical.add(circuitTitle);
		cc = new CircuitComponents(lungSimulator);
		vertical.add(cc);
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
		vertical.add(new HorizontalLayout(start, stop));
		H4 demoTitle = new H4("Demographic data");
		vertical.add(demoTitle);
		vertical.add(new DemographicComponents(lungSimulator));
		plots = new VerticalLayout();
		rvl = new RightVerticalLayout(lungSimulator);
		plots.add(rvl);
	}

	@Override
	protected Component initContent() {
		return new HorizontalLayout(vertical, plots);
	}

	public void simulationManager() {
		// moment of time (in seconds) where simulation starts
		double tStart = System.currentTimeMillis() / 1000.0;
		stop.setVisible(true);
		stop.setEnabled(true);
		flag = true;
		var ui = UI.getCurrent();
		new Thread(() -> {
			double lastT = 0;
			double step = 0.1;
			while (flag) {
				double ntStart = System.currentTimeMillis() / 1000.0;
				double initialT = ntStart - tStart;
				// wait for step seconds until next resolution
				if (initialT - lastT >= step) {
					ui.access(() -> {
						lungSimulator.miniSimulation(initialT);
						cc.updateVentilator(lungSimulator.getCircuitBuilder().getCurrentVentilatorValue());
						rvl.updateChart(lungSimulator);
						/*
						 * così funziona ma devo ricreare tutto il lato destro plots.remove(rvl); rvl =
						 * new RightVerticalLayout(lungSimulator); plots.add(rvl);
						 */
					});
					lastT = initialT;
				}
			}
		}).start();

	}

}
