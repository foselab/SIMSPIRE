package view;

import java.util.Arrays;

import org.vaadin.example.CircuitComponents;
import org.vaadin.example.DemographicComponents;
import org.vaadin.example.RightVerticalLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.VaadinSession;

import lungsimulator.LungSimulator;

public class SimulationView extends Composite<Component> {
	LungSimulator lungSimulator;
	VerticalLayout vertical;
	VerticalLayout plots;
	CircuitComponents cc;
	RightVerticalLayout rvl;
	Button start;
	Button stop;
	boolean go;
	boolean wait;

	public SimulationView() {
		vertical = new VerticalLayout(new H2("INSPIRE"));
		this.lungSimulator = (LungSimulator) VaadinSession.getCurrent().getAttribute("lungSimulator");
		lungSimulator.mini();
		H4 circuitTitle = new H4("Circuit Components");
		vertical.add(circuitTitle);
		cc = new CircuitComponents(lungSimulator);
		vertical.add(cc);
		start = new Button("Start", e -> simulationManager());
		// start.setDisableOnClick(true);
		vertical.add(start);
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

		var ui = UI.getCurrent();
		new Thread(() -> {
			double lastT = 0;
			double step = 0.1;
			while (true) {
				double ntStart = System.currentTimeMillis() / 1000.0;
				double initialT = ntStart - tStart;
				// wait for step seconds until next resolution
				if (initialT - lastT >= step) {
					
					ui.access(() -> {
						lungSimulator.miniSimulation(initialT);
						plots.remove(rvl);
						rvl = new RightVerticalLayout(lungSimulator);
						plots.add(rvl);
					});
					lastT = initialT;
				}
			}
		}).start();

	}

}
