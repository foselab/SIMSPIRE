package lungsimulator.components;

public class SimulatorParams {
	long sleepTime;
	double step;
	boolean useCom;
	boolean useZMQ;
	String comName;

	public SimulatorParams(long sleepTime, double step, boolean useCom, String comName, boolean useZMQ) {
		super();
		this.sleepTime = sleepTime;
		this.step = step;
		this.useCom = useCom;
		this.comName = comName;
		this.useZMQ = useZMQ;
	}

	public SimulatorParams() {
		super();
		this.sleepTime = 0;
		this.step = 0;
		this.useCom = false;
		this.useZMQ = false;
		this.comName = "";
	}

	public long getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
	}

	public double getStep() {
		return step;
	}

	public void setStep(double step) {
		this.step = step;
	}

	public boolean isUseCom() {
		return useCom;
	}

	public void setUseCom(boolean useCom) {
		this.useCom = useCom;
	}

	public boolean isUseZMQ() {
		return useZMQ;
	}

	public void setUseZMQ(boolean useZMQ) {
		this.useZMQ = useZMQ;
	}

	public String getComName() {
		return comName;
	}

	public void setComName(String comName) {
		this.comName = comName;
	}


}
