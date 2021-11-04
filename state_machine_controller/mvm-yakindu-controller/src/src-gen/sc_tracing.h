/* generated - DO NOT EDIT - MIT license. @authors: Angelo Gargantini, Silvia Bonfanti, Elvinia Riccobene, Andrea Bombarda */

#ifndef SC_TRACING_H_
#define SC_TRACING_H_

namespace sc {
namespace trace {

template<typename T>
class TraceObserver
{
public:
	virtual ~TraceObserver(){}

	virtual void stateEntered(T state) = 0;

	virtual void stateExited(T state) = 0;
};
} /* namespace sc::trace */
} /* namespace sc */

#endif /* SC_TRACING */

