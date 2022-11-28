package simulationsection;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import utils.GraphicConstants;

public class ControlButtons {

	private int imageCounter;
	private boolean state;
	private int yButton;
	
	public ControlButtons(int yInit, JPanel leftPanel) {	
		state = true;
		imageCounter = 1;
		yButton = yInit + 28;
		
		//stop button set up
		JButton stop = new JButton("Stop");
		stop.setFont(new Font("Arial", Font.BOLD, 16));
		stop.setBounds(GraphicConstants.IDELEMENTX, yButton, 100, 50);
		stop.setBackground(Color.RED);
		stop.setForeground(Color.WHITE);
		stop.setVisible(true);

		//start button set up
		JButton start = new JButton("Start");
		start.setFont(new Font("Arial", Font.BOLD, 16));
		start.setBounds(GraphicConstants.IDELEMENTX, yButton, 100, 50);
		start.setBackground(Color.GREEN);
		start.setForeground(Color.WHITE);
		start.setVisible(false);

		//print button set up
		JButton printData = new JButton("Print");
		printData.setFont(new Font("Arial", Font.BOLD, 16));
		printData.setBounds(GraphicConstants.VALELEMENTX, yButton, 100, 50);
		printData.setBackground(Color.YELLOW);
		printData.setForeground(Color.BLACK);
		printData.setVisible(true);
		
		//events set up
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
		
		printData.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				/*try {
					BitmapEncoder.saveBitmap(flowChart, "./Images/" + imageCounter + "_" + flowChart.getTitle(),
							BitmapFormat.PNG);
					BitmapEncoder.saveBitmap(pressureChart, "./Images/" + imageCounter + "_" + pressureChart.getTitle(),
							BitmapFormat.PNG);
					imageCounter++;
				} catch (IOException e1) {
					e1.printStackTrace();
				}*/
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
