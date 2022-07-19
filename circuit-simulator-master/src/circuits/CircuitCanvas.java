package circuits;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;

import simulator.CirSim;

public class CircuitCanvas extends Canvas {
	CirSim pg;

	public CircuitCanvas(CirSim p) {
		pg = p;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(300, 400);
	}

	@Override
	public void update(Graphics g) {
		pg.updateCircuit(g);
	}

	@Override
	public void paint(Graphics g) {
		pg.updateCircuit(g);
	}
};
