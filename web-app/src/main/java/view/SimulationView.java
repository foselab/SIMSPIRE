package view;

import java.util.Arrays;

import org.vaadin.example.CircuitComponents;
import org.vaadin.example.RightVerticalLayout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
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
		cc = new CircuitComponents(lungSimulator);
		vertical.add(cc);
		start = new Button("Start", e -> simulationManager());
		//start.setDisableOnClick(true);
		vertical.add(start);
		plots = new VerticalLayout();
		rvl = new RightVerticalLayout(lungSimulator);
		plots.add(rvl);
	}

	@Override
	protected Component initContent() {
		return new HorizontalLayout(vertical, plots);
	}

	public void simulationManager() {
		lungSimulator.miniSimulation();
		rvl.getFlowChart().getChart().updateChart(Arrays.asList(lungSimulator.getCircuitBuilder().getTimeline()),
				lungSimulator.getCircuitBuilder().getInitdataFlow());
	}

}
