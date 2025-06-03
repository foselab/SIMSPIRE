package simspire.swing.simulationsection;

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import lungsimulator.LungSimulator;

/**
 * Manages the simulation view
 */
public class SimulationView {

	/**
	 * Show if the frame state (open or close)
	 */
	private transient boolean windowIsOpen;

	/**
	 * Circuit component section
	 */
	private final transient CircuitComponents circuitComponents;

	/**
	 * Buttons section
	 */
	private final transient ControlButtons buttonSection;

	/**
	 * Plot section
	 */
	private final transient PlotSection plotSection;

	/**
	 * Init the simulation view
	 * @param lungSimulator backend access
	 */
	public SimulationView(final LungSimulator lungSimulator) {
		// frame configuration
		final JFrame frame = new JFrame();
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // fullscreen option
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		windowIsOpen = true;

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent event) {
				windowIsOpen = false;
			}
		});

		final JPanel leftPanel = new JPanel();
		leftPanel.setLayout(null);
		leftPanel.setVisible(true);

		circuitComponents = new CircuitComponents(lungSimulator, leftPanel);
		buttonSection = new ControlButtons(circuitComponents.getyInit(), leftPanel);
		final DemographicComponents demoData = new DemographicComponents(lungSimulator, buttonSection.getyButton(),
				leftPanel);

		frame.getContentPane().add(leftPanel);

		final JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

		plotSection = new PlotSection(lungSimulator, rightPanel, true);

		frame.getContentPane().add(rightPanel);
		frame.getContentPane().setLayout(new GridLayout(1, 2, 0, 0));
		// frame.pack();
		frame.setVisible(true);
	}

	public boolean isWindowIsOpen() {
		return windowIsOpen;
	}

	/**
	 * Get the state of the execution
	 * @return current state of execution
	 */
	public boolean getStateOfExecution() {
		return buttonSection.isState();
	}

	/**
	 * Updates time dependent elements
	 */
	public void updateTimeDependentElms() {
		circuitComponents.updateTimeDependentElms();
	}

	/**
	 * Updates ventilator value
	 * @param currentVentValue current ventilator value
	 */
	public void updateVentilator(final double currentVentValue) {
		circuitComponents.updateVentilator(currentVentValue);
	}

	/**
	 * Updates chars values
	 * @param lungSimulator backend access
	 */
	public void updateCharts(final LungSimulator lungSimulator) {
		plotSection.updateCharts(lungSimulator, true);
	}
}
