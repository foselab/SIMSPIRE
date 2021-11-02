#ifndef _VC_H
#define _VC_H

namespace mvm {
class ValvesController
{
  float v1_input, v2_out;
 public:
  ValvesController(){}

  void begin(){}
  void loop(){}

  void running(bool r){}
  void set_v1(float v);
  void set_v2(float);
  void breath(bool b) {}
  void spontaneus(bool r) {}
};
} // namespace mvm

#endif
