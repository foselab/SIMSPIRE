package charts;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import lungsimulator.LungSimulator;

/**
 * Manages all the components on the right side of the simulation view
 */
public class RightVerticalLayout extends Composite<VerticalLayout> implements HasComponents, HasSize{
	/**
	 * Flow chart
	 */
	private final transient PlotSection flowChart;
	
	/**
	 * Pressure chart
	 */
	private final transient PlotSection pressureChart;
	
	/**
	 * Init the right part of simulation view (plots section)
	 * @param lungSimulator backend access
	 */
	public RightVerticalLayout(final LungSimulator lungSimulator) {
		setSizeFull();
		getContent().setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.STRETCH);
		
		final Div plotWrapper = new Div();
		add(plotWrapper);
		getContent().setFlexGrow(1, plotWrapper);
		
		flowChart = new PlotSection(lungSimulator.getCircuitBuilder().getFlowIds(), lungSimulator.getCircuitBuilder().getTimeline(), lungSimulator.getCircuitBuilder().getInitdataFlow(), null, "Flow");
		plotWrapper.add(flowChart);
		pressureChart = new PlotSection(lungSimulator.getCircuitBuilder().getPressureIds(), lungSimulator.getCircuitBuilder().getTimeline(), lungSimulator.getCircuitBuilder().getInitdataPressure(), lungSimulator.getCircuitBuilder().getInitdataVentilatorPressure(), "Pressure");
		plotWrapper.add(pressureChart);
	}

	/**
	 * Updates chart values 
	 * @param lungSimulator backend access
	 */
	public void updateChart(final LungSimulator lungSimulator) {
		flowChart.updateChart(lungSimulator.getCircuitBuilder().getTimeline(), lungSimulator.getCircuitBuilder().getInitdataFlow(), null);
		pressureChart.updateChart(lungSimulator.getCircuitBuilder().getTimeline(), lungSimulator.getCircuitBuilder().getInitdataPressure(), lungSimulator.getCircuitBuilder().getInitdataVentilatorPressure());
	}
}
