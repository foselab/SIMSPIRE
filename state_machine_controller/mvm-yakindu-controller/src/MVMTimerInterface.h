#ifndef MVM_MVMTIMERINTERFACE_H
#define MVM_MVMTIMERINTERFACE_H

#include <src-gen/MVMStateMachineCore.h>
#include <src-gen/sc_timer.h>
#include <array>

namespace mvm {

class MVMTimerInterface : public sc::timer::TimerServiceInterface
{
 private:
  static constexpr std::size_t MaxNTimers =
      MVMStateMachineCore::timeEventsCount;

  struct Timer
  {
    sc_integer delay;
    sc_eventid event;
    void update(sc_integer elapsed)
    {
      delay -= elapsed;
    }
    bool is_expired() const
    {
      return delay <= 0;
    }
  };

  std::array<Timer, MaxNTimers> m_timers{};
  std::size_t m_size{0};

 public:
  MVMTimerInterface(MVMTimerInterface const&) = delete;
  MVMTimerInterface& operator=(MVMTimerInterface const&) = delete;

  MVMTimerInterface() = default;

  std::size_t size() const
  {
    return m_size;
  }

  std::size_t capacity() const
  {
    return m_timers.size();
  }

  void setTimer(sc::timer::TimedInterface* statemachine, sc_eventid event,
                sc_integer delay, sc_boolean isPeriodic) override;

  void unsetTimer(sc::timer::TimedInterface* statemachine,
                  sc_eventid event) override;

  void updateTimers(sc::timer::TimedInterface* statemachine,
                    sc_integer elapsed);

  void cancel() override;
};

} // namespace mvm

#endif
