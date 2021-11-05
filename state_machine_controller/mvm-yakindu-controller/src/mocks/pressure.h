#ifndef MVN_PRESSURE_H
#define MVN_PRESSURE_H

namespace mvm {

class Pressure
{
  static constexpr double cmH2O_to_millibar = 0.980665;
  static constexpr double millibar_to_cmH2O = 1. / cmH2O_to_millibar;

  float m_cmH2O;

public:

  explicit constexpr Pressure(float cm)
      : m_cmH2O(cm)
  {
  }

  constexpr float millibar() const
  {
    return static_cast<float>(m_cmH2O * cmH2O_to_millibar);
  }

  constexpr float cmH2O() const
  {
    return m_cmH2O;
  }

  static constexpr Pressure millibar(float arg)
  {
    return Pressure(arg * millibar_to_cmH2O);
  }
  static constexpr Pressure cmH2O(float arg)
  {
    return Pressure{arg};
  }
};

inline constexpr bool operator==(Pressure const& lhs, Pressure const& rhs)
{
  return lhs.cmH2O() == rhs.cmH2O();
}

inline constexpr bool operator<(Pressure const& lhs, Pressure const& rhs)
{
  return lhs.cmH2O() < rhs.cmH2O();
}

inline constexpr bool operator!=(Pressure const& lhs, Pressure const& rhs)
{
  return !(lhs == rhs);
}

inline constexpr bool operator>(Pressure const& lhs, Pressure const& rhs)
{
  return rhs < lhs;
}

inline constexpr bool operator<=(Pressure const& lhs, Pressure const& rhs)
{
  return !(lhs > rhs);
}

inline constexpr bool operator>=(Pressure const& lhs, Pressure const& rhs)
{
  return !(lhs < rhs);
}

inline constexpr Pressure operator""_mbar(long double arg)
{
  return Pressure::millibar(static_cast<float>(arg));
}

// uint64_t is not allowed (unless is an alias as ull)
inline constexpr Pressure operator""_mbar(unsigned long long arg)
{
  return Pressure::millibar(static_cast<float>(arg));
}

inline constexpr Pressure operator""_cmH2O(long double arg)
{
  return Pressure::cmH2O(static_cast<float>(arg));
}

inline constexpr Pressure operator""_cmH2O(unsigned long long arg)
{
  return Pressure::cmH2O(static_cast<float>(arg));
}

} // namespace mvm

#endif // MVN_PRESSURE_H
