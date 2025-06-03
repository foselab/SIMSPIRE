package circuitsimulator.components;

import java.util.List;

public class ExternalVoltageElm extends VoltageElm{

	public ExternalVoltageElm(int xx, int yy, int wf) {
		super(xx, yy, wf);
	}
	
	public ExternalVoltageElm(int xx, int yy, int wf, double maxVoltage, double frequency, double dutyCycle) {
		super(xx, yy, wf);
		setMaxVoltage(maxVoltage);
		this.frequency = frequency;
		this.dutyCycle = dutyCycle;
		reset();
	}
	
	public ExternalVoltageElm(int xx, int yy, int wf, double maxVoltage, String formula, List<String> variables) {
		super(xx, yy, wf);
		setMaxVoltage(maxVoltage);
	}
	
	@Override
	public Class getDumpClass() {
		return ExternalVoltageElm.class;
	}

	@Override
	public int getShortcut() {
		return 'e';
	}

}
