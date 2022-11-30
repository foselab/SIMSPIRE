package simulationsection;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import utils.GraphicConstants;

/**
 * Manages buttons section
 */
public class ControlButtons {

	/**
	 * State of the simulation
	 */
	private transient boolean state;

	/**
	 * Coordinate helper
	 */
	private final transient int yButton;

	/**
	 * Init buttons section
	 * 
	 * @param yInit     coordinate helper
	 * @param leftPanel panel where buttons have to be displyed
	 */
	public ControlButtons(final int yInit, final JPanel leftPanel) {
		state = true;
		yButton = yInit + 28;

		// stop button set up
		final JButton stop = new JButton("Stop");
		stop.setFont(new Font("Arial", Font.BOLD, 16));
		stop.setBounds(GraphicConstants.IDELEMENTX, yButton, 100, 50);
		stop.setBackground(Color.RED);
		stop.setForeground(Color.WHITE);
		stop.setVisible(true);

		// start button set up
		final JButton start = new JButton("Start");
		start.setFont(new Font("Arial", Font.BOLD, 16));
		start.setBounds(GraphicConstants.IDELEMENTX, yButton, 100, 50);
		start.setBackground(Color.GREEN);
		start.setForeground(Color.WHITE);
		start.setVisible(false);

		// print button set up
		final JButton printData = new JButton("Print");
		printData.setFont(new Font("Arial", Font.BOLD, 16));
		printData.setBounds(GraphicConstants.VALELEMENTX, yButton, 100, 50);
		printData.setBackground(Color.YELLOW);
		printData.setForeground(Color.BLACK);
		printData.setVisible(true);

		// events set up
		stop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				stop.setVisible(false);
				start.setVisible(true);
				state = false;
			}
		});

		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				start.setVisible(false);
				stop.setVisible(true);
				state = true;
			}
		});

		printData.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				PlotSection.saveCharts();
			}
		});

		leftPanel.add(stop);
		leftPanel.add(start);
		leftPanel.add(printData);
	}

	public boolean isState() {
		return state;
	}

	public int getyButton() {
		return yButton;
	}
}
