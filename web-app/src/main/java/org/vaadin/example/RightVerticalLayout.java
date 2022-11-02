package org.vaadin.example;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import lungsimulator.LungSimulator;

public class RightVerticalLayout extends Composite<Component>{
	private Plot flowChart;
	private Plot pressureChart;
	
	private LungSimulator lungSimulator;
	
	public RightVerticalLayout(LungSimulator lungSimulator) {
		this.lungSimulator = lungSimulator;
		this.flowChart = new Plot(lungSimulator.getCircuitBuilder().getFlowIds(), lungSimulator.getCircuitBuilder().getTimeline(), lungSimulator.getCircuitBuilder().getInitdataFlow());
		this.pressureChart = new Plot(lungSimulator.getCircuitBuilder().getPressureIds(), lungSimulator.getCircuitBuilder().getTimeline(), lungSimulator.getCircuitBuilder().getInitdataPressure());
	}
	
	@Override
	protected Component initContent() {
		return new VerticalLayout(flowChart, pressureChart);
	}

	public Plot getFlowChart() {
		return flowChart;
	}
}
