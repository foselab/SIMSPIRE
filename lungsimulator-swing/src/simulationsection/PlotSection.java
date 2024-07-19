package simulationsection;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.Marker;
import org.knowm.xchart.style.markers.SeriesMarkers;

import lungsimulator.LungSimulator;
import utils.GraphicConstants;

/**
 * Manages the plot section in the simulation view
 */
public class PlotSection {
	/**
	 * Flow chart
	 */
	private static XYChart flowChart;
	
	/**
	 * Pressure chart
	 */
	private static XYChart pressureChart;
	
	/**
	 * Flow chart manager
	 */
	private final transient XChartPanel<XYChart> flowConstructor;
	
	/**
	 * Pressure chart manager
	 */
	private final transient XChartPanel<XYChart> presConstructor;
	
	/**
	 * Selected flow id
	 */
	private transient String flowChoice;
	
	/**
	 * Selected pressure id
	 */
	private transient String pressureChoice;
	
	/**
	 * Counter for saved images
	 */
	private static int imageCounter;
	
	/**
	 * Internal logger for errors report
	 */
	private static final Logger LOGGER = Logger.getLogger(PlotSection.class.getName());

	/**
	 * Init plot section
	 * @param lungSimulator backend access
	 * @param rightPanel panel where plots have to be added
	 * @param showVentilator true if ventilator data has to be shown
	 */
	public PlotSection(final LungSimulator lungSimulator, final JPanel rightPanel, final boolean showVentilator) {
		imageCounter = 1;
		
		final List<String> flowIds = lungSimulator.getCircuitBuilder().getFlowIds();
		flowChoice = flowIds.get(0);
		
		final List<String> pressureIds = lungSimulator.getCircuitBuilder().getPressureIds();
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

		final JComboBox<String> flowList = new JComboBox<>(flowIds.toArray(new String[0]));
		flowList.setMaximumSize(new Dimension(GraphicConstants.PLOTWIDTH, 200));
		rightPanel.add(flowList);

		flowList.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(final ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					flowChoice = (String) event.getItem();
					flowChart.setTitle(GraphicConstants.FLOWSERIES + " in " + flowChoice);
					flowChart.updateXYSeries(GraphicConstants.FLOWSERIES,
							lungSimulator.getCircuitBuilder().getTimeline(),
							lungSimulator.getCircuitBuilder().getInitdataFlow().get(flowChoice), null);
				}
			}
		});

		flowConstructor = new XChartPanel<>(flowChart);
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

		final JComboBox<String> pressureList = new JComboBox<>(pressureIds.toArray(new String[0]));
		pressureList.setMaximumSize(new Dimension(GraphicConstants.PLOTWIDTH, 200));
		rightPanel.add(pressureList);

		pressureList.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(final ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					pressureChoice = (String) event.getItem();
					pressureChart.setTitle(GraphicConstants.PRESSURE_TITLE + " in " + pressureChoice);
					pressureChart.updateXYSeries(GraphicConstants.PRESSURESERIES,
							lungSimulator.getCircuitBuilder().getTimeline(),
							lungSimulator.getCircuitBuilder().getInitdataPressure().get(pressureChoice), null);
				}
			}
		});
		
		presConstructor = new XChartPanel<>(pressureChart);
		rightPanel.add(presConstructor);
	}

	/**
	 * Update charts values
	 * @param lungSimulator backend access
	 * @param showVentilator true if ventilator values have to be shown
	 */
	public void updateCharts(final LungSimulator lungSimulator, final boolean showVentilator) {
		final List<Double> timeline = lungSimulator.getCircuitBuilder().getTimeline();
		
		//update flowChart
		final List<Double> dataFlow = lungSimulator.getCircuitBuilder().getInitdataFlow().get(flowChoice);
		final double absFlow = Collections.max(dataFlow);
		flowChart.getStyler().setYAxisMax(absFlow);
		flowChart.getStyler().setYAxisMin(-absFlow);
		flowChart.updateXYSeries(GraphicConstants.FLOWSERIES, timeline, dataFlow, null);
		flowConstructor.revalidate();
		flowConstructor.repaint();
		
		//update pressureChart
		final List<Double> ventPressure = lungSimulator.getCircuitBuilder().getInitdataVentilatorPressure();
		final List<Double> dataPressure = lungSimulator.getCircuitBuilder().getInitdataPressure().get(pressureChoice);
		
		if (showVentilator) {
			pressureChart.getStyler().setYAxisMax(Collections.max(ventPressure));
			pressureChart.updateXYSeries("Ventilator Pressure", timeline,
					ventPressure, null);
		}else {
			pressureChart.getStyler().setYAxisMax(Collections.max(dataPressure));
		}
		
		pressureChart.updateXYSeries(GraphicConstants.PRESSURESERIES, timeline, dataPressure, null);
		presConstructor.revalidate();
		presConstructor.repaint();
	}
	
	/**
	 * Saves charts images
	 */
	public static void saveCharts() {
		try {
			BitmapEncoder.saveBitmap(flowChart, "./Images/" + imageCounter + "_" + flowChart.getTitle(),
					BitmapFormat.PNG);
			BitmapEncoder.saveBitmap(pressureChart, "./Images/" + imageCounter + "_" + pressureChart.getTitle(),
					BitmapFormat.PNG);
			imageCounter++;
		} catch (IOException e1) {
			LOGGER.info("Save Error");
		}
	}
}
