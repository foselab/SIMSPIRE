package simulationsection;

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import lungsimulator.LungSimulator;

public class SimulationView {
	/**
	 * The frame of user interface
	 */
	private transient JFrame frame;
	
	/**
	 * Show if the frame state (open or close)
	 */
	private transient boolean windowIsOpen;
	
	private CircuitComponents circuitComponents;
	
	private ControlButtons buttonSection;
	
	private DemographicComponents demoData;
	
	private PlotSection plotSection;

	public SimulationView(LungSimulator lungSimulator) {
		// frame configuration
		frame = new JFrame();
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // fullscreen option
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		windowIsOpen = true;
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent event) {
				windowIsOpen = false;
			}
		});

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(null);
		leftPanel.setVisible(true);
		
		circuitComponents = new CircuitComponents(lungSimulator, leftPanel);
		buttonSection = new ControlButtons(circuitComponents.getyInit(), leftPanel);
		demoData = new DemographicComponents(lungSimulator, buttonSection.getyButton(), leftPanel);

		frame.getContentPane().add(leftPanel);
		
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		
		plotSection = new PlotSection(lungSimulator, rightPanel, true);
		
		frame.getContentPane().add(rightPanel);
		frame.getContentPane().setLayout(new GridLayout(1, 2, 0, 0));
		//frame.pack();
		frame.setVisible(true);
	}

	public boolean isWindowIsOpen() {
		return windowIsOpen;
	}

	public boolean getStateOfExecution() {
		return buttonSection.isState();
	}

	public void updateTimeDependentElms() {
		circuitComponents.updateTimeDependentElms();
	}

	public void updateVentilator(double currentVentilatorValue) {
		circuitComponents.updateVentilator(currentVentilatorValue);
	}

	public void updateCharts(LungSimulator lungSimulator) {
		plotSection.updateCharts(lungSimulator, true);
	}
}
