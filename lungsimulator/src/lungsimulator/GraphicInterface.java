package lungsimulator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.Marker;
import org.knowm.xchart.style.markers.SeriesMarkers;

import components.CircuitElm;
import lungsimulator.components.Archetype;
import lungsimulator.components.Element;
import lungsimulator.components.Patient;
import lungsimulator.utils.Utils;
import simulator.CirSim;

public class GraphicInterface {

	private static final String PRESSURE_TITLE = "Pressure";
	private static final String PRESSURESERIES = "Patient Pressure";
	private static final String FLOWSERIES = "Flow";
	private static final int MAXDATA = 200;

	private static final int IDELEMENTX = 33;
	private static final int IDELEMENTWIDTH = 400;
	private static final int IDELEMENTHEIGHT = 20;

	private static final int VALELEMENTX = 250;
	private static final int VALELEMENTWIDTH = 50;
	private static final int VALELEMENTHEIGHT = 20;

	private static final String UMRES = "cmH2O/L/s";
	private static final String UMCAP = "L/cmH2O";
	private static final String UMGEN = "cmH2O";

	private static final int UMELEMENTX = 310;
	private static final int UMELEMENTWIDTH = 100;
	private static final int UMELEMENTHEIGHT = 20;

	private boolean showVentilator = true;
	private double[][] initdataPressure;
	private double[][] initdataVentilatorPressure;
	private double[][] initdataFlow;

	private List<String> flowIds = new ArrayList<>();
	private List<String> pressureIds = new ArrayList<>();
	private Map<String, String> pressureCoord = new LinkedHashMap<>();

	private int flowIndexElm = 1;
	private int pressureIndexElm = 1;

	private JFrame frame;
	private XYChart pressureChart;
	private XYChart flowChart;
	private XChartPanel<XYChart> sw;
	private XChartPanel<XYChart> sw2;
	private JPanel patientPanel;
	private JSpinner element;
	private JSpinner ventilator;
	private JButton stop;
	private JButton start;
	private JButton printData;
	private boolean state = true;
	private int imageCounter = 1;
	private String modelChoice;

	public String selectSchema() {

		// options configuration
		String[] models = { "Model of Albanese", "Model of Al-Naggar", "Model of Baker", "Model of Jain",
				"Model of Campbell-Brown", "Your own model..." };
		Object choice = JOptionPane.showInputDialog(null, "Select model", "ChooseModel", JOptionPane.PLAIN_MESSAGE,
				null, models, models[0]);
		String var = String.valueOf(choice);

		if (var.equals("Your own model...")) {
			Object lungFile = JOptionPane.showInputDialog(null,
					"Insert your lung model file name (e.g. myLungModel.yaml) \n The file must be in config folder",
					"Choose Lung Model File", JOptionPane.PLAIN_MESSAGE, null, null, null);
			Object arcFile = JOptionPane.showInputDialog(null,
					"Insert your archetype file name (e.g. myArcModel.yaml) \n The file must be in config folder",
					"Choose Archetype File", JOptionPane.PLAIN_MESSAGE, null, null, null);
			String config = "config/";
			modelChoice = config + String.valueOf(lungFile) + "***" + config + String.valueOf(arcFile);

		} else {
			String name = var.replace("Model of ", "");
			modelChoice = name;
		}
		return modelChoice;
	}

	/**
	 * Graphic interface set up
	 * 
	 * @param patient
	 * @param archetype
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void frameConfig(Patient patient, Archetype archetype) {
		// frame configuration
		frame = new JFrame();
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // fullscreen option
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		int w = 1500;
		int h = 400;

		patientPanelConfig(patient, archetype);

		// Init the arrays for the data
		initdataPressure = new double[pressureCoord.size() + 1][MAXDATA];
		// One row for time values and one row for pressure values
		initdataVentilatorPressure = new double[2][MAXDATA];
		// One row for time values and one row for each element of the circuit
		initdataFlow = new double[flowIds.size() + 1][MAXDATA];

		// Fill initData with zeros
		Utils.initVectors(MAXDATA, initdataPressure, initdataVentilatorPressure, initdataFlow);

		JPanel flowPanel = new JPanel();
		frame.getContentPane().add(flowPanel);
		flowPanel.setLayout(new BoxLayout(flowPanel, BoxLayout.Y_AXIS));

		// Create the flow chart
		flowChart = new XYChartBuilder().width(w).height(h).title(FLOWSERIES + " in " + flowIds.get(0))
				.xAxisTitle("Time [s]").yAxisTitle(FLOWSERIES).build();
		flowChart.addSeries(FLOWSERIES, initdataFlow[0], initdataFlow[1]);
		flowChart.getStyler().setYAxisMax(2.0).setYAxisMin(-2.0).setSeriesMarkers(new Marker[] { SeriesMarkers.NONE })
				.setLegendPosition(LegendPosition.InsideS);

		flowChart.getStyler().setXAxisDecimalPattern("0.0");

		frame.getContentPane().setLayout(new GridLayout(1, 2, 0, 0));

		JComboBox flowList = new JComboBox(flowIds.toArray());
		flowList.setMaximumSize(new Dimension(w, 200));
		flowPanel.add(flowList);

		flowList.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox) e.getSource();
				String choice = (String) cb.getSelectedItem();
				flowIndexElm = flowIds.indexOf(choice) + 1;
				flowChart.setTitle(FLOWSERIES + " in " + choice);
				flowChart.updateXYSeries(FLOWSERIES, initdataFlow[0], initdataFlow[flowIndexElm], null);
			}
		});

		sw2 = new XChartPanel<XYChart>(flowChart);
		flowPanel.add(sw2);

		// Create the pressure chart
		pressureChart = new XYChartBuilder().width(w).height(h).title(PRESSURE_TITLE + " at " + pressureIds.get(0))
				.xAxisTitle("Time [s]").yAxisTitle("Pressure").build();
		pressureChart.addSeries(PRESSURESERIES, initdataPressure[0], initdataPressure[1]);

		if (showVentilator) {
			pressureChart.addSeries("Ventilator Pressure", initdataVentilatorPressure[0],
					initdataVentilatorPressure[1]);
		}

		pressureChart.getStyler().setYAxisMax(25.5).setYAxisMin(0.0)
				.setSeriesMarkers(new Marker[] { SeriesMarkers.NONE, SeriesMarkers.NONE })
				.setLegendPosition(LegendPosition.InsideS);

		pressureChart.getStyler().setXAxisDecimalPattern("0.0");

		JComboBox pressureList = new JComboBox(pressureIds.toArray());
		pressureList.setMaximumSize(new Dimension(w, 200));
		flowPanel.add(pressureList);

		pressureList.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox) e.getSource();
				String choice = (String) cb.getSelectedItem();
				pressureIndexElm = pressureIds.indexOf(choice) + 1;
				pressureChart.setTitle(PRESSURE_TITLE + " in " + choice);
				pressureChart.updateXYSeries(PRESSURESERIES, initdataPressure[0], initdataPressure[pressureIndexElm],
						null);

			}
		});
		sw = new XChartPanel<XYChart>(pressureChart);
		flowPanel.add(sw);

	}

	private void patientPanelConfig(final Patient patient, final Archetype archetype) {
		patientPanel = new JPanel();
		frame.getContentPane().add(patientPanel);
		patientPanel.setLayout(null);

		int yStart = 27;
		for (final Element e : patient.getElementsList()) {
			String elmValue;
			String elmId = e.getAssociatedFormula().getId();

			if (e.isShowLeft()) {
				pressureCoord.put(e.getIdLeft(), "left");
			}

			if (e.isShowRight()) {
				pressureCoord.put(e.getIdRight(), "right");
			}

			// resistance
			if (e.getType().equals("ResistorElm")) {
				elmValue = archetype.getParameters().get(e.getAssociatedFormula().getVariables().get(0));
				graphicDesignForElement(elmId, UMRES, yStart, Double.parseDouble(elmValue), false);
				flowIds.add(elmId);
			}

			// capacitor
			if (e.getType().equals("CapacitorElm")) {
				elmValue = archetype.getParameters().get(e.getAssociatedFormula().getVariables().get(0));
				graphicDesignForElement(elmId, UMCAP, yStart, Double.parseDouble(elmValue), false);
				flowIds.add(elmId);
			}

			if (e.getType().equals("ExternalVoltageElm")) {
				graphicDesignForElement(elmId, UMGEN, yStart, 0, true);
			}

			yStart += 28;
		}

		pressureIds = new ArrayList<>(pressureCoord.keySet());

		stop = new JButton("Stop");
		stop.setFont(new Font("Arial", Font.BOLD, 16));
		stop.setBounds(IDELEMENTX, yStart + 28, 100, 50);
		stop.setBackground(Color.RED);
		stop.setForeground(Color.WHITE);
		stop.setVisible(true);

		start = new JButton("Start");
		start.setFont(new Font("Arial", Font.BOLD, 16));
		start.setBounds(IDELEMENTX, yStart + 28, 100, 50);
		start.setBackground(Color.GREEN);
		start.setForeground(Color.WHITE);
		start.setVisible(false);

		printData = new JButton("Print");
		printData.setFont(new Font("Arial", Font.BOLD, 16));
		printData.setBounds(VALELEMENTX, yStart + 28, 100, 50);
		printData.setBackground(Color.YELLOW);
		printData.setForeground(Color.BLACK);
		printData.setVisible(true);

		printData.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					BitmapEncoder.saveBitmap(flowChart, "./Images/" + imageCounter + "_" + flowChart.getTitle(),
							BitmapFormat.PNG);
					BitmapEncoder.saveBitmap(pressureChart, "./Images/" + imageCounter + "_" + pressureChart.getTitle(),
							BitmapFormat.PNG);
					imageCounter++;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		patientPanel.add(stop);
		patientPanel.add(start);
		patientPanel.add(printData);
	}

	/**
	 * Shown data are shifted and updated
	 * 
	 * @param time      moment in time of the execution
	 * @param myCircSim circuit build according to the model
	 */
	public void updateShownDataValues(final double time, final CirSim myCircSim) {
		// Shift data in vectors
		Utils.shiftData(MAXDATA, initdataPressure, initdataVentilatorPressure, initdataFlow);

		// Update time
		initdataPressure[0][MAXDATA - 1] = time;
		initdataVentilatorPressure[0][MAXDATA - 1] = time;
		initdataFlow[0][MAXDATA - 1] = time;

		int count = 1;
		int countPressure = 1;

		for (final CircuitElm cir : myCircSim.getElmList()) {
			if (cir.getIdLeft() != null && pressureCoord.containsKey(cir.getIdLeft())
					&& pressureCoord.get(cir.getIdLeft()).equals("left")) {
				initdataPressure[countPressure][MAXDATA - 1] = cir.getVoltZero();
				countPressure++;
			}

			if (cir.getIdRight() != null && pressureCoord.containsKey(cir.getIdRight())
					&& pressureCoord.get(cir.getIdRight()).equals("right")) {
				initdataPressure[countPressure][MAXDATA - 1] = cir.getVoltOne();
				countPressure++;
			}

			if (cir.getClass().getSimpleName().equals("ExternalVoltageElm")) {
				initdataVentilatorPressure[1][MAXDATA - 1] = cir.getVoltageDiff();
				ventilator.setValue(cir.getVoltageDiff());
			} else {
				initdataFlow[count][MAXDATA - 1] = cir.getCurrent();
				count++;
			}
		}

		pressureChart.updateXYSeries(PRESSURESERIES, initdataPressure[0], initdataPressure[1], null);
		if (showVentilator) {
			pressureChart.updateXYSeries("Ventilator Pressure", initdataVentilatorPressure[0],
					initdataVentilatorPressure[1], null);
		}

		pressureChart.getStyler().setYAxisMax(getMax(initdataVentilatorPressure[1]));
		flowChart.getStyler().setYAxisMax(getMax(initdataFlow[flowIndexElm]));
		flowChart.getStyler().setYAxisMin(-getMax(initdataFlow[flowIndexElm]));

		flowChart.updateXYSeries(FLOWSERIES, initdataFlow[0], initdataFlow[flowIndexElm], null);
		pressureChart.updateXYSeries(PRESSURESERIES, initdataPressure[0], initdataPressure[pressureIndexElm], null);

		stop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				stop.setVisible(false);
				start.setVisible(true);
				state = false;
			}
		});

		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				start.setVisible(false);
				stop.setVisible(true);
				state = true;
			}
		});

		// Update the charts
		sw.revalidate();
		sw.repaint();
		sw2.revalidate();
		sw2.repaint();

	}

	/**
	 * Create graphic design for circuit element
	 * 
	 * @param elementDescr element description
	 * @param elementUnit  unit of measurement
	 * @param elementY     component height
	 * @param value        element value
	 * @param isVentilator true if the element is the ventilator
	 */
	private void graphicDesignForElement(final String elementDescr, final String elementUnit, final int elementY,
			final double value, final boolean isVentilator) {
		element = new JSpinner();

		// Element description
		final JLabel elementId = new JLabel(elementDescr);
		elementId.setBounds(IDELEMENTX, elementY, IDELEMENTWIDTH, IDELEMENTHEIGHT);
		patientPanel.add(elementId);

		// Element value
		final SpinnerNumberModel elementModel = new SpinnerNumberModel(0.0, 0.000, 100.0, 0.001);
		element.setModel(elementModel);
		element.setBounds(VALELEMENTX, elementY, VALELEMENTWIDTH, VALELEMENTHEIGHT);
		element.setValue(value);
		patientPanel.add(element);

		if (isVentilator) {
			ventilator = element;
		}

		// Element unit of measurement
		final JLabel unit = new JLabel(elementUnit);
		unit.setBounds(UMELEMENTX, elementY, UMELEMENTWIDTH, UMELEMENTHEIGHT);
		patientPanel.add(unit);

	}

	private static Double getMax(final double[] data) {
		double max = data[0];

		for (final Double value : data) {
			if (value > max) {
				max = value;
			}
		}

		return max;
	}

	public boolean getStateOfExecution() {
		return state;
	}

}
