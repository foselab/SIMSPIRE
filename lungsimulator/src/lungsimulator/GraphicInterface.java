package lungsimulator;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.Marker;
import org.knowm.xchart.style.markers.SeriesMarkers;

import components.CircuitElm;
import lungsimulator.components.Archetype;
import lungsimulator.components.Patient;
import lungsimulator.utils.Element;
import lungsimulator.utils.Utils;
import simulator.CirSim;

public class GraphicInterface {

	private static final String PRESSURE_TITLE = "Pressure";
	private static final String FLOW_TITLE = "Flow";
	boolean showVentilator;
	int max_data;
	
	String[] columnNames = { "Element", "V0", "V1", "Current" };
	String[][] data;
	double[][] initdataPressure;
	double[][] initdataVentilatorPressure;
	double[][] initdataFlow;

	private JFrame frame;
	private XYChart pressureChart;
	private XYChart flowChart;
	private XChartPanel<XYChart> sw;
	private XChartPanel<XYChart> sw2;
	private JPanel patientPanel;
	private JSpinner resistance = new JSpinner();
	private JSpinner compliance = new JSpinner();
	private JSpinner voltage = new JSpinner();
	private JTable dataTable = new JTable();

	public GraphicInterface() {
	}

	public void frameConfig(Patient patient, Archetype archetype) {
		frame = new JFrame();
		frame.setBounds(15, 15, 1500, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		int w = 1500;
		int h = 400;
		showVentilator = true;
		max_data = 1000;

		// init the arrays for the data
		initdataPressure = new double[max_data][max_data];
		initdataVentilatorPressure = new double[max_data][max_data];
		initdataFlow = new double[max_data][max_data];

		// Fill initData with zeros
		Utils.initVectors(max_data, initdataPressure, initdataVentilatorPressure, initdataFlow);

		// Create the pressure Chart
		pressureChart = new XYChartBuilder().width(w).height(h).title(PRESSURE_TITLE).xAxisTitle("Time [s]")
				.yAxisTitle("Pressure").build();
		pressureChart.addSeries("Patient Pressure", initdataPressure[0], initdataPressure[1]);

		if (showVentilator)
			pressureChart.addSeries("Ventilator Pressure", initdataVentilatorPressure[0],
					initdataVentilatorPressure[1]);

		pressureChart.getStyler().setYAxisMax(25.5).setYAxisMin(0.0)
				.setSeriesMarkers(new Marker[] { SeriesMarkers.NONE, SeriesMarkers.NONE })
				.setLegendPosition(LegendPosition.InsideS);

		// Create the flow chart
		flowChart = new XYChartBuilder().width(w).height(h).title(FLOW_TITLE).xAxisTitle("Time [s]").yAxisTitle("Flow")
				.build();
		flowChart.addSeries("Flow", initdataFlow[0], initdataFlow[1]);
		flowChart.getStyler().setYAxisMax(1.0).setYAxisMin(-1.0).setSeriesMarkers(new Marker[] { SeriesMarkers.NONE })
				.setLegendPosition(LegendPosition.InsideS);

		frame.getContentPane().setLayout(new GridLayout(1, 2, 0, 0));

		JPanel flowPanel = new JPanel();
		frame.getContentPane().add(flowPanel);
		flowPanel.setLayout(new BoxLayout(flowPanel, BoxLayout.Y_AXIS));
		sw2 = new XChartPanel<XYChart>(flowChart);
		flowPanel.add(sw2);

		sw = new XChartPanel<XYChart>(pressureChart);
		flowPanel.add(sw);

		patientPanel = new JPanel();
		frame.getContentPane().add(patientPanel);
		patientPanel.setLayout(null);
		
		int size = patient.getElementsList().size();
		data = new String[size][size];
		int c=0;

		int y = 27;
		for (Element e : patient.getElementsList()) {
			String elmValue;
			// resistance
			if (e.getType().equals("ResistorElm")) {
				elmValue = archetype.getParameters().get(e.getAssociatedFormula().getVariables().get(0));
				graphicDesignForResistorElm(e.getAssociatedFormula().getId(), y, Double.parseDouble(elmValue));
				data[c++][0] = e.getAssociatedFormula().getId();
			}

			// capacitor
			if (e.getType().equals("CapacitorElm")) {
				elmValue = archetype.getParameters().get(e.getAssociatedFormula().getVariables().get(0));
				graphicDesignForCapacitorElm(e.getAssociatedFormula().getId(), y, Double.parseDouble(elmValue));
				data[c++][0] = e.getAssociatedFormula().getId();
			}

			if (e.getType().equals("ExternalVoltageElm")) {
				voltage = new JSpinner();

				JLabel lblVoltageUnit = new JLabel("cmH2O");
				lblVoltageUnit.setBounds(200, y, 100, 20);
				patientPanel.add(lblVoltageUnit);

				JLabel lblVoltage = new JLabel(e.getAssociatedFormula().getId());
				lblVoltage.setBounds(33, y, 100, 20);
				patientPanel.add(lblVoltage);

				voltage.setBounds(140, y, 50, 20);
				patientPanel.add(voltage);
			}

			y += 28;
		}

		frame.setVisible(true);

	}

	public void updateShownDataValues(double time, CirSim myCircSim) {
		// Shift data in vectors
		Utils.shiftData(max_data, initdataPressure, initdataVentilatorPressure, initdataFlow);

		// Insert new data
		initdataPressure[0][max_data - 1] = time;
		initdataPressure[1][max_data - 1] = myCircSim.getElm(1).getVoltOne();

		for (CircuitElm cir : myCircSim.getElmList()) {
			if (cir.getClass().getSimpleName().equals("ExternalVoltageElm")) {
				initdataVentilatorPressure[0][max_data - 1] = time;
				initdataVentilatorPressure[1][max_data - 1] = cir.getVoltageDiff();
			}
		}

		initdataFlow[0][max_data - 1] = time;
		initdataFlow[1][max_data - 1] = myCircSim.getElm(0).getCurrent();

		int c = 0;

		for (CircuitElm cir : myCircSim.getElmList()) {
			if (cir.getClass().getSimpleName().equals("ResistorElm")) {
				String[] d = {data[c][0], String.valueOf(cir.getVoltZero()), String.valueOf(cir.getVoltOne()),
						String.valueOf(cir.getCurrent()) };
				data[c++] = d;
			}

			if (cir.getClass().getSimpleName().equals("CapacitorElm")) {
				String[] d = {data[c][0], String.valueOf(cir.getVoltZero()), String.valueOf(cir.getVoltOne()),
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
		dataTable.setBounds(33, 250, 400, 100);
		for(int i = 0; i < columnNames.length; i++) {
			dataTable.getColumnModel().getColumn(i).setMinWidth(70);
		}
		dataTable.setVisible(true);
		dataTable.getTableHeader().setBounds(33, 230, 400, 20);
		patientPanel.add(dataTable);
		patientPanel.add(dataTable.getTableHeader());

		pressureChart.updateXYSeries("Patient Pressure", initdataPressure[0], initdataPressure[1], null);
		if (showVentilator) {
			pressureChart.updateXYSeries("Ventilator Pressure", initdataVentilatorPressure[0],
					initdataVentilatorPressure[1], null);
		}

		pressureChart.getStyler().setYAxisMax(getMax(initdataVentilatorPressure[1]));

		flowChart.updateXYSeries("Flow", initdataFlow[0], initdataFlow[1], null);

		// Update the charts
		sw.revalidate();
		sw.repaint();
		sw2.revalidate();
		sw2.repaint();

	}

	/**
	 * Create the graphic design for resistances
	 */
	private void graphicDesignForResistorElm(String id, int y, double resistanceValue) {
		resistance = new JSpinner();

		JLabel lblResistanceUnit = new JLabel("cmH2O/L/s");
		lblResistanceUnit.setBounds(200, y, 100, 20);
		patientPanel.add(lblResistanceUnit);

		JLabel lblResistance = new JLabel(id);
		lblResistance.setBounds(33, y, 100, 20);
		patientPanel.add(lblResistance);

		SpinnerNumberModel resistanceModel = new SpinnerNumberModel(0.0, 0.000, 100.0, 0.001);
		resistance.setModel(resistanceModel);
		resistance.setBounds(140, y, 50, 20);
		resistance.setValue(resistanceValue);
		patientPanel.add(resistance);
	}

	/**
	 * Create the graphic design for capacitors
	 */
	private void graphicDesignForCapacitorElm(String id, int y, double capacitorValue) {
		compliance = new JSpinner();

		JLabel lblComplianceUnit = new JLabel("L/cmH2O");
		lblComplianceUnit.setBounds(200, y, 100, 20);
		patientPanel.add(lblComplianceUnit);

		JLabel lblCompliance = new JLabel(id);
		lblCompliance.setBounds(33, y, 100, 20);
		patientPanel.add(lblCompliance);

		SpinnerNumberModel complianceModel = new SpinnerNumberModel(0.0, 0.000, 100.0, 0.001);
		compliance.setBounds(140, y, 50, 20);
		compliance.setModel(complianceModel);
		compliance.setValue(capacitorValue);
		patientPanel.add(compliance);
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
