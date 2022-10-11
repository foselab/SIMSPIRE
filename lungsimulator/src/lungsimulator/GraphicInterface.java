package lungsimulator;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;

import org.apache.commons.math3.util.Precision;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.XYStyler;
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
	private static final String FLOWSTRING = "Flow";
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
	private String[] columnNames = { "Element", "V0", "V1", "Current" };
	private String[][] data;
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
	private JSpinner voltage = new JSpinner();
	private JTable dataTable = new JTable();

	public GraphicInterface() {
	}

	/**
	 * Graphic interface set up
	 * 
	 * @param patient
	 * @param archetype
	 */
	public void frameConfig(Patient patient, Archetype archetype) {
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
		flowChart = new XYChartBuilder().width(w).height(h).title(FLOWSTRING + " in " + flowIds.get(0))
				.xAxisTitle("Time [s]").yAxisTitle(FLOWSTRING).build();
		flowChart.addSeries(FLOWSTRING, initdataFlow[0], initdataFlow[1]);
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
				flowChart.setTitle(FLOWSTRING + " in " + choice);
				flowChart.updateXYSeries(FLOWSTRING, initdataFlow[0], initdataFlow[flowIndexElm], null);
			}
		});

		sw2 = new XChartPanel<XYChart>(flowChart);
		flowPanel.add(sw2);

		// Create the pressure chart
		pressureChart = new XYChartBuilder().width(w).height(h).title(PRESSURE_TITLE + " at " + pressureIds.get(0))
				.xAxisTitle("Time [s]").yAxisTitle("Pressure").build();
		pressureChart.addSeries("Patient Pressure", initdataPressure[0], initdataPressure[1]);

		if (showVentilator) {
			pressureChart.addSeries("Ventilator Pressure", initdataVentilatorPressure[0],
					initdataVentilatorPressure[1]);
		}

		pressureChart.getStyler().setYAxisMax(25.5).setYAxisMin(0.0)
				.setSeriesMarkers(new Marker[] { SeriesMarkers.NONE, SeriesMarkers.NONE })
				.setLegendPosition(LegendPosition.InsideS);
		
		pressureChart.getStyler().setXAxisDecimalPattern("0.0");
		//pressureChart.getStyler().setXAxisMax(100.0); //imposta il max valore sull'asse x
		//pressureChart.getStyler().setXAxisTickMarkSpacingHint(pressureChart.getWidth()/10);
		//pressureChart.getStyler().setAxisTicksMarksVisible(true);
		//pressureChart.getStyler().setAxisTickMarkLength(15); lunghezza trattini
		//pressureChart.getStyler().setXAxisMaxLabelCount(10); non funziona per XYStyler
		
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
				pressureChart.updateXYSeries("Patient Pressure", initdataPressure[0], initdataPressure[pressureIndexElm], null);
				
			}
		});
		sw = new XChartPanel<XYChart>(pressureChart);
		flowPanel.add(sw);

	}

	private void patientPanelConfig(Patient patient, Archetype archetype) {
		patientPanel = new JPanel();
		frame.getContentPane().add(patientPanel);
		patientPanel.setLayout(null);

		int size = patient.getElementsList().size();
		data = new String[size][size];
		int c = 0;

		int y = 27;
		for (Element e : patient.getElementsList()) {
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
				graphicDesignForElement(elmId, UMRES, y, Double.parseDouble(elmValue));
				data[c++][0] = elmId;
				flowIds.add(elmId);
			}

			// capacitor
			if (e.getType().equals("CapacitorElm")) {
				elmValue = archetype.getParameters().get(e.getAssociatedFormula().getVariables().get(0));
				graphicDesignForElement(elmId, UMCAP, y, Double.parseDouble(elmValue));
				data[c++][0] = elmId;
				flowIds.add(elmId);
			}

			if (e.getType().equals("ExternalVoltageElm")) {
				graphicDesignForElement(elmId, UMGEN, y, 0);
			}

			y += 28;
		}

		pressureIds = new ArrayList<>(pressureCoord.keySet());
	}

	public void updateShownDataValues(double time, CirSim myCircSim) {
		// Shift data in vectors
		Utils.shiftData(MAXDATA, initdataPressure, initdataVentilatorPressure, initdataFlow);

		// Update time
		initdataPressure[0][MAXDATA - 1] = time;
		initdataVentilatorPressure[0][MAXDATA - 1] = time;
		initdataFlow[0][MAXDATA - 1] = time;

		int count = 1;
		int countPressure = 1;

		for (CircuitElm cir : myCircSim.getElmList()) {
			if(cir.getIdLeft() != null && pressureCoord.containsKey(cir.getIdLeft())) {
				if(pressureCoord.get(cir.getIdLeft()).equals("left")) {
					initdataPressure[countPressure][MAXDATA - 1] = cir.getVoltZero();
					countPressure++;
				}
			}
			
			if(cir.getIdRight() != null && pressureCoord.containsKey(cir.getIdRight())) {
				if(pressureCoord.get(cir.getIdRight()).equals("right")) {
					initdataPressure[countPressure][MAXDATA - 1] = cir.getVoltOne();
					countPressure++;
				}
			}
			
			if (cir.getClass().getSimpleName().equals("ExternalVoltageElm")) {
				initdataVentilatorPressure[1][MAXDATA - 1] = cir.getVoltageDiff();
			} else {
				initdataFlow[count][MAXDATA - 1] = cir.getCurrent();
				count++;
			}
		}

		int c = 0;

		for (CircuitElm cir : myCircSim.getElmList()) {
			if (cir.getClass().getSimpleName().equals("ResistorElm")) {
				String[] d = { data[c][0], String.valueOf(cir.getVoltZero()), String.valueOf(cir.getVoltOne()),
						String.valueOf(cir.getCurrent()) };
				data[c++] = d;
			}

			if (cir.getClass().getSimpleName().equals("CapacitorElm")) {
				String[] d = { data[c][0], String.valueOf(cir.getVoltZero()), String.valueOf(cir.getVoltOne()),
						String.valueOf(cir.getCurrent()) };
				data[c++] = d;
			}

			if (cir.getClass().getSimpleName().equals("ExternalVoltageElm")) {
				voltage = new JSpinner();
				SpinnerNumberModel resistanceModel = new SpinnerNumberModel(0.0, 0.000, 100.0, 0.001);
				voltage.setModel(resistanceModel);
				voltage.setBounds(140, 28 * myCircSim.getElmList().size() - 1, 50, 20);
				voltage.setValue(cir.getVoltageDiff());
				patientPanel.add(voltage);
			}
		}

		dataTable = new JTable(data, columnNames);
		dataTable.setBounds(33, 400, 400, 200);
		for (int i = 0; i < columnNames.length; i++) {
			dataTable.getColumnModel().getColumn(i).setMinWidth(70);
		}
		dataTable.setVisible(true);
		dataTable.getTableHeader().setBounds(33, 380, 400, 20);
		patientPanel.add(dataTable);
		patientPanel.add(dataTable.getTableHeader());

		pressureChart.updateXYSeries("Patient Pressure", initdataPressure[0], initdataPressure[1], null);
		if (showVentilator) {
			pressureChart.updateXYSeries("Ventilator Pressure", initdataVentilatorPressure[0],
					initdataVentilatorPressure[1], null);
		}

		pressureChart.getStyler().setYAxisMax(getMax(initdataVentilatorPressure[1]));

		flowChart.updateXYSeries(FLOWSTRING, initdataFlow[0], initdataFlow[flowIndexElm], null);
		pressureChart.updateXYSeries("Patient Pressure", initdataPressure[0], initdataPressure[pressureIndexElm], null);

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
	 */
	private void graphicDesignForElement(final String elementDescr, final String elementUnit, final int elementY,
			final double value) {
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

		// Element unit of measurement
		final JLabel unit = new JLabel(elementUnit);
		unit.setBounds(UMELEMENTX, elementY, UMELEMENTWIDTH, UMELEMENTHEIGHT);
		patientPanel.add(unit);

	}

	private static Double getMax(double[] ds) {
		double max = ds[0];

		for (Double d : ds) {
			if (d > max) {
				max = d;
			}
		}

		return max;
	}

}
