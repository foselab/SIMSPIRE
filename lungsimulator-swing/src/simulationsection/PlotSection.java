package simulationsection;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.Marker;
import org.knowm.xchart.style.markers.SeriesMarkers;

import lungsimulator.LungSimulator;
import utils.GraphicConstants;

public class PlotSection {
	private XYChart flowChart;
	private XYChart pressureChart;
	private XChartPanel<XYChart> flowConstructor;
	private XChartPanel<XYChart> pressureConstructor;
	
	private String flowChoice;
	private String pressureChoice;

	public PlotSection(LungSimulator lungSimulator, JPanel rightPanel, boolean showVentilator) {
		List<String> flowIds = lungSimulator.getCircuitBuilder().getFlowIds();
		flowChoice = flowIds.get(0);
		
		List<String> pressureIds = lungSimulator.getCircuitBuilder().getPressureIds();
		pressureChoice = pressureIds.get(0);

		// Create the flow chart
		flowChart = new XYChartBuilder().width(GraphicConstants.PLOTWIDTH).height(GraphicConstants.PLOTHEIGTH)
				.title(GraphicConstants.FLOWSERIES + " in " + flowChoice).xAxisTitle("Time [s]")
				.yAxisTitle(GraphicConstants.FLOWSERIES).build();
		flowChart.addSeries(GraphicConstants.FLOWSERIES, lungSimulator.getCircuitBuilder().getTimeline(),
				lungSimulator.getCircuitBuilder().getInitdataFlow().get(flowChoice));
		flowChart.getStyler().setYAxisMax(2.0).setYAxisMin(-2.0).setSeriesMarkers(new Marker[] { SeriesMarkers.NONE })
				.setLegendPosition(LegendPosition.InsideS);
		flowChart.getStyler().setXAxisDecimalPattern("0.0");

		JComboBox<String> flowList = new JComboBox<String>(flowIds.toArray(new String[0]));
		flowList.setMaximumSize(new Dimension(GraphicConstants.PLOTWIDTH, 200));
		rightPanel.add(flowList);

		flowList.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					flowChoice = (String) event.getItem();
					flowChart.setTitle(GraphicConstants.FLOWSERIES + " in " + flowChoice);
					flowChart.updateXYSeries(GraphicConstants.FLOWSERIES,
							lungSimulator.getCircuitBuilder().getTimeline(),
							lungSimulator.getCircuitBuilder().getInitdataFlow().get(flowChoice), null);
				}
			}
		});

		flowConstructor = new XChartPanel<XYChart>(flowChart);
		rightPanel.add(flowConstructor);

		// Create the pressure chart
		pressureChart = new XYChartBuilder().width(GraphicConstants.PLOTWIDTH).height(GraphicConstants.PLOTHEIGTH)
				.title(GraphicConstants.PRESSURE_TITLE + " at " + pressureChoice).xAxisTitle("Time [s]")
				.yAxisTitle("Pressure").build();
		pressureChart.addSeries(GraphicConstants.PRESSURESERIES, lungSimulator.getCircuitBuilder().getTimeline(),
				lungSimulator.getCircuitBuilder().getInitdataPressure().get(pressureChoice));

		if (showVentilator) {
			pressureChart.addSeries("Ventilator Pressure", lungSimulator.getCircuitBuilder().getTimeline(),
					lungSimulator.getCircuitBuilder().getInitdataVentilatorPressure());
		}

		pressureChart.getStyler().setYAxisMax(25.5).setYAxisMin(0.0)
				.setSeriesMarkers(new Marker[] { SeriesMarkers.NONE, SeriesMarkers.NONE })
				.setLegendPosition(LegendPosition.InsideS);

		pressureChart.getStyler().setXAxisDecimalPattern("0.0");

		JComboBox<String> pressureList = new JComboBox<String>(pressureIds.toArray(new String[0]));
		pressureList.setMaximumSize(new Dimension(GraphicConstants.PLOTWIDTH, 200));
		rightPanel.add(pressureList);

		pressureList.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					pressureChoice = (String) event.getItem();
					pressureChart.setTitle(GraphicConstants.PRESSURE_TITLE + " in " + pressureChoice);
					pressureChart.updateXYSeries(GraphicConstants.PRESSURESERIES,
							lungSimulator.getCircuitBuilder().getTimeline(),
							lungSimulator.getCircuitBuilder().getInitdataPressure().get(pressureChoice), null);
				}
			}
		});
		
		pressureConstructor = new XChartPanel<XYChart>(pressureChart);
		rightPanel.add(pressureConstructor);
	}

	public void updateCharts(LungSimulator lungSimulator, boolean showVentilator) {
		List<Double> timeline = lungSimulator.getCircuitBuilder().getTimeline();
		
		//update flowChart
		List<Double> dataFlow = lungSimulator.getCircuitBuilder().getInitdataFlow().get(flowChoice);
		double absFlow = Collections.max(dataFlow);
		flowChart.getStyler().setYAxisMax(absFlow);
		flowChart.getStyler().setYAxisMin(-absFlow);
		flowChart.updateXYSeries(GraphicConstants.FLOWSERIES, timeline, dataFlow, null);
		flowConstructor.revalidate();
		flowConstructor.repaint();
		
		//update pressureChart
		List<Double> ventPressure = lungSimulator.getCircuitBuilder().getInitdataVentilatorPressure();
		List<Double> dataPressure = lungSimulator.getCircuitBuilder().getInitdataPressure().get(pressureChoice);
		
		if (showVentilator) {
			pressureChart.getStyler().setYAxisMax(Collections.max(ventPressure));
			pressureChart.updateXYSeries("Ventilator Pressure", timeline,
					ventPressure, null);
		}else {
			pressureChart.getStyler().setYAxisMax(Collections.max(dataPressure));
		}
		
		pressureChart.updateXYSeries(GraphicConstants.PRESSURESERIES, timeline, dataPressure, null);
		pressureConstructor.revalidate();
		pressureConstructor.repaint();
	}
}
