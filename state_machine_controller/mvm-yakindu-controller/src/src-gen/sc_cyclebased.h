/* generated - DO NOT EDIT - MIT license. @authors: Angelo Gargantini, Silvia Bonfanti, Elvinia Riccobene, Andrea Bombarda */

#ifndef SC_CYCLEBASED_H_
#define SC_CYCLEBASED_H_

namespace sc {

/*! \file Interface for cycle-based state machines.
 */
class CycleBasedInterface
{
	public:
	
		virtual ~CycleBasedInterface() = 0;
	
		/*! Start a run-to-completion cycle.
		*/
		virtual void runCycle() = 0;
};

inline CycleBasedInterface::~CycleBasedInterface() {}

} /* namespace sc */

#endif /* SC_CYCLEBASED_H_ */
