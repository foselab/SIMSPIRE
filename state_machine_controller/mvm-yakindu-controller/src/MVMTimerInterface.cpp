#include "MVMTimerInterface.h"

#include <algorithm>
#include <cassert>

namespace mvm {

void MVMTimerInterface::setTimer(sc::timer::TimedInterface* statemachine, sc_eventid event,
                                 sc_integer delay, sc_boolean)
{
  assert(size() < capacity());
  m_timers[size()] = Timer{delay, event};
  ++m_size;
}

void MVMTimerInterface::unsetTimer(sc::timer::TimedInterface* statemachine,
                                   sc_eventid event)
{
  auto const it =
      std::remove_if(m_timers.begin(), m_timers.begin() + size(),
                     [=](Timer const& timer) { return timer.event == event; });
  m_size = std::distance(m_timers.begin(), it);
}

void MVMTimerInterface::updateTimers(sc::timer::TimedInterface* statemachine,
                                     sc_integer elapsed)
{
  std::for_each(m_timers.begin(), m_timers.begin() + size(), [=](Timer& timer) {
    timer.update(elapsed);
    if (timer.is_expired()) {
      statemachine->raiseTimeEvent(timer.event);
    }
  });
}

void MVMTimerInterface::cancel()
{
  m_size = 0;
}

} // namespace mvm
