package charts;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import lungsimulator.LungSimulator;

public class RightVerticalLayout extends Composite<VerticalLayout> implements HasComponents, HasSize{
	public Plot flowChart;
	public Plot pressureChart;
	
	public RightVerticalLayout(LungSimulator lungSimulator) {
		setSizeFull();
		getContent().setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
		
		Div plotWrapper = new Div();
		add(plotWrapper);
		getContent().setFlexGrow(1, plotWrapper);
		
		flowChart = new Plot(lungSimulator.getCircuitBuilder().getFlowIds(), lungSimulator.getCircuitBuilder().getTimeline(), lungSimulator.getCircuitBuilder().getInitdataFlow(), null, "Flow");
		plotWrapper.add(flowChart);
		pressureChart = new Plot(lungSimulator.getCircuitBuilder().getPressureIds(), lungSimulator.getCircuitBuilder().getTimeline(), lungSimulator.getCircuitBuilder().getInitdataPressure(), lungSimulator.getCircuitBuilder().getInitdataVentilatorPressure(), "Pressure");
		plotWrapper.add(pressureChart);
	}

	public void updateChart(LungSimulator lungSimulator) {
		flowChart.updateChart(lungSimulator.getCircuitBuilder().getTimeline(), lungSimulator.getCircuitBuilder().getInitdataFlow(), null);
		pressureChart.updateChart(lungSimulator.getCircuitBuilder().getTimeline(), lungSimulator.getCircuitBuilder().getInitdataPressure(), lungSimulator.getCircuitBuilder().getInitdataVentilatorPressure());
	}
}
