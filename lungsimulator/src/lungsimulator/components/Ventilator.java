package lungsimulator.components;

public class Ventilator {

	double peepValue;
	double batteryValue;
	double t1;
	double t2;
	double t3;
	double t4;
	double rVentilatorValue;

	public Ventilator(double peepValue, double batteryValue, double t1, double t2, double t3, double t4,
			double rVentilatorValue) {
		super();
		this.peepValue = peepValue;
		this.batteryValue = batteryValue;
		this.t1 = t1;
		this.t2 = t2;
		this.t3 = t3;
		this.t4 = t4;
		this.rVentilatorValue = rVentilatorValue;
	}

	public Ventilator() {
		this.peepValue = 0;
		this.batteryValue = 0;
		this.t1 = 0;
		this.t2 = 0;
		this.t3 = 0;
		this.t4 = 0;
		this.rVentilatorValue = 0;
	}

	public double getPeepValue() {
		return peepValue;
	}
	public void setPeepValue(double peepValue) {
		this.peepValue = peepValue;
	}
	public double getBatteryValue() {
		return batteryValue;
	}
	public void setBatteryValue(double batteryValue) {
		this.batteryValue = batteryValue;
	}
	public double getT1() {
		return t1;
	}
	public void setT1(double t1) {
		this.t1 = t1;
	}
	public double getT2() {
		return t2;
	}
	public void setT2(double t2) {
		this.t2 = t2;
	}
	public double getT3() {
		return t3;
	}
	public void setT3(double t3) {
		this.t3 = t3;
	}
	public double getT4() {
		return t4;
	}
	public void setT4(double t4) {
		this.t4 = t4;
	}
	public double getrVentilatorValue() {
		return rVentilatorValue;
	}
	public void setrVentilatorValue(double rVentilatorValue) {
		this.rVentilatorValue = rVentilatorValue;
	}



}
