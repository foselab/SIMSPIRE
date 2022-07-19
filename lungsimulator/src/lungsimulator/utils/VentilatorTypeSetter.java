package lungsimulator.utils;

import java.util.Arrays;
import java.util.List;

import lungsimulator.RealTimePlot;

public class VentilatorTypeSetter {

	/**
	 * Set the com ventilator
	 *
	 * @param rtp
	 * @param com
	 */
	public static List<Object> setComVentilator(RealTimePlot rtp, COMPortAdapter com, boolean expirationStarted, double expirationTime) {
		int readValue = com.readData();
		if (readValue != 0)
			System.out.println("++" + readValue + "++");
		switch (readValue) {
		// 1
		case 49:
			expirationStarted = false;
			if (rtp.getSwDrop().getPosition() == 0)
				rtp.getSwDrop().toggle();
			rtp.getBattery().setMaxVoltage(rtp.getVentilator().getBatteryValue());
			break;
		// 0
		case 48:
			if (!expirationStarted)
				expirationTime = rtp.getElapsedSeconds();

			expirationStarted = true;
			rtp.getBattery().setMaxVoltage(rtp.getVentilator().getPeepValue());
			break;
		}
		return Arrays.asList(expirationStarted, expirationTime);
	}

	/**
	 * Set the ramp ventilator value depending on the time
	 *
	 * @param rtp
	 * @param time
	 * @param timeStart
	 * @param expirationTime
	 * @param expirationStarted
	 * @return
	 */
	public static List<Object> setRampVentilator(RealTimePlot rtp, double time, double timeStart,
			boolean expirationStarted, double expirationTime) {
		if (time < timeStart + rtp.getVentilator().getT1()) {
			// Increasing ramp
			rtp.getBattery().setMaxVoltage(
					(rtp.getVentilator().getBatteryValue() - rtp.getVentilator().getPeepValue()) * ((time - timeStart) / rtp.getVentilator().getT1()) + rtp.getVentilator().getPeepValue());
		} else if (time < timeStart + rtp.getVentilator().getT1() + rtp.getVentilator().getT2()) {
			// Pmax
			rtp.getBattery().setMaxVoltage(rtp.getVentilator().getBatteryValue());
		} else if (time < timeStart + rtp.getVentilator().getT1() + rtp.getVentilator().getT2() + rtp.getVentilator().getT3()) {
			// Decreasing ramp
			if (!expirationStarted)
				expirationTime = rtp.getElapsedSeconds();

			expirationStarted = true;
			rtp.getBattery().setMaxVoltage(rtp.getVentilator().getBatteryValue() + (rtp.getVentilator().getPeepValue() - rtp.getVentilator().getBatteryValue())
					* (((time - timeStart) - rtp.getVentilator().getT1() - rtp.getVentilator().getT2()) / rtp.getVentilator().getT3()));
		} else if (time < timeStart + rtp.getVentilator().getT1() + rtp.getVentilator().getT2() + rtp.getVentilator().getT3() + rtp.getVentilator().getT4()) {
			// Peep
			rtp.getBattery().setMaxVoltage(rtp.getVentilator().getPeepValue());
		} else {
			// Reset
			expirationStarted = false;
			timeStart = rtp.getElapsedSeconds();
			if (rtp.getSwDrop().getPosition() == 0)
				rtp.getSwDrop().toggle();
		}
		return Arrays.asList(timeStart, expirationStarted, expirationTime);
	}
}
