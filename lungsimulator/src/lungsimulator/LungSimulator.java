package lungsimulator;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.json.simple.parser.ParseException;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.Marker;
import org.knowm.xchart.style.markers.SeriesMarkers;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

import com.udojava.evalex.Expression;

import components.CapacitorElm;
import components.CircuitElm;
import components.DCVoltageElm;
import components.ResistorElm;
import lungsimulator.components.Archetype;
import lungsimulator.components.Patient;
import lungsimulator.utils.COMPortAdapter;
import lungsimulator.utils.Element;
import lungsimulator.utils.Formula;
import lungsimulator.utils.JsonReader;
import lungsimulator.utils.Randomizer;
import lungsimulator.utils.Utils;
import lungsimulator.utils.VentilatorTypeSetter;
import lungsimulator.utils.YamlReader;
import simulator.CirSim;
import java.awt.GridLayout;

public class LungSimulator {

	/*private static final String PRESSURE_TITLE = "Pressure";
	private static final String FLOW_TITLE = "Flow";*/
	public static double maxFlow = 0.0;
	public static double volume = 0.0;
	public static double lastCycleTime = 0;
	public static double startCycleTime = 0;
	public static double oldPressureValue = 0;
	public static boolean computeRR = true;
	public static double peakOfPressure = 0;
	public static double oldPeakOfPressure = 0;
	public static double targetMinuteVolume = 0;
	public static Logger lungSimulatorLogger;

	/*private JFrame frame;
	boolean showVentilator;*/
	// Define the new plotter
	RealTimePlot rtp = new RealTimePlot();
	COMPortAdapter com; // port for communication, can be null

	/*double[][] initdataPressure;
	double[][] initdataVentilatorPressure;
	double[][] initdataFlow;
	private XYChart pressureChart;
	private XYChart flowChart;
	private XChartPanel<XYChart> sw;
	private XChartPanel<XYChart> sw2;
	private JPanel patientPanel;*/
	/*private JLabel txtRespiratoryRate = new JLabel("----");
	private JLabel txtPmax = new JLabel("----");
	private JLabel txtVTidal = new JLabel("----");
	private JLabel txtPPatient = new JLabel("----");
	private JLabel txtFlow = new JLabel("----");
	private JLabel txtPeep = new JLabel("----");
	private JLabel txtFluxPeak = new JLabel("----");
	private JLabel txtEffort = new JLabel("----");
	private JLabel txtTargetMinuteVolume = new JLabel("----");
	private JSpinner volMin = new JSpinner();
	private JSpinner IBW = new JSpinner();*/
	/*private JSpinner resistance = new JSpinner();
	private JSpinner compliance = new JSpinner();
	private JSpinner voltage = new JSpinner();*/
	
	public Patient patient;
	public Archetype archetype;
	public GraphicInterface gi = new GraphicInterface();
	
	/**
	 * Init the lung simulator by reading and validating the patient model and
	 * archetype and set the frame configuration
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParseException
	 * @throws Exception
	 */
	public LungSimulator() throws FileNotFoundException, IOException, ParseException, Exception {
		// Read patient model and patient archetype
		YamlReader yr = new YamlReader();
		patient = yr.readPatientModel();
		archetype = yr.readArchetypeParameters();

		// Validation
		yr.validator(patient, archetype);

		// Frame configuration
		gi.frameConfig(patient, archetype);
	}


	/*
	 * public LungSimulator() throws FileNotFoundException, IOException,
	 * ParseException, Exception{ initialize();
	 * 
	 * // Set the logger programmatically ConfigurationBuilder<BuiltConfiguration>
	 * builder = ConfigurationBuilderFactory.newConfigurationBuilder();
	 * AppenderComponentBuilder console = builder.newAppender("stdout", "Console");
	 * builder.add(console); RootLoggerComponentBuilder rootLogger =
	 * builder.newRootLogger(Level.INFO);
	 * rootLogger.add(builder.newAppenderRef("stdout")); builder.add(rootLogger);
	 * Configurator.initialize(builder.build()); lungSimulatorLogger =
	 * LogManager.getRootLogger();
	 * lungSimulatorLogger.info("[NEW Cycle]: t;RR;Vt;R;C;Effort;"); }
	 */

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @throws ParseException
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	/*private void initialize() throws FileNotFoundException, IOException, ParseException, Exception {

		frame = new JFrame();
		frame.setBounds(100, 100, 493, 371);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		int w, h;
		showVentilator = true;

		// Read the configuration params
		JsonReader jr = new JsonReader();
		jr.readConfig(rtp);
		Object[] chartConfig = jr.readGraphicsConfig();

		// Set the necessary config values
		rtp.max_data = ((Long) chartConfig[0]).intValue();
		w = ((Long) chartConfig[1]).intValue();
		h = ((Long) chartConfig[2]).intValue();
		showVentilator = (boolean) chartConfig[3];
		// init the arrays for the data
		initdataPressure = new double[rtp.max_data][rtp.max_data];
		initdataVentilatorPressure = new double[rtp.max_data][rtp.max_data];
		initdataFlow = new double[rtp.max_data][rtp.max_data];

		// Fill initData with zeros
		Utils.initVectors(rtp, initdataPressure, initdataVentilatorPressure, initdataFlow);

		// Create the pressure Chart
		pressureChart = new XYChartBuilder().width(w).height(h).title(PRESSURE_TITLE).xAxisTitle("Time [s]")
				.yAxisTitle("Pressure").build();
		pressureChart.addSeries("Patient Pressure", initdataPressure[0], initdataPressure[1]);

		if (showVentilator)
			pressureChart.addSeries("Ventilator Pressure", initdataVentilatorPressure[0],
					initdataVentilatorPressure[1]);

		pressureChart.getStyler().setYAxisMax(20.5).setYAxisMin(0.0)
				.setSeriesMarkers(new Marker[] { SeriesMarkers.NONE, SeriesMarkers.NONE })
				.setLegendPosition(LegendPosition.InsideS);

		// Create the flow chart
		flowChart = new XYChartBuilder().width(w).height(h).title(FLOW_TITLE).xAxisTitle("Time [s]").yAxisTitle("Flow")
				.build();
		flowChart.addSeries("Flow", initdataFlow[0], initdataFlow[1]);
		flowChart.getStyler().setYAxisMax(2.0).setYAxisMin(-2.0).setSeriesMarkers(new Marker[] { SeriesMarkers.NONE })
				.setLegendPosition(LegendPosition.InsideS);
		frame.getContentPane().setLayout(new GridLayout(1, 2, 0, 0));

		JPanel flowPanel = new JPanel();
		frame.getContentPane().add(flowPanel);
		flowPanel.setLayout(new BoxLayout(flowPanel, BoxLayout.Y_AXIS));
		sw2 = new XChartPanel<XYChart>(flowChart);
		flowPanel.add(sw2);

		// Show the chart
		// SwingWrapper<XYChart> sw = new SwingWrapper<>(chart);
		// sw.setTitle("Pressure monitor").displayChart();
		// SwingWrapper<XYChart> sw2 = new SwingWrapper<>(chart2);
		// sw2.setTitle("Flow monitor").displayChart();
		sw = new XChartPanel<XYChart>(pressureChart);
		flowPanel.add(sw);

		patientPanel = new JPanel();
		frame.getContentPane().add(patientPanel);
		patientPanel.setLayout(null);

		JLabel lblResistanceUnit = new JLabel("cmH2O/L/s");
		lblResistanceUnit.setBounds(200, 27, 100, 20);
		patientPanel.add(lblResistanceUnit);

		JLabel lblResistance = new JLabel("Resistance");
		lblResistance.setBounds(33, 27, 100, 20);
		patientPanel.add(lblResistance);

		SpinnerNumberModel resistanceModel = new SpinnerNumberModel(0.0, 0.000, 100.0, 0.001);
		resistance.setModel(resistanceModel);
		resistance.setBounds(140, 27, 50, 20);
		resistance.setValue(rtp.rPatValue);
		resistance.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				rtp.getRPat().setResistance(Double.parseDouble(resistance.getValue().toString()));
			}
		});
		patientPanel.add(resistance);

		JLabel lblComplianceUnit = new JLabel("L/cmH2O");
		lblComplianceUnit.setBounds(200, 55, 100, 20);
		patientPanel.add(lblComplianceUnit);

		JLabel lblCompliance = new JLabel("Compliance");
		lblCompliance.setBounds(33, 55, 100, 20);
		patientPanel.add(lblCompliance);

		SpinnerNumberModel complianceModel = new SpinnerNumberModel(0.0, 0.000, 100.0, 0.001);
		compliance.setBounds(140, 55, 50, 20);
		compliance.setModel(complianceModel);
		compliance.setValue(rtp.cPatValue);
		compliance.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				rtp.getcPat().setCapacitance(Double.parseDouble(compliance.getValue().toString()));
			}
		});
		patientPanel.add(compliance);

		JLabel lblRespiratoryRate = new JLabel("RR");
		lblRespiratoryRate.setBounds(33, 82, 100, 20);
		patientPanel.add(lblRespiratoryRate);

		txtRespiratoryRate.setBounds(140, 82, 50, 20);
		patientPanel.add(txtRespiratoryRate);

		JLabel lblRRUnit = new JLabel("bpm");
		lblRRUnit.setBounds(200, 82, 100, 20);
		patientPanel.add(lblRRUnit);

		JLabel lblPmax = new JLabel("Pmax");
		lblPmax.setBounds(33, 109, 100, 20);
		patientPanel.add(lblPmax);

		txtPmax.setBounds(140, 109, 50, 20);
		patientPanel.add(txtPmax);

		JLabel lblPmaxUnit = new JLabel("cmH2O");
		lblPmaxUnit.setBounds(200, 109, 100, 20);
		patientPanel.add(lblPmaxUnit);

		JLabel lblVTidal = new JLabel("VTidal");
		lblVTidal.setBounds(33, 136, 100, 20);
		patientPanel.add(lblVTidal);

		txtVTidal.setBounds(140, 136, 50, 20);
		patientPanel.add(txtVTidal);

		JLabel lblVTidalUnit = new JLabel("L");
		lblVTidalUnit.setBounds(200, 136, 100, 20);
		patientPanel.add(lblVTidalUnit);

		JLabel lblPPatient = new JLabel("PPatient");
		lblPPatient.setBounds(33, 163, 100, 20);
		patientPanel.add(lblPPatient);

		txtPPatient.setBounds(140, 163, 50, 20);
		patientPanel.add(txtPPatient);

		JLabel lblPPatientUnit = new JLabel("cmH2O");
		lblPPatientUnit.setBounds(200, 163, 100, 20);
		patientPanel.add(lblPPatientUnit);

		JLabel lblFlow = new JLabel("Flow");
		lblFlow.setBounds(33, 190, 100, 20);
		patientPanel.add(lblFlow);

		txtFlow.setBounds(140, 190, 50, 20);
		patientPanel.add(txtFlow);

		JLabel lblFlowUnit = new JLabel("L/s");
		lblFlowUnit.setBounds(200, 190, 100, 20);
		patientPanel.add(lblFlowUnit);

		JLabel lblPeep = new JLabel("PEEP");
		lblPeep.setBounds(33, 217, 100, 20);
		patientPanel.add(lblPeep);

		txtPeep.setBounds(140, 217, 50, 20);
		patientPanel.add(txtPeep);

		JLabel lblPeepUnit = new JLabel("cmH2O");
		lblPeepUnit.setBounds(200, 217, 100, 20);
		patientPanel.add(lblPeepUnit);

		JLabel lblFluxPeak = new JLabel("Flow Max");
		lblFluxPeak.setBounds(33, 244, 100, 20);
		patientPanel.add(lblFluxPeak);

		txtFluxPeak.setBounds(140, 244, 50, 20);
		patientPanel.add(txtFluxPeak);

		JLabel lblFluxPeakUnit = new JLabel("L/s");
		lblFluxPeakUnit.setBounds(200, 244, 100, 20);
		patientPanel.add(lblFluxPeakUnit);

		JLabel lblEffort = new JLabel("Effort");
		lblEffort.setBounds(33, 271, 100, 20);
		patientPanel.add(lblEffort);

		txtEffort.setBounds(140, 271, 50, 20);
		patientPanel.add(txtEffort);

		JLabel lblEffortUnit = new JLabel("J/L");
		lblEffortUnit.setBounds(200, 271, 100, 20);
		patientPanel.add(lblEffortUnit);

		JLabel lblIBWUnit = new JLabel("kg");
		lblIBWUnit.setBounds(200, 298, 100, 20);
		patientPanel.add(lblIBWUnit);

		JLabel lblIBW = new JLabel("IBW");
		lblIBW.setBounds(33, 298, 100, 20);
		patientPanel.add(lblIBW);

		SpinnerNumberModel ibwModel = new SpinnerNumberModel(70.0, 0.0, 100.0, 0.5);
		IBW.setModel(ibwModel);
		IBW.setBounds(140, 298, 50, 20);
		IBW.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if (IBW.getValue() != null && volMin.getValue() != null)
					LungSimulator.targetMinuteVolume = Double.parseDouble(IBW.getValue().toString()) * 100.0
							* Double.parseDouble(volMin.getValue().toString()) / 100.0;
				txtTargetMinuteVolume.setText(
						String.valueOf(Math.round((double) (LungSimulator.targetMinuteVolume) * 100.0) / 100.0));
			}
		});
		patientPanel.add(IBW);

		JLabel lblVolMinUnit = new JLabel("%");
		lblVolMinUnit.setBounds(200, 325, 100, 20);
		patientPanel.add(lblVolMinUnit);

		JLabel lblVolMin = new JLabel("% Vol.Min.");
		lblVolMin.setBounds(33, 325, 100, 20);
		patientPanel.add(lblVolMin);

		SpinnerNumberModel volMinModel = new SpinnerNumberModel(0.0, 0.0, 100.0, 0.5);
		volMin.setModel(volMinModel);
		volMin.setBounds(140, 325, 50, 20);
		volMin.setValue(100);
		patientPanel.add(volMin);

		JLabel lblTargetMinuteVolume = new JLabel("Target Min. Vol");
		lblTargetMinuteVolume.setBounds(33, 352, 100, 20);
		patientPanel.add(lblTargetMinuteVolume);

		txtTargetMinuteVolume.setBounds(140, 352, 50, 20);
		patientPanel.add(txtTargetMinuteVolume);

		JLabel lblTargetMinuteVolumeUnit = new JLabel("mL");
		lblTargetMinuteVolumeUnit.setBounds(200, 352, 100, 20);
		patientPanel.add(lblTargetMinuteVolumeUnit);

		// Use com?
		com = null;
		if (rtp.getSimulatorParams().isUseCom()) {
			com = new COMPortAdapter(rtp.getSimulatorParams().getComName());
			openCommandsPrompt(com, rtp);
		}
	}

	@SuppressWarnings("deprecation")
	private static void openCommandsPrompt(COMPortAdapter com, RealTimePlot rtp) {
		JFrame commandsFrame = new JFrame("Commands");
		commandsFrame.getContentPane().setLayout(new FlowLayout());
		commandsFrame.setSize(200, 100);
		JButton btnPAWGTMaxPInsp = new JButton("PAW GT Max PInsp");
		btnPAWGTMaxPInsp.setSize(200, 50);
		btnPAWGTMaxPInsp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				com.sendData("1");
			}
		});
		commandsFrame.getContentPane().add(btnPAWGTMaxPInsp);

		JButton btnEnableDisableDrop = new JButton("Enable/Disable drop");
		btnEnableDisableDrop.setSize(200, 50);
		btnEnableDisableDrop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rtp.setPressureDropEnabled(!rtp.isPressureDropEnabled());
			}
		});
		commandsFrame.getContentPane().add(btnEnableDisableDrop);

		commandsFrame.show();
	}*/

	

	/*
	 * private void simulateCircuit(boolean showVentilator, int step, RealTimePlot
	 * rtp, double[][] initdataPressure, double[][] initdataVentilatorPressure,
	 * double[][] initdataFlow, XYChart chart, XYChart chart2, XChartPanel<XYChart>
	 * sw, XChartPanel<XYChart> sw2, COMPortAdapter com)
	 */
	/*
	 * @SuppressWarnings({ "deprecation", "resource" }) private void
	 * simulateCircuit() throws InterruptedException { double time = 0; double
	 * oldChangeTime = 0; double timeStart = rtp.getElapsedSeconds(); boolean
	 * expirationStarted = false; double expirationTime = 0;
	 * 
	 * boolean missedBreath = false; boolean rChanged = false; boolean cChanged =
	 * false; boolean rDropChanged = false;
	 * 
	 * double effort = 0; double oldT = 0;
	 * 
	 * // Compute the minumum effort target volume if (IBW.getValue() != null &&
	 * volMin.getValue() != null) LungSimulator.targetMinuteVolume =
	 * Double.parseDouble(IBW.getValue().toString()) * 100.0 *
	 * Double.parseDouble(volMin.getValue().toString()) / 100.0;
	 * 
	 * // Create the circuit equivalent to the lung CirSim cirSim =
	 * rtp.buildSimulator(rtp.rPatValue, rtp.cPatValue,
	 * rtp.getVentilator().getBatteryValue(), rtp.rDropValue);
	 * cirSim.setTimeStep(rtp.getSimulatorParams().getStep());
	 * 
	 * cirSim.analyzeCircuit();
	 * 
	 * ZMQ.Socket socket = null; ZContext context;
	 * 
	 * if (rtp.getSimulatorParams().isUseZMQ()) { context = new ZContext(); //
	 * Socket to talk to clients socket = context.createSocket(ZMQ.REP);
	 * socket.bind("tcp://localhost:5555"); } int count = 0; while (true) { time =
	 * rtp.getElapsedSeconds(); boolean sendFlux = false, sendPressure = false,
	 * sendFluxPeak = false, sendRespiratoryRate = false, sendPeakOfPressure =
	 * false; String response = "";
	 * 
	 * // Save the old generator value oldPressureValue =
	 * rtp.getBattery().getMaxVoltage();
	 * 
	 * if (!rtp.getSimulatorParams().isUseCom() &&
	 * !rtp.getSimulatorParams().isUseZMQ()) { // Set ventilator generator
	 * List<Object> values = VentilatorTypeSetter.setRampVentilator(rtp, time,
	 * timeStart, expirationStarted, expirationTime); timeStart = (double)
	 * values.get(0); expirationStarted = (boolean) values.get(1); expirationTime =
	 * (double) values.get(2); } else { if (rtp.getSimulatorParams().isUseCom()) {
	 * // Read ventilator generator values from serial line List<Object> values =
	 * VentilatorTypeSetter.setComVentilator(rtp, com, expirationStarted,
	 * expirationTime); expirationStarted = (boolean) values.get(0); expirationTime
	 * = (double) values.get(1); } else { // Read ventilator generator values from
	 * ZMQ
	 * 
	 * double old = 0; if(count==20) { old = rtp.getBattery().getMaxVoltage(); old
	 * += Math.random(); rtp.getBattery().setMaxVoltage(old); count = 0; }else {
	 * count++; }
	 * 
	 * byte[] reply = socket.recv(1); if (reply == null && count == 20) { // Print
	 * the message //String msg = new String(reply, ZMQ.CHARSET); String msg =
	 * "setPressure " + old; System.out.println(msg);
	 * lungSimulatorLogger.debug("[ZMQ Received]: " + msg);
	 * 
	 * switch (msg.split(" ")[0]) { case "getVolume": // Compute the volume in order
	 * to send it to the ventilator State Machine response = String.valueOf(volume);
	 * 
	 * break; case "setPressure": double n = Double.parseDouble(msg.split(" ")[1]);
	 * 
	 * if (n == 0) { // PEEP if (!expirationStarted) expirationTime =
	 * rtp.getElapsedSeconds();
	 * 
	 * expirationStarted = true;
	 * rtp.getBattery().setMaxVoltage(rtp.getVentilator().getPeepValue()); } else {
	 * // Normal inspiration expirationStarted = false; if
	 * (rtp.getSwDrop().getPosition() == 0) rtp.getSwDrop().toggle();
	 * rtp.getBattery().setMaxVoltage(n); }
	 * 
	 * response = "ACK"; break; case "getFlux": sendFlux = true; break; case
	 * "getFluxPeak": sendFluxPeak = true; break; case "getPressure": sendPressure =
	 * true; break; case "getRespiratoryRate": sendRespiratoryRate = true; break;
	 * case "getFlatTopPressure": sendPeakOfPressure = true; case "getPeep":
	 * response = String.valueOf(rtp.getVentilator().getPeepValue()); default:
	 * break; }
	 * 
	 * } } } cirSim.analyzeCircuit();
	 * 
	 * // Drop if needed -> close the circuit if (rtp.isPressureDropEnabled()) {
	 * manageDrop(rtp, com, time, expirationStarted, expirationTime); }
	 * 
	 * // Manage uncertainty on cough if (rtp.coughEnabled) {
	 * manageUncertainCough(rtp, com); }
	 * 
	 * // Values can change every two minutes if (time-oldChangeTime > 60) { //
	 * Manage uncertainty on resistance if (rtp.resistanceChangeEnabled) { rChanged
	 * = manageRChanged(rtp, expirationStarted, rChanged); if (rChanged) {
	 * this.resistance.setValue(rtp.getRPat().getResistance()); } }
	 * 
	 * // Manage uncertainty on compliance if (rtp.complianceChangeEnabled) {
	 * cChanged = manageCChanged(rtp, expirationStarted, cChanged); if (cChanged) {
	 * this.compliance.setValue(rtp.getcPat().getCapacitance()); } }
	 * 
	 * // Manage uncertainty on pressure trigger if
	 * (rtp.dropResistanceChangeEnabled) { rDropChanged = manageRDropChanged(rtp,
	 * expirationStarted, rDropChanged); }
	 * 
	 * // Manage uncertainty on patient breathing if
	 * (rtp.pressureDropEnabledWithProbability) { missedBreath =
	 * manageMissedDrop(rtp, expirationStarted, missedBreath); }
	 * 
	 * if (rChanged || cChanged || rDropChanged || missedBreath) oldChangeTime =
	 * time; }
	 * 
	 * cirSim.loopAndContinue(false);
	 * 
	 * // After having analyzed the circuit, then check if a new cycle has started
	 * if (oldPressureValue == rtp.getVentilator().getPeepValue() &&
	 * rtp.getBattery().getMaxVoltage() > oldPressureValue && computeRR) {
	 * lastCycleTime = rtp.getElapsedSeconds() - startCycleTime; startCycleTime =
	 * rtp.getElapsedSeconds(); oldPeakOfPressure = peakOfPressure; peakOfPressure =
	 * 0; LungSimulator.maxFlow = 0; computeRR = false;
	 * 
	 * // Log values lungSimulatorLogger.info("[NEW Cycle]: " + (oldT + 1/(60.0 /
	 * lastCycleTime)) + ";" + (60.0 / lastCycleTime) + ";" + volume + ";" +
	 * rtp.getRPat().getResistance() + ";" + rtp.getcPat().getCapacitance() + ";" +
	 * effort + ";");
	 * 
	 * oldT += 1/(60.0 / lastCycleTime); } else { if (!computeRR &&
	 * rtp.getBattery().getMaxVoltage() < oldPressureValue) { computeRR = true; } }
	 * 
	 * Thread.sleep(rtp.getSimulatorParams().getSleepTime());
	 * 
	 * // Shift data in vectors Utils.shiftData(rtp, initdataPressure,
	 * initdataVentilatorPressure, initdataFlow);
	 * 
	 * // Insert new data initdataPressure[0][rtp.max_data - 1] = time;
	 * initdataPressure[1][rtp.max_data - 1] = rtp.getcPat().getVoltageDiff();
	 * initdataVentilatorPressure[0][rtp.max_data - 1] = time;
	 * initdataVentilatorPressure[1][rtp.max_data - 1] =
	 * rtp.getBattery().getVoltageDiff(); initdataFlow[0][rtp.max_data - 1] = time;
	 * initdataFlow[1][rtp.max_data - 1] = rtp.getcPat().getCurrent();
	 * 
	 * // Update the peak of pressure if (rtp.getcPat().getVoltageDiff() >
	 * peakOfPressure) peakOfPressure = rtp.getcPat().getVoltageDiff();
	 * 
	 * // Update the max flow Utils.updateMaxFlow(rtp, initdataFlow);
	 * 
	 * // Send response with ZMQ if needed if (rtp.getSimulatorParams().isUseZMQ())
	 * { if (sendFlux) { response = String.valueOf(rtp.getcPat().getCurrent()); } if
	 * (sendPressure) { response = String.valueOf(rtp.getcPat().getVoltageDiff()); }
	 * if (sendFluxPeak) { response = String.valueOf(LungSimulator.maxFlow); } if
	 * (sendRespiratoryRate) { if (lastCycleTime > 0) response = String.valueOf(60 /
	 * lastCycleTime); else response = "0"; } if (sendPeakOfPressure) { response =
	 * String.valueOf(oldPeakOfPressure); } if (!response.equals("")) {
	 * lungSimulatorLogger.debug("[ZMQ Sending]: " + response);
	 * socket.send(response.getBytes(ZMQ.CHARSET), 0); } }
	 * 
	 * // Signal the flowDropPSV if needed if (initdataFlow[1][rtp.max_data - 1] > 0
	 * && initdataFlow[1][rtp.max_data - 1] < maxFlow * 0.05 && !expirationStarted)
	 * { if (rtp.getSimulatorParams().isUseCom()) { com.sendData("2"); } }
	 * 
	 * // Compute the volume during expiration, otherwise keep it zero if
	 * (expirationStarted) { // The volume is computed throug integration of the
	 * flow volume += Math.abs((initdataFlow[1][rtp.max_data - 1] +
	 * initdataFlow[1][rtp.max_data - 2]) (initdataFlow[0][rtp.max_data - 1] -
	 * initdataFlow[0][rtp.max_data - 2])) / 2; } else { volume = 0.0; }
	 * 
	 * // Update data pressureChart.updateXYSeries("Patient Pressure",
	 * initdataPressure[0], initdataPressure[1], null); if (showVentilator) {
	 * pressureChart.updateXYSeries("Ventilator Pressure",
	 * initdataVentilatorPressure[0], initdataVentilatorPressure[1], null); }
	 * 
	 * pressureChart.getStyler().setYAxisMax(getMax(initdataVentilatorPressure[1]));
	 * 
	 * flowChart.updateXYSeries("Flow", initdataFlow[0], initdataFlow[1], null);
	 * 
	 * // Update text data
	 * this.txtRespiratoryRate.setText(String.valueOf(Math.round((double)(60 /
	 * lastCycleTime)*100.0) / 100.0));
	 * this.txtPmax.setText(String.valueOf(Math.round((double)(oldPeakOfPressure)*
	 * 100.0) / 100.0));
	 * this.txtVTidal.setText(String.valueOf(Math.round((double)(volume)*100.0) /
	 * 100.0));
	 * this.txtPPatient.setText(String.valueOf(Math.round((double)(rtp.getcPat().
	 * getVoltageDiff())*100.0) / 100.0));
	 * this.txtFlow.setText(String.valueOf(Math.round((double)(rtp.getcPat().
	 * getCurrent())*100.0) / 100.0));
	 * this.txtPeep.setText(String.valueOf(Math.round((double)(rtp.getVentilator().
	 * getPeepValue())*100.0) / 100.0));
	 * this.txtFluxPeak.setText(String.valueOf(Math.round((double)(LungSimulator.
	 * maxFlow)*100.0) / 100.0)); // We compute the effort as the average value
	 * between the distances between the horizontal and vertical corresponding
	 * points effort = ((Math.abs((60.0 / lastCycleTime) -
	 * (LungSimulator.targetMinuteVolume / (volume * 1000))) + Math.abs((volume *
	 * 1000) - (LungSimulator.targetMinuteVolume / (60.0 / lastCycleTime)))) / 2.0);
	 * this.txtEffort.setText(String.valueOf(Math.round((double)(effort*100.0) /
	 * 100.0)));
	 * this.txtTargetMinuteVolume.setText(String.valueOf(Math.round((double)(
	 * LungSimulator.targetMinuteVolume)*100.0) / 100.0));
	 * 
	 * // Update the charts sw.revalidate(); sw.repaint(); sw2.revalidate();
	 * sw2.repaint(); } }
	 */
/*
	private static boolean manageMissedDrop(RealTimePlot rtp, boolean expirationStarted, boolean missedBreath) {
		if (expirationStarted) {
			double num = Randomizer.generate(0, 1000) / 1000.0;
			if (num <= rtp.pressureDropMissProbability && !missedBreath) {
				rtp.setPressureDropEnabled(false);
			}
			missedBreath = true;
		} else {
			missedBreath = false;
			rtp.setPressureDropEnabled(true);
		}
		return missedBreath;
	}

	private static void manageUncertainCough(RealTimePlot rtp, COMPortAdapter com) {
		double num = Randomizer.generate(0, 1000) / 1000.0;
		if (num <= rtp.coughProbability) {
			com.sendData("1");
		}
	}

	private static void manageDrop(RealTimePlot rtp, COMPortAdapter com, double time, boolean expirationStarted,
			double expirationTime) {
		if (time - expirationTime > rtp.getPressureDropAfterSec() && expirationStarted
				&& rtp.getSwDrop().getPosition() == 1) {
			rtp.getSwDrop().toggle();
		}
		if (rtp.getSwDrop().getPosition() == 0 && time - expirationTime > rtp.getPressureDropAfterSec() + 0.1)
			if (rtp.getSimulatorParams().isUseCom())
				com.sendData("0");
	}

	private static boolean manageRChanged(RealTimePlot rtp, boolean expirationStarted, boolean rChanged) {
		if (expirationStarted) {
			double num = Randomizer.generate(0, 1000) / 1000.0;
			if (num <= rtp.probabilityChangeResistance && !rChanged) {
				// Change the value of the resistance
				double valueToChange = Randomizer.generate(0, rtp.maxVariabilityResistance);
				// Add or subtract?
				if (Randomizer.generate(0, 1) == 0 || rtp.getRPat().getResistance() - valueToChange < 0) {
					// add
					rtp.getRPat()
							.setResistance(((rtp.getRPat().getResistance() + valueToChange <= 30)
									? rtp.getRPat().getResistance() + valueToChange
									: rtp.getRPat().getResistance()));
				} else {
					// subtract
					rtp.getRPat()
							.setResistance(((rtp.getRPat().getResistance() - valueToChange >= 10)
									? rtp.getRPat().getResistance() - valueToChange
									: rtp.getRPat().getResistance()));
				}
				lungSimulatorLogger.debug("[Debug]: Resistance changed");
				rChanged = true;
			}
		} else {
			rChanged = false;
		}
		return rChanged;
	}

	private static boolean manageCChanged(RealTimePlot rtp, boolean expirationStarted, boolean cChanged) {
		if (expirationStarted) {
			double num = Randomizer.generate(0, 1000) / 1000.0;
			if (num <= rtp.probabilityChangeCompliance && !cChanged) {
				// Change the value of the resistance
				double valueToChange = Randomizer.generate(0, rtp.maxVariabilityCompliance);
				// Add or subtract?
				if (Randomizer.generate(0, 1) == 0 || rtp.getcPat().getCapacitance() - valueToChange < 0) {
					// add
					rtp.getcPat()
							.setCapacitance((rtp.getcPat().getCapacitance() + valueToChange <= 0.08
									? rtp.getcPat().getCapacitance() + valueToChange
									: rtp.getcPat().getCapacitance()));
				} else {
					// subtract
					rtp.getcPat()
							.setCapacitance((rtp.getcPat().getCapacitance() - valueToChange >= 0.04
									? rtp.getcPat().getCapacitance() - valueToChange
									: rtp.getcPat().getCapacitance()));
				}
				lungSimulatorLogger.debug("[Debug]: Compliance changed");
				cChanged = true;
			}
		} else {
			cChanged = false;
		}
		return cChanged;
	}

	private static boolean manageRDropChanged(RealTimePlot rtp, boolean expirationStarted, boolean rDropChanged) {
		if (expirationStarted) {
			double num = Randomizer.generate(0, 1000) / 1000.0;
			if (num <= rtp.dropResistanceChangeProbability && !rDropChanged) {
				// Change the value of the resistance
				double valueToChange = Randomizer.generate(0, rtp.maxDropResistanceChange);
				// Add or subtract?
				if (Randomizer.generate(0, 1) == 0 || rtp.getrDrop().getResistance() - valueToChange < 0) {
					// add
					rtp.getrDrop().setResistance(rtp.getrDrop().getResistance() + valueToChange);
				} else {
					// subtract
					rtp.getrDrop().setResistance(rtp.getrDrop().getResistance() - valueToChange);
				}
			}
			rDropChanged = true;
		} else {
			rDropChanged = false;
		}
		return rDropChanged;
	}*/
	
	public void simulateCircuit() throws InterruptedException {
		// Create the circuit equivalent to the lung
		CirSim myCircSim = rtp.buildCircuitSimulator(patient, archetype);
		myCircSim.setTimeStep(0.01);
		List<Double> resValues = new ArrayList<>();

		double time = 0;

		while (true) {
			time = rtp.getElapsedSeconds();
			
			if(time%2==0) {
			archetype.getParameters().put("TIME", String.valueOf(time));
			myCircSim = rtp.updateCircuitSimulator(patient, archetype);
			}
			
			// Analyze the circuit and simulate a step
			myCircSim.analyzeCircuit();
			myCircSim.loopAndContinue(true);

			/* After having analyzed the circuit, then check if a new cycle has started
			lastCycleTime = rtp.getElapsedSeconds() - startCycleTime;
			startCycleTime = rtp.getElapsedSeconds();*/

			//Thread.sleep(10);
			
			gi.updateShownDataValues(time, myCircSim);

		}

	}

	/**
	 * Launch the application.
	 * 
	 * @throws ParseException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 */
	public static void main(String[] args)
			throws FileNotFoundException, IOException, ParseException, InterruptedException, Exception {

		LungSimulator mySimulator = new LungSimulator();
		mySimulator.simulateCircuit();
	}
}
