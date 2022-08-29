package simulator;
// CirSim.java (c) 2010 by Paul Falstad

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterInputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.lang3.builder.ToStringBuilder;

import circuits.Circuit;
import circuits.CircuitCanvas;
import circuits.CircuitLayout;
import components.CapacitorElm;
import components.CircuitElm;
import components.CircuitNode;
import components.CircuitNodeLink;
import components.ComponentDrawer;
import components.CurrentElm;
import components.GraphicElm;
import components.GroundElm;
import components.InductorElm;
import components.RailElm;
import components.ResistorElm;
import components.SwitchElm;
import components.VoltageElm;
import components.WireElm;
import scope.Scope;
import utils.EditDialog;
import utils.EditOptions;
import utils.Editable;
import utils.ImportExportDialog;
import utils.ImportExportDialogFactory;
import utils.RowInfo;

public class CirSim extends Frame implements ComponentListener, ActionListener, AdjustmentListener, MouseMotionListener,
		MouseListener, ItemListener, KeyListener {

	Thread engine = null;

	private Dimension winSize;
	Image dbimage;

	Random random;
	public static final int sourceRadius = 7;
	public static final double freqMult = 3.14159265 * 2 * 4;

	public String getAppletInfo() {
		return "Circuit by Paul Falstad";
	}

	private static Container main;
	Label titleLabel;
	Button resetButton;
	Button dumpMatrixButton;
	MenuItem exportItem, exportLinkItem, importItem, exitItem, undoItem, redoItem, cutItem, copyItem, pasteItem,
			selectAllItem, optionsItem;
	Menu optionsMenu;
	private Checkbox stoppedCheck;
	private CheckboxMenuItem dotsCheckItem;
	private CheckboxMenuItem voltsCheckItem;
	private CheckboxMenuItem powerCheckItem;
	private CheckboxMenuItem smallGridCheckItem;
	private CheckboxMenuItem showValuesCheckItem;
	CheckboxMenuItem conductanceCheckItem;
	private CheckboxMenuItem euroResistorCheckItem;
	private CheckboxMenuItem printableCheckItem;
	CheckboxMenuItem conventionCheckItem;
	CheckboxMenuItem idealWireCheckItem;
	Scrollbar speedBar;
	Scrollbar currentBar;
	Label powerLabel;
	Scrollbar powerBar;
	PopupMenu elmMenu;
	MenuItem elmEditMenuItem;
	MenuItem elmCutMenuItem;
	MenuItem elmCopyMenuItem;
	MenuItem elmDeleteMenuItem;
	MenuItem elmScopeMenuItem;
	private PopupMenu scopeMenu;
	private PopupMenu transScopeMenu;
	PopupMenu mainMenu;
	private CheckboxMenuItem scopeVMenuItem;
	private CheckboxMenuItem scopeIMenuItem;
	private CheckboxMenuItem scopeMaxMenuItem;
	private CheckboxMenuItem scopeMinMenuItem;
	private CheckboxMenuItem scopeFreqMenuItem;
	private CheckboxMenuItem scopePowerMenuItem;
	private CheckboxMenuItem scopeIbMenuItem;
	private CheckboxMenuItem scopeIcMenuItem;
	private CheckboxMenuItem scopeIeMenuItem;
	private CheckboxMenuItem scopeVbeMenuItem;
	private CheckboxMenuItem scopeVbcMenuItem;
	private CheckboxMenuItem scopeVceMenuItem;
	private CheckboxMenuItem scopeVIMenuItem;
	private CheckboxMenuItem scopeXYMenuItem;
	private CheckboxMenuItem scopeResistMenuItem;
	private CheckboxMenuItem scopeVceIcMenuItem;
	private MenuItem scopeSelectYMenuItem;
	Class addingClass;
	private int mouseMode = MODE_SELECT;
	int tempMouseMode = MODE_SELECT;
	String mouseModeStr = "Select";
	public static final double pi = 3.14159265358979323846;
	public static final int MODE_ADD_ELM = 0;
	public static final int MODE_DRAG_ALL = 1;
	public static final int MODE_DRAG_ROW = 2;
	public static final int MODE_DRAG_COLUMN = 3;
	public static final int MODE_DRAG_SELECTED = 4;
	public static final int MODE_DRAG_POST = 5;
	public static final int MODE_SELECT = 6;
	public static final int infoWidth = 120;
	int dragX, dragY, initDragX, initDragY;
	int selectedSource;
	Rectangle selectedArea;
	private int gridSize;

	int gridMask;

	int gridRound;
	boolean dragging;
	private boolean analyzeFlag;
	boolean dumpMatrix;
	private boolean useBufferedImage;
	boolean isMac;
	String ctrlMetaKey;
	private double t;
	int pause = 10;
	private int scopeSelected = -1;
	int menuScope = -1;
	int hintType = -1, hintItem1, hintItem2;
	String stopMessage;
	private double timeStep;
	static final int HINT_LC = 1;
	static final int HINT_RC = 2;
	static final int HINT_3DB_C = 3;
	static final int HINT_TWINT = 4;
	static final int HINT_3DB_L = 5;
	/**
	 * List of circuit components
	 */
	private List<CircuitElm> elmList;
//    Vector setupList;
	private CircuitElm dragElm;

	CircuitElm menuElm;

	private CircuitElm mouseElm;

	CircuitElm stopElm;
	boolean didSwitch = false;
	int mousePost = -1;
	private CircuitElm plotXElm;

	private CircuitElm plotYElm;
	int draggingPost;
	SwitchElm heldSwitchElm;
	double circuitMatrix[][], circuitRightSide[], origRightSide[], origMatrix[][];
	RowInfo circuitRowInfo[];
	int circuitPermute[];
	boolean circuitNonLinear;
	int voltageSourceCount;
	int circuitMatrixSize, circuitMatrixFullSize;
	boolean circuitNeedsMap;
	public boolean useFrame;
	int scopeCount;
	Scope scopes[];
	int scopeColCount[];
	private static EditDialog editDialog;
	private static ImportExportDialog impDialog;

	static ImportExportDialog expDialog;
	Class dumpTypes[], shortcuts[];
	private static String muString = "u";
	private static String ohmString = "ohm";
	String clipboard;
	Rectangle circuitArea;
	int circuitBottom;
	Vector<String> undoStack, redoStack;

	// TODO
	public int ventilatorIndex;

	public int getrand(int x) {
		int q = random.nextInt();
		if (q < 0)
			q = -q;
		return q % x;
	}

	private CircuitCanvas cv;
	private Circuit applet;

	public CirSim(Circuit a) {
		super("Circuit Simulator v1.6.1a");
		setApplet(a);
		useFrame = false;
	}

	String startCircuit = null;
	String startLabel = null;
	String startCircuitText = null;
	String baseURL = "http://www.falstad.com/circuit/";

	public void init() {
		String euroResistor = null;
		String useFrameStr = null;
		boolean printable = true; // hausen: changed from false to true
		boolean convention = true;

		CircuitElm.initClass(this);

		try {
			baseURL = getApplet().getDocumentBase().getFile();
			// look for circuit embedded in URL
			String doc = getApplet().getDocumentBase().toString();
			int in = doc.indexOf('#');
			if (in > 0) {
				String x = null;
				try {
					x = doc.substring(in + 1);
					x = URLDecoder.decode(x);
					startCircuitText = x;
				} catch (Exception e) {
					System.out.println("can't decode " + x);
					e.printStackTrace();
				}
			}
			in = doc.lastIndexOf('/');
			if (in > 0)
				baseURL = doc.substring(0, in + 1);

			String param = getApplet().getParameter("PAUSE");
			if (param != null)
				pause = Integer.parseInt(param);
			startCircuit = getApplet().getParameter("startCircuit");
			startLabel = getApplet().getParameter("startLabel");
			euroResistor = getApplet().getParameter("euroResistors");
			useFrameStr = getApplet().getParameter("useFrame");
			String x = getApplet().getParameter("whiteBackground");
			if (x != null && x.equalsIgnoreCase("true"))
				printable = true;
			x = getApplet().getParameter("conventionalCurrent");
			if (x != null && x.equalsIgnoreCase("true"))
				convention = false;
		} catch (Exception e) {
		}

		boolean euro = (euroResistor != null && euroResistor.equalsIgnoreCase("true"));
		useFrame = (useFrameStr == null || !useFrameStr.equalsIgnoreCase("false"));
		if (useFrame)
			setMain(this);
		else
			setMain(getApplet());

		String os = System.getProperty("os.name");
		isMac = (os.indexOf("Mac ") == 0);
		ctrlMetaKey = (isMac) ? "\u2318" : "Ctrl";
		String jv = System.getProperty("java.class.version");
		double jvf = new Double(jv).doubleValue();
		if (jvf >= 48) {
			setMuString("\u03bc");
			setOhmString("\u03a9");
			setUseBufferedImage(true);
		}

		dumpTypes = new Class[300];
		shortcuts = new Class[127];

		// these characters are reserved
		dumpTypes['o'] = Scope.class;
		dumpTypes['h'] = Scope.class;
		dumpTypes['$'] = Scope.class;
		dumpTypes['%'] = Scope.class;
		dumpTypes['?'] = Scope.class;
		dumpTypes['B'] = Scope.class;

		getMain().setLayout(new CircuitLayout());
		setCv(new CircuitCanvas(this));
		getCv().addComponentListener(this);
		getCv().addMouseMotionListener(this);
		getCv().addMouseListener(this);
		getCv().addKeyListener(this);
		getMain().add(getCv());

		mainMenu = new PopupMenu();
		MenuBar mb = null;
		if (useFrame)
			mb = new MenuBar();
		Menu m = new Menu("File");
		if (useFrame)
			mb.add(m);
		else
			mainMenu.add(m);
		m.add(importItem = getMenuItem("Import"));
		m.add(exportItem = getMenuItem("Export"));
		// m.add(exportLinkItem = getMenuItem("Export Link"));
		m.addSeparator();
		m.add(exitItem = getMenuItem("Exit"));

		m = new Menu("Edit");
		m.add(undoItem = getMenuItem("Undo"));
		undoItem.setShortcut(new MenuShortcut(KeyEvent.VK_Z));
		m.add(redoItem = getMenuItem("Redo"));
		redoItem.setShortcut(new MenuShortcut(KeyEvent.VK_Z, true));
		m.addSeparator();
		m.add(cutItem = getMenuItem("Cut"));
		cutItem.setShortcut(new MenuShortcut(KeyEvent.VK_X));
		m.add(copyItem = getMenuItem("Copy"));
		copyItem.setShortcut(new MenuShortcut(KeyEvent.VK_C));
		m.add(pasteItem = getMenuItem("Paste"));
		pasteItem.setShortcut(new MenuShortcut(KeyEvent.VK_V));
		pasteItem.setEnabled(false);
		m.add(selectAllItem = getMenuItem("Select All"));
		selectAllItem.setShortcut(new MenuShortcut(KeyEvent.VK_A));
		if (useFrame)
			mb.add(m);
		else
			mainMenu.add(m);

		m = new Menu("Scope");
		if (useFrame)
			mb.add(m);
		else
			mainMenu.add(m);
		m.add(getMenuItem("Stack All", "stackAll"));
		m.add(getMenuItem("Unstack All", "unstackAll"));

		optionsMenu = m = new Menu("Options");
		if (useFrame)
			mb.add(m);
		else
			mainMenu.add(m);
		m.add(setDotsCheckItem(getCheckItem("Show Current")));
		getDotsCheckItem().setState(false); // hausen: changed from true to false
		m.add(setVoltsCheckItem(getCheckItem("Show Voltage")));
		getVoltsCheckItem().setState(true);
		m.add(setPowerCheckItem(getCheckItem("Show Power")));
		m.add(setShowValuesCheckItem(getCheckItem("Show Values")));
		getShowValuesCheckItem().setState(true);
		// m.add(conductanceCheckItem = getCheckItem("Show Conductance"));
		m.add(setSmallGridCheckItem(getCheckItem("Small Grid")));
		m.add(setEuroResistorCheckItem(getCheckItem("European Resistors")));
		getEuroResistorCheckItem().setState(euro);
		m.add(setPrintableCheckItem(getCheckItem("White Background")));
		getPrintableCheckItem().setState(printable);
		m.add(conventionCheckItem = getCheckItem("Conventional Current Motion"));
		conventionCheckItem.setState(convention);
		m.add(idealWireCheckItem = getCheckItem("Ideal Wires"));
		idealWireCheckItem.setState(WireElm.ideal);
		idealWireCheckItem.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					WireElm.ideal = true;
				} else {
					WireElm.ideal = false;
				}
				System.err.println("ideal wires: " + WireElm.ideal);
			}
		});

		m.add(optionsItem = getMenuItem("Other Options..."));

		Menu circuitsMenu = new Menu("Circuits");
		if (useFrame)
			mb.add(circuitsMenu);
		else
			mainMenu.add(circuitsMenu);

		mainMenu.add(getClassCheckItem("Add Wire", "components.WireElm"));
		mainMenu.add(getClassCheckItem("Add Resistor", "components.ResistorElm"));

		Menu passMenu = new Menu("Passive Components");
		mainMenu.add(passMenu);
		passMenu.add(getClassCheckItem("Add Capacitor", "components.CapacitorElm"));
		passMenu.add(getClassCheckItem("Add Inductor", "components.InductorElm"));
		passMenu.add(getClassCheckItem("Add Switch", "components.SwitchElm"));
		passMenu.add(getClassCheckItem("Add Push Switch", "components.PushSwitchElm"));
		passMenu.add(getClassCheckItem("Add SPDT Switch", "components.Switch2Elm"));
		passMenu.add(getClassCheckItem("Add Potentiometer", "components.PotElm"));
		passMenu.add(getClassCheckItem("Add Transformer", "components.TransformerElm"));
		passMenu.add(getClassCheckItem("Add Tapped Transformer", "components.TappedTransformerElm"));
		passMenu.add(getClassCheckItem("Add Transmission Line", "components.TransLineElm"));
		passMenu.add(getClassCheckItem("Add Relay", "components.RelayElm"));
		passMenu.add(getClassCheckItem("Add Memristor", "components.MemristorElm"));
		passMenu.add(getClassCheckItem("Add Spark Gap", "components.SparkGapElm"));

		Menu inputMenu = new Menu("Inputs/Outputs");
		mainMenu.add(inputMenu);
		inputMenu.add(getClassCheckItem("Add Ground", "components.GroundElm"));
		inputMenu.add(getClassCheckItem("Add Voltage Source (2-terminal)", "components.DCVoltageElm"));
		inputMenu.add(getClassCheckItem("Add A/C Source (2-terminal)", "components.ACVoltageElm"));
		inputMenu.add(getClassCheckItem("Add Voltage Source (1-terminal)", "components.RailElm"));
		inputMenu.add(getClassCheckItem("Add A/C Source (1-terminal)", "components.ACRailElm"));
		inputMenu.add(getClassCheckItem("Add Square Wave (1-terminal)", "components.SquareRailElm"));
		inputMenu.add(getClassCheckItem("Add Analog Output", "components.OutputElm"));
		inputMenu.add(getClassCheckItem("Add Logic Input", "components.LogicInputElm"));
		inputMenu.add(getClassCheckItem("Add Logic Output", "components.LogicOutputElm"));
		inputMenu.add(getClassCheckItem("Add Clock", "components.ClockElm"));
		inputMenu.add(getClassCheckItem("Add A/C Sweep", "components.SweepElm"));
		inputMenu.add(getClassCheckItem("Add Var. Voltage", "components.VarRailElm"));
		inputMenu.add(getClassCheckItem("Add Antenna", "components.AntennaElm"));
		inputMenu.add(getClassCheckItem("Add Current Source", "components.CurrentElm"));
		inputMenu.add(getClassCheckItem("Add LED", "components.LEDElm"));
		inputMenu.add(getClassCheckItem("Add Lamp (beta)", "components.LampElm"));

		Menu activeMenu = new Menu("Active Components");
		mainMenu.add(activeMenu);
		activeMenu.add(getClassCheckItem("Add Diode", "components.DiodeElm"));
		activeMenu.add(getClassCheckItem("Add Zener Diode", "components.ZenerElm"));
		activeMenu.add(getClassCheckItem("Add Transistor (bipolar, NPN)", "components.NTransistorElm"));
		activeMenu.add(getClassCheckItem("Add Transistor (bipolar, PNP)", "components.PTransistorElm"));
		activeMenu.add(getClassCheckItem("Add Op Amp (- on top)", "components.OpAmpElm"));
		activeMenu.add(getClassCheckItem("Add Op Amp (+ on top)", "components.OpAmpSwapElm"));
		activeMenu.add(getClassCheckItem("Add MOSFET (n-channel)", "components.NMosfetElm"));
		activeMenu.add(getClassCheckItem("Add MOSFET (p-channel)", "components.PMosfetElm"));
		activeMenu.add(getClassCheckItem("Add JFET (n-channel)", "components.NJfetElm"));
		activeMenu.add(getClassCheckItem("Add JFET (p-channel)", "components.PJfetElm"));
		activeMenu.add(getClassCheckItem("Add Analog Switch (SPST)", "components.AnalogSwitchElm"));
		activeMenu.add(getClassCheckItem("Add Analog Switch (SPDT)", "components.AnalogSwitch2Elm"));
		activeMenu.add(getClassCheckItem("Add SCR", "components.SCRElm"));
		// activeMenu.add(getClassCheckItem("Add Varactor/Varicap", "VaractorElm"));
		activeMenu.add(getClassCheckItem("Add Tunnel Diode", "components.TunnelDiodeElm"));
		activeMenu.add(getClassCheckItem("Add Triode", "components.TriodeElm"));
		// activeMenu.add(getClassCheckItem("Add Diac", "DiacElm"));
		// activeMenu.add(getClassCheckItem("Add Triac", "TriacElm"));
		// activeMenu.add(getClassCheckItem("Add Photoresistor", "PhotoResistorElm"));
		// activeMenu.add(getClassCheckItem("Add Thermistor", "ThermistorElm"));
		activeMenu.add(getClassCheckItem("Add CCII+", "components.CC2Elm"));
		activeMenu.add(getClassCheckItem("Add CCII-", "components.CC2NegElm"));

		Menu gateMenu = new Menu("Logic Gates");
		mainMenu.add(gateMenu);
		gateMenu.add(getClassCheckItem("Add Inverter", "components.InverterElm"));
		gateMenu.add(getClassCheckItem("Add NAND Gate", "components.NandGateElm"));
		gateMenu.add(getClassCheckItem("Add NOR Gate", "components.NorGateElm"));
		gateMenu.add(getClassCheckItem("Add AND Gate", "components.AndGateElm"));
		gateMenu.add(getClassCheckItem("Add OR Gate", "components.OrGateElm"));
		gateMenu.add(getClassCheckItem("Add XOR Gate", "components.XorGateElm"));

		Menu chipMenu = new Menu("Chips");
		mainMenu.add(chipMenu);
		chipMenu.add(getClassCheckItem("Add D Flip-Flop", "components.DFlipFlopElm"));
		chipMenu.add(getClassCheckItem("Add JK Flip-Flop", "components.JKFlipFlopElm"));
		chipMenu.add(getClassCheckItem("Add 7 Segment LED", "components.SevenSegElm"));
		chipMenu.add(getClassCheckItem("Add VCO", "components.VCOElm"));
		chipMenu.add(getClassCheckItem("Add Phase Comparator", "components.PhaseCompElm"));
		chipMenu.add(getClassCheckItem("Add Counter", "components.CounterElm"));
		chipMenu.add(getClassCheckItem("Add Decade Counter", "components.DecadeElm"));
		chipMenu.add(getClassCheckItem("Add 555 Timer", "components.TimerElm"));
		chipMenu.add(getClassCheckItem("Add DAC", "components.DACElm"));
		chipMenu.add(getClassCheckItem("Add ADC", "components.ADCElm"));
		chipMenu.add(getClassCheckItem("Add Latch", "components.LatchElm"));

		Menu otherMenu = new Menu("Other");
		mainMenu.add(otherMenu);
		otherMenu.add(getClassCheckItem("Add Text", "components.TextElm"));
		otherMenu.add(getClassCheckItem("Add Box", "components.BoxElm"));
		otherMenu.add(getClassCheckItem("Add Scope Probe", "components.ProbeElm"));
		otherMenu.add(getCheckItem("Drag All (Alt-drag)", "DragAll"));
		otherMenu.add(getCheckItem(isMac ? "Drag Row (Alt-S-drag, S-right)" : "Drag Row (S-right)", "DragRow"));
		otherMenu.add(getCheckItem(isMac ? "Drag Column (Alt-\u2318-drag, \u2318-right)" : "Drag Column (C-right)",
				"DragColumn"));
		otherMenu.add(getCheckItem("Drag Selected", "DragSelected"));
		otherMenu.add(getCheckItem("Drag Post (" + ctrlMetaKey + "-drag)", "DragPost"));

		mainMenu.add(getCheckItem("Select/Drag Selected (space or Shift-drag)", "Select"));
		getMain().add(mainMenu);

		getMain().add(resetButton = new Button("Reset"));
		resetButton.addActionListener(this);
		dumpMatrixButton = new Button("Dump Matrix");
		// main.add(dumpMatrixButton);
		dumpMatrixButton.addActionListener(this);
		setStoppedCheck(new Checkbox("Stopped"));
		getStoppedCheck().addItemListener(this);
		getMain().add(getStoppedCheck());

		getMain().add(new Label("Simulation Speed", Label.CENTER));

		// was max of 140
		getMain().add(speedBar = new Scrollbar(Scrollbar.HORIZONTAL, 3, 1, 0, 260));
		speedBar.addAdjustmentListener(this);

		getMain().add(new Label("Current Speed", Label.CENTER));
		currentBar = new Scrollbar(Scrollbar.HORIZONTAL, 50, 1, 1, 100);
		currentBar.addAdjustmentListener(this);
		getMain().add(currentBar);

		getMain().add(powerLabel = new Label("Power Brightness", Label.CENTER));
		getMain().add(powerBar = new Scrollbar(Scrollbar.HORIZONTAL, 50, 1, 1, 100));
		powerBar.addAdjustmentListener(this);
		powerBar.disable();
		powerLabel.disable();

		getMain().add(new Label("www.falstad.com"));

		if (useFrame)
			getMain().add(new Label(""));
		Font f = new Font("SansSerif", 0, 10);
		Label l;
		l = new Label("Current Circuit:");
		l.setFont(f);
		titleLabel = new Label("Label");
		titleLabel.setFont(f);
		if (useFrame) {
			getMain().add(l);
			getMain().add(titleLabel);
		}

		setGrid();
		setElmList(new Vector<CircuitElm>());
//	setupList = new Vector();
		undoStack = new Vector<String>();
		redoStack = new Vector<String>();

		scopes = new Scope[20];
		scopeColCount = new int[20];
		scopeCount = 0;

		random = new Random();
		getCv().setBackground(Color.black);
		getCv().setForeground(Color.lightGray);

		elmMenu = new PopupMenu();
		elmMenu.add(elmEditMenuItem = getMenuItem("Edit"));
		elmMenu.add(elmScopeMenuItem = getMenuItem("View in Scope"));
		elmMenu.add(elmCutMenuItem = getMenuItem("Cut"));
		elmMenu.add(elmCopyMenuItem = getMenuItem("Copy"));
		elmMenu.add(elmDeleteMenuItem = getMenuItem("Delete"));
		getMain().add(elmMenu);

		setScopeMenu(buildScopeMenu(false));
		setTransScopeMenu(buildScopeMenu(true));

		getSetupList(circuitsMenu, false);
		if (useFrame)
			setMenuBar(mb);
		if (startCircuitText != null)
			readSetup(startCircuitText);
		else if (stopMessage == null && startCircuit != null)
			readSetupFile(startCircuit, startLabel);
		else
			readSetup(null, 0, false);

		if (useFrame) {
			Dimension screen = getToolkit().getScreenSize();
			resize(860, 640);
			handleResize();
			Dimension x = getSize();
			setLocation((screen.width - x.width) / 2, (screen.height - x.height) / 2);
			show();
		} else {
			if (!getPowerCheckItem().getState()) {
				getMain().remove(powerBar);
				getMain().remove(powerLabel);
				getMain().validate();
			}
			hide();
			handleResize();
			getApplet().validate();
		}
		requestFocus();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				destroyFrame();
			}
		});
	}

	boolean shown = false;

	public void triggerShow() {
		if (!shown)
			show();
		shown = true;
	}

	@Override
	public void requestFocus() {
		super.requestFocus();
		getCv().requestFocus();
	}

	PopupMenu buildScopeMenu(boolean t) {
		PopupMenu m = new PopupMenu();
		m.add(getMenuItem("Remove", "remove"));
		m.add(getMenuItem("Speed 2x", "speed2"));
		m.add(getMenuItem("Speed 1/2x", "speed1/2"));
		m.add(getMenuItem("Scale 2x", "scale"));
		m.add(getMenuItem("Max Scale", "maxscale"));
		m.add(getMenuItem("Stack", "stack"));
		m.add(getMenuItem("Unstack", "unstack"));
		m.add(getMenuItem("Reset", "reset"));
		if (t) {
			m.add(setScopeIbMenuItem(getCheckItem("Show Ib")));
			m.add(setScopeIcMenuItem(getCheckItem("Show Ic")));
			m.add(setScopeIeMenuItem(getCheckItem("Show Ie")));
			m.add(setScopeVbeMenuItem(getCheckItem("Show Vbe")));
			m.add(setScopeVbcMenuItem(getCheckItem("Show Vbc")));
			m.add(setScopeVceMenuItem(getCheckItem("Show Vce")));
			m.add(setScopeVceIcMenuItem(getCheckItem("Show Vce vs Ic")));
		} else {
			m.add(setScopeVMenuItem(getCheckItem("Show Voltage")));
			m.add(setScopeIMenuItem(getCheckItem("Show Current")));
			m.add(setScopePowerMenuItem(getCheckItem("Show Power Consumed")));
			m.add(setScopeMaxMenuItem(getCheckItem("Show Peak Value")));
			m.add(setScopeMinMenuItem(getCheckItem("Show Negative Peak Value")));
			m.add(setScopeFreqMenuItem(getCheckItem("Show Frequency")));
			m.add(setScopeVIMenuItem(getCheckItem("Show V vs I")));
			m.add(setScopeXYMenuItem(getCheckItem("Plot X/Y")));
			m.add(setScopeSelectYMenuItem(getMenuItem("Select Y", "selecty")));
			m.add(setScopeResistMenuItem(getCheckItem("Show Resistance")));
		}
		getMain().add(m);
		return m;
	}

	MenuItem getMenuItem(String s) {
		MenuItem mi = new MenuItem(s);
		mi.addActionListener(this);
		return mi;
	}

	MenuItem getMenuItem(String s, String ac) {
		MenuItem mi = new MenuItem(s);
		mi.setActionCommand(ac);
		mi.addActionListener(this);
		return mi;
	}

	CheckboxMenuItem getCheckItem(String s) {
		CheckboxMenuItem mi = new CheckboxMenuItem(s);
		mi.addItemListener(this);
		mi.setActionCommand("");
		return mi;
	}

	CheckboxMenuItem getClassCheckItem(String s, String t) {
		try {
			Class c = Class.forName(t);
			CircuitElm elm = constructElement(c, 0, 0);
			register(c, elm);
			if (elm.needsShortcut()) {
				s += " (" + (char) elm.getShortcut() + ")";
			}
			elm.delete();
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return getCheckItem(s, t);
	}

	CheckboxMenuItem getCheckItem(String s, String t) {
		CheckboxMenuItem mi = new CheckboxMenuItem(s);
		mi.addItemListener(this);
		mi.setActionCommand(t);
		return mi;
	}

	void register(Class c, CircuitElm elm) {
		int t = elm.getDumpType();
		if (t == 0) {
			System.out.println("no dump type: " + c);
			return;
		}

		int s = elm.getShortcut();
		if (elm.needsShortcut() && s == 0) {
			if (s == 0) {
				System.err.println("no shortcut " + c + " for " + c);
				return;
			} else if (s <= ' ' || s >= 127) {
				System.err.println("invalid shortcut " + c + " for " + c);
				return;
			}
		}

		Class dclass = elm.getDumpClass();

		if (dumpTypes[t] != null && dumpTypes[t] != dclass) {
			System.out.println("dump type conflict: " + c + " " + dumpTypes[t]);
			return;
		}
		dumpTypes[t] = dclass;

		Class sclass = elm.getClass();

		if (elm.needsShortcut() && shortcuts[s] != null && shortcuts[s] != sclass) {
			System.err.println("shortcut conflict: " + c + " (previously assigned to " + shortcuts[s] + ")");
		} else {
			shortcuts[s] = sclass;
		}
	}

	void handleResize() {
		setWinSize(getCv().getSize());
		if (getWinSize().width == 0)
			return;
		dbimage = getMain().createImage(getWinSize().width, getWinSize().height);
		int h = getWinSize().height / 5;
		/*
		 * if (h < 128 && winSize.height > 300) h = 128;
		 */
		circuitArea = new Rectangle(0, 0, getWinSize().width, getWinSize().height - h);
		int i;
		int minx = 1000, maxx = 0, miny = 1000, maxy = 0;
		for (i = 0; i != getElmList().size(); i++) {
			CircuitElm ce = getElm(i);
			// centered text causes problems when trying to center the circuit,
			// so we special-case it here
			if (!ce.isCenteredText()) {
				minx = min(ce.getX(), min(ce.getX2(), minx));
				maxx = max(ce.getX(), max(ce.getX2(), maxx));
			}
			miny = min(ce.getY(), min(ce.getY2(), miny));
			maxy = max(ce.getY(), max(ce.getY2(), maxy));
		}
		// center circuit; we don't use snapGrid() because that rounds
		int dx = gridMask & ((circuitArea.width - (maxx - minx)) / 2 - minx);
		int dy = gridMask & ((circuitArea.height - (maxy - miny)) / 2 - miny);
		if (dx + minx < 0)
			dx = gridMask & (-minx);
		if (dy + miny < 0)
			dy = gridMask & (-miny);
		for (i = 0; i != getElmList().size(); i++) {
			CircuitElm ce = getElm(i);
			ce.move(dx, dy);
		}
		// after moving elements, need this to avoid singular matrix probs
		needAnalyze();
		circuitBottom = 0;
	}

	void destroyFrame() {
		if (getApplet() == null) {
			dispose();
			System.exit(0);
		} else {
			getApplet().destroyFrame();
		}
	}

	@Override
	public boolean handleEvent(Event ev) {
		if (ev.id == Event.WINDOW_DESTROY) {
			destroyFrame();
			return true;
		}
		return super.handleEvent(ev);
	}

	@Override
	public void paint(Graphics g) {
		getCv().repaint();
	}

	static final int resct = 6;
	long lastTime = 0, lastFrameTime, lastIterTime, secTime = 0;
	int frames = 0;
	int steps = 0;
	int framerate = 0, steprate = 0;

	public void updateCircuit(Graphics realg) {
		CircuitElm realMouseElm;
		if (getWinSize() == null || getWinSize().width == 0)
			return;
		if (isAnalyzeFlag()) {
			analyzeCircuit();
			setAnalyzeFlag(false);
		}
		if (getEditDialog() != null && getEditDialog().getElm() instanceof CircuitElm)
			setMouseElm((CircuitElm) (getEditDialog().getElm()));
		realMouseElm = getMouseElm();
		if (getMouseElm() == null)
			setMouseElm(stopElm);
		setupScopes();
		Graphics2D g = null; // hausen: changed to Graphics2D
		g = (Graphics2D) dbimage.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		CircuitElm.setSelectColor(Color.cyan);
		if (getPrintableCheckItem().getState()) {
			CircuitElm.setWhiteColor(Color.black);
			CircuitElm.setLightGrayColor(Color.black);
			g.setColor(Color.white);
		} else {
			CircuitElm.setWhiteColor(Color.white);
			CircuitElm.setLightGrayColor(Color.lightGray);
			g.setColor(Color.black);
		}
		g.fillRect(0, 0, getWinSize().width, getWinSize().height);
		if (!getStoppedCheck().getState()) {
			try {
				runCircuit();
			} catch (Exception e) {
				e.printStackTrace();
				setAnalyzeFlag(true);
				getCv().repaint();
				return;
			}
		}
		if (!getStoppedCheck().getState()) {
			long sysTime = System.currentTimeMillis();
			if (lastTime != 0) {
				int inc = (int) (sysTime - lastTime);
				double c = currentBar.getValue();
				c = java.lang.Math.exp(c / 3.5 - 14.2);
				CircuitElm.setCurrentMult(1.7 * inc * c);
				if (!conventionCheckItem.getState())
					CircuitElm.setCurrentMult(-CircuitElm.getCurrentMult());
			}
			if (sysTime - secTime >= 1000) {
				framerate = frames;
				steprate = steps;
				frames = 0;
				steps = 0;
				secTime = sysTime;
			}
			lastTime = sysTime;
		} else
			lastTime = 0;
		CircuitElm.setPowerMult(Math.exp(powerBar.getValue() / 4.762 - 7));

		int i;
		Font oldfont = g.getFont();
		for (i = 0; i != getElmList().size(); i++) {
			if (getPowerCheckItem().getState())
				g.setColor(Color.gray);
			/*
			 * else if (conductanceCheckItem.getState()) g.setColor(Color.white);
			 */
			ComponentDrawer.draw(getElm(i), g);
		}
		if (tempMouseMode == MODE_DRAG_ROW || tempMouseMode == MODE_DRAG_COLUMN || tempMouseMode == MODE_DRAG_POST
				|| tempMouseMode == MODE_DRAG_SELECTED)
			for (i = 0; i != getElmList().size(); i++) {
				CircuitElm ce = getElm(i);
				ce.drawPost(g, ce.getX(), ce.getY());
				ce.drawPost(g, ce.getX2(), ce.getY2());
			}
		int badnodes = 0;
		// find bad connections, nodes not connected to other elements which
		// intersect other elements' bounding boxes
		// debugged by hausen: nullPointerException
		if (getNodeList() != null)
			for (i = 0; i != getNodeList().size(); i++) {
				CircuitNode cn = getCircuitNode(i);
				if (!cn.isInternal() && cn.getLinks().size() == 1) {
					int bb = 0, j;
					CircuitNodeLink cnl = cn.getLinks().elementAt(0);
					for (j = 0; j != getElmList().size(); j++) { // TODO: (hausen) see if this change does not break
																	// stuff
						CircuitElm ce = getElm(j);
						if (ce instanceof GraphicElm)
							continue;
						if (cnl.getElm() != ce && getElm(j).getBoundingBox().contains(cn.getX(), cn.getY()))
							bb++;
					}
					if (bb > 0) {
						g.setColor(Color.red);
						g.fillOval(cn.getX() - 3, cn.getY() - 3, 7, 7);
						badnodes++;
					}
				}
			}
		/*
		 * if (mouseElm != null) { g.setFont(oldfont); g.drawString("+", mouseElm.x+10,
		 * mouseElm.y); }
		 */
		if (getDragElm() != null
				&& (getDragElm().getX() != getDragElm().getX2() || getDragElm().getY() != getDragElm().getY2()))
			ComponentDrawer.draw(getDragElm(), g);
		g.setFont(oldfont);
		int ct = scopeCount;
		if (stopMessage != null)
			ct = 0;
		for (i = 0; i != ct; i++)
			scopes[i].draw(g);
		g.setColor(CircuitElm.getWhiteColor());
		if (stopMessage != null) {
			g.drawString(stopMessage, 10, circuitArea.height);
		} else {
			if (circuitBottom == 0)
				calcCircuitBottom();
			String info[] = new String[10];
			if (getMouseElm() != null) {
				if (mousePost == -1)
					getMouseElm().getInfo(info);
				else
					info[0] = "V = " + CircuitElm.getUnitText(getMouseElm().getPostVoltage(mousePost), "V");
				/*
				 * //shownodes for (i = 0; i != mouseElm.getPostCount(); i++) info[0] += " " +
				 * mouseElm.nodes[i]; if (mouseElm.getVoltageSourceCount() > 0) info[0] += ";" +
				 * (mouseElm.getVoltageSource()+nodeList.size());
				 */

			} else {
				CircuitElm.showFormat.setMinimumFractionDigits(2);
				info[0] = "t = " + CircuitElm.getUnitText(getT(), "s");
				CircuitElm.showFormat.setMinimumFractionDigits(0);
			}
			if (hintType != -1) {
				for (i = 0; info[i] != null; i++)
					;
				String s = getHint();
				if (s == null)
					hintType = -1;
				else
					info[i] = s;
			}
			int x = 0;
			if (ct != 0)
				x = scopes[ct - 1].rightEdge() + 20;
			x = max(x, getWinSize().width * 2 / 3);

			// count lines of data
			for (i = 0; info[i] != null; i++)
				;
			if (badnodes > 0)
				info[i++] = badnodes + ((badnodes == 1) ? " bad connection" : " bad connections");

			// find where to show data; below circuit, not too high unless we need it
			int ybase = getWinSize().height - 15 * i - 5;
			ybase = min(ybase, circuitArea.height);
			ybase = max(ybase, circuitBottom);
			for (i = 0; info[i] != null; i++)
				g.drawString(info[i], x, ybase + 15 * (i + 1));
		}
		if (selectedArea != null) {
			g.setColor(CircuitElm.getSelectColor());
			g.drawRect(selectedArea.x, selectedArea.y, selectedArea.width, selectedArea.height);
		}
		setMouseElm(realMouseElm);
		frames++;
		/*
		 * g.setColor(Color.white); g.drawString("Framerate: " + framerate, 10, 10);
		 * g.drawString("Steprate: " + steprate, 10, 30); g.drawString("Steprate/iter: "
		 * + (steprate/getIterCount()), 10, 50); g.drawString("iterc: " +
		 * (getIterCount()), 10, 70);
		 */

		realg.drawImage(dbimage, 0, 0, this);
		if (!getStoppedCheck().getState() && circuitMatrix != null) {
			// Limit to 50 fps (thanks to Jurgen Klotzer for this)
			long delay = 1000 / 50 - (System.currentTimeMillis() - lastFrameTime);
			// realg.drawString("delay: " + delay, 10, 90);
			if (delay > 0) {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
				}
			}

			getCv().repaint(0);
		}
		lastFrameTime = lastTime;
	}

	void setupScopes() {
		int i;

		// check scopes to make sure the elements still exist, and remove
		// unused scopes/columns
		int pos = -1;
		for (i = 0; i < scopeCount; i++) {
			if (locateElm(scopes[i].getElm()) < 0)
				scopes[i].setElm(null);
			if (scopes[i].getElm() == null) {
				int j;
				for (j = i; j != scopeCount; j++)
					scopes[j] = scopes[j + 1];
				scopeCount--;
				i--;
				continue;
			}
			if (scopes[i].getPosition() > pos + 1)
				scopes[i].setPosition(pos + 1);
			pos = scopes[i].getPosition();
		}
		while (scopeCount > 0 && scopes[scopeCount - 1].getElm() == null)
			scopeCount--;
		int h = getWinSize().height - circuitArea.height;
		pos = 0;
		for (i = 0; i != scopeCount; i++)
			scopeColCount[i] = 0;
		for (i = 0; i != scopeCount; i++) {
			pos = max(scopes[i].getPosition(), pos);
			scopeColCount[scopes[i].getPosition()]++;
		}
		int colct = pos + 1;
		int iw = infoWidth;
		if (colct <= 2)
			iw = iw * 3 / 2;
		int w = (getWinSize().width - iw) / colct;
		int marg = 10;
		if (w < marg * 2)
			w = marg * 2;
		pos = -1;
		int colh = 0;
		int row = 0;
		int speed = 0;
		for (i = 0; i != scopeCount; i++) {
			Scope s = scopes[i];
			if (s.getPosition() > pos) {
				pos = s.getPosition();
				colh = h / scopeColCount[pos];
				row = 0;
				speed = s.getSpeed();
			}
			if (s.getSpeed() != speed) {
				s.setSpeed(speed);
				s.resetGraph();
			}
			Rectangle r = new Rectangle(pos * w, getWinSize().height - h + colh * row, w - marg, colh);
			row++;
			if (!r.equals(s.getRect()))
				s.setRect(r);
		}
	}

	String getHint() {
		CircuitElm c1 = getElm(hintItem1);
		CircuitElm c2 = getElm(hintItem2);
		if (c1 == null || c2 == null)
			return null;
		if (hintType == HINT_LC) {
			if (!(c1 instanceof InductorElm))
				return null;
			if (!(c2 instanceof CapacitorElm))
				return null;
			InductorElm ie = (InductorElm) c1;
			CapacitorElm ce = (CapacitorElm) c2;
			return "res.f = "
					+ CircuitElm.getUnitText(1 / (2 * pi * Math.sqrt(ie.getInductance() * ce.getCapacitance())), "Hz");
		}
		if (hintType == HINT_RC) {
			if (!(c1 instanceof ResistorElm))
				return null;
			if (!(c2 instanceof CapacitorElm))
				return null;
			ResistorElm re = (ResistorElm) c1;
			CapacitorElm ce = (CapacitorElm) c2;
			return "RC = " + CircuitElm.getUnitText(re.getResistance() * ce.getCapacitance(), "s");
		}
		if (hintType == HINT_3DB_C) {
			if (!(c1 instanceof ResistorElm))
				return null;
			if (!(c2 instanceof CapacitorElm))
				return null;
			ResistorElm re = (ResistorElm) c1;
			CapacitorElm ce = (CapacitorElm) c2;
			return "f.3db = " + CircuitElm.getUnitText(1 / (2 * pi * re.getResistance() * ce.getCapacitance()), "Hz");
		}
		if (hintType == HINT_3DB_L) {
			if (!(c1 instanceof ResistorElm))
				return null;
			if (!(c2 instanceof InductorElm))
				return null;
			ResistorElm re = (ResistorElm) c1;
			InductorElm ie = (InductorElm) c2;
			return "f.3db = " + CircuitElm.getUnitText(re.getResistance() / (2 * pi * ie.getInductance()), "Hz");
		}
		if (hintType == HINT_TWINT) {
			if (!(c1 instanceof ResistorElm))
				return null;
			if (!(c2 instanceof CapacitorElm))
				return null;
			ResistorElm re = (ResistorElm) c1;
			CapacitorElm ce = (CapacitorElm) c2;
			return "fc = " + CircuitElm.getUnitText(1 / (2 * pi * re.getResistance() * ce.getCapacitance()), "Hz");
		}
		return null;
	}

	public void toggleSwitch(int n) {
		int i;
		for (i = 0; i != getElmList().size(); i++) {
			CircuitElm ce = getElm(i);
			if (ce instanceof SwitchElm) {
				n--;
				if (n == 0) {
					((SwitchElm) ce).toggle();
					setAnalyzeFlag(true);
					getCv().repaint();
					return;
				}
			}
		}
	}

	public void needAnalyze() {
		setAnalyzeFlag(true);
		getCv().repaint();
	}

	/**
	 * How circuit components are interconnected
	 */
	private Vector<CircuitNode> nodeList;

	CircuitElm voltageSources[];

	/**
	 * Returns the nth node of nodeList
	 */
	public CircuitNode getCircuitNode(int n) {
		if (n >= getNodeList().size())
			return null;
		return getNodeList().elementAt(n);
	}

	public CircuitElm getElm(int n) {
		if (n >= getElmList().size())
			return null;
		return getElmList().get(n);
	}

	public void circuitSetUp() {
		boolean gotGround = false;
		boolean gotRail = false;
		CircuitElm volt = null;
		int vscount = 0;

		// If there are no elements, the circuit cannot be analyzed
		if (getElmList().isEmpty())
			return;

		setNodeList(new Vector<CircuitNode>());

		System.out.println("analyzeCircuit - 1st step: look for voltage or ground element");
		for (int i = 0; i != getElmList().size(); i++) {
			CircuitElm ce = getElm(i);
			if (ce instanceof GroundElm) {
				gotGround = true;
				break;
			}
			if (ce instanceof RailElm)
				gotRail = true;
			if (volt == null && ce instanceof VoltageElm)
				volt = ce;
		}

		// if no ground, and no rails, then the voltage elm's first terminal is ground
		if (!gotGround && volt != null && !gotRail) {
			System.out.println("analyzeCircuit - 1st step: voltage element was found");
			CircuitNode cn = new CircuitNode();
			Point pt = volt.getPost(0);
			cn.setX(pt.x);
			cn.setY(pt.y);
			getNodeList().addElement(cn);
		} else {
			// otherwise allocate extra node for ground
			System.out.println("analyzeCircuit - 1st step: no voltage or ground element found");
			System.out.println("analyzeCircuit - 1st step: allocate extra node for ground");
			CircuitNode cn = new CircuitNode();
			cn.setX(cn.setY(-1));
			getNodeList().addElement(cn);
		}

		System.out.println("analyzeCircuit - 2nd step: allocate nodes and voltage sources");
		for (int i = 0; i != getElmList().size(); i++) {
			CircuitElm ce = getElm(i);
			int inodes = ce.getInternalNodeCount();
			int ivs = ce.getVoltageSourceCount();
			int posts = ce.getPostCount();

			// allocate a node for each post and match posts to nodes
			for (int j = 0; j != posts; j++) {
				Point pt = ce.getPost(j);
				assert pt != null;
				int k;
				for (k = 0; k != getNodeList().size(); k++) {
					CircuitNode cn = getCircuitNode(k);
					if (pt.x == cn.getX() && pt.y == cn.getY())
						break;
				}
				if (k == getNodeList().size()) {
					CircuitNode cn = new CircuitNode();
					cn.setX(pt.x);
					cn.setY(pt.y);
					CircuitNodeLink cnl = new CircuitNodeLink();
					cnl.setNum(j);
					cnl.setElm(ce);
					cn.getLinks().addElement(cnl);
					ce.setNode(j, getNodeList().size());
					getNodeList().addElement(cn);
				} else {
					CircuitNodeLink cnl = new CircuitNodeLink();
					cnl.setNum(j);
					cnl.setElm(ce);
					getCircuitNode(k).getLinks().addElement(cnl);
					ce.setNode(j, k);
					// if it's the ground node, make sure the node voltage is 0,
					// cause it may not get set later
					if (k == 0)
						ce.setNodeVoltage(j, 0);
				}
			}

			for (int j = 0; j != inodes; j++) {
				CircuitNode cn = new CircuitNode();
				cn.setX(cn.setY(-1));
				cn.setInternal(true);
				CircuitNodeLink cnl = new CircuitNodeLink();
				cnl.setNum(j + posts);
				cnl.setElm(ce);
				cn.getLinks().addElement(cnl);
				ce.setNode(cnl.getNum(), getNodeList().size());
				getNodeList().addElement(cn);
			}
			vscount += ivs;
		}

		System.out.println("analyzeCircuit - 2nd step: #nodes for CirSim: " + getNodeList().size());
		for (int k = 0; k < getNodeList().size(); k++) {
			CircuitNode cn = getCircuitNode(k);
			System.out.println("analyzeCircuit - 2nd step: Node{" + cn.getX() + ";" + cn.getY() + "}");
		}

		voltageSources = new CircuitElm[vscount];
		vscount = 0;
		circuitNonLinear = false;

		System.out.println("analyzeCircuit - 3rd step: determine if circuit is nonlinear");
		for (int i = 0; i != getElmList().size(); i++) {
			CircuitElm ce = getElm(i);
			if (ce.nonLinear())
				circuitNonLinear = true;
			int ivs = ce.getVoltageSourceCount();
			for (int j = 0; j != ivs; j++) {
				voltageSources[vscount] = ce;
				ce.setVoltageSource(j, vscount++);
			}
		}
		voltageSourceCount = vscount;
	}

	public void analyzeCircuit() {
		// System.out.println("circuitBottom: " + this.circuitBottom);
		calcCircuitBottom();
		// System.out.println("circuitBottom: " + this.circuitBottom);

		// System.out.println("elmList: " + this.elmList);
		if (getElmList().isEmpty())
			return;
		stopMessage = null;
		stopElm = null;
		int i, j;
		int vscount = 0;
		// System.out.println("nodeList: " + this.nodeList);
		setNodeList(new Vector<CircuitNode>());
		// System.out.println("nodeList: " + this.nodeList);
		boolean gotGround = false;
		boolean gotRail = false;
		CircuitElm volt = null;

		//System.out.println("analyzeCircuit - 1st step: look for voltage or ground element");
		for (i = 0; i != getElmList().size(); i++) {
			CircuitElm ce = getElm(i);
			if (ce instanceof GroundElm) {
				gotGround = true;
				break;
			}
			if (ce instanceof RailElm)
				gotRail = true;
			if (volt == null && ce instanceof VoltageElm)
				volt = ce;
		}

		// if no ground, and no rails, then the voltage elm's first terminal
		// is ground
		if (!gotGround && volt != null && !gotRail) {
			CircuitNode cn = new CircuitNode();
			Point pt = volt.getPost(0);
			cn.setX(pt.x);
			cn.setY(pt.y);
			getNodeList().addElement(cn);
		} else {
			// otherwise allocate extra node for ground
			System.out.println("analyzeCircuit - 1st step: no voltage or ground element found");
			System.out.println("analyzeCircuit - 1st step: allocate extra node for ground");
			CircuitNode cn = new CircuitNode();
			cn.setX(cn.setY(-1));
			getNodeList().addElement(cn);
		}

		//System.out.println("analyzeCircuit - 2nd step: allocate nodes and voltage sources");
		for (i = 0; i != getElmList().size(); i++) {
			CircuitElm ce = getElm(i);
			int inodes = ce.getInternalNodeCount();
			int ivs = ce.getVoltageSourceCount();
			int posts = ce.getPostCount();

			// System.out.println(ce.getClass() + " internalNodeCount: " + inodes + "
			// voltageSourceCount: " + ivs + " postCount: " + posts);

			// allocate a node for each post and match posts to nodes
			for (j = 0; j != posts; j++) {
				Point pt = ce.getPost(j);
				assert pt != null;
				int k;
				for (k = 0; k != getNodeList().size(); k++) {
					CircuitNode cn = getCircuitNode(k);
					if (pt.x == cn.getX() && pt.y == cn.getY())
						break;
				}
				if (k == getNodeList().size()) {
					CircuitNode cn = new CircuitNode();
					cn.setX(pt.x);
					cn.setY(pt.y);
					CircuitNodeLink cnl = new CircuitNodeLink();
					cnl.setNum(j);
					cnl.setElm(ce);
					cn.getLinks().addElement(cnl);
					ce.setNode(j, getNodeList().size());
					getNodeList().addElement(cn);
				} else {
					CircuitNodeLink cnl = new CircuitNodeLink();
					cnl.setNum(j);
					cnl.setElm(ce);
					getCircuitNode(k).getLinks().addElement(cnl);
					ce.setNode(j, k);
					// if it's the ground node, make sure the node voltage is 0,
					// cause it may not get set later
					if (k == 0)
						ce.setNodeVoltage(j, 0);
				}
			}
			for (j = 0; j != inodes; j++) {
				CircuitNode cn = new CircuitNode();
				cn.setX(cn.setY(-1));
				cn.setInternal(true);
				CircuitNodeLink cnl = new CircuitNodeLink();
				cnl.setNum(j + posts);
				cnl.setElm(ce);
				cn.getLinks().addElement(cnl);
				ce.setNode(cnl.getNum(), getNodeList().size());
				getNodeList().addElement(cn);
			}
			vscount += ivs;
		}
		// System.out.println("vscount: " + vscount);
		System.out.println("analyzeCircuit - 2nd step: #nodes for CirSim: " + getNodeList().size());
		for (int k = 0; k < getNodeList().size(); k++) {
			CircuitNode cn = getCircuitNode(k);
			//System.out.println("analyzeCircuit - 2nd step: Node{" + cn.getX() + ";" + cn.getY() + "}");
		}

		voltageSources = new CircuitElm[vscount];
		vscount = 0;
		circuitNonLinear = false;
		//System.out.println("analyzeCircuit - 3rd step: determine if circuit is nonlinear");
		for (i = 0; i != getElmList().size(); i++) {
			CircuitElm ce = getElm(i);
			if (ce.nonLinear())
				circuitNonLinear = true;
			int ivs = ce.getVoltageSourceCount();
			for (j = 0; j != ivs; j++) {
				voltageSources[vscount] = ce;
				ce.setVoltageSource(j, vscount++);
			}
		}
		// System.out.println("vscount: " + vscount);
		voltageSourceCount = vscount;

		int matrixSize = getNodeList().size() - 1 + vscount;
		// System.out.println("matrixSize: " + matrixSize);
		circuitMatrix = new double[matrixSize][matrixSize];
		circuitRightSide = new double[matrixSize];
		origMatrix = new double[matrixSize][matrixSize];
		origRightSide = new double[matrixSize];
		circuitMatrixSize = circuitMatrixFullSize = matrixSize;
		circuitRowInfo = new RowInfo[matrixSize];
		circuitPermute = new int[matrixSize];
		int vs = 0;
		for (i = 0; i != matrixSize; i++)
			circuitRowInfo[i] = new RowInfo();
		circuitNeedsMap = false;

		System.out.println("circuitMatrix: " + Arrays.deepToString(circuitMatrix));
		System.out.println("circuitRightSide: " + Arrays.toString(circuitRightSide));

		System.out.println("analyzeCircuit - 3rd step: init circuitMatrix and circuitRightSide");

		// stamp linear circuit elements
		for (i = 0; i != getElmList().size(); i++) {
			CircuitElm ce = getElm(i);
			System.out.println("\nanalyzeCircuit - 3rd step: " + ce.getClass().getSimpleName() + " component");
			ce.stamp();
		}

		System.out.println("\ncircuitMatrix: " + Arrays.deepToString(circuitMatrix));
		System.out.println("circuitRightSide: " + Arrays.toString(circuitRightSide));

		// System.out.println("ac4");
		System.out.println("analyzeCircuit - 4th step: determine nodes that are unconnected");
		// determine nodes that are unconnected
		boolean closure[] = new boolean[getNodeList().size()];
		boolean tempclosure[] = new boolean[getNodeList().size()];
		boolean changed = true;
		closure[0] = true;
		while (changed) {
			changed = false;
			for (i = 0; i != getElmList().size(); i++) {
				CircuitElm ce = getElm(i);
				// loop through all ce's nodes to see if they are connected
				// to other nodes not in closure
				for (j = 0; j < ce.getPostCount(); j++) {
					if (!closure[ce.getNode(j)]) {
						if (ce.hasGroundConnection(j))
							closure[ce.getNode(j)] = changed = true;
						continue;
					}
					int k;
					for (k = 0; k != ce.getPostCount(); k++) {
						if (j == k)
							continue;
						int kn = ce.getNode(k);
						if (ce.getConnection(j, k) && !closure[kn]) {
							closure[kn] = true;
							changed = true;
						}
					}
				}
			}
			if (changed)
				continue;

			// connect unconnected nodes
			for (i = 0; i != getNodeList().size(); i++)
				if (!closure[i] && !getCircuitNode(i).isInternal()) {
					System.out.println("node " + i + " unconnected");
					System.out.println("analyzeCircuit - 4th step: Node{" + getCircuitNode(i).getX() + "; "
							+ getCircuitNode(i).getY() + "}");
					stampResistor(0, i, 1e8);
					closure[i] = true;
					changed = true;
					break;
				}
		}
		// System.out.println("ac5");
		System.out.println("analyzeCircuit - 5th step: check for circuit integrity");
		for (i = 0; i != getElmList().size(); i++) {
			CircuitElm ce = getElm(i);
			// look for inductors with no current path
			if (ce instanceof InductorElm) {
				FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce, ce.getNode(1));
				// first try findPath with maximum depth of 5, to avoid slowdowns
				if (!fpi.findPath(ce.getNode(0), 5) && !fpi.findPath(ce.getNode(0))) {
					System.out.println(ce + " no path");
					ce.reset();
				}
			}
			// look for current sources with no current path
			if (ce instanceof CurrentElm) {
				FindPathInfo fpi = new FindPathInfo(FindPathInfo.INDUCT, ce, ce.getNode(1));
				if (!fpi.findPath(ce.getNode(0))) {
					stop("No path for current source!", ce);
					return;
				}
			}
			// look for voltage source loops
			if ((ce instanceof VoltageElm && ce.getPostCount() == 2) || ce instanceof WireElm) {
				FindPathInfo fpi = new FindPathInfo(FindPathInfo.VOLTAGE, ce, ce.getNode(1));
				if (fpi.findPath(ce.getNode(0))) {
					stop("Voltage source/wire loop with no resistance!", ce);
					return;
				}
			}
			// look for shorted caps, or caps w/ voltage but no R
			if (ce instanceof CapacitorElm) {
				FindPathInfo fpi = new FindPathInfo(FindPathInfo.SHORT, ce, ce.getNode(1));
				if (fpi.findPath(ce.getNode(0))) {
					System.out.println(ce + " shorted");
					ce.reset();
				} else {
					fpi = new FindPathInfo(FindPathInfo.CAP_V, ce, ce.getNode(1));
					if (fpi.findPath(ce.getNode(0))) {
						stop("Capacitor loop with no resistance!", ce);
						return;
					}
				}
			}
		}

		System.out.println("circuitRowInfo: ");
		for (RowInfo ri : circuitRowInfo) {
			System.out.println(ri.toString());
		}

		// System.out.println("ac6");
		System.out.println("analyzeCircuit - 6th step: simplify the matrix");
		System.out.println("circuitMatrix: " + Arrays.deepToString(circuitMatrix));
		System.out.println("circuitRightSide: " + Arrays.toString(circuitRightSide));

		// simplify the matrix; this speeds things up quite a bit
		for (i = 0; i != matrixSize; i++) {
			int qm = -1, qp = -1;
			double qv = 0;
			RowInfo re = circuitRowInfo[i];
			/*
			 * System.out.println("row " + i + " " + re.lsChanges + " " + re.rsChanges + " "
			 * + re.dropRow);
			 */
			if (re.isLsChanges() || re.isDropRow() || re.isRsChanges())
				continue;
			double rsadd = 0;

			// look for rows that can be removed
			for (j = 0; j != matrixSize; j++) {
				double q = circuitMatrix[i][j];
				if (circuitRowInfo[j].getType() == RowInfo.ROW_CONST) {
					// keep a running total of const values that have been
					// removed already
					rsadd -= circuitRowInfo[j].getValue() * q;
					continue;
				}
				if (q == 0)
					continue;
				if (qp == -1) {
					qp = j;
					qv = q;
					continue;
				}
				if (qm == -1 && q == -qv) {
					qm = j;
					continue;
				}
				break;
			}
			// System.out.println("line " + i + " " + qp + " " + qm + " " + j);
			/*
			 * if (qp != -1 && circuitRowInfo[qp].lsChanges) {
			 * System.out.println("lschanges"); continue; } if (qm != -1 &&
			 * circuitRowInfo[qm].lsChanges) { System.out.println("lschanges"); continue; }
			 */
			if (j == matrixSize) {
				if (qp == -1) {
					stop("Matrix error", null);
					return;
				}
				RowInfo elt = circuitRowInfo[qp];
				if (qm == -1) {
					// we found a row with only one nonzero entry; that value
					// is a constant
					int k;
					for (k = 0; elt.getType() == RowInfo.ROW_EQUAL && k < 100; k++) {
						// follow the chain
						/*
						 * System.out.println("following equal chain from " + i + " " + qp + " to " +
						 * elt.nodeEq);
						 */
						qp = elt.getNodeEq();
						elt = circuitRowInfo[qp];
					}
					if (elt.getType() == RowInfo.ROW_EQUAL) {
						// break equal chains
						// System.out.println("Break equal chain");
						elt.setType(RowInfo.ROW_NORMAL);
						continue;
					}
					if (elt.getType() != RowInfo.ROW_NORMAL) {
						System.out.println("type already " + elt.getType() + " for " + qp + "!");
						continue;
					}
					elt.setType(RowInfo.ROW_CONST);
					elt.setValue((circuitRightSide[i] + rsadd) / qv);
					circuitRowInfo[i].setDropRow(true);
					// System.out.println(qp + " * " + qv + " = const " + elt.value);
					i = -1; // start over from scratch
				} else if (circuitRightSide[i] + rsadd == 0) {
					// we found a row with only two nonzero entries, and one
					// is the negative of the other; the values are equal
					if (elt.getType() != RowInfo.ROW_NORMAL) {
						// System.out.println("swapping");
						int qq = qm;
						qm = qp;
						qp = qq;
						elt = circuitRowInfo[qp];
						if (elt.getType() != RowInfo.ROW_NORMAL) {
							// we should follow the chain here, but this
							// hardly ever happens so it's not worth worrying
							// about
							System.out.println("swap failed");
							continue;
						}
					}
					elt.setType(RowInfo.ROW_EQUAL);
					elt.setNodeEq(qm);
					circuitRowInfo[i].setDropRow(true);
					// System.out.println(qp + " = " + qm);
				}
			}
		}

		System.out.println("circuitMatrix: " + Arrays.deepToString(circuitMatrix));
		System.out.println("circuitRightSide: " + Arrays.toString(circuitRightSide));
		// System.out.println("ac7");
		System.out.println("analyzeCircuit - 7th step: find size of new matrix");
		// find size of new matrix
		int nn = 0;
		for (i = 0; i != matrixSize; i++) {
			RowInfo elt = circuitRowInfo[i];
			if (elt.getType() == RowInfo.ROW_NORMAL) {
				elt.setMapCol(nn++);
				// System.out.println("col " + i + " maps to " + elt.mapCol);
				continue;
			}
			if (elt.getType() == RowInfo.ROW_EQUAL) {
				RowInfo e2 = null;
				// resolve chains of equality; 100 max steps to avoid loops
				for (j = 0; j != 100; j++) {
					e2 = circuitRowInfo[elt.getNodeEq()];
					if (e2.getType() != RowInfo.ROW_EQUAL)
						break;
					if (i == e2.getNodeEq())
						break;
					elt.setNodeEq(e2.getNodeEq());
				}
			}
			if (elt.getType() == RowInfo.ROW_CONST)
				elt.setMapCol(-1);
		}
		for (i = 0; i != matrixSize; i++) {
			RowInfo elt = circuitRowInfo[i];
			if (elt.getType() == RowInfo.ROW_EQUAL) {
				RowInfo e2 = circuitRowInfo[elt.getNodeEq()];
				if (e2.getType() == RowInfo.ROW_CONST) {
					// if something is equal to a const, it's a const
					elt.setType(e2.getType());
					elt.setValue(e2.getValue());
					elt.setMapCol(-1);
					// System.out.println(i + " = [late]const " + elt.value);
				} else {
					elt.setMapCol(e2.getMapCol());
					// System.out.println(i + " maps to: " + e2.mapCol);
				}
			}
		}
		// System.out.println("ac8");
		System.out.println("analyzeCircuit - 8th step: creation of new matrix");
		System.out.println("circuitMatrix: " + Arrays.deepToString(circuitMatrix));
		System.out.println("circuitRightSide: " + Arrays.toString(circuitRightSide));
		/*
		 * System.out.println("matrixSize = " + matrixSize);
		 * 
		 * for (j = 0; j != circuitMatrixSize; j++) { System.out.println(j + ": "); for
		 * (i = 0; i != circuitMatrixSize; i++) System.out.print(circuitMatrix[j][i] +
		 * " "); System.out.print("  " + circuitRightSide[j] + "\n"); }
		 * System.out.print("\n");
		 */

		// make the new, simplified matrix
		int newsize = nn;
		double newmatx[][] = new double[newsize][newsize];
		double newrs[] = new double[newsize];
		int ii = 0;
		for (i = 0; i != matrixSize; i++) {
			RowInfo rri = circuitRowInfo[i];
			if (rri.isDropRow()) {
				rri.setMapRow(-1);
				continue;
			}
			newrs[ii] = circuitRightSide[i];
			rri.setMapRow(ii);
			// System.out.println("Row " + i + " maps to " + ii);
			for (j = 0; j != matrixSize; j++) {
				RowInfo ri = circuitRowInfo[j];
				if (ri.getType() == RowInfo.ROW_CONST)
					newrs[ii] -= ri.getValue() * circuitMatrix[i][j];
				else
					newmatx[ii][ri.getMapCol()] += circuitMatrix[i][j];
			}
			ii++;
		}

		circuitMatrix = newmatx;
		circuitRightSide = newrs;
		matrixSize = circuitMatrixSize = newsize;
		for (i = 0; i != matrixSize; i++)
			origRightSide[i] = circuitRightSide[i];
		for (i = 0; i != matrixSize; i++)
			for (j = 0; j != matrixSize; j++)
				origMatrix[i][j] = circuitMatrix[i][j];
		circuitNeedsMap = true;

		System.out.println("circuitMatrix: " + Arrays.deepToString(circuitMatrix));
		System.out.println("circuitRightSide: " + Arrays.toString(circuitRightSide));

		/*
		 * System.out.println("matrixSize = " + matrixSize + " " + circuitNonLinear);
		 * for (j = 0; j != circuitMatrixSize; j++) { for (i = 0; i !=
		 * circuitMatrixSize; i++) System.out.print(circuitMatrix[j][i] + " ");
		 * System.out.print("  " + circuitRightSide[j] + "\n"); }
		 * System.out.print("\n");
		 */

		// if a matrix is linear, we can do the lu_factor here instead of
		// needing to do it every frame
		if (!circuitNonLinear) {
			if (!lu_factor(circuitMatrix, circuitMatrixSize, circuitPermute)) {
				stop("Singular matrix!", null);
				return;
			}
		}
	}

	/**
	 * Set the field circuitBottom with the lowest value of (height + y) of the
	 * Rectangle object, previously initialized for each element of the circuit
	 */
	void calcCircuitBottom() {
		int i;
		circuitBottom = 0;
		for (i = 0; i != getElmList().size(); i++) {
			Rectangle rect = getElm(i).getBoundingBox();
			int bottom = rect.height + rect.y;
			if (bottom > circuitBottom)
				circuitBottom = bottom;
		}
	}

	class FindPathInfo {
		static final int INDUCT = 1;
		static final int VOLTAGE = 2;
		static final int SHORT = 3;
		static final int CAP_V = 4;
		boolean used[];
		int dest;
		CircuitElm firstElm;
		int type;

		FindPathInfo(int t, CircuitElm e, int d) {
			dest = d;
			type = t;
			firstElm = e;
			used = new boolean[getNodeList().size()];
		}

		boolean findPath(int n1) {
			return findPath(n1, -1);
		}

		boolean findPath(int n1, int depth) {
			if (n1 == dest)
				return true;
			if (depth-- == 0)
				return false;
			if (used[n1]) {
				// System.out.println("used " + n1);
				return false;
			}
			used[n1] = true;
			int i;
			for (i = 0; i != getElmList().size(); i++) {
				CircuitElm ce = getElm(i);
				if (ce == firstElm)
					continue;
				if (type == INDUCT) {
					if (ce instanceof CurrentElm)
						continue;
				}
				if (type == VOLTAGE) {
					if (!(ce.isWire() || ce instanceof VoltageElm))
						continue;
				}
				if (type == SHORT && !ce.isWire())
					continue;
				if (type == CAP_V) {
					if (!(ce.isWire() || ce instanceof CapacitorElm || ce instanceof VoltageElm))
						continue;
				}
				if (n1 == 0) {
					// look for posts which have a ground connection;
					// our path can go through ground
					int j;
					for (j = 0; j != ce.getPostCount(); j++)
						if (ce.hasGroundConnection(j) && findPath(ce.getNode(j), depth)) {
							used[n1] = false;
							return true;
						}
				}
				int j;
				for (j = 0; j != ce.getPostCount(); j++) {
					// System.out.println(ce + " " + ce.getNode(j));
					if (ce.getNode(j) == n1)
						break;
				}
				if (j == ce.getPostCount())
					continue;
				if (ce.hasGroundConnection(j) && findPath(0, depth)) {
					// System.out.println(ce + " has ground");
					used[n1] = false;
					return true;
				}
				if (type == INDUCT && ce instanceof InductorElm) {
					double c = ce.getCurrent();
					if (j == 0)
						c = -c;
					// System.out.println("matching " + c + " to " + firstElm.getCurrent());
					// System.out.println(ce + " " + firstElm);
					if (Math.abs(c - firstElm.getCurrent()) > 1e-10)
						continue;
				}
				int k;
				for (k = 0; k != ce.getPostCount(); k++) {
					if (j == k)
						continue;
					// System.out.println(ce + " " + ce.getNode(j) + "-" + ce.getNode(k));
					if (ce.getConnection(j, k) && findPath(ce.getNode(k), depth)) {
						// System.out.println("got findpath " + n1);
						used[n1] = false;
						return true;
					}
					// System.out.println("back on findpath " + n1);
				}
			}
			used[n1] = false;
			// System.out.println(n1 + " failed");
			return false;
		}
	}

	public void stop(String s, CircuitElm ce) {
		stopMessage = s;
		circuitMatrix = null;
		stopElm = ce;
		getStoppedCheck().setState(true);
		setAnalyzeFlag(false);
		getCv().repaint();
	}

	// control voltage source vs with voltage from n1 to n2 (must
	// also call stampVoltageSource())
	public void stampVCVS(int n1, int n2, double coef, int vs) {
		int vn = getNodeList().size() + vs;
		stampMatrix(vn, n1, coef);
		stampMatrix(vn, n2, -coef);
	}

	/**
	 * Stamp independent voltage source #vs, from n1 to n2, amount v
	 * 
	 * @param n1
	 * @param n2
	 * @param vs
	 * @param v
	 */
	public void stampVoltageSource(int n1, int n2, int vs, double v) {
		int vn = getNodeList().size() + vs;
		stampMatrix(vn, n1, -1);
		stampMatrix(vn, n2, 1);
		stampRightSide(vn, v);
		stampMatrix(n1, vn, 1);
		stampMatrix(n2, vn, -1);
	}

	// use this if the amount of voltage is going to be updated in doStep()
	public void stampVoltageSource(int n1, int n2, int vs) {
		int vn = getNodeList().size() + vs;
		stampMatrix(vn, n1, -1);
		stampMatrix(vn, n2, 1);
		stampRightSide(vn);
		stampMatrix(n1, vn, 1);
		stampMatrix(n2, vn, -1);
	}

	public void updateVoltageSource(int n1, int n2, int vs, double v) {
		int vn = getNodeList().size() + vs;
		stampRightSide(vn, v);
	}

	public void stampResistor(int n1, int n2, double r) {
		assert r != 0;
		double r0 = 1 / r;
		if (Double.isNaN(r0) || Double.isInfinite(r0)) {
			System.out.print("bad resistance " + r + " " + r0 + "\n");
			int a = 0;
			a /= a;
		}

		System.out.println("nodes[0]: " + n1 + " nodes[1]: " + n2 + " r0: " + r0);
		stampMatrix(n1, n1, r0);
		stampMatrix(n2, n2, r0);
		stampMatrix(n1, n2, -r0);
		stampMatrix(n2, n1, -r0);
	}

	public void stampConductance(int n1, int n2, double r0) {
		stampMatrix(n1, n1, r0);
		stampMatrix(n2, n2, r0);
		stampMatrix(n1, n2, -r0);
		stampMatrix(n2, n1, -r0);
	}

	// current from cn1 to cn2 is equal to voltage from vn1 to 2, divided by g
	public void stampVCCurrentSource(int cn1, int cn2, int vn1, int vn2, double g) {
		stampMatrix(cn1, vn1, g);
		stampMatrix(cn2, vn2, g);
		stampMatrix(cn1, vn2, -g);
		stampMatrix(cn2, vn1, -g);
	}

	public void stampCurrentSource(int n1, int n2, double i) {
		stampRightSide(n1, -i);
		stampRightSide(n2, i);
	}

	// stamp a current source from n1 to n2 depending on current through vs
	public void stampCCCS(int n1, int n2, int vs, double gain) {
		int vn = getNodeList().size() + vs;
		stampMatrix(n1, vn, gain);
		stampMatrix(n2, vn, -gain);
	}

	// stamp value x in row i, column j, meaning that a voltage change
	// of dv in node j will increase the current into node i by x dv.
	// (Unless i or j is a voltage source node.)
	public void stampMatrix(int i, int j, double x) {
		if (i > 0 && j > 0) {
			if (circuitNeedsMap) {
				i = circuitRowInfo[i - 1].getMapRow();
				RowInfo ri = circuitRowInfo[j - 1];
				if (ri.getType() == RowInfo.ROW_CONST) {
					// System.out.println("Stamping constant " + i + " " + j + " " + x);
					circuitRightSide[i] -= x * ri.getValue();
					return;
				}
				j = ri.getMapCol();
				// System.out.println("stamping " + i + " " + j + " " + x);
			} else {
				i--;
				j--;
			}
			System.out.print("circuitMatrix[" + i + "][" + j + "]: " + circuitMatrix[i][j] + " + x = " + x + " --> ");

			circuitMatrix[i][j] += x;

			System.out.println("circuitMatrix[" + i + "][" + j + "]: " + circuitMatrix[i][j]);
		}
	}

	/**
	 * Stamp value x on the right side of row i, representing an independent current
	 * source flowing into node i
	 */
	public void stampRightSide(int i, double x) {
		if (i > 0) {
			if (circuitNeedsMap) {
				i = circuitRowInfo[i - 1].getMapRow();
				// System.out.println("stamping " + i + " " + x);
			} else
				i--;

			System.out.print("circuitRightSide[" + i + "]: " + circuitRightSide[i] + " + x = " + x + " --> ");

			circuitRightSide[i] += x;

			System.out.println("circuitRightSide[" + i + "]: " + circuitRightSide[i]);
		}
	}

	/**
	 * Indicate that the value on the right side of row i changes in doStep()
	 * 
	 * @param i
	 */
	public void stampRightSide(int i) {
		// System.out.println("rschanges true " + (i-1));

		if (i > 0) {
			System.out.print("circuitRowInfo[" + (i - 1) + "]: " + circuitRowInfo[i - 1] + " --> ");
			circuitRowInfo[i - 1].setRsChanges(true);
			System.out.println("circuitRowInfo[" + (i - 1) + "]: " + circuitRowInfo[i - 1]);
		}

	}

	// indicate that the values on the left side of row i change in doStep()
	public void stampNonLinear(int i) {
		if (i > 0)
			circuitRowInfo[i - 1].setLsChanges(true);
	}

	double getIterCount() {
		if (speedBar.getValue() == 0)
			return 0;
		// return (Math.exp((speedBar.getValue()-1)/24.) + .5);
		return .1 * Math.exp((speedBar.getValue() - 61) / 24.);
	}

	private boolean converged;
	private int subIterations;

	public void runCircuit() {
		if (circuitMatrix == null || getElmList().size() == 0) {
			circuitMatrix = null;
			return;
		}
		// int maxIter = getIterCount();
		boolean debugprint = dumpMatrix;
		dumpMatrix = false;
		long steprate = (long) (160 * getIterCount());
		long tm = System.currentTimeMillis();
		long lit = lastIterTime;
		if (1000 >= steprate * (tm - lastIterTime))
			return;
		for (int iter = 1;; iter++) {
			boolean continueSimulation = loopAndContinue(debugprint);
			lit = System.currentTimeMillis();
			if (iter * 1000 >= steprate * (lit - lastIterTime) || (lit - lastFrameTime > 500))
				break;
			if (!continueSimulation)
				break;
		}
		lastIterTime = lit;
		// System.out.println((System.currentTimeMillis()-lastFrameTime)/(double) iter);
	}

	// do a step and continue if true
	public boolean loopAndContinue(boolean debugprint) {
		int i, j, k, subiter;

		System.out.println("loopAndContinue - 1st step: start iteration");
		for (i = 0; i != getElmList().size(); i++) {
			CircuitElm ce = getElm(i);
			ce.startIteration();
		}

		steps++;
		final int subiterCount = 5000;
		System.out.println("loopAndContinue - 2nd step: doStep");
		for (subiter = 0; subiter != subiterCount; subiter++) {
			setConverged(true);
			setSubIterations(subiter);

			for (i = 0; i != circuitMatrixSize; i++)
				circuitRightSide[i] = origRightSide[i];

			if (circuitNonLinear) {
				for (i = 0; i != circuitMatrixSize; i++)
					for (j = 0; j != circuitMatrixSize; j++)
						circuitMatrix[i][j] = origMatrix[i][j];
			}

			for (i = 0; i != getElmList().size(); i++) {
				CircuitElm ce = getElm(i);
				ce.doStep();
			}

			if (stopMessage != null)
				return false;

			boolean printit = debugprint;
			debugprint = false;

			// check circuitMatrix for invalid values
			for (j = 0; j != circuitMatrixSize; j++) {
				for (i = 0; i != circuitMatrixSize; i++) {
					double x = circuitMatrix[i][j];
					if (Double.isNaN(x) || Double.isInfinite(x)) {
						stop("nan/infinite matrix!", null);
						return false;
					}
				}
			}

			// print circuitMatrix | circuitRightSide
			if (printit) {
				for (j = 0; j != circuitMatrixSize; j++) {
					for (i = 0; i != circuitMatrixSize; i++)
						System.out.print(circuitMatrix[j][i] + ",");
					System.out.print("  " + circuitRightSide[j] + "\n");
				}
				System.out.print("\n");
			}

			if (circuitNonLinear) {
				if (isConverged() && subiter > 0)
					break;
				if (!lu_factor(circuitMatrix, circuitMatrixSize, circuitPermute)) {
					stop("Singular matrix!", null);
					return false;
				}
			}

			System.out.println("loopAndContinue - 3rd step: lu_solve");
			System.out.println("circuitMatrix: " + Arrays.deepToString(circuitMatrix));
			System.out.println("circuitRightSide: " + Arrays.toString(circuitRightSide));

			lu_solve(circuitMatrix, circuitMatrixSize, circuitPermute, circuitRightSide);

			System.out.println("circuitMatrix: " + Arrays.deepToString(circuitMatrix));
			System.out.println("circuitRightSide: " + Arrays.toString(circuitRightSide));

			for (j = 0; j != circuitMatrixFullSize; j++) {
				RowInfo ri = circuitRowInfo[j];
				double res = 0;
				if (ri.getType() == RowInfo.ROW_CONST)
					res = ri.getValue();
				else
					res = circuitRightSide[ri.getMapCol()];

				System.out.println("\nj = " + j + ", res = " + res + ", type = " + ri.getType() + ", mapCol = " + ri.getMapCol());

				if (Double.isNaN(res)) {
					setConverged(false);
					// debugprint = true;
					break;
				}
				
				if (j < getNodeList().size() - 1) {
					CircuitNode cn = getCircuitNode(j + 1);
					for (k = 0; k != cn.getLinks().size(); k++) {
						CircuitNodeLink cnl = cn.getLinks().elementAt(k);
						cnl.getElm().setNodeVoltage(cnl.getNum(), res);
					}
				} else {
					int ji = j - (getNodeList().size() - 1);
					// System.out.println("setting vsrc " + ji + " to " + res);
					voltageSources[ji].setCurrent(ji, res);
				}
			}

			if (!circuitNonLinear)
				break;
		}
		
		if (subiter > 5)
			System.out.print("converged after " + subiter + " iterations\n");
		if (subiter == subiterCount) {
			stop("Convergence failed!", null);
			// break;
			return false;
		}
		
		System.out.println("\nt = " + getT() + ", timeStep = " + getTimeStep());
		setT(getT() + getTimeStep());
		System.out.println("t = " + getT() + ", timeStep = " + getTimeStep());
		
		for (i = 0; i != scopeCount; i++)
			scopes[i].timeStep();
		// continue
		return true;
	}

	int min(int a, int b) {
		return (a < b) ? a : b;
	}

	int max(int a, int b) {
		return (a > b) ? a : b;
	}

	void editFuncPoint(int x, int y) {
		// XXX
		getCv().repaint(pause);
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentShown(ComponentEvent e) {
		getCv().repaint();
	}

	@Override
	public void componentResized(ComponentEvent e) {
		handleResize();
		getCv().repaint(100);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String ac = e.getActionCommand();
		if (e.getSource() == resetButton) {
			int i;

			// on IE, drawImage() stops working inexplicably every once in
			// a while. Recreating it fixes the problem, so we do that here.
			dbimage = getMain().createImage(getWinSize().width, getWinSize().height);

			for (i = 0; i != getElmList().size(); i++)
				getElm(i).reset();
			for (i = 0; i != scopeCount; i++)
				scopes[i].resetGraph();
			setAnalyzeFlag(true);
			setT(0);
			getStoppedCheck().setState(false);
			getCv().repaint();
		}
		if (e.getSource() == dumpMatrixButton)
			dumpMatrix = true;
		if (e.getSource() == exportItem)
			doExport(false);
		if (e.getSource() == optionsItem)
			doEdit(new EditOptions(this));
		if (e.getSource() == importItem)
			doImport();
		if (e.getSource() == exportLinkItem)
			doExport(true);
		if (e.getSource() == undoItem)
			doUndo();
		if (e.getSource() == redoItem)
			doRedo();
		if (ac.compareTo("Cut") == 0) {
			if (e.getSource() != elmCutMenuItem)
				menuElm = null;
			doCut();
		}
		if (ac.compareTo("Copy") == 0) {
			if (e.getSource() != elmCopyMenuItem)
				menuElm = null;
			doCopy();
		}
		if (ac.compareTo("Paste") == 0)
			doPaste();
		if (e.getSource() == selectAllItem)
			doSelectAll();
		if (e.getSource() == exitItem) {
			destroyFrame();
			return;
		}
		if (ac.compareTo("stackAll") == 0)
			stackAll();
		if (ac.compareTo("unstackAll") == 0)
			unstackAll();
		if (e.getSource() == elmEditMenuItem)
			doEdit(menuElm);
		if (ac.compareTo("Delete") == 0) {
			if (e.getSource() != elmDeleteMenuItem)
				menuElm = null;
			doDelete();
		}
		if (e.getSource() == elmScopeMenuItem && menuElm != null) {
			int i;
			for (i = 0; i != scopeCount; i++)
				if (scopes[i].getElm() == null)
					break;
			if (i == scopeCount) {
				if (scopeCount == scopes.length)
					return;
				scopeCount++;
				scopes[i] = new Scope(this);
				scopes[i].setPosition(i);
				handleResize();
			}
			scopes[i].setElm(menuElm);
		}
		if (menuScope != -1) {
			if (ac.compareTo("remove") == 0)
				scopes[menuScope].setElm(null);
			if (ac.compareTo("speed2") == 0)
				scopes[menuScope].speedUp();
			if (ac.compareTo("speed1/2") == 0)
				scopes[menuScope].slowDown();
			if (ac.compareTo("scale") == 0)
				scopes[menuScope].adjustScale(.5);
			if (ac.compareTo("maxscale") == 0)
				scopes[menuScope].adjustScale(1e-50);
			if (ac.compareTo("stack") == 0)
				stackScope(menuScope);
			if (ac.compareTo("unstack") == 0)
				unstackScope(menuScope);
			if (ac.compareTo("selecty") == 0)
				scopes[menuScope].selectY();
			if (ac.compareTo("reset") == 0)
				scopes[menuScope].resetGraph();
			getCv().repaint();
		}
		if (ac.indexOf("setup ") == 0) {
			pushUndo();
			readSetupFile(ac.substring(6), ((MenuItem) e.getSource()).getLabel());
		}
	}

	void stackScope(int s) {
		if (s == 0) {
			if (scopeCount < 2)
				return;
			s = 1;
		}
		if (scopes[s].getPosition() == scopes[s - 1].getPosition())
			return;
		scopes[s].setPosition(scopes[s - 1].getPosition());
		for (s++; s < scopeCount; s++)
			scopes[s].setPosition(scopes[s].getPosition() - 1);
	}

	void unstackScope(int s) {
		if (s == 0) {
			if (scopeCount < 2)
				return;
			s = 1;
		}
		if (scopes[s].getPosition() != scopes[s - 1].getPosition())
			return;
		for (; s < scopeCount; s++)
			scopes[s].setPosition(scopes[s].getPosition() + 1);
	}

	void stackAll() {
		int i;
		for (i = 0; i != scopeCount; i++) {
			scopes[i].setPosition(0);
			scopes[i].setShowMax(scopes[i].setShowMin(false));
		}
	}

	void unstackAll() {
		int i;
		for (i = 0; i != scopeCount; i++) {
			scopes[i].setPosition(i);
			scopes[i].setShowMax(true);
		}
	}

	void doEdit(Editable eable) {
		clearSelection();
		pushUndo();
		if (getEditDialog() != null) {
			requestFocus();
			getEditDialog().setVisible(false);
			setEditDialog(null);
		}
		setEditDialog(new EditDialog(eable, this));
		getEditDialog().show();
	}

	void doImport() {
		if (getImpDialog() == null)
			setImpDialog(ImportExportDialogFactory.Create(this, ImportExportDialog.Action.IMPORT));
//	    impDialog = new ImportExportClipboardDialog(this,
//		ImportExportDialog.Action.IMPORT);
		pushUndo();
		getImpDialog().execute();
	}

	void doExport(boolean url) {
		String dump = dumpCircuit();
		if (url)
			dump = baseURL + "#" + URLEncoder.encode(dump);
		if (expDialog == null) {
			expDialog = ImportExportDialogFactory.Create(this, ImportExportDialog.Action.EXPORT);
//	    expDialog = new ImportExportClipboardDialog(this,
//		 ImportExportDialog.Action.EXPORT);
		}
		expDialog.setDump(dump);
		expDialog.execute();
	}

	String dumpCircuit() {
		int i;
		int f = (getDotsCheckItem().getState()) ? 1 : 0;
		f |= (getSmallGridCheckItem().getState()) ? 2 : 0;
		f |= (getVoltsCheckItem().getState()) ? 0 : 4;
		f |= (getPowerCheckItem().getState()) ? 8 : 0;
		f |= (getShowValuesCheckItem().getState()) ? 0 : 16;
		// 32 = linear scale in afilter
		String dump = "$ " + f + " " + getTimeStep() + " " + getIterCount() + " " + currentBar.getValue() + " "
				+ CircuitElm.getVoltageRange() + " " + powerBar.getValue() + "\n";
		for (i = 0; i != getElmList().size(); i++)
			dump += getElm(i).dump() + "\n";
		for (i = 0; i != scopeCount; i++) {
			String d = scopes[i].dump();
			if (d != null)
				dump += d + "\n";
		}
		if (hintType != -1)
			dump += "h " + hintType + " " + hintItem1 + " " + hintItem2 + "\n";
		return dump;
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		System.out.print(((Scrollbar) e.getSource()).getValue() + "\n");
	}

	ByteArrayOutputStream readUrlData(URL url) throws java.io.IOException {
		Object o = url.getContent();
		FilterInputStream fis = (FilterInputStream) o;
		ByteArrayOutputStream ba = new ByteArrayOutputStream(fis.available());
		int blen = 1024;
		byte b[] = new byte[blen];
		while (true) {
			int len = fis.read(b);
			if (len <= 0)
				break;
			ba.write(b, 0, len);
		}
		return ba;
	}

	URL getCodeBase() {
		try {
			if (getApplet() != null)
				return getApplet().getCodeBase();
			File f = new File(".");
			return new URL("file:" + f.getCanonicalPath() + "/");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	void getSetupList(Menu menu, boolean retry) {
		Menu stack[] = new Menu[6];
		int stackptr = 0;
		stack[stackptr++] = menu;
		try {
			// hausen: if setuplist.txt does not exist in the same
			// directory, try reading from the jar file
			ByteArrayOutputStream ba = null;
			try {
				URL url = new URL(getCodeBase() + "setuplist.txt");
				ba = readUrlData(url);
			} catch (Exception e) {
				URL url = getClass().getClassLoader().getResource("setuplist.txt");
				ba = readUrlData(url);
			}
			// /hausen

			byte b[] = ba.toByteArray();
			int len = ba.size();
			int p;
			if (len == 0 || b[0] != '#') {
				// got a redirect, try again
				getSetupList(menu, true);
				return;
			}
			for (p = 0; p < len;) {
				int l;
				for (l = 0; l != len - p; l++)
					if (b[l + p] == '\n') {
						l++;
						break;
					}
				String line = new String(b, p, l - 1);
				if (line.charAt(0) == '#')
					;
				else if (line.charAt(0) == '+') {
					Menu n = new Menu(line.substring(1));
					menu.add(n);
					menu = stack[stackptr++] = n;
				} else if (line.charAt(0) == '-') {
					menu = stack[--stackptr - 1];
				} else {
					int i = line.indexOf(' ');
					if (i > 0) {
						String title = line.substring(i + 1);
						boolean first = false;
						if (line.charAt(0) == '>')
							first = true;
						String file = line.substring(first ? 1 : 0, i);
						menu.add(getMenuItem(title, "setup " + file));
						if (first && startCircuit == null) {
							startCircuit = file;
							startLabel = title;
						}
					}
				}
				p += l;
			}
		} catch (Exception e) {
			e.printStackTrace();
			stop("Can't read setuplist.txt!", null);
		}
	}

	public void readSetup(String text) {
		readSetup(text, false);
	}

	void readSetup(String text, boolean retain) {
		readSetup(text.getBytes(), text.length(), retain);
		titleLabel.setText("untitled");
	}

	void readSetupFile(String str, String title) {
		setT(0);
		System.out.println(str);
		try {
			URL url = new URL(getCodeBase() + "circuits/" + str);
			ByteArrayOutputStream ba = readUrlData(url);
			readSetup(ba.toByteArray(), ba.size(), false);
		} catch (Exception e1) {
			try {
				URL url = getClass().getClassLoader().getResource("circuits/" + str);
				ByteArrayOutputStream ba = readUrlData(url);
				readSetup(ba.toByteArray(), ba.size(), false);
			} catch (Exception e) {
				e.printStackTrace();
				stop("Unable to read " + str + "!", null);
			}
		}
		titleLabel.setText(title);
	}

	void readSetup(byte b[], int len, boolean retain) {
		int i;
		if (!retain) {
			for (i = 0; i != getElmList().size(); i++) {
				CircuitElm ce = getElm(i);
				ce.delete();
			}
			getElmList().clear();
			hintType = -1;
			setTimeStep(5e-6);
			getDotsCheckItem().setState(false); // hausen: changed from true to false
			getSmallGridCheckItem().setState(false);
			getPowerCheckItem().setState(false);
			getVoltsCheckItem().setState(true);
			getShowValuesCheckItem().setState(true);
			setGrid();
			speedBar.setValue(117); // 57
			currentBar.setValue(50);
			powerBar.setValue(50);
			CircuitElm.setVoltageRange(5);
			scopeCount = 0;
		}
		getCv().repaint();
		int p;
		for (p = 0; p < len;) {
			int l;
			int linelen = 0;
			for (l = 0; l != len - p; l++)
				if (b[l + p] == '\n' || b[l + p] == '\r') {
					linelen = l++;
					if (l + p < b.length && b[l + p] == '\n')
						l++;
					break;
				}
			String line = new String(b, p, linelen);
			StringTokenizer st = new StringTokenizer(line);
			while (st.hasMoreTokens()) {
				String type = st.nextToken();
				int tint = type.charAt(0);
				try {
					if (tint == 'o') {
						Scope sc = new Scope(this);
						sc.setPosition(scopeCount);
						sc.undump(st);
						scopes[scopeCount++] = sc;
						break;
					}
					if (tint == 'h') {
						readHint(st);
						break;
					}
					if (tint == '$') {
						readOptions(st);
						break;
					}
					if (tint == '%' || tint == '?' || tint == 'B') {
						// ignore afilter-specific stuff
						break;
					}
					if (tint >= '0' && tint <= '9')
						tint = new Integer(type).intValue();
					int x1 = new Integer(st.nextToken()).intValue();
					int y1 = new Integer(st.nextToken()).intValue();
					int x2 = new Integer(st.nextToken()).intValue();
					int y2 = new Integer(st.nextToken()).intValue();
					int f = new Integer(st.nextToken()).intValue();
					CircuitElm ce = null;
					Class cls = dumpTypes[tint];
					if (cls == null) {
						System.out.println("unrecognized dump type: " + type);
						break;
					}
					// find element class
					Class carr[] = new Class[6];
					// carr[0] = getClass();
					carr[0] = carr[1] = carr[2] = carr[3] = carr[4] = int.class;
					carr[5] = StringTokenizer.class;
					Constructor cstr = null;
					cstr = cls.getConstructor(carr);

					// invoke constructor with starting coordinates
					Object oarr[] = new Object[6];
					// oarr[0] = this;
					oarr[0] = new Integer(x1);
					oarr[1] = new Integer(y1);
					oarr[2] = new Integer(x2);
					oarr[3] = new Integer(y2);
					oarr[4] = new Integer(f);
					oarr[5] = st;
					ce = (CircuitElm) cstr.newInstance(oarr);
					ce.setPoints();
					getElmList().add(ce);
				} catch (java.lang.reflect.InvocationTargetException ee) {
					ee.getTargetException().printStackTrace();
					break;
				} catch (Exception ee) {
					ee.printStackTrace();
					break;
				}
				break;
			}
			p += l;

		}
		enableItems();
		if (!retain)
			handleResize(); // for scopes
		needAnalyze();
	}

	void readHint(StringTokenizer st) {
		hintType = new Integer(st.nextToken()).intValue();
		hintItem1 = new Integer(st.nextToken()).intValue();
		hintItem2 = new Integer(st.nextToken()).intValue();
	}

	void readOptions(StringTokenizer st) {
		int flags = new Integer(st.nextToken()).intValue();
		getDotsCheckItem().setState((flags & 1) != 0);
		getSmallGridCheckItem().setState((flags & 2) != 0);
		getVoltsCheckItem().setState((flags & 4) == 0);
		getPowerCheckItem().setState((flags & 8) == 8);
		getShowValuesCheckItem().setState((flags & 16) == 0);
		setTimeStep(new Double(st.nextToken()).doubleValue());
		double sp = new Double(st.nextToken()).doubleValue();
		int sp2 = (int) (Math.log(10 * sp) * 24 + 61.5);
		// int sp2 = (int) (Math.log(sp)*24+1.5);
		speedBar.setValue(sp2);
		currentBar.setValue(new Integer(st.nextToken()).intValue());
		CircuitElm.setVoltageRange(new Double(st.nextToken()).doubleValue());
		try {
			powerBar.setValue(new Integer(st.nextToken()).intValue());
		} catch (Exception e) {
		}
		setGrid();
	}

	public int snapGrid(int x) {
		return (x + gridRound) & gridMask;
	}

	boolean doSwitch(int x, int y) {
		if (getMouseElm() == null || !(getMouseElm() instanceof SwitchElm))
			return false;
		SwitchElm se = (SwitchElm) getMouseElm();
		se.toggle();
		if (se.isMomentary())
			heldSwitchElm = se;
		needAnalyze();
		return true;
	}

	public int locateElm(CircuitElm elm) {
		int i;
		for (i = 0; i != getElmList().size(); i++)
			if (elm == getElmList().get(i))
				return i;
		return -1;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// ignore right mouse button with no modifiers (needed on PC)
		if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
			int ex = e.getModifiersEx();
			if ((ex & (InputEvent.META_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK
					| InputEvent.ALT_DOWN_MASK)) == 0)
				return;
		}
		if (!circuitArea.contains(e.getX(), e.getY()))
			return;
		if (getDragElm() != null)
			getDragElm().drag(e.getX(), e.getY());
		boolean success = true;
		switch (tempMouseMode) {
		case MODE_DRAG_ALL:
			dragAll(snapGrid(e.getX()), snapGrid(e.getY()));
			break;
		case MODE_DRAG_ROW:
			dragRow(snapGrid(e.getX()), snapGrid(e.getY()));
			break;
		case MODE_DRAG_COLUMN:
			dragColumn(snapGrid(e.getX()), snapGrid(e.getY()));
			break;
		case MODE_DRAG_POST:
			if (getMouseElm() != null)
				dragPost(snapGrid(e.getX()), snapGrid(e.getY()));
			break;
		case MODE_SELECT:
			if (getMouseElm() == null)
				selectArea(e.getX(), e.getY());
			else {
				tempMouseMode = MODE_DRAG_SELECTED;
				success = dragSelected(e.getX(), e.getY());
			}
			break;
		case MODE_DRAG_SELECTED:
			success = dragSelected(e.getX(), e.getY());
			break;
		}
		dragging = true;
		if (success) {
			if (tempMouseMode == MODE_DRAG_SELECTED && getMouseElm() instanceof GraphicElm) {
				dragX = e.getX();
				dragY = e.getY();
			} else {
				dragX = snapGrid(e.getX());
				dragY = snapGrid(e.getY());
			}
		}
		getCv().repaint(pause);
	}

	void dragAll(int x, int y) {
		int dx = x - dragX;
		int dy = y - dragY;
		if (dx == 0 && dy == 0)
			return;
		int i;
		for (i = 0; i != getElmList().size(); i++) {
			CircuitElm ce = getElm(i);
			ce.move(dx, dy);
		}
		removeZeroLengthElements();
	}

	void dragRow(int x, int y) {
		int dy = y - dragY;
		if (dy == 0)
			return;
		int i;
		for (i = 0; i != getElmList().size(); i++) {
			CircuitElm ce = getElm(i);
			if (ce.getY() == dragY)
				ce.movePoint(0, 0, dy);
			if (ce.getY2() == dragY)
				ce.movePoint(1, 0, dy);
		}
		removeZeroLengthElements();
	}

	void dragColumn(int x, int y) {
		int dx = x - dragX;
		if (dx == 0)
			return;
		int i;
		for (i = 0; i != getElmList().size(); i++) {
			CircuitElm ce = getElm(i);
			if (ce.getX() == dragX)
				ce.movePoint(0, dx, 0);
			if (ce.getX2() == dragX)
				ce.movePoint(1, dx, 0);
		}
		removeZeroLengthElements();
	}

	boolean dragSelected(int x, int y) {
		boolean me = false;
		if (getMouseElm() != null && !getMouseElm().isSelected())
			getMouseElm().setSelected(me = true);

		// snap grid, unless we're only dragging text elements
		int i;
		for (i = 0; i != getElmList().size(); i++) {
			CircuitElm ce = getElm(i);
			if (ce.isSelected() && !(ce instanceof GraphicElm))
				break;
		}
		if (i != getElmList().size()) {
			x = snapGrid(x);
			y = snapGrid(y);
		}

		int dx = x - dragX;
		int dy = y - dragY;
		if (dx == 0 && dy == 0) {
			// don't leave mouseElm selected if we selected it above
			if (me)
				getMouseElm().setSelected(false);
			return false;
		}
		boolean allowed = true;

		// check if moves are allowed
		for (i = 0; allowed && i != getElmList().size(); i++) {
			CircuitElm ce = getElm(i);
			if (ce.isSelected() && !ce.allowMove(dx, dy))
				allowed = false;
		}

		if (allowed) {
			for (i = 0; i != getElmList().size(); i++) {
				CircuitElm ce = getElm(i);
				if (ce.isSelected())
					ce.move(dx, dy);
			}
			needAnalyze();
		}

		// don't leave mouseElm selected if we selected it above
		if (me)
			getMouseElm().setSelected(false);

		return allowed;
	}

	void dragPost(int x, int y) {
		if (draggingPost == -1) {
			draggingPost = (distanceSq(getMouseElm().getX(), getMouseElm().getY(), x,
					y) > distanceSq(getMouseElm().getX2(), getMouseElm().getY2(), x, y)) ? 1 : 0;
		}
		int dx = x - dragX;
		int dy = y - dragY;
		if (dx == 0 && dy == 0)
			return;
		getMouseElm().movePoint(draggingPost, dx, dy);
		needAnalyze();
	}

	void selectArea(int x, int y) {
		int x1 = min(x, initDragX);
		int x2 = max(x, initDragX);
		int y1 = min(y, initDragY);
		int y2 = max(y, initDragY);
		selectedArea = new Rectangle(x1, y1, x2 - x1, y2 - y1);
		int i;
		for (i = 0; i != getElmList().size(); i++) {
			CircuitElm ce = getElm(i);
			ce.selectRect(selectedArea);
		}
	}

	void setSelectedElm(CircuitElm cs) {
		int i;
		for (i = 0; i != getElmList().size(); i++) {
			CircuitElm ce = getElm(i);
			ce.setSelected(ce == cs);
		}
		setMouseElm(cs);
	}

	void removeZeroLengthElements() {
		int i;
		boolean changed = false;
		for (i = getElmList().size() - 1; i >= 0; i--) {
			CircuitElm ce = getElm(i);
			if (ce.getX() == ce.getX2() && ce.getY() == ce.getY2()) {
				getElmList().remove(i);
				ce.delete();
				changed = true;
			}
		}
		needAnalyze();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0)
			return;
		int x = e.getX();
		int y = e.getY();
		dragX = snapGrid(x);
		dragY = snapGrid(y);
		draggingPost = -1;
		int i;
		CircuitElm origMouse = getMouseElm();
		setMouseElm(null);
		mousePost = -1;
		setPlotXElm(setPlotYElm(null));
		int bestDist = 100000;
		int bestArea = 100000;
		for (i = 0; i != getElmList().size(); i++) {
			CircuitElm ce = getElm(i);
			if (ce.getBoundingBox().contains(x, y)) {
				int j;
				int area = ce.getBoundingBox().width * ce.getBoundingBox().height;
				int jn = ce.getPostCount();
				if (jn > 2)
					jn = 2;
				for (j = 0; j != jn; j++) {
					Point pt = ce.getPost(j);
					int dist = distanceSq(x, y, pt.x, pt.y);

					// if multiple elements have overlapping bounding boxes,
					// we prefer selecting elements that have posts close
					// to the mouse pointer and that have a small bounding
					// box area.
					if (dist <= bestDist && area <= bestArea) {
						bestDist = dist;
						bestArea = area;
						setMouseElm(ce);
					}
				}
				if (ce.getPostCount() == 0)
					setMouseElm(ce);
			}
		}
		setScopeSelected(-1);
		if (getMouseElm() == null) {
			for (i = 0; i != scopeCount; i++) {
				Scope s = scopes[i];
				if (s.getRect().contains(x, y)) {
					s.select();
					setScopeSelected(i);
				}
			}
			// the mouse pointer was not in any of the bounding boxes, but we
			// might still be close to a post
			for (i = 0; i != getElmList().size(); i++) {
				CircuitElm ce = getElm(i);
				int j;
				int jn = ce.getPostCount();
				for (j = 0; j != jn; j++) {
					Point pt = ce.getPost(j);
					int dist = distanceSq(x, y, pt.x, pt.y);
					if (distanceSq(pt.x, pt.y, x, y) < 26) {
						setMouseElm(ce);
						mousePost = j;
						break;
					}
				}
			}
		} else {
			mousePost = -1;
			// look for post close to the mouse pointer
			for (i = 0; i != getMouseElm().getPostCount(); i++) {
				Point pt = getMouseElm().getPost(i);
				if (distanceSq(pt.x, pt.y, x, y) < 26)
					mousePost = i;
			}
		}
		if (getMouseElm() != origMouse)
			getCv().repaint();
	}

	int distanceSq(int x1, int y1, int x2, int y2) {
		x2 -= x1;
		y2 -= y1;
		return x2 * x2 + y2 * y2;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && !didSwitch)
			doEditMenu(e);
		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
			if (getMouseMode() == MODE_SELECT || getMouseMode() == MODE_DRAG_SELECTED)
				clearSelection();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
		setScopeSelected(-1);
		setMouseElm(setPlotXElm(setPlotYElm(null)));
		getCv().repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		didSwitch = false;

		System.out.println(e.getModifiers());
		int ex = e.getModifiersEx();
		if ((ex & (InputEvent.META_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)) == 0 && e.isPopupTrigger()) {
			doPopupMenu(e);
			return;
		}
		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) != 0) {
			// left mouse
			tempMouseMode = getMouseMode();
			if ((ex & InputEvent.ALT_DOWN_MASK) != 0 && (ex & InputEvent.META_DOWN_MASK) != 0)
				tempMouseMode = MODE_DRAG_COLUMN;
			else if ((ex & InputEvent.ALT_DOWN_MASK) != 0 && (ex & InputEvent.SHIFT_DOWN_MASK) != 0)
				tempMouseMode = MODE_DRAG_ROW;
			else if ((ex & InputEvent.SHIFT_DOWN_MASK) != 0)
				tempMouseMode = MODE_SELECT;
			else if ((ex & InputEvent.ALT_DOWN_MASK) != 0)
				tempMouseMode = MODE_DRAG_ALL;
			else if ((ex & (InputEvent.CTRL_DOWN_MASK | InputEvent.META_DOWN_MASK)) != 0)
				tempMouseMode = MODE_DRAG_POST;
		} else if ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) {
			// right mouse
			if ((ex & InputEvent.SHIFT_DOWN_MASK) != 0)
				tempMouseMode = MODE_DRAG_ROW;
			else if ((ex & (InputEvent.CTRL_DOWN_MASK | InputEvent.META_DOWN_MASK)) != 0)
				tempMouseMode = MODE_DRAG_COLUMN;
			else
				return;
		}

		if (tempMouseMode != MODE_SELECT && tempMouseMode != MODE_DRAG_SELECTED)
			clearSelection();
		if (getMouseMode() == MODE_SELECT && doSwitch(e.getX(), e.getY())) {
			didSwitch = true;
			return;
		}

		pushUndo();
		initDragX = e.getX();
		initDragY = e.getY();
		dragging = true;
		if (tempMouseMode != MODE_ADD_ELM || addingClass == null)
			return;

		int x0 = snapGrid(e.getX());
		int y0 = snapGrid(e.getY());
		if (!circuitArea.contains(x0, y0))
			return;

		setDragElm(constructElement(addingClass, x0, y0));
	}

	CircuitElm constructElement(Class c, int x0, int y0) {
		// find element class
		Class carr[] = new Class[2];
		// carr[0] = getClass();
		carr[0] = carr[1] = int.class;
		Constructor cstr = null;
		try {
			cstr = c.getConstructor(carr);
		} catch (NoSuchMethodException ee) {
			System.out.println("caught NoSuchMethodException " + c);
			return null;
		} catch (Exception ee) {
			ee.printStackTrace();
			return null;
		}

		// invoke constructor with starting coordinates
		Object oarr[] = new Object[2];
		oarr[0] = new Integer(x0);
		oarr[1] = new Integer(y0);
		try {
			return (CircuitElm) cstr.newInstance(oarr);
		} catch (Exception ee) {
			ee.printStackTrace();
		}
		return null;
	}

	// hausen: add doEditMenu
	void doEditMenu(MouseEvent e) {
		if (getMouseElm() != null)
			doEdit(getMouseElm());
	}

	void doPopupMenu(MouseEvent e) {
		menuElm = getMouseElm();
		menuScope = -1;
		if (getScopeSelected() != -1) {
			PopupMenu m = scopes[getScopeSelected()].getMenu();
			menuScope = getScopeSelected();
			if (m != null)
				m.show(e.getComponent(), e.getX(), e.getY());
		} else if (getMouseElm() != null) {
			elmEditMenuItem.setEnabled(getMouseElm().getEditInfo(0) != null);
			elmScopeMenuItem.setEnabled(getMouseElm().canViewInScope());
			elmMenu.show(e.getComponent(), e.getX(), e.getY());
		} else {
			doMainMenuChecks(mainMenu);
			mainMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	void doMainMenuChecks(Menu m) {
		int i;
		if (m == optionsMenu)
			return;
		for (i = 0; i != m.getItemCount(); i++) {
			MenuItem mc = m.getItem(i);
			if (mc instanceof Menu)
				doMainMenuChecks((Menu) mc);
			if (mc instanceof CheckboxMenuItem) {
				CheckboxMenuItem cmi = (CheckboxMenuItem) mc;
				cmi.setState(mouseModeStr.compareTo(cmi.getActionCommand()) == 0);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int ex = e.getModifiersEx();
		if ((ex & (InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK | InputEvent.META_DOWN_MASK)) == 0
				&& e.isPopupTrigger()) {
			doPopupMenu(e);
			return;
		}
		tempMouseMode = getMouseMode();
		selectedArea = null;
		dragging = false;
		boolean circuitChanged = false;
		if (heldSwitchElm != null) {
			heldSwitchElm.mouseUp();
			heldSwitchElm = null;
			circuitChanged = true;
		}
		if (getDragElm() != null) {
			// if the element is zero size then don't create it
			if (getDragElm().getX() == getDragElm().getX2() && getDragElm().getY() == getDragElm().getY2())
				getDragElm().delete();
			else {
				getElmList().add(getDragElm());
				circuitChanged = true;
			}
			setDragElm(null);
		}
		if (circuitChanged)
			needAnalyze();
		if (getDragElm() != null)
			getDragElm().delete();
		setDragElm(null);
		getCv().repaint();
	}

	void enableItems() {
		if (getPowerCheckItem().getState()) {
			powerBar.enable();
			powerLabel.enable();
		} else {
			powerBar.disable();
			powerLabel.disable();
		}
		enableUndoRedo();
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		getCv().repaint(pause);
		Object mi = e.getItemSelectable();
		if (mi == getStoppedCheck())
			return;
		if (mi == getSmallGridCheckItem())
			setGrid();
		if (mi == getPowerCheckItem()) {
			if (getPowerCheckItem().getState())
				getVoltsCheckItem().setState(false);
			else
				getVoltsCheckItem().setState(true);
		}
		if (mi == getVoltsCheckItem() && getVoltsCheckItem().getState())
			getPowerCheckItem().setState(false);
		enableItems();
		if (menuScope != -1) {
			Scope sc = scopes[menuScope];
			sc.handleMenu(e, mi);
		}
		if (mi instanceof CheckboxMenuItem) {
			MenuItem mmi = (MenuItem) mi;
			int prevMouseMode = getMouseMode();
			setMouseMode(MODE_ADD_ELM);
			String s = mmi.getActionCommand();
			if (s.length() > 0)
				mouseModeStr = s;
			if (s.compareTo("DragAll") == 0)
				setMouseMode(MODE_DRAG_ALL);
			else if (s.compareTo("DragRow") == 0)
				setMouseMode(MODE_DRAG_ROW);
			else if (s.compareTo("DragColumn") == 0)
				setMouseMode(MODE_DRAG_COLUMN);
			else if (s.compareTo("DragSelected") == 0)
				setMouseMode(MODE_DRAG_SELECTED);
			else if (s.compareTo("DragPost") == 0)
				setMouseMode(MODE_DRAG_POST);
			else if (s.compareTo("Select") == 0)
				setMouseMode(MODE_SELECT);
			else if (s.length() > 0) {
				try {
					addingClass = Class.forName(s);
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			} else
				setMouseMode(prevMouseMode);
			tempMouseMode = getMouseMode();
		}
	}

	void setGrid() {
		setGridSize((getSmallGridCheckItem().getState()) ? 8 : 16);
		gridMask = ~(getGridSize() - 1);
		gridRound = getGridSize() / 2 - 1;
	}

	void pushUndo() {
		redoStack.removeAllElements();
		String s = dumpCircuit();
		if (undoStack.size() > 0 && s.compareTo(undoStack.lastElement()) == 0)
			return;
		undoStack.add(s);
		enableUndoRedo();
	}

	void doUndo() {
		if (undoStack.size() == 0)
			return;
		redoStack.add(dumpCircuit());
		String s = undoStack.remove(undoStack.size() - 1);
		readSetup(s);
		enableUndoRedo();
	}

	void doRedo() {
		if (redoStack.size() == 0)
			return;
		undoStack.add(dumpCircuit());
		String s = redoStack.remove(redoStack.size() - 1);
		readSetup(s);
		enableUndoRedo();
	}

	void enableUndoRedo() {
		redoItem.setEnabled(redoStack.size() > 0);
		undoItem.setEnabled(undoStack.size() > 0);
	}

	void setMouseMode(int mode) {
		mouseMode = mode;
		if (mode == MODE_ADD_ELM)
			getCv().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		else
			getCv().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	void setMenuSelection() {
		if (menuElm != null) {
			if (menuElm.selected)
				return;
			clearSelection();
			menuElm.setSelected(true);
		}
	}

	void doCut() {
		int i;
		pushUndo();
		setMenuSelection();
		clipboard = "";
		for (i = getElmList().size() - 1; i >= 0; i--) {
			CircuitElm ce = getElm(i);
			if (ce.isSelected()) {
				clipboard += ce.dump() + "\n";
				ce.delete();
				getElmList().remove(i);
			}
		}
		enablePaste();
		needAnalyze();
	}

	void doDelete() {
		int i;
		pushUndo();
		setMenuSelection();
		boolean hasDeleted = false;

		for (i = getElmList().size() - 1; i >= 0; i--) {
			CircuitElm ce = getElm(i);
			if (ce.isSelected()) {
				ce.delete();
				getElmList().remove(i);
				hasDeleted = true;
			}
		}

		if (!hasDeleted) {
			for (i = getElmList().size() - 1; i >= 0; i--) {
				CircuitElm ce = getElm(i);
				if (ce == getMouseElm()) {
					ce.delete();
					getElmList().remove(i);
					hasDeleted = true;
					setMouseElm(null);
					break;
				}
			}
		}

		if (hasDeleted)
			needAnalyze();
	}

	void doCopy() {
		int i;
		clipboard = "";
		setMenuSelection();
		for (i = getElmList().size() - 1; i >= 0; i--) {
			CircuitElm ce = getElm(i);
			if (ce.isSelected())
				clipboard += ce.dump() + "\n";
		}
		enablePaste();
	}

	void enablePaste() {
		pasteItem.setEnabled(clipboard.length() > 0);
	}

	void doPaste() {
		pushUndo();
		clearSelection();
		int i;
		Rectangle oldbb = null;
		for (i = 0; i != getElmList().size(); i++) {
			CircuitElm ce = getElm(i);
			Rectangle bb = ce.getBoundingBox();
			if (oldbb != null)
				oldbb = oldbb.union(bb);
			else
				oldbb = bb;
		}
		int oldsz = getElmList().size();
		readSetup(clipboard, true);

		// select new items
		Rectangle newbb = null;
		for (i = oldsz; i != getElmList().size(); i++) {
			CircuitElm ce = getElm(i);
			ce.setSelected(true);
			Rectangle bb = ce.getBoundingBox();
			if (newbb != null)
				newbb = newbb.union(bb);
			else
				newbb = bb;
		}
		if (oldbb != null && newbb != null && oldbb.intersects(newbb)) {
			// find a place for new items
			int dx = 0, dy = 0;
			int spacew = circuitArea.width - oldbb.width - newbb.width;
			int spaceh = circuitArea.height - oldbb.height - newbb.height;
			if (spacew > spaceh)
				dx = snapGrid(oldbb.x + oldbb.width - newbb.x + getGridSize());
			else
				dy = snapGrid(oldbb.y + oldbb.height - newbb.y + getGridSize());
			for (i = oldsz; i != getElmList().size(); i++) {
				CircuitElm ce = getElm(i);
				ce.move(dx, dy);
			}
			// center circuit
			handleResize();
		}
		needAnalyze();
	}

	void clearSelection() {
		int i;
		for (i = 0; i != getElmList().size(); i++) {
			CircuitElm ce = getElm(i);
			ce.setSelected(false);
		}
	}

	void doSelectAll() {
		int i;
		for (i = 0; i != getElmList().size(); i++) {
			CircuitElm ce = getElm(i);
			ce.setSelected(true);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == 127) {
			doDelete();
			return;
		}
		if (e.getKeyChar() > ' ' && e.getKeyChar() < 127) {
			Class c = shortcuts[e.getKeyChar()];
			if (c == null)
				return;
			CircuitElm elm = null;
			elm = constructElement(c, 0, 0);
			if (elm == null)
				return;
			setMouseMode(MODE_ADD_ELM);
			mouseModeStr = c.getName();
			addingClass = c;
		}
		if (e.getKeyChar() == ' ' || e.getKeyChar() == KeyEvent.VK_ESCAPE) {
			setMouseMode(MODE_SELECT);
			mouseModeStr = "Select";
		}
		tempMouseMode = getMouseMode();
	}

	// factors a matrix into upper and lower triangular matrices by
	// gaussian elimination. On entry, a[0..n-1][0..n-1] is the
	// matrix to be factored. ipvt[] returns an integer vector of pivot
	// indices, used in the lu_solve() routine.
	boolean lu_factor(double a[][], int n, int ipvt[]) {
		double scaleFactors[];
		int i, j, k;

		scaleFactors = new double[n];

		// divide each row by its largest element, keeping track of the
		// scaling factors
		for (i = 0; i != n; i++) {
			double largest = 0;
			for (j = 0; j != n; j++) {
				double x = Math.abs(a[i][j]);
				if (x > largest)
					largest = x;
			}
			// if all zeros, it's a singular matrix
			if (largest == 0)
				return false;
			scaleFactors[i] = 1.0 / largest;
		}

		// use Crout's method; loop through the columns
		for (j = 0; j != n; j++) {

			// calculate upper triangular elements for this column
			for (i = 0; i != j; i++) {
				double q = a[i][j];
				for (k = 0; k != i; k++)
					q -= a[i][k] * a[k][j];
				a[i][j] = q;
			}

			// calculate lower triangular elements for this column
			double largest = 0;
			int largestRow = -1;
			for (i = j; i != n; i++) {
				double q = a[i][j];
				for (k = 0; k != j; k++)
					q -= a[i][k] * a[k][j];
				a[i][j] = q;
				double x = Math.abs(q);
				if (x >= largest) {
					largest = x;
					largestRow = i;
				}
			}

			// pivoting
			if (j != largestRow) {
				double x;
				for (k = 0; k != n; k++) {
					x = a[largestRow][k];
					a[largestRow][k] = a[j][k];
					a[j][k] = x;
				}
				scaleFactors[largestRow] = scaleFactors[j];
			}

			// keep track of row interchanges
			ipvt[j] = largestRow;

			// avoid zeros
			if (a[j][j] == 0.0) {
				System.out.println("avoided zero");
				a[j][j] = 1e-18;
			}

			if (j != n - 1) {
				double mult = 1.0 / a[j][j];
				for (i = j + 1; i != n; i++)
					a[i][j] *= mult;
			}
		}
		return true;
	}

	/**
	 * Solves the set of n linear equations using a LU factorization previously
	 * performed by lu_factor. On input, b[0..n-1] is the right hand side of the
	 * equations, and on output, contains the solution.
	 * 
	 * @param a
	 * @param n
	 * @param ipvt
	 * @param b
	 */
	void lu_solve(double a[][], int n, int ipvt[], double b[]) {
		int i;

		// find first nonzero b element
		for (i = 0; i != n; i++) {
			int row = ipvt[i];

			double swap = b[row];
			b[row] = b[i];
			b[i] = swap;
			if (swap != 0)
				break;
		}

		int bi = i++;
		for (; i < n; i++) {
			int row = ipvt[i];
			int j;
			double tot = b[row];

			b[row] = b[i];
			// forward substitution using the lower triangular matrix
			for (j = bi; j < i; j++)
				tot -= a[i][j] * b[j];
			b[i] = tot;
		}
		for (i = n - 1; i >= 0; i--) {
			double tot = b[i];

			// back-substitution using the upper triangular matrix
			int j;
			for (j = i + 1; j != n; j++)
				tot -= a[i][j] * b[j];
			b[i] = tot / a[i][i];
		}
	}

	public Dimension getWinSize() {
		return winSize;
	}

	public void setWinSize(Dimension winSize) {
		this.winSize = winSize;
	}

	public static Container getMain() {
		return main;
	}

	public static void setMain(Container main) {
		CirSim.main = main;
	}

	public double getTimeStep() {
		assert timeStep > 0;
		return timeStep;
	}

	public void setTimeStep(double timeStep) {
		this.timeStep = timeStep;
	}

	public CheckboxMenuItem getPrintableCheckItem() {
		return printableCheckItem;
	}

	public CheckboxMenuItem setPrintableCheckItem(CheckboxMenuItem printableCheckItem) {
		this.printableCheckItem = printableCheckItem;
		return printableCheckItem;
	}

	public int getScopeSelected() {
		return scopeSelected;
	}

	public void setScopeSelected(int scopeSelected) {
		this.scopeSelected = scopeSelected;
	}

	public CircuitElm getMouseElm() {
		return mouseElm;
	}

	public void setMouseElm(CircuitElm mouseElm) {
		this.mouseElm = mouseElm;
	}

	public double getT() {
		return t;
	}

	public void setT(double t) {
		this.t = t;
	}

	public CheckboxMenuItem getScopeIbMenuItem() {
		return scopeIbMenuItem;
	}

	public CheckboxMenuItem setScopeIbMenuItem(CheckboxMenuItem scopeIbMenuItem) {
		this.scopeIbMenuItem = scopeIbMenuItem;
		return scopeIbMenuItem;
	}

	public CheckboxMenuItem getScopeIcMenuItem() {
		return scopeIcMenuItem;
	}

	public CheckboxMenuItem setScopeIcMenuItem(CheckboxMenuItem scopeIcMenuItem) {
		this.scopeIcMenuItem = scopeIcMenuItem;
		return scopeIcMenuItem;
	}

	public CheckboxMenuItem getScopeIeMenuItem() {
		return scopeIeMenuItem;
	}

	public CheckboxMenuItem setScopeIeMenuItem(CheckboxMenuItem scopeIeMenuItem) {
		this.scopeIeMenuItem = scopeIeMenuItem;
		return scopeIeMenuItem;
	}

	public CheckboxMenuItem getScopeVbeMenuItem() {
		return scopeVbeMenuItem;
	}

	public CheckboxMenuItem setScopeVbeMenuItem(CheckboxMenuItem scopeVbeMenuItem) {
		this.scopeVbeMenuItem = scopeVbeMenuItem;
		return scopeVbeMenuItem;
	}

	public CheckboxMenuItem getScopeVbcMenuItem() {
		return scopeVbcMenuItem;
	}

	public CheckboxMenuItem setScopeVbcMenuItem(CheckboxMenuItem scopeVbcMenuItem) {
		this.scopeVbcMenuItem = scopeVbcMenuItem;
		return scopeVbcMenuItem;
	}

	public CheckboxMenuItem getScopeVceMenuItem() {
		return scopeVceMenuItem;
	}

	public CheckboxMenuItem setScopeVceMenuItem(CheckboxMenuItem scopeVceMenuItem) {
		this.scopeVceMenuItem = scopeVceMenuItem;
		return scopeVceMenuItem;
	}

	public CheckboxMenuItem getScopeVceIcMenuItem() {
		return scopeVceIcMenuItem;
	}

	public CheckboxMenuItem setScopeVceIcMenuItem(CheckboxMenuItem scopeVceIcMenuItem) {
		this.scopeVceIcMenuItem = scopeVceIcMenuItem;
		return scopeVceIcMenuItem;
	}

	public PopupMenu getTransScopeMenu() {
		return transScopeMenu;
	}

	public void setTransScopeMenu(PopupMenu transScopeMenu) {
		this.transScopeMenu = transScopeMenu;
	}

	public CircuitElm getPlotXElm() {
		return plotXElm;
	}

	public CircuitElm setPlotXElm(CircuitElm plotXElm) {
		this.plotXElm = plotXElm;
		return plotXElm;
	}

	public CircuitElm getPlotYElm() {
		return plotYElm;
	}

	public CircuitElm setPlotYElm(CircuitElm plotYElm) {
		this.plotYElm = plotYElm;
		return plotYElm;
	}

	public CheckboxMenuItem getScopeResistMenuItem() {
		return scopeResistMenuItem;
	}

	public CheckboxMenuItem setScopeResistMenuItem(CheckboxMenuItem scopeResistMenuItem) {
		this.scopeResistMenuItem = scopeResistMenuItem;
		return scopeResistMenuItem;
	}

	public CheckboxMenuItem getScopeXYMenuItem() {
		return scopeXYMenuItem;
	}

	public CheckboxMenuItem setScopeXYMenuItem(CheckboxMenuItem scopeXYMenuItem) {
		this.scopeXYMenuItem = scopeXYMenuItem;
		return scopeXYMenuItem;
	}

	public PopupMenu getScopeMenu() {
		return scopeMenu;
	}

	public void setScopeMenu(PopupMenu scopeMenu) {
		this.scopeMenu = scopeMenu;
	}

	public CheckboxMenuItem getScopeVMenuItem() {
		return scopeVMenuItem;
	}

	public CheckboxMenuItem setScopeVMenuItem(CheckboxMenuItem scopeVMenuItem) {
		this.scopeVMenuItem = scopeVMenuItem;
		return scopeVMenuItem;
	}

	public CheckboxMenuItem getScopeIMenuItem() {
		return scopeIMenuItem;
	}

	public CheckboxMenuItem setScopeIMenuItem(CheckboxMenuItem scopeIMenuItem) {
		this.scopeIMenuItem = scopeIMenuItem;
		return scopeIMenuItem;
	}

	public CheckboxMenuItem getScopeMaxMenuItem() {
		return scopeMaxMenuItem;
	}

	public CheckboxMenuItem setScopeMaxMenuItem(CheckboxMenuItem scopeMaxMenuItem) {
		this.scopeMaxMenuItem = scopeMaxMenuItem;
		return scopeMaxMenuItem;
	}

	public CheckboxMenuItem getScopeMinMenuItem() {
		return scopeMinMenuItem;
	}

	public CheckboxMenuItem setScopeMinMenuItem(CheckboxMenuItem scopeMinMenuItem) {
		this.scopeMinMenuItem = scopeMinMenuItem;
		return scopeMinMenuItem;
	}

	public CheckboxMenuItem getScopeFreqMenuItem() {
		return scopeFreqMenuItem;
	}

	public CheckboxMenuItem setScopeFreqMenuItem(CheckboxMenuItem scopeFreqMenuItem) {
		this.scopeFreqMenuItem = scopeFreqMenuItem;
		return scopeFreqMenuItem;
	}

	public CheckboxMenuItem getScopePowerMenuItem() {
		return scopePowerMenuItem;
	}

	public CheckboxMenuItem setScopePowerMenuItem(CheckboxMenuItem scopePowerMenuItem) {
		this.scopePowerMenuItem = scopePowerMenuItem;
		return scopePowerMenuItem;
	}

	public CheckboxMenuItem getScopeVIMenuItem() {
		return scopeVIMenuItem;
	}

	public CheckboxMenuItem setScopeVIMenuItem(CheckboxMenuItem scopeVIMenuItem) {
		this.scopeVIMenuItem = scopeVIMenuItem;
		return scopeVIMenuItem;
	}

	public MenuItem getScopeSelectYMenuItem() {
		return scopeSelectYMenuItem;
	}

	public MenuItem setScopeSelectYMenuItem(MenuItem scopeSelectYMenuItem) {
		this.scopeSelectYMenuItem = scopeSelectYMenuItem;
		return scopeSelectYMenuItem;
	}

	public boolean isUseBufferedImage() {
		return useBufferedImage;
	}

	public void setUseBufferedImage(boolean useBufferedImage) {
		this.useBufferedImage = useBufferedImage;
	}

	public List<CircuitElm> getElmList() {
		return elmList;
	}

	public void setElmList(List<CircuitElm> elmList) {
		this.elmList = elmList;
	}

	public Checkbox getStoppedCheck() {
		return stoppedCheck;
	}

	public void setStoppedCheck(Checkbox stoppedCheck) {
		this.stoppedCheck = stoppedCheck;
	}

	public CheckboxMenuItem getDotsCheckItem() {
		return dotsCheckItem;
	}

	public CheckboxMenuItem setDotsCheckItem(CheckboxMenuItem dotsCheckItem) {
		this.dotsCheckItem = dotsCheckItem;
		return dotsCheckItem;
	}

	public CircuitElm getDragElm() {
		return dragElm;
	}

	public void setDragElm(CircuitElm dragElm) {
		this.dragElm = dragElm;
	}

	public int getMouseMode() {
		return mouseMode;
	}

	public static String getMuString() {
		return muString;
	}

	public static void setMuString(String muString) {
		CirSim.muString = muString;
	}

	public CheckboxMenuItem getVoltsCheckItem() {
		return voltsCheckItem;
	}

	public CheckboxMenuItem setVoltsCheckItem(CheckboxMenuItem voltsCheckItem) {
		this.voltsCheckItem = voltsCheckItem;
		return voltsCheckItem;
	}

	public CheckboxMenuItem getPowerCheckItem() {
		return powerCheckItem;
	}

	public CheckboxMenuItem setPowerCheckItem(CheckboxMenuItem powerCheckItem) {
		this.powerCheckItem = powerCheckItem;
		return powerCheckItem;
	}

	public CheckboxMenuItem getShowValuesCheckItem() {
		return showValuesCheckItem;
	}

	public CheckboxMenuItem setShowValuesCheckItem(CheckboxMenuItem showValuesCheckItem) {
		this.showValuesCheckItem = showValuesCheckItem;
		return showValuesCheckItem;
	}

	public CircuitCanvas getCv() {
		return cv;
	}

	public void setCv(CircuitCanvas cv) {
		this.cv = cv;
	}

	public static EditDialog getEditDialog() {
		return editDialog;
	}

	public static void setEditDialog(EditDialog editDialog) {
		CirSim.editDialog = editDialog;
	}

	public Circuit getApplet() {
		return applet;
	}

	public void setApplet(Circuit applet) {
		this.applet = applet;
	}

	public static ImportExportDialog getImpDialog() {
		return impDialog;
	}

	public static void setImpDialog(ImportExportDialog impDialog) {
		CirSim.impDialog = impDialog;
	}

	public int getGridSize() {
		return gridSize;
	}

	public void setGridSize(int gridSize) {
		this.gridSize = gridSize;
	}

	public CheckboxMenuItem getSmallGridCheckItem() {
		return smallGridCheckItem;
	}

	public CheckboxMenuItem setSmallGridCheckItem(CheckboxMenuItem smallGridCheckItem) {
		this.smallGridCheckItem = smallGridCheckItem;
		return smallGridCheckItem;
	}

	public boolean isConverged() {
		return converged;
	}

	public void setConverged(boolean converged) {
		this.converged = converged;
	}

	public Vector<CircuitNode> getNodeList() {
		return nodeList;
	}

	public void setNodeList(Vector<CircuitNode> nodeList) {
		this.nodeList = nodeList;
	}

	public static String getOhmString() {
		return ohmString;
	}

	public static void setOhmString(String ohmString) {
		CirSim.ohmString = ohmString;
	}

	public int getSubIterations() {
		return subIterations;
	}

	public void setSubIterations(int subIterations) {
		this.subIterations = subIterations;
	}

	public CheckboxMenuItem getEuroResistorCheckItem() {
		return euroResistorCheckItem;
	}

	public CheckboxMenuItem setEuroResistorCheckItem(CheckboxMenuItem euroResistorCheckItem) {
		this.euroResistorCheckItem = euroResistorCheckItem;
		return euroResistorCheckItem;
	}

	public boolean isAnalyzeFlag() {
		return analyzeFlag;
	}

	public void setAnalyzeFlag(boolean analyzeFlag) {
		this.analyzeFlag = analyzeFlag;
	}

	public String toString() {
		// TODO
		return new ToStringBuilder(this).append("elmList", elmList).append("nodeList", nodeList).toString();
	}

}
