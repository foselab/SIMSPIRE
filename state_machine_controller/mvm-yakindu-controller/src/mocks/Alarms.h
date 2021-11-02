/*
 * Alarms.h
 *
 *  Created on: 25 mag 2020
 *      Author: AngeloGargantini
 */

#ifndef ALARMS_H_
#define ALARMS_H_

// fake class
namespace mvm {

enum class ALARM_ENUM { ALARM_APNEA };

class Alarms
{
 public:
  void TransitionInhaleExhale_Event_cb()
  {
  }
  void TransitionEndCycle_Event_cb()
  {
  }
  void TransitionNewCycle_Event_cb()
  {
  }

  void TriggerAlarm(ALARM_ENUM Alarm)
  {
  }

  enum class Config {
    O2_MIN,
    O2_MAX,
    PEAK_MIN,
    PEAK_MAX,
    PEEP_MIN,
    PEEP_MAX,
    RR_MIN,
    RR_MAX,
    TEMPERATURE_MAX,
    TIDAL_VOLUME_MIN,
    TIDAL_VOLUME_MAX,
    EXP_VOLUME_MIN,
    EXP_VOLUME_MAX,
    VOLUME_MINUTE_MIN,
    VOLUME_MINUTE_MAX,
    VENTILATOR_RUN,
    PRESSURE_SETPOINT,
  };

  bool SetConfigurationValue(Config probe, float value)
  {
    return true;
  }
};
} // namespace mvm

#endif /* ALARMS_H_ */
