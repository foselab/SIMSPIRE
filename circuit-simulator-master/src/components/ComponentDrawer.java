package components;

import java.awt.Font;
import java.awt.Graphics;

public class ComponentDrawer {
	
	public static void draw(RailElm circuitElm, Graphics g) {
		circuitElm.setBbox(circuitElm.point1, circuitElm.point2, circuitElm.circleSize);
		CircuitElm.drawThickLine(g, circuitElm.point1, circuitElm.lead1);
		boolean clock = circuitElm.waveform == VoltageElm.WF_SQUARE && (circuitElm.flags & circuitElm.FLAG_CLOCK) != 0;
		if (circuitElm.waveform == VoltageElm.WF_DC || circuitElm.waveform == VoltageElm.WF_VAR || clock) {
			Font f = new Font("SansSerif", 0, 12);
			g.setFont(f);
			g.setColor(circuitElm.needsHighlight() ? CircuitElm.getSelectColor() : CircuitElm.getWhiteColor());
			double v = circuitElm.getVoltage();
			String s = CircuitElm.getShortUnitText(v, "V");
			if (Math.abs(v) < 1)
				s = CircuitElm.showFormat.format(v) + "V";
			if (circuitElm.getVoltage() > 0)
				s = "+" + s;
			if (circuitElm instanceof AntennaElm)
				s = "Ant";
			if (clock)
				s = "CLK";
			circuitElm.drawCenteredText(g, s, circuitElm.getX2(), circuitElm.getY2(), true);
		} else {
			circuitElm.drawWaveform(g, circuitElm.point2);
		}
		circuitElm.drawPosts(g);
	}

}
