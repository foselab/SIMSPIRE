/*
 * BreathingMonitor.h
 *
 *  Created on: 24 mag 2020
 *      Author: AngeloGargantini
 */

#ifndef BREATHINGMONITOR_H_
#define BREATHINGMONITOR_H_

#include "Serial.h"
#include <string>
#include <iostream>
#include <zmq.hpp>
#include <chrono>
#include <ctime>

namespace mvm {

/**
 * FAKE class of the real BreathingMonitor
 */
class BreathingMonitor {
	bool first;
	double oldTime;
	float pressure_p;
	double volume;
	double oldFlux;
	double flux;
	float flux_peak;
	float r_rate;
	float p_peak;
	float peep;

	void getFlux();
	void getPressure();
	void getFluxPeak();
	void getRespiratoryRate();
	void getPeakPressure();
	void getPeep();

public:
	BreathingMonitor();
	float getVolume();
	void setVolume(float volume);

	enum class Config {
		VENTILATOR_RUN,
		PRESSURE_SETPOINT,
		VENTURI_COEFF_0,
		VENTURI_COEFF_1,
		VENTURI_COEFF_2,
		VENTURI_COEFF_3,
		VENTURI_COEFF_4
	};

	enum class Output {
		FLUX,         // REALTIME: Flux
		PRESSURE_L,   // REALTIME: Pressure Loop
		PRESSURE_P,   // REALTIME: Pressure Patient
		TIDAL_VOLUME, // REALTIME: Tidal Volume
		VOL_INSP,     // CYCLE-TO-CYCLE: Inspred volume
		VOL_EXP,      // CYCLE-TO-CYCLE: Expired volume
		VOL_MINUTE,   // CYCLE-TO-CYCLE: Volume Minute
		RESP_RATE,    // CYCLE-TO-CYCLE: Measured respiratory rate
		PDELTA_1,     // CYCLE-TO-CYCLE: Pressure delta '
		PDELTA_2,     // CYCLE-TO-CYCLE: Pressure delta ''
		PPEAK,        // CYCLE-TO-CYCLE: Peak of pressure
		FLUXPEAK,     // CYCLE-TO-CYCLE: Peak of flux
		FLAT_TOP_P,   // CYCLE-TO-CYCLE: Peak of pressure
		PEEP,         // CYCLE-TO-CYCLE: Measured peep
		TINSP         // TODO
	};

	void TransitionNewCycle_Event_cb() {
	}
	void TransitionInhaleExhale_Event_cb() {
	}
	void TransitionEndCycle_Event_cb() {
	}
	void GetOutputValue(Output probe, float *value);
	bool SetConfigurationValue(Config probe, float value);
	void GetConfigurationValue(Config probe, float *value) const;
	void GetOutputValue(Output probe, float *value) const;
	void loop();

};
} // namespace mvm
#endif /* BREATHINGMONITOR_H_ */
