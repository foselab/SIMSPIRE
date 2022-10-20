package lungsimulator.graphic;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.Marker;
import org.knowm.xchart.style.markers.SeriesMarkers;

import components.ACVoltageElm;
import components.CapacitorElm;
import components.CircuitElm;
import components.DCVoltageElm;
import components.ResistorElm;
import lungsimulator.components.Archetype;
import lungsimulator.components.Element;
import lungsimulator.components.Patient;
import lungsimulator.components.SimulatorParams;
import lungsimulator.utils.Utils;
import simulator.CirSim;

/**
 * Graphic view for final user
 */
public class GraphicInterface {
	/**
	 * The frame of user interface
	 */
	private transient JFrame frame;
	
	/**
	 * Show if the frame state (open or close)
	 */
	public transient boolean windowIsOpen = true;

	private boolean showVentilator = true;
	private double[][] initdataPressure;
	private double[][] initdataVentilatorPressure;
	private double[][] initdataFlow;

	private List<String> flowIds = new ArrayList<>();
	private List<String> pressureIds = new ArrayList<>();
	private Map<String, String> pressureCoord = new LinkedHashMap<>();
	private Map<String, JSpinner> spinnersTime = new HashMap<>();

	private int flowIndexElm = 1;
	private int pressureIndexElm = 1;

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

	/**
	 * Open window for model selection
	 * @return the selected model
	 */
	public String selectSchema() {
		// options configuration
		final String[] models = { "Model of Albanese", "Model of Al-Naggar", "Model of Baker", "Model of Jain",
				"Model of Campbell-Brown", "Your own model..." };
		final Object choice = JOptionPane.showInputDialog(null, "Select model", "ChooseModel", JOptionPane.PLAIN_MESSAGE,
				null, models, models[0]);
		final String var = String.valueOf(choice);

		if (var.equals("Your own model...")) {
			final ChooseFileWindow chooseFileWindow = new ChooseFileWindow();
			modelChoice = chooseFileWindow.getResult();
		} else {
			modelChoice = var.replace("Model of ", "");
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
	public void frameConfig(final Patient patient, final Archetype archetype, final SimulatorParams demographicData) {
		// frame configuration
		frame = new JFrame();
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // fullscreen option
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent event) {
				windowIsOpen = false;
			}
		});

		//TODO inizializzare circuitcomponents
		//TODO calcolare pressureCoord
		patientPanelConfig(patient, archetype, demographicData);

		// Init the arrays for the data
		initdataPressure = new double[pressureCoord.size() + 1][GraphicConstants.MAXDATA];
		// One row for time values and one row for pressure values
		initdataVentilatorPressure = new double[2][GraphicConstants.MAXDATA];
		// One row for time values and one row for each element of the circuit
		initdataFlow = new double[flowIds.size() + 1][GraphicConstants.MAXDATA];

		// Fill initData with zeros
		Utils.initVectors(GraphicConstants.MAXDATA, initdataPressure, initdataVentilatorPressure, initdataFlow);

		JPanel flowPanel = new JPanel();
		frame.getContentPane().add(flowPanel);
		flowPanel.setLayout(new BoxLayout(flowPanel, BoxLayout.Y_AXIS));
		
		int w = 1500;
		int h = 400;

		// Create the flow chart
		flowChart = new XYChartBuilder().width(w).height(h).title(GraphicConstants.FLOWSERIES + " in " + flowIds.get(0))
				.xAxisTitle("Time [s]").yAxisTitle(GraphicConstants.FLOWSERIES).build();
		flowChart.addSeries(GraphicConstants.FLOWSERIES, initdataFlow[0], initdataFlow[1]);
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
				flowChart.setTitle(GraphicConstants.FLOWSERIES + " in " + choice);
				flowChart.updateXYSeries(GraphicConstants.FLOWSERIES, initdataFlow[0], initdataFlow[flowIndexElm], null);
			}
		});

		sw2 = new XChartPanel<XYChart>(flowChart);
		flowPanel.add(sw2);

		// Create the pressure chart
		pressureChart = new XYChartBuilder().width(w).height(h).title(GraphicConstants.PRESSURE_TITLE + " at " + pressureIds.get(0))
				.xAxisTitle("Time [s]").yAxisTitle("Pressure").build();
		pressureChart.addSeries(GraphicConstants.PRESSURESERIES, initdataPressure[0], initdataPressure[1]);

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
				pressureChart.setTitle(GraphicConstants.PRESSURE_TITLE + " in " + choice);
				pressureChart.updateXYSeries(GraphicConstants.PRESSURESERIES, initdataPressure[0], initdataPressure[pressureIndexElm],
						null);

			}
		});
		sw = new XChartPanel<XYChart>(pressureChart);
		flowPanel.add(sw);

	}
	
	//public initValuesCircuit(myCircSim) --> circuitComponents.initComponent(myCirSim)
	
	private void patientPanelConfig(final Patient patient, final Archetype archetype, final SimulatorParams demographicData) {
		patientPanel = new JPanel();
		frame.getContentPane().add(patientPanel);
		patientPanel.setLayout(null);

		int yInit = 27;
		for (final Element e : patient.getElementsList()) {
			String elmValue;
			String elmId = e.getElementName();
			boolean isTimeDependent = e.getAssociatedFormula().getIsTimeDependent();

			if (e.isShowLeft()) {
				pressureCoord.put(e.getIdLeft(), "left");
			}

			if (e.isShowRight()) {
				pressureCoord.put(e.getIdRight(), "right");
			}

			// resistance
			if (e.getType().equals("ResistorElm")) {
				elmValue = archetype.getParameters().get(e.getAssociatedFormula().getVariables().get(0));
				graphicDesignForElement(elmId, GraphicConstants.UMRES, yInit, Double.parseDouble(elmValue), false, isTimeDependent);
				flowIds.add(elmId);
			}

			// capacitor
			if (e.getType().equals("CapacitorElm")) {
				elmValue = archetype.getParameters().get(e.getAssociatedFormula().getVariables().get(0));
				graphicDesignForElement(elmId, GraphicConstants.UMCAP, yInit, Double.parseDouble(elmValue), false, isTimeDependent);
				flowIds.add(elmId);
			}

			if (e.getType().equals("ExternalVoltageElm")) {
				graphicDesignForElement(elmId, GraphicConstants.UMGEN, yInit, 0, true, isTimeDependent);
			}

			yInit += 28;
		}

		pressureIds = new ArrayList<>(pressureCoord.keySet());

		int yButton = yInit + 28;
		stop = new JButton("Stop");
		stop.setFont(new Font("Arial", Font.BOLD, 16));
		stop.setBounds(GraphicConstants.IDELEMENTX, yButton, 100, 50);
		stop.setBackground(Color.RED);
		stop.setForeground(Color.WHITE);
		stop.setVisible(true);

		start = new JButton("Start");
		start.setFont(new Font("Arial", Font.BOLD, 16));
		start.setBounds(GraphicConstants.IDELEMENTX, yButton, 100, 50);
		start.setBackground(Color.GREEN);
		start.setForeground(Color.WHITE);
		start.setVisible(false);

		printData = new JButton("Print");
		printData.setFont(new Font("Arial", Font.BOLD, 16));
		printData.setBounds(GraphicConstants.VALELEMENTX, yButton, 100, 50);
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

		demographicDataSetUp(demographicData, yButton);
	}

	private void demographicDataSetUp(SimulatorParams demographicData, int yLast) {
		yLast += 102;
		final JLabel elementId = new JLabel("Patient demographic data");
		elementId.setBounds(GraphicConstants.IDELEMENTX, yLast, 200, GraphicConstants.IDELEMENTHEIGHT);
		patientPanel.add(elementId);

		yLast += 28;

		final JLabel gender = new JLabel("Gender ");
		gender.setBounds(GraphicConstants.IDELEMENTX, yLast, 200, GraphicConstants.IDELEMENTHEIGHT);
		patientPanel.add(gender);

		String[] genders = { "Male", "Female" };
		JComboBox<String> gendersBox = new JComboBox<String>(genders);
		gendersBox.setBounds(GraphicConstants.IDELEMENTX + 80, yLast, 100, 20);
		if ("MALE".equalsIgnoreCase(demographicData.getGender())) {
			gendersBox.setSelectedItem("Male");
		} else {
			gendersBox.setSelectedItem("Female");
		}
		patientPanel.add(gendersBox);

		yLast += 28;

		final JLabel age = new JLabel("Age (years)");
		age.setBounds(GraphicConstants.IDELEMENTX, yLast, 200, GraphicConstants.IDELEMENTHEIGHT);
		patientPanel.add(age);

		final SpinnerNumberModel ageModel = new SpinnerNumberModel(demographicData.getAge(), 18, 126, 1);
		JSpinner ageElm = new JSpinner(ageModel);
		ageElm.setBounds(GraphicConstants.IDELEMENTX + 80, yLast, GraphicConstants.VALELEMENTWIDTH, GraphicConstants.VALELEMENTHEIGHT);
		patientPanel.add(ageElm);

		yLast += 28;

		final JLabel height = new JLabel("Height (m)");
		height.setBounds(GraphicConstants.IDELEMENTX, yLast, 200, GraphicConstants.IDELEMENTHEIGHT);
		patientPanel.add(height);

		final SpinnerNumberModel heightModel = new SpinnerNumberModel(demographicData.getHeight(), 0.55, 2.60, 0.01);
		JSpinner heightElm = new JSpinner(heightModel);
		heightElm.setBounds(GraphicConstants.IDELEMENTX + 80, yLast, GraphicConstants.VALELEMENTWIDTH, GraphicConstants.VALELEMENTHEIGHT);
		patientPanel.add(heightElm);

		yLast += 28;

		final JLabel weight = new JLabel("Weigth (kg)");
		weight.setBounds(GraphicConstants.IDELEMENTX, yLast, 200, GraphicConstants.IDELEMENTHEIGHT);
		patientPanel.add(weight);

		final SpinnerNumberModel weightModel = new SpinnerNumberModel(demographicData.getWeight(), 25, 600, 0.1);
		JSpinner weightElm = new JSpinner(weightModel);
		weightElm.setBounds(GraphicConstants.IDELEMENTX + 80, yLast, GraphicConstants.VALELEMENTWIDTH, GraphicConstants.VALELEMENTHEIGHT);
		patientPanel.add(weightElm);

		yLast += 28;

		final JLabel ibw = new JLabel("Ibw (kg)");
		ibw.setBounds(GraphicConstants.IDELEMENTX, yLast, 200, GraphicConstants.IDELEMENTHEIGHT);
		patientPanel.add(ibw);

		final JLabel ibwValue = new JLabel(String.valueOf(demographicData.getIbw()));
		ibwValue.setBounds(GraphicConstants.IDELEMENTX + 80, yLast, GraphicConstants.VALELEMENTWIDTH, GraphicConstants.VALELEMENTHEIGHT);
		patientPanel.add(ibwValue);
	}

	/**
	 * Shown data are shifted and updated
	 * 
	 * @param time      moment in time of the execution
	 * @param myCircSim circuit build according to the model
	 */
	public void updateShownDataValues(final double time, final CirSim myCircSim) {
		// Shift data in vectors
		Utils.shiftData(GraphicConstants.MAXDATA, initdataPressure, initdataVentilatorPressure, initdataFlow);

		// Update time
		initdataPressure[0][GraphicConstants.MAXDATA - 1] = time;
		initdataVentilatorPressure[0][GraphicConstants.MAXDATA - 1] = time;
		initdataFlow[0][GraphicConstants.MAXDATA - 1] = time;

		int count = 1;
		int countPressure = 1;

		for (final CircuitElm cir : myCircSim.getElmList()) {
			if (spinnersTime.containsKey(cir.getId())) {
				updateSpinners(cir);
			}

			if (cir.getIdLeft() != null && pressureCoord.containsKey(cir.getIdLeft())
					&& pressureCoord.get(cir.getIdLeft()).equals("left")) {
				initdataPressure[countPressure][GraphicConstants.MAXDATA - 1] = cir.getVoltZero();
				countPressure++;
			}

			if (cir.getIdRight() != null && pressureCoord.containsKey(cir.getIdRight())
					&& pressureCoord.get(cir.getIdRight()).equals("right")) {
				initdataPressure[countPressure][GraphicConstants.MAXDATA - 1] = cir.getVoltOne();
				countPressure++;
			}

			if (cir.getClass().getSimpleName().equals("ExternalVoltageElm")) {
				initdataVentilatorPressure[1][GraphicConstants.MAXDATA - 1] = cir.getVoltageDiff();
				ventilator.setValue(cir.getVoltageDiff());
			} else {
				initdataFlow[count][GraphicConstants.MAXDATA - 1] = cir.getCurrent();
				count++;
			}
		}

		pressureChart.updateXYSeries(GraphicConstants.PRESSURESERIES, initdataPressure[0], initdataPressure[1], null);
		if (showVentilator) {
			pressureChart.updateXYSeries("Ventilator Pressure", initdataVentilatorPressure[0],
					initdataVentilatorPressure[1], null);
		}

		pressureChart.getStyler().setYAxisMax(getMax(initdataVentilatorPressure[1]));
		flowChart.getStyler().setYAxisMax(getMax(initdataFlow[flowIndexElm]));
		flowChart.getStyler().setYAxisMin(-getMax(initdataFlow[flowIndexElm]));

		flowChart.updateXYSeries(GraphicConstants.FLOWSERIES, initdataFlow[0], initdataFlow[flowIndexElm], null);
		pressureChart.updateXYSeries(GraphicConstants.PRESSURESERIES, initdataPressure[0], initdataPressure[pressureIndexElm], null);

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

	private void updateSpinners(CircuitElm cir) {

		// resistance
		if (cir instanceof ResistorElm) {
			final ResistorElm resistance = (ResistorElm) cir;
			spinnersTime.get(cir.getId()).setValue(resistance.getResistance());
		}

		// capacitor
		if (cir instanceof CapacitorElm) {
			final CapacitorElm capacitance = (CapacitorElm) cir;
			spinnersTime.get(cir.getId()).setValue(capacitance.getCapacitance());
		}

		// acVoltage
		if (cir instanceof ACVoltageElm) {
			final ACVoltageElm acVoltage = (ACVoltageElm) cir;
			spinnersTime.get(cir.getId()).setValue(acVoltage.getMaxVoltage());
		}

		// dcVoltage
		if (cir instanceof DCVoltageElm) {
			final DCVoltageElm dcVoltage = (DCVoltageElm) cir;
			spinnersTime.get(cir.getId()).setValue(dcVoltage.getMaxVoltage());
		}

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
			final double value, final boolean isVentilator, final boolean isTimeDependent) {
		element = new JSpinner();

		// Element description
		final JLabel elementId = new JLabel(elementDescr);
		elementId.setBounds(GraphicConstants.IDELEMENTX, elementY, GraphicConstants.IDELEMENTWIDTH, GraphicConstants.IDELEMENTHEIGHT);
		patientPanel.add(elementId);

		// Element value
		final SpinnerNumberModel elementModel = new SpinnerNumberModel(0.0, 0.000, 100.0, 0.001);
		element.setModel(elementModel);
		element.setBounds(GraphicConstants.VALELEMENTX, elementY, GraphicConstants.VALELEMENTWIDTH, GraphicConstants.VALELEMENTHEIGHT);
		element.setValue(value);
		patientPanel.add(element);

		if (isTimeDependent) {
			spinnersTime.put(elementDescr, element);
		}

		if (isVentilator) {
			ventilator = element;
		}

		// Element unit of measurement
		final JLabel unit = new JLabel(elementUnit);
		unit.setBounds(GraphicConstants.UMELEMENTX, elementY, GraphicConstants.UMELEMENTWIDTH, GraphicConstants.UMELEMENTHEIGHT);
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
	
	public boolean isWindowOpen() {
		return windowIsOpen;
	}

}
