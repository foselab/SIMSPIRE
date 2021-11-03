/* generated - DO NOT EDIT - MIT license. @authors: Angelo Gargantini, Silvia Bonfanti, Elvinia Riccobene, Andrea Bombarda */

#include "MVMStateMachineCore.h"

/*! \file Implementation of the state machine 'MVMStateMachineCore'
*/


const int32_t MVMStateMachineCore::max_exp_pause = 40000;
const int32_t MVMStateMachineCore::max_ins_pause = 40000;
const int32_t MVMStateMachineCore::triggerWindowDelay_ms = 700;
const int32_t MVMStateMachineCore::min_insp_time_ms = 300;



MVMStateMachineCore::MVMStateMachineCore()  :
startupEnded_raised(false),
resume_raised(false),
poweroff_raised(false),
selfTestPassed_raised(false),
startVentilation_raised(false),
stopVentilation(false),
mode(P_CONTROLLED_V),
max_rm_time(0),
exp_pause(false),
ins_pause(false),
rm_request(false),
ibwASV(70),
normalMinuteVentilationASV(7000),
targetMinuteVentilationASV(100),
vTidalASV(0.0),
rrASV(0.0),
rcASV(0.0),
numCycle(0),
inspiration_duration_ms(1666),
expiration_duration_ms(3334),
max_insp_time_psv(7000),
max_insp_time_asv(7000),
apnealag(30000),
apnea_backup_mode(false),
timerService(sc_null),
ifaceTraceObserver(0),
ifaceOperationCallback(sc_null),
current(),
isExecuting(false)
{
	for (sc_ushort i = 0; i < maxOrthogonalStates; ++i)
		stateConfVector[i] = MVMStateMachineCore_last_state;
	
	clearInEvents();
	swapInEvents();
}

MVMStateMachineCore::~MVMStateMachineCore()
{
}


sc_boolean MVMStateMachineCore::isActive() const
{
	return stateConfVector[0] != MVMStateMachineCore_last_state;
}

sc_boolean MVMStateMachineCore::isFinal() const
{
	return (stateConfVector[0] == main_region__final_);
}

sc_boolean MVMStateMachineCore::check() {
	if(timerService == sc_null) {
		return false;
	}
	if (this->ifaceOperationCallback == sc_null) {
		return false;
	}
	return true;
}


void MVMStateMachineCore::setTimerService(sc::timer::TimerServiceInterface* timerService)
{
	this->timerService = timerService;
}

sc::timer::TimerServiceInterface* MVMStateMachineCore::getTimerService()
{
	return timerService;
}

sc_integer MVMStateMachineCore::getNumberOfParallelTimeEvents() {
	return parallelTimeEventsCount;
}

void MVMStateMachineCore::raiseTimeEvent(sc_eventid evid)
{
	if ((evid >= (sc_eventid)timeEvents) && (evid < (sc_eventid)(&timeEvents[timeEventsCount])))
	{
		*(sc_boolean*)evid = true;
	}
}


sc_boolean MVMStateMachineCore::isStateActive(MVMStateMachineCoreStates state) const
{
	switch (state)
	{
		case main_region_StartUp : 
			return (sc_boolean) (stateConfVector[SCVI_MAIN_REGION_STARTUP] == main_region_StartUp
			);
		case main_region_PCV : 
			return (sc_boolean) (stateConfVector[SCVI_MAIN_REGION_PCV] >= main_region_PCV
				&& stateConfVector[SCVI_MAIN_REGION_PCV] <= main_region_PCV_r1_RM);
		case main_region_PCV_r1_ExpiratoryPause : 
			return (sc_boolean) (stateConfVector[SCVI_MAIN_REGION_PCV_R1_EXPIRATORYPAUSE] == main_region_PCV_r1_ExpiratoryPause
			);
		case main_region_PCV_r1_Expiration : 
			return (sc_boolean) (stateConfVector[SCVI_MAIN_REGION_PCV_R1_EXPIRATION] == main_region_PCV_r1_Expiration
			);
		case main_region_PCV_r1_Inspiration : 
			return (sc_boolean) (stateConfVector[SCVI_MAIN_REGION_PCV_R1_INSPIRATION] == main_region_PCV_r1_Inspiration
			);
		case main_region_PCV_r1_InspiratoryPause : 
			return (sc_boolean) (stateConfVector[SCVI_MAIN_REGION_PCV_R1_INSPIRATORYPAUSE] == main_region_PCV_r1_InspiratoryPause
			);
		case main_region_PCV_r1_RM : 
			return (sc_boolean) (stateConfVector[SCVI_MAIN_REGION_PCV_R1_RM] == main_region_PCV_r1_RM
			);
		case main_region_PSV : 
			return (sc_boolean) (stateConfVector[SCVI_MAIN_REGION_PSV] >= main_region_PSV
				&& stateConfVector[SCVI_MAIN_REGION_PSV] <= main_region_PSV_r1_RM);
		case main_region_PSV_r1_ExpiratoryPause : 
			return (sc_boolean) (stateConfVector[SCVI_MAIN_REGION_PSV_R1_EXPIRATORYPAUSE] == main_region_PSV_r1_ExpiratoryPause
			);
		case main_region_PSV_r1_Expiration : 
			return (sc_boolean) (stateConfVector[SCVI_MAIN_REGION_PSV_R1_EXPIRATION] == main_region_PSV_r1_Expiration
			);
		case main_region_PSV_r1_Inspiration : 
			return (sc_boolean) (stateConfVector[SCVI_MAIN_REGION_PSV_R1_INSPIRATION] == main_region_PSV_r1_Inspiration
			);
		case main_region_PSV_r1_InspiratoryPause : 
			return (sc_boolean) (stateConfVector[SCVI_MAIN_REGION_PSV_R1_INSPIRATORYPAUSE] == main_region_PSV_r1_InspiratoryPause
			);
		case main_region_PSV_r1_RM : 
			return (sc_boolean) (stateConfVector[SCVI_MAIN_REGION_PSV_R1_RM] == main_region_PSV_r1_RM
			);
		case main_region__final_ : 
			return (sc_boolean) (stateConfVector[SCVI_MAIN_REGION__FINAL_] == main_region__final_
			);
		case main_region_SelfTest : 
			return (sc_boolean) (stateConfVector[SCVI_MAIN_REGION_SELFTEST] == main_region_SelfTest
			);
		case main_region_VentilationOff : 
			return (sc_boolean) (stateConfVector[SCVI_MAIN_REGION_VENTILATIONOFF] == main_region_VentilationOff
			);
		case main_region_ASV : 
			return (sc_boolean) (stateConfVector[SCVI_MAIN_REGION_ASV] >= main_region_ASV
				&& stateConfVector[SCVI_MAIN_REGION_ASV] <= main_region_ASV_r1_Inspiration);
		case main_region_ASV_r1_InitialExpiration : 
			return (sc_boolean) (stateConfVector[SCVI_MAIN_REGION_ASV_R1_INITIALEXPIRATION] == main_region_ASV_r1_InitialExpiration
			);
		case main_region_ASV_r1_InitialInspiration : 
			return (sc_boolean) (stateConfVector[SCVI_MAIN_REGION_ASV_R1_INITIALINSPIRATION] == main_region_ASV_r1_InitialInspiration
			);
		case main_region_ASV_r1_Expiration : 
			return (sc_boolean) (stateConfVector[SCVI_MAIN_REGION_ASV_R1_EXPIRATION] == main_region_ASV_r1_Expiration
			);
		case main_region_ASV_r1_Inspiration : 
			return (sc_boolean) (stateConfVector[SCVI_MAIN_REGION_ASV_R1_INSPIRATION] == main_region_ASV_r1_Inspiration
			);
		default: return false;
	}
}

/* Functions for event startupEnded in interface  */
void MVMStateMachineCore::raiseStartupEnded()
{
	startupEnded_raised = true;
}
/* Functions for event resume in interface  */
void MVMStateMachineCore::raiseResume()
{
	resume_raised = true;
}
/* Functions for event poweroff in interface  */
void MVMStateMachineCore::raisePoweroff()
{
	poweroff_raised = true;
}
/* Functions for event selfTestPassed in interface  */
void MVMStateMachineCore::raiseSelfTestPassed()
{
	selfTestPassed_raised = true;
}
/* Functions for event startVentilation in interface  */
void MVMStateMachineCore::raiseStartVentilation()
{
	startVentilation_raised = true;
}
sc_boolean MVMStateMachineCore::getStopVentilation() const
{
	return stopVentilation;
}

void MVMStateMachineCore::setStopVentilation(sc_boolean value)
{
	this->stopVentilation = value;
}

MVM_mode MVMStateMachineCore::getMode() const
{
	return mode;
}

void MVMStateMachineCore::setMode(MVM_mode value)
{
	this->mode = value;
}

int32_t MVMStateMachineCore::getMax_exp_pause() const
{
	return max_exp_pause;
}

int32_t MVMStateMachineCore::getMax_ins_pause() const
{
	return max_ins_pause;
}

int32_t MVMStateMachineCore::getMax_rm_time() const
{
	return max_rm_time;
}

void MVMStateMachineCore::setMax_rm_time(int32_t value)
{
	this->max_rm_time = value;
}

sc_boolean MVMStateMachineCore::getExp_pause() const
{
	return exp_pause;
}

void MVMStateMachineCore::setExp_pause(sc_boolean value)
{
	this->exp_pause = value;
}

sc_boolean MVMStateMachineCore::getIns_pause() const
{
	return ins_pause;
}

void MVMStateMachineCore::setIns_pause(sc_boolean value)
{
	this->ins_pause = value;
}

sc_boolean MVMStateMachineCore::getRm_request() const
{
	return rm_request;
}

void MVMStateMachineCore::setRm_request(sc_boolean value)
{
	this->rm_request = value;
}

float MVMStateMachineCore::getIbwASV() const
{
	return ibwASV;
}

void MVMStateMachineCore::setIbwASV(float value)
{
	this->ibwASV = value;
}

float MVMStateMachineCore::getNormalMinuteVentilationASV() const
{
	return normalMinuteVentilationASV;
}

void MVMStateMachineCore::setNormalMinuteVentilationASV(float value)
{
	this->normalMinuteVentilationASV = value;
}

float MVMStateMachineCore::getTargetMinuteVentilationASV() const
{
	return targetMinuteVentilationASV;
}

void MVMStateMachineCore::setTargetMinuteVentilationASV(float value)
{
	this->targetMinuteVentilationASV = value;
}

float MVMStateMachineCore::getVTidalASV() const
{
	return vTidalASV;
}

void MVMStateMachineCore::setVTidalASV(float value)
{
	this->vTidalASV = value;
}

float MVMStateMachineCore::getRrASV() const
{
	return rrASV;
}

void MVMStateMachineCore::setRrASV(float value)
{
	this->rrASV = value;
}

float MVMStateMachineCore::getRcASV() const
{
	return rcASV;
}

void MVMStateMachineCore::setRcASV(float value)
{
	this->rcASV = value;
}

int32_t MVMStateMachineCore::getNumCycle() const
{
	return numCycle;
}

void MVMStateMachineCore::setNumCycle(int32_t value)
{
	this->numCycle = value;
}

int32_t MVMStateMachineCore::getInspiration_duration_ms() const
{
	return inspiration_duration_ms;
}

void MVMStateMachineCore::setInspiration_duration_ms(int32_t value)
{
	this->inspiration_duration_ms = value;
}

int32_t MVMStateMachineCore::getExpiration_duration_ms() const
{
	return expiration_duration_ms;
}

void MVMStateMachineCore::setExpiration_duration_ms(int32_t value)
{
	this->expiration_duration_ms = value;
}

int32_t MVMStateMachineCore::getTriggerWindowDelay_ms() const
{
	return triggerWindowDelay_ms;
}

int32_t MVMStateMachineCore::getMin_insp_time_ms() const
{
	return min_insp_time_ms;
}

int32_t MVMStateMachineCore::getMax_insp_time_psv() const
{
	return max_insp_time_psv;
}

void MVMStateMachineCore::setMax_insp_time_psv(int32_t value)
{
	this->max_insp_time_psv = value;
}

int32_t MVMStateMachineCore::getMax_insp_time_asv() const
{
	return max_insp_time_asv;
}

void MVMStateMachineCore::setMax_insp_time_asv(int32_t value)
{
	this->max_insp_time_asv = value;
}

int32_t MVMStateMachineCore::getApnealag() const
{
	return apnealag;
}

void MVMStateMachineCore::setApnealag(int32_t value)
{
	this->apnealag = value;
}

sc_boolean MVMStateMachineCore::getApnea_backup_mode() const
{
	return apnea_backup_mode;
}

void MVMStateMachineCore::setApnea_backup_mode(sc_boolean value)
{
	this->apnea_backup_mode = value;
}

void MVMStateMachineCore::setOperationCallback(OperationCallback* operationCallback)
{
	ifaceOperationCallback = operationCallback;
}

void MVMStateMachineCore::setTraceObserver(sc::trace::TraceObserver<MVMStateMachineCoreStates>* tracingcallback) {
	ifaceTraceObserver = tracingcallback;
}

sc::trace::TraceObserver<MVMStateMachineCore::MVMStateMachineCoreStates>* MVMStateMachineCore::getTraceObserver() {
	return ifaceTraceObserver;
}

// implementations of all internal functions

sc_boolean MVMStateMachineCore::check_main_region_PCV_r1__choice_0_tr0_tr0()
{
	return exp_pause;
}

sc_boolean MVMStateMachineCore::check_main_region_PCV_r1__choice_1_tr0_tr0()
{
	return (mode) == (P_SUPPORTED_V);
}

sc_boolean MVMStateMachineCore::check_main_region_PCV_r1__choice_1_tr1_tr1()
{
	return ins_pause;
}

sc_boolean MVMStateMachineCore::check_main_region_PCV_r1__choice_2_tr0_tr0()
{
	return rm_request;
}

sc_boolean MVMStateMachineCore::check_main_region_PSV_r1__choice_0_tr0_tr0()
{
	return ins_pause;
}

sc_boolean MVMStateMachineCore::check_main_region_PSV_r1__choice_1_tr1_tr1()
{
	return rm_request;
}

void MVMStateMachineCore::effect_main_region_PCV_r1__choice_0_tr0()
{
	enseq_main_region_PCV_r1_ExpiratoryPause_default();
}

void MVMStateMachineCore::effect_main_region_PCV_r1__choice_0_tr1()
{
	enseq_main_region_PCV_r1_Inspiration_default();
}

void MVMStateMachineCore::effect_main_region_PCV_r1__choice_1_tr0()
{
	exseq_main_region_PCV();
	enact_main_region_PSV();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateEntered(MVMStateMachineCore::main_region_PSV);
	}
	react_main_region_PSV_r1__choice_0();
	react(0);
}

void MVMStateMachineCore::effect_main_region_PCV_r1__choice_1_tr1()
{
	enseq_main_region_PCV_r1_InspiratoryPause_default();
}

void MVMStateMachineCore::effect_main_region_PCV_r1__choice_1_tr2()
{
	react_main_region_PCV_r1__choice_2();
}

void MVMStateMachineCore::effect_main_region_PCV_r1__choice_2_tr0()
{
	enseq_main_region_PCV_r1_RM_default();
}

void MVMStateMachineCore::effect_main_region_PCV_r1__choice_2_tr1()
{
	enseq_main_region_PCV_r1_Expiration_default();
}

void MVMStateMachineCore::effect_main_region_PSV_r1__choice_0_tr0()
{
	enseq_main_region_PSV_r1_InspiratoryPause_default();
}

void MVMStateMachineCore::effect_main_region_PSV_r1__choice_0_tr1()
{
	react_main_region_PSV_r1__choice_1();
}

void MVMStateMachineCore::effect_main_region_PSV_r1__choice_1_tr1()
{
	enseq_main_region_PSV_r1_RM_default();
}

void MVMStateMachineCore::effect_main_region_PSV_r1__choice_1_tr0()
{
	enseq_main_region_PSV_r1_Expiration_default();
}

/* Entry action for state 'ExpiratoryPause'. */
void MVMStateMachineCore::enact_main_region_PCV_r1_ExpiratoryPause()
{
	/* Entry action for state 'ExpiratoryPause'. */
	timerService->setTimer(this, (sc_eventid)(&timeEvents[0]), MVMStateMachineCore::max_exp_pause, false);
}

/* Entry action for state 'Expiration'. */
void MVMStateMachineCore::enact_main_region_PCV_r1_Expiration()
{
	/* Entry action for state 'Expiration'. */
	timerService->setTimer(this, (sc_eventid)(&timeEvents[1]), expiration_duration_ms, false);
	timerService->setTimer(this, (sc_eventid)(&timeEvents[2]), MVMStateMachineCore::triggerWindowDelay_ms, false);
	ifaceOperationCallback->closeInputValve();
	ifaceOperationCallback->openOutputValve();
}

/* Entry action for state 'Inspiration'. */
void MVMStateMachineCore::enact_main_region_PCV_r1_Inspiration()
{
	/* Entry action for state 'Inspiration'. */
	timerService->setTimer(this, (sc_eventid)(&timeEvents[3]), inspiration_duration_ms, false);
	ifaceOperationCallback->openInputValve(PCV);
}

/* Entry action for state 'InspiratoryPause'. */
void MVMStateMachineCore::enact_main_region_PCV_r1_InspiratoryPause()
{
	/* Entry action for state 'InspiratoryPause'. */
	timerService->setTimer(this, (sc_eventid)(&timeEvents[4]), MVMStateMachineCore::max_ins_pause, false);
	ifaceOperationCallback->closeInputValve();
}

/* Entry action for state 'RM'. */
void MVMStateMachineCore::enact_main_region_PCV_r1_RM()
{
	/* Entry action for state 'RM'. */
	timerService->setTimer(this, (sc_eventid)(&timeEvents[5]), max_rm_time, false);
	ifaceOperationCallback->openInputValve(RM);
}

/* Entry action for state 'PSV'. */
void MVMStateMachineCore::enact_main_region_PSV()
{
	/* Entry action for state 'PSV'. */
	apnea_backup_mode = false;
}

/* Entry action for state 'ExpiratoryPause'. */
void MVMStateMachineCore::enact_main_region_PSV_r1_ExpiratoryPause()
{
	/* Entry action for state 'ExpiratoryPause'. */
	timerService->setTimer(this, (sc_eventid)(&timeEvents[6]), MVMStateMachineCore::max_exp_pause, false);
	ifaceOperationCallback->closeOutputValve();
}

/* Entry action for state 'Expiration'. */
void MVMStateMachineCore::enact_main_region_PSV_r1_Expiration()
{
	/* Entry action for state 'Expiration'. */
	timerService->setTimer(this, (sc_eventid)(&timeEvents[7]), MVMStateMachineCore::triggerWindowDelay_ms, false);
	timerService->setTimer(this, (sc_eventid)(&timeEvents[8]), apnealag, false);
	timerService->setTimer(this, (sc_eventid)(&timeEvents[9]), ifaceOperationCallback->min_exp_time_psv(), false);
	timerService->setTimer(this, (sc_eventid)(&timeEvents[10]), ifaceOperationCallback->min_exp_time_psv(), false);
	ifaceOperationCallback->closeInputValve();
	ifaceOperationCallback->openOutputValve();
}

/* Entry action for state 'Inspiration'. */
void MVMStateMachineCore::enact_main_region_PSV_r1_Inspiration()
{
	/* Entry action for state 'Inspiration'. */
	timerService->setTimer(this, (sc_eventid)(&timeEvents[11]), MVMStateMachineCore::min_insp_time_ms, false);
	timerService->setTimer(this, (sc_eventid)(&timeEvents[12]), max_insp_time_psv, false);
	ifaceOperationCallback->openInputValve(PSV);
}

/* Entry action for state 'InspiratoryPause'. */
void MVMStateMachineCore::enact_main_region_PSV_r1_InspiratoryPause()
{
	/* Entry action for state 'InspiratoryPause'. */
	timerService->setTimer(this, (sc_eventid)(&timeEvents[13]), MVMStateMachineCore::max_ins_pause, false);
	ifaceOperationCallback->closeInputValve();
}

/* Entry action for state 'RM'. */
void MVMStateMachineCore::enact_main_region_PSV_r1_RM()
{
	/* Entry action for state 'RM'. */
	timerService->setTimer(this, (sc_eventid)(&timeEvents[14]), max_rm_time, false);
	ifaceOperationCallback->openInputValve(RM);
}

/* Entry action for state 'VentilationOff'. */
void MVMStateMachineCore::enact_main_region_VentilationOff()
{
	/* Entry action for state 'VentilationOff'. */
	ifaceOperationCallback->closeInputValve();
	ifaceOperationCallback->openOutputValve();
	ifaceOperationCallback->finish();
}

/* Entry action for state 'ASV'. */
void MVMStateMachineCore::enact_main_region_ASV()
{
	/* Entry action for state 'ASV'. */
	apnea_backup_mode = false;
}

/* Entry action for state 'InitialExpiration'. */
void MVMStateMachineCore::enact_main_region_ASV_r1_InitialExpiration()
{
	/* Entry action for state 'InitialExpiration'. */
	timerService->setTimer(this, (sc_eventid)(&timeEvents[15]), MVMStateMachineCore::triggerWindowDelay_ms, false);
	timerService->setTimer(this, (sc_eventid)(&timeEvents[16]), expiration_duration_ms, false);
	timerService->setTimer(this, (sc_eventid)(&timeEvents[17]), expiration_duration_ms, false);
	timerService->setTimer(this, (sc_eventid)(&timeEvents[18]), MVMStateMachineCore::triggerWindowDelay_ms, false);
	ifaceOperationCallback->closeInputValve();
	ifaceOperationCallback->openOutputValve();
}

/* Entry action for state 'InitialInspiration'. */
void MVMStateMachineCore::enact_main_region_ASV_r1_InitialInspiration()
{
	/* Entry action for state 'InitialInspiration'. */
	timerService->setTimer(this, (sc_eventid)(&timeEvents[19]), MVMStateMachineCore::min_insp_time_ms, false);
	timerService->setTimer(this, (sc_eventid)(&timeEvents[20]), max_insp_time_asv, false);
	timerService->setTimer(this, (sc_eventid)(&timeEvents[21]), inspiration_duration_ms, false);
	numCycle = (numCycle + 1);
	ifaceOperationCallback->openInputValve(ASV);
}

/* Entry action for state 'Expiration'. */
void MVMStateMachineCore::enact_main_region_ASV_r1_Expiration()
{
	/* Entry action for state 'Expiration'. */
	timerService->setTimer(this, (sc_eventid)(&timeEvents[22]), MVMStateMachineCore::triggerWindowDelay_ms, false);
	ifaceOperationCallback->closeInputValve();
	ifaceOperationCallback->openOutputValve();
}

/* Entry action for state 'Inspiration'. */
void MVMStateMachineCore::enact_main_region_ASV_r1_Inspiration()
{
	/* Entry action for state 'Inspiration'. */
	timerService->setTimer(this, (sc_eventid)(&timeEvents[23]), MVMStateMachineCore::min_insp_time_ms, false);
	timerService->setTimer(this, (sc_eventid)(&timeEvents[24]), max_insp_time_asv, false);
	ifaceOperationCallback->openInputValve(ASV);
	numCycle = (numCycle + 1);
}

/* Exit action for state 'ExpiratoryPause'. */
void MVMStateMachineCore::exact_main_region_PCV_r1_ExpiratoryPause()
{
	/* Exit action for state 'ExpiratoryPause'. */
	timerService->unsetTimer(this, (sc_eventid)(&timeEvents[0]));
}

/* Exit action for state 'Expiration'. */
void MVMStateMachineCore::exact_main_region_PCV_r1_Expiration()
{
	/* Exit action for state 'Expiration'. */
	timerService->unsetTimer(this, (sc_eventid)(&timeEvents[1]));
	timerService->unsetTimer(this, (sc_eventid)(&timeEvents[2]));
	ifaceOperationCallback->closeOutputValve();
}

/* Exit action for state 'Inspiration'. */
void MVMStateMachineCore::exact_main_region_PCV_r1_Inspiration()
{
	/* Exit action for state 'Inspiration'. */
	timerService->unsetTimer(this, (sc_eventid)(&timeEvents[3]));
}

/* Exit action for state 'InspiratoryPause'. */
void MVMStateMachineCore::exact_main_region_PCV_r1_InspiratoryPause()
{
	/* Exit action for state 'InspiratoryPause'. */
	timerService->unsetTimer(this, (sc_eventid)(&timeEvents[4]));
}

/* Exit action for state 'RM'. */
void MVMStateMachineCore::exact_main_region_PCV_r1_RM()
{
	/* Exit action for state 'RM'. */
	timerService->unsetTimer(this, (sc_eventid)(&timeEvents[5]));
}

/* Exit action for state 'ExpiratoryPause'. */
void MVMStateMachineCore::exact_main_region_PSV_r1_ExpiratoryPause()
{
	/* Exit action for state 'ExpiratoryPause'. */
	timerService->unsetTimer(this, (sc_eventid)(&timeEvents[6]));
}

/* Exit action for state 'Expiration'. */
void MVMStateMachineCore::exact_main_region_PSV_r1_Expiration()
{
	/* Exit action for state 'Expiration'. */
	timerService->unsetTimer(this, (sc_eventid)(&timeEvents[7]));
	timerService->unsetTimer(this, (sc_eventid)(&timeEvents[8]));
	timerService->unsetTimer(this, (sc_eventid)(&timeEvents[9]));
	timerService->unsetTimer(this, (sc_eventid)(&timeEvents[10]));
	ifaceOperationCallback->closeOutputValve();
}

/* Exit action for state 'Inspiration'. */
void MVMStateMachineCore::exact_main_region_PSV_r1_Inspiration()
{
	/* Exit action for state 'Inspiration'. */
	timerService->unsetTimer(this, (sc_eventid)(&timeEvents[11]));
	timerService->unsetTimer(this, (sc_eventid)(&timeEvents[12]));
}

/* Exit action for state 'InspiratoryPause'. */
void MVMStateMachineCore::exact_main_region_PSV_r1_InspiratoryPause()
{
	/* Exit action for state 'InspiratoryPause'. */
	timerService->unsetTimer(this, (sc_eventid)(&timeEvents[13]));
}

/* Exit action for state 'RM'. */
void MVMStateMachineCore::exact_main_region_PSV_r1_RM()
{
	/* Exit action for state 'RM'. */
	timerService->unsetTimer(this, (sc_eventid)(&timeEvents[14]));
}

/* Exit action for state 'SelfTest'. */
void MVMStateMachineCore::exact_main_region_SelfTest()
{
	/* Exit action for state 'SelfTest'. */
	mode = P_CONTROLLED_V;
}

/* Exit action for state 'InitialExpiration'. */
void MVMStateMachineCore::exact_main_region_ASV_r1_InitialExpiration()
{
	/* Exit action for state 'InitialExpiration'. */
	timerService->unsetTimer(this, (sc_eventid)(&timeEvents[15]));
	timerService->unsetTimer(this, (sc_eventid)(&timeEvents[16]));
	timerService->unsetTimer(this, (sc_eventid)(&timeEvents[17]));
	timerService->unsetTimer(this, (sc_eventid)(&timeEvents[18]));
	ifaceOperationCallback->closeOutputValve();
}

/* Exit action for state 'InitialInspiration'. */
void MVMStateMachineCore::exact_main_region_ASV_r1_InitialInspiration()
{
	/* Exit action for state 'InitialInspiration'. */
	timerService->unsetTimer(this, (sc_eventid)(&timeEvents[19]));
	timerService->unsetTimer(this, (sc_eventid)(&timeEvents[20]));
	timerService->unsetTimer(this, (sc_eventid)(&timeEvents[21]));
}

/* Exit action for state 'Expiration'. */
void MVMStateMachineCore::exact_main_region_ASV_r1_Expiration()
{
	/* Exit action for state 'Expiration'. */
	timerService->unsetTimer(this, (sc_eventid)(&timeEvents[22]));
	ifaceOperationCallback->closeOutputValve();
}

/* Exit action for state 'Inspiration'. */
void MVMStateMachineCore::exact_main_region_ASV_r1_Inspiration()
{
	/* Exit action for state 'Inspiration'. */
	timerService->unsetTimer(this, (sc_eventid)(&timeEvents[23]));
	timerService->unsetTimer(this, (sc_eventid)(&timeEvents[24]));
}

/* 'default' enter sequence for state StartUp */
void MVMStateMachineCore::enseq_main_region_StartUp_default()
{
	/* 'default' enter sequence for state StartUp */
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateEntered(MVMStateMachineCore::main_region_StartUp);
	}
	stateConfVector[0] = main_region_StartUp;
}

/* 'default' enter sequence for state ExpiratoryPause */
void MVMStateMachineCore::enseq_main_region_PCV_r1_ExpiratoryPause_default()
{
	/* 'default' enter sequence for state ExpiratoryPause */
	enact_main_region_PCV_r1_ExpiratoryPause();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateEntered(MVMStateMachineCore::main_region_PCV_r1_ExpiratoryPause);
	}
	stateConfVector[0] = main_region_PCV_r1_ExpiratoryPause;
}

/* 'default' enter sequence for state Expiration */
void MVMStateMachineCore::enseq_main_region_PCV_r1_Expiration_default()
{
	/* 'default' enter sequence for state Expiration */
	enact_main_region_PCV_r1_Expiration();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateEntered(MVMStateMachineCore::main_region_PCV_r1_Expiration);
	}
	stateConfVector[0] = main_region_PCV_r1_Expiration;
}

/* 'default' enter sequence for state Inspiration */
void MVMStateMachineCore::enseq_main_region_PCV_r1_Inspiration_default()
{
	/* 'default' enter sequence for state Inspiration */
	enact_main_region_PCV_r1_Inspiration();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateEntered(MVMStateMachineCore::main_region_PCV_r1_Inspiration);
	}
	stateConfVector[0] = main_region_PCV_r1_Inspiration;
}

/* 'default' enter sequence for state InspiratoryPause */
void MVMStateMachineCore::enseq_main_region_PCV_r1_InspiratoryPause_default()
{
	/* 'default' enter sequence for state InspiratoryPause */
	enact_main_region_PCV_r1_InspiratoryPause();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateEntered(MVMStateMachineCore::main_region_PCV_r1_InspiratoryPause);
	}
	stateConfVector[0] = main_region_PCV_r1_InspiratoryPause;
}

/* 'default' enter sequence for state RM */
void MVMStateMachineCore::enseq_main_region_PCV_r1_RM_default()
{
	/* 'default' enter sequence for state RM */
	enact_main_region_PCV_r1_RM();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateEntered(MVMStateMachineCore::main_region_PCV_r1_RM);
	}
	stateConfVector[0] = main_region_PCV_r1_RM;
}

/* 'default' enter sequence for state ExpiratoryPause */
void MVMStateMachineCore::enseq_main_region_PSV_r1_ExpiratoryPause_default()
{
	/* 'default' enter sequence for state ExpiratoryPause */
	enact_main_region_PSV_r1_ExpiratoryPause();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateEntered(MVMStateMachineCore::main_region_PSV_r1_ExpiratoryPause);
	}
	stateConfVector[0] = main_region_PSV_r1_ExpiratoryPause;
}

/* 'default' enter sequence for state Expiration */
void MVMStateMachineCore::enseq_main_region_PSV_r1_Expiration_default()
{
	/* 'default' enter sequence for state Expiration */
	enact_main_region_PSV_r1_Expiration();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateEntered(MVMStateMachineCore::main_region_PSV_r1_Expiration);
	}
	stateConfVector[0] = main_region_PSV_r1_Expiration;
}

/* 'default' enter sequence for state Inspiration */
void MVMStateMachineCore::enseq_main_region_PSV_r1_Inspiration_default()
{
	/* 'default' enter sequence for state Inspiration */
	enact_main_region_PSV_r1_Inspiration();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateEntered(MVMStateMachineCore::main_region_PSV_r1_Inspiration);
	}
	stateConfVector[0] = main_region_PSV_r1_Inspiration;
}

/* 'default' enter sequence for state InspiratoryPause */
void MVMStateMachineCore::enseq_main_region_PSV_r1_InspiratoryPause_default()
{
	/* 'default' enter sequence for state InspiratoryPause */
	enact_main_region_PSV_r1_InspiratoryPause();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateEntered(MVMStateMachineCore::main_region_PSV_r1_InspiratoryPause);
	}
	stateConfVector[0] = main_region_PSV_r1_InspiratoryPause;
}

/* 'default' enter sequence for state RM */
void MVMStateMachineCore::enseq_main_region_PSV_r1_RM_default()
{
	/* 'default' enter sequence for state RM */
	enact_main_region_PSV_r1_RM();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateEntered(MVMStateMachineCore::main_region_PSV_r1_RM);
	}
	stateConfVector[0] = main_region_PSV_r1_RM;
}

/* Default enter sequence for state null */
void MVMStateMachineCore::enseq_main_region__final__default()
{
	/* Default enter sequence for state null */
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateEntered(MVMStateMachineCore::main_region__final_);
	}
	stateConfVector[0] = main_region__final_;
}

/* 'default' enter sequence for state SelfTest */
void MVMStateMachineCore::enseq_main_region_SelfTest_default()
{
	/* 'default' enter sequence for state SelfTest */
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateEntered(MVMStateMachineCore::main_region_SelfTest);
	}
	stateConfVector[0] = main_region_SelfTest;
}

/* 'default' enter sequence for state VentilationOff */
void MVMStateMachineCore::enseq_main_region_VentilationOff_default()
{
	/* 'default' enter sequence for state VentilationOff */
	enact_main_region_VentilationOff();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateEntered(MVMStateMachineCore::main_region_VentilationOff);
	}
	stateConfVector[0] = main_region_VentilationOff;
}

/* 'default' enter sequence for state InitialExpiration */
void MVMStateMachineCore::enseq_main_region_ASV_r1_InitialExpiration_default()
{
	/* 'default' enter sequence for state InitialExpiration */
	enact_main_region_ASV_r1_InitialExpiration();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateEntered(MVMStateMachineCore::main_region_ASV_r1_InitialExpiration);
	}
	stateConfVector[0] = main_region_ASV_r1_InitialExpiration;
}

/* 'default' enter sequence for state InitialInspiration */
void MVMStateMachineCore::enseq_main_region_ASV_r1_InitialInspiration_default()
{
	/* 'default' enter sequence for state InitialInspiration */
	enact_main_region_ASV_r1_InitialInspiration();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateEntered(MVMStateMachineCore::main_region_ASV_r1_InitialInspiration);
	}
	stateConfVector[0] = main_region_ASV_r1_InitialInspiration;
}

/* 'default' enter sequence for state Expiration */
void MVMStateMachineCore::enseq_main_region_ASV_r1_Expiration_default()
{
	/* 'default' enter sequence for state Expiration */
	enact_main_region_ASV_r1_Expiration();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateEntered(MVMStateMachineCore::main_region_ASV_r1_Expiration);
	}
	stateConfVector[0] = main_region_ASV_r1_Expiration;
}

/* 'default' enter sequence for state Inspiration */
void MVMStateMachineCore::enseq_main_region_ASV_r1_Inspiration_default()
{
	/* 'default' enter sequence for state Inspiration */
	enact_main_region_ASV_r1_Inspiration();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateEntered(MVMStateMachineCore::main_region_ASV_r1_Inspiration);
	}
	stateConfVector[0] = main_region_ASV_r1_Inspiration;
}

/* 'default' enter sequence for region main region */
void MVMStateMachineCore::enseq_main_region_default()
{
	/* 'default' enter sequence for region main region */
	react_main_region__entry_Default();
}

/* Default exit sequence for state StartUp */
void MVMStateMachineCore::exseq_main_region_StartUp()
{
	/* Default exit sequence for state StartUp */
	stateConfVector[0] = MVMStateMachineCore_last_state;
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_StartUp);
	}
}

/* Default exit sequence for state PCV */
void MVMStateMachineCore::exseq_main_region_PCV()
{
	/* Default exit sequence for state PCV */
	exseq_main_region_PCV_r1();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_PCV);
	}
}

/* Default exit sequence for state ExpiratoryPause */
void MVMStateMachineCore::exseq_main_region_PCV_r1_ExpiratoryPause()
{
	/* Default exit sequence for state ExpiratoryPause */
	stateConfVector[0] = MVMStateMachineCore_last_state;
	exact_main_region_PCV_r1_ExpiratoryPause();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_PCV_r1_ExpiratoryPause);
	}
}

/* Default exit sequence for state Expiration */
void MVMStateMachineCore::exseq_main_region_PCV_r1_Expiration()
{
	/* Default exit sequence for state Expiration */
	stateConfVector[0] = MVMStateMachineCore_last_state;
	exact_main_region_PCV_r1_Expiration();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_PCV_r1_Expiration);
	}
}

/* Default exit sequence for state Inspiration */
void MVMStateMachineCore::exseq_main_region_PCV_r1_Inspiration()
{
	/* Default exit sequence for state Inspiration */
	stateConfVector[0] = MVMStateMachineCore_last_state;
	exact_main_region_PCV_r1_Inspiration();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_PCV_r1_Inspiration);
	}
}

/* Default exit sequence for state InspiratoryPause */
void MVMStateMachineCore::exseq_main_region_PCV_r1_InspiratoryPause()
{
	/* Default exit sequence for state InspiratoryPause */
	stateConfVector[0] = MVMStateMachineCore_last_state;
	exact_main_region_PCV_r1_InspiratoryPause();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_PCV_r1_InspiratoryPause);
	}
}

/* Default exit sequence for state RM */
void MVMStateMachineCore::exseq_main_region_PCV_r1_RM()
{
	/* Default exit sequence for state RM */
	stateConfVector[0] = MVMStateMachineCore_last_state;
	exact_main_region_PCV_r1_RM();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_PCV_r1_RM);
	}
}

/* Default exit sequence for state PSV */
void MVMStateMachineCore::exseq_main_region_PSV()
{
	/* Default exit sequence for state PSV */
	exseq_main_region_PSV_r1();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_PSV);
	}
}

/* Default exit sequence for state ExpiratoryPause */
void MVMStateMachineCore::exseq_main_region_PSV_r1_ExpiratoryPause()
{
	/* Default exit sequence for state ExpiratoryPause */
	stateConfVector[0] = MVMStateMachineCore_last_state;
	exact_main_region_PSV_r1_ExpiratoryPause();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_PSV_r1_ExpiratoryPause);
	}
}

/* Default exit sequence for state Expiration */
void MVMStateMachineCore::exseq_main_region_PSV_r1_Expiration()
{
	/* Default exit sequence for state Expiration */
	stateConfVector[0] = MVMStateMachineCore_last_state;
	exact_main_region_PSV_r1_Expiration();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_PSV_r1_Expiration);
	}
}

/* Default exit sequence for state Inspiration */
void MVMStateMachineCore::exseq_main_region_PSV_r1_Inspiration()
{
	/* Default exit sequence for state Inspiration */
	stateConfVector[0] = MVMStateMachineCore_last_state;
	exact_main_region_PSV_r1_Inspiration();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_PSV_r1_Inspiration);
	}
}

/* Default exit sequence for state InspiratoryPause */
void MVMStateMachineCore::exseq_main_region_PSV_r1_InspiratoryPause()
{
	/* Default exit sequence for state InspiratoryPause */
	stateConfVector[0] = MVMStateMachineCore_last_state;
	exact_main_region_PSV_r1_InspiratoryPause();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_PSV_r1_InspiratoryPause);
	}
}

/* Default exit sequence for state RM */
void MVMStateMachineCore::exseq_main_region_PSV_r1_RM()
{
	/* Default exit sequence for state RM */
	stateConfVector[0] = MVMStateMachineCore_last_state;
	exact_main_region_PSV_r1_RM();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_PSV_r1_RM);
	}
}

/* Default exit sequence for final state. */
void MVMStateMachineCore::exseq_main_region__final_()
{
	/* Default exit sequence for final state. */
	stateConfVector[0] = MVMStateMachineCore_last_state;
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region__final_);
	}
}

/* Default exit sequence for state SelfTest */
void MVMStateMachineCore::exseq_main_region_SelfTest()
{
	/* Default exit sequence for state SelfTest */
	stateConfVector[0] = MVMStateMachineCore_last_state;
	exact_main_region_SelfTest();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_SelfTest);
	}
}

/* Default exit sequence for state VentilationOff */
void MVMStateMachineCore::exseq_main_region_VentilationOff()
{
	/* Default exit sequence for state VentilationOff */
	stateConfVector[0] = MVMStateMachineCore_last_state;
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_VentilationOff);
	}
}

/* Default exit sequence for state ASV */
void MVMStateMachineCore::exseq_main_region_ASV()
{
	/* Default exit sequence for state ASV */
	exseq_main_region_ASV_r1();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_ASV);
	}
}

/* Default exit sequence for state InitialExpiration */
void MVMStateMachineCore::exseq_main_region_ASV_r1_InitialExpiration()
{
	/* Default exit sequence for state InitialExpiration */
	stateConfVector[0] = MVMStateMachineCore_last_state;
	exact_main_region_ASV_r1_InitialExpiration();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_ASV_r1_InitialExpiration);
	}
}

/* Default exit sequence for state InitialInspiration */
void MVMStateMachineCore::exseq_main_region_ASV_r1_InitialInspiration()
{
	/* Default exit sequence for state InitialInspiration */
	stateConfVector[0] = MVMStateMachineCore_last_state;
	exact_main_region_ASV_r1_InitialInspiration();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_ASV_r1_InitialInspiration);
	}
}

/* Default exit sequence for state Expiration */
void MVMStateMachineCore::exseq_main_region_ASV_r1_Expiration()
{
	/* Default exit sequence for state Expiration */
	stateConfVector[0] = MVMStateMachineCore_last_state;
	exact_main_region_ASV_r1_Expiration();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_ASV_r1_Expiration);
	}
}

/* Default exit sequence for state Inspiration */
void MVMStateMachineCore::exseq_main_region_ASV_r1_Inspiration()
{
	/* Default exit sequence for state Inspiration */
	stateConfVector[0] = MVMStateMachineCore_last_state;
	exact_main_region_ASV_r1_Inspiration();
	if(ifaceTraceObserver != sc_null) {
		ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_ASV_r1_Inspiration);
	}
}

/* Default exit sequence for region main region */
void MVMStateMachineCore::exseq_main_region()
{
	/* Default exit sequence for region main region */
	/* Handle exit of all possible states (of MVMStateMachineCore.main_region) at position 0... */
	switch(stateConfVector[ 0 ])
	{
		case main_region_StartUp :
		{
			exseq_main_region_StartUp();
			break;
		}
		case main_region_PCV_r1_ExpiratoryPause :
		{
			exseq_main_region_PCV_r1_ExpiratoryPause();
			if(ifaceTraceObserver != sc_null) {
				ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_PCV);
			}
			break;
		}
		case main_region_PCV_r1_Expiration :
		{
			exseq_main_region_PCV_r1_Expiration();
			if(ifaceTraceObserver != sc_null) {
				ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_PCV);
			}
			break;
		}
		case main_region_PCV_r1_Inspiration :
		{
			exseq_main_region_PCV_r1_Inspiration();
			if(ifaceTraceObserver != sc_null) {
				ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_PCV);
			}
			break;
		}
		case main_region_PCV_r1_InspiratoryPause :
		{
			exseq_main_region_PCV_r1_InspiratoryPause();
			if(ifaceTraceObserver != sc_null) {
				ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_PCV);
			}
			break;
		}
		case main_region_PCV_r1_RM :
		{
			exseq_main_region_PCV_r1_RM();
			if(ifaceTraceObserver != sc_null) {
				ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_PCV);
			}
			break;
		}
		case main_region_PSV_r1_ExpiratoryPause :
		{
			exseq_main_region_PSV_r1_ExpiratoryPause();
			if(ifaceTraceObserver != sc_null) {
				ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_PSV);
			}
			break;
		}
		case main_region_PSV_r1_Expiration :
		{
			exseq_main_region_PSV_r1_Expiration();
			if(ifaceTraceObserver != sc_null) {
				ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_PSV);
			}
			break;
		}
		case main_region_PSV_r1_Inspiration :
		{
			exseq_main_region_PSV_r1_Inspiration();
			if(ifaceTraceObserver != sc_null) {
				ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_PSV);
			}
			break;
		}
		case main_region_PSV_r1_InspiratoryPause :
		{
			exseq_main_region_PSV_r1_InspiratoryPause();
			if(ifaceTraceObserver != sc_null) {
				ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_PSV);
			}
			break;
		}
		case main_region_PSV_r1_RM :
		{
			exseq_main_region_PSV_r1_RM();
			if(ifaceTraceObserver != sc_null) {
				ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_PSV);
			}
			break;
		}
		case main_region__final_ :
		{
			exseq_main_region__final_();
			break;
		}
		case main_region_SelfTest :
		{
			exseq_main_region_SelfTest();
			break;
		}
		case main_region_VentilationOff :
		{
			exseq_main_region_VentilationOff();
			break;
		}
		case main_region_ASV_r1_InitialExpiration :
		{
			exseq_main_region_ASV_r1_InitialExpiration();
			if(ifaceTraceObserver != sc_null) {
				ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_ASV);
			}
			break;
		}
		case main_region_ASV_r1_InitialInspiration :
		{
			exseq_main_region_ASV_r1_InitialInspiration();
			if(ifaceTraceObserver != sc_null) {
				ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_ASV);
			}
			break;
		}
		case main_region_ASV_r1_Expiration :
		{
			exseq_main_region_ASV_r1_Expiration();
			if(ifaceTraceObserver != sc_null) {
				ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_ASV);
			}
			break;
		}
		case main_region_ASV_r1_Inspiration :
		{
			exseq_main_region_ASV_r1_Inspiration();
			if(ifaceTraceObserver != sc_null) {
				ifaceTraceObserver->stateExited(MVMStateMachineCore::main_region_ASV);
			}
			break;
		}
		default: break;
	}
}

/* Default exit sequence for region r1 */
void MVMStateMachineCore::exseq_main_region_PCV_r1()
{
	/* Default exit sequence for region r1 */
	/* Handle exit of all possible states (of MVMStateMachineCore.main_region.PCV.r1) at position 0... */
	switch(stateConfVector[ 0 ])
	{
		case main_region_PCV_r1_ExpiratoryPause :
		{
			exseq_main_region_PCV_r1_ExpiratoryPause();
			break;
		}
		case main_region_PCV_r1_Expiration :
		{
			exseq_main_region_PCV_r1_Expiration();
			break;
		}
		case main_region_PCV_r1_Inspiration :
		{
			exseq_main_region_PCV_r1_Inspiration();
			break;
		}
		case main_region_PCV_r1_InspiratoryPause :
		{
			exseq_main_region_PCV_r1_InspiratoryPause();
			break;
		}
		case main_region_PCV_r1_RM :
		{
			exseq_main_region_PCV_r1_RM();
			break;
		}
		default: break;
	}
}

/* Default exit sequence for region r1 */
void MVMStateMachineCore::exseq_main_region_PSV_r1()
{
	/* Default exit sequence for region r1 */
	/* Handle exit of all possible states (of MVMStateMachineCore.main_region.PSV.r1) at position 0... */
	switch(stateConfVector[ 0 ])
	{
		case main_region_PSV_r1_ExpiratoryPause :
		{
			exseq_main_region_PSV_r1_ExpiratoryPause();
			break;
		}
		case main_region_PSV_r1_Expiration :
		{
			exseq_main_region_PSV_r1_Expiration();
			break;
		}
		case main_region_PSV_r1_Inspiration :
		{
			exseq_main_region_PSV_r1_Inspiration();
			break;
		}
		case main_region_PSV_r1_InspiratoryPause :
		{
			exseq_main_region_PSV_r1_InspiratoryPause();
			break;
		}
		case main_region_PSV_r1_RM :
		{
			exseq_main_region_PSV_r1_RM();
			break;
		}
		default: break;
	}
}

/* Default exit sequence for region r1 */
void MVMStateMachineCore::exseq_main_region_ASV_r1()
{
	/* Default exit sequence for region r1 */
	/* Handle exit of all possible states (of MVMStateMachineCore.main_region.ASV.r1) at position 0... */
	switch(stateConfVector[ 0 ])
	{
		case main_region_ASV_r1_InitialExpiration :
		{
			exseq_main_region_ASV_r1_InitialExpiration();
			break;
		}
		case main_region_ASV_r1_InitialInspiration :
		{
			exseq_main_region_ASV_r1_InitialInspiration();
			break;
		}
		case main_region_ASV_r1_Expiration :
		{
			exseq_main_region_ASV_r1_Expiration();
			break;
		}
		case main_region_ASV_r1_Inspiration :
		{
			exseq_main_region_ASV_r1_Inspiration();
			break;
		}
		default: break;
	}
}

/* The reactions of state null. */
void MVMStateMachineCore::react_main_region_PCV_r1__choice_0()
{
	/* The reactions of state null. */
	if (check_main_region_PCV_r1__choice_0_tr0_tr0())
	{ 
		effect_main_region_PCV_r1__choice_0_tr0();
	}  else
	{
		effect_main_region_PCV_r1__choice_0_tr1();
	}
}

/* The reactions of state null. */
void MVMStateMachineCore::react_main_region_PCV_r1__choice_1()
{
	/* The reactions of state null. */
	if (check_main_region_PCV_r1__choice_1_tr0_tr0())
	{ 
		effect_main_region_PCV_r1__choice_1_tr0();
	}  else
	{
		if (check_main_region_PCV_r1__choice_1_tr1_tr1())
		{ 
			effect_main_region_PCV_r1__choice_1_tr1();
		}  else
		{
			effect_main_region_PCV_r1__choice_1_tr2();
		}
	}
}

/* The reactions of state null. */
void MVMStateMachineCore::react_main_region_PCV_r1__choice_2()
{
	/* The reactions of state null. */
	if (check_main_region_PCV_r1__choice_2_tr0_tr0())
	{ 
		effect_main_region_PCV_r1__choice_2_tr0();
	}  else
	{
		effect_main_region_PCV_r1__choice_2_tr1();
	}
}

/* The reactions of state null. */
void MVMStateMachineCore::react_main_region_PSV_r1__choice_0()
{
	/* The reactions of state null. */
	if (check_main_region_PSV_r1__choice_0_tr0_tr0())
	{ 
		effect_main_region_PSV_r1__choice_0_tr0();
	}  else
	{
		effect_main_region_PSV_r1__choice_0_tr1();
	}
}

/* The reactions of state null. */
void MVMStateMachineCore::react_main_region_PSV_r1__choice_1()
{
	/* The reactions of state null. */
	if (check_main_region_PSV_r1__choice_1_tr1_tr1())
	{ 
		effect_main_region_PSV_r1__choice_1_tr1();
	}  else
	{
		effect_main_region_PSV_r1__choice_1_tr0();
	}
}

/* Default react sequence for initial entry  */
void MVMStateMachineCore::react_main_region__entry_Default()
{
	/* Default react sequence for initial entry  */
	enseq_main_region_StartUp_default();
}

sc_integer MVMStateMachineCore::react(const sc_integer transitioned_before) {
	/* State machine reactions. */
	return transitioned_before;
}

sc_integer MVMStateMachineCore::main_region_StartUp_react(const sc_integer transitioned_before) {
	/* The reactions of state StartUp. */
	sc_integer transitioned_after = transitioned_before;
	if ((transitioned_after) < (0))
	{ 
		if (current.iface.poweroff_raised)
		{ 
			exseq_main_region_StartUp();
			enseq_main_region__final__default();
			transitioned_after = 0;
		}  else
		{
			if (current.iface.startupEnded_raised)
			{ 
				exseq_main_region_StartUp();
				enseq_main_region_SelfTest_default();
				react(0);
				transitioned_after = 0;
			} 
		}
	} 
	/* If no transition was taken then execute local reactions */
	if ((transitioned_after) == (transitioned_before))
	{ 
		transitioned_after = react(transitioned_before);
	} 
	return transitioned_after;
}

sc_integer MVMStateMachineCore::main_region_PCV_react(const sc_integer transitioned_before) {
	/* The reactions of state PCV. */
	sc_integer transitioned_after = transitioned_before;
	if ((transitioned_after) < (0))
	{ 
		if (current.iface.poweroff_raised)
		{ 
			exseq_main_region_PCV();
			enseq_main_region__final__default();
			transitioned_after = 0;
		}  else
		{
			if (stopVentilation)
			{ 
				exseq_main_region_PCV();
				apnea_backup_mode = false;
				stopVentilation = false;
				enseq_main_region_VentilationOff_default();
				react(0);
				transitioned_after = 0;
			} 
		}
	} 
	/* If no transition was taken then execute local reactions */
	if ((transitioned_after) == (transitioned_before))
	{ 
		transitioned_after = react(transitioned_before);
	} 
	return transitioned_after;
}

sc_integer MVMStateMachineCore::main_region_PCV_r1_ExpiratoryPause_react(const sc_integer transitioned_before) {
	/* The reactions of state ExpiratoryPause. */
	sc_integer transitioned_after = transitioned_before;
	if ((transitioned_after) < (0))
	{ 
		if (current.timeEvents.MVMStateMachineCore_main_region_PCV_r1_ExpiratoryPause_time_event_0_raised)
		{ 
			exseq_main_region_PCV_r1_ExpiratoryPause();
			exp_pause = false;
			enseq_main_region_PCV_r1_Inspiration_default();
			main_region_PCV_react(0);
			transitioned_after = 0;
		}  else
		{
			if (!exp_pause)
			{ 
				exseq_main_region_PCV_r1_ExpiratoryPause();
				enseq_main_region_PCV_r1_Inspiration_default();
				main_region_PCV_react(0);
				transitioned_after = 0;
			} 
		}
	} 
	/* If no transition was taken then execute local reactions */
	if ((transitioned_after) == (transitioned_before))
	{ 
		transitioned_after = main_region_PCV_react(transitioned_before);
	} 
	return transitioned_after;
}

sc_integer MVMStateMachineCore::main_region_PCV_r1_Expiration_react(const sc_integer transitioned_before) {
	/* The reactions of state Expiration. */
	sc_integer transitioned_after = transitioned_before;
	if ((transitioned_after) < (0))
	{ 
		if (current.timeEvents.MVMStateMachineCore_main_region_PCV_r1_Expiration_time_event_0_raised)
		{ 
			exseq_main_region_PCV_r1_Expiration();
			ifaceOperationCallback->autoBreath(false);
			react_main_region_PCV_r1__choice_0();
			transitioned_after = 0;
		}  else
		{
			if (((current.timeEvents.MVMStateMachineCore_main_region_PCV_r1_Expiration_time_event_1_raised)) && (((ifaceOperationCallback->dropPAW_ITS_PCV()) && (!exp_pause))))
			{ 
				exseq_main_region_PCV_r1_Expiration();
				ifaceOperationCallback->autoBreath(true);
				enseq_main_region_PCV_r1_Inspiration_default();
				main_region_PCV_react(0);
				transitioned_after = 0;
			} 
		}
	} 
	/* If no transition was taken then execute local reactions */
	if ((transitioned_after) == (transitioned_before))
	{ 
		transitioned_after = main_region_PCV_react(transitioned_before);
	} 
	return transitioned_after;
}

sc_integer MVMStateMachineCore::main_region_PCV_r1_Inspiration_react(const sc_integer transitioned_before) {
	/* The reactions of state Inspiration. */
	sc_integer transitioned_after = transitioned_before;
	if ((transitioned_after) < (0))
	{ 
		if (current.timeEvents.MVMStateMachineCore_main_region_PCV_r1_Inspiration_time_event_0_raised)
		{ 
			exseq_main_region_PCV_r1_Inspiration();
			react_main_region_PCV_r1__choice_1();
			transitioned_after = 0;
		}  else
		{
			if (ifaceOperationCallback->pawGTMaxPinsp())
			{ 
				exseq_main_region_PCV_r1_Inspiration();
				enseq_main_region_PCV_r1_Expiration_default();
				main_region_PCV_react(0);
				transitioned_after = 0;
			} 
		}
	} 
	/* If no transition was taken then execute local reactions */
	if ((transitioned_after) == (transitioned_before))
	{ 
		transitioned_after = main_region_PCV_react(transitioned_before);
	} 
	return transitioned_after;
}

sc_integer MVMStateMachineCore::main_region_PCV_r1_InspiratoryPause_react(const sc_integer transitioned_before) {
	/* The reactions of state InspiratoryPause. */
	sc_integer transitioned_after = transitioned_before;
	if ((transitioned_after) < (0))
	{ 
		if (!ins_pause)
		{ 
			exseq_main_region_PCV_r1_InspiratoryPause();
			enseq_main_region_PCV_r1_Expiration_default();
			main_region_PCV_react(0);
			transitioned_after = 0;
		}  else
		{
			if (current.timeEvents.MVMStateMachineCore_main_region_PCV_r1_InspiratoryPause_time_event_0_raised)
			{ 
				exseq_main_region_PCV_r1_InspiratoryPause();
				ins_pause = false;
				enseq_main_region_PCV_r1_Expiration_default();
				main_region_PCV_react(0);
				transitioned_after = 0;
			} 
		}
	} 
	/* If no transition was taken then execute local reactions */
	if ((transitioned_after) == (transitioned_before))
	{ 
		transitioned_after = main_region_PCV_react(transitioned_before);
	} 
	return transitioned_after;
}

sc_integer MVMStateMachineCore::main_region_PCV_r1_RM_react(const sc_integer transitioned_before) {
	/* The reactions of state RM. */
	sc_integer transitioned_after = transitioned_before;
	if ((transitioned_after) < (0))
	{ 
		if (!rm_request)
		{ 
			exseq_main_region_PCV_r1_RM();
			enseq_main_region_PCV_r1_Expiration_default();
			main_region_PCV_react(0);
			transitioned_after = 0;
		}  else
		{
			if (current.timeEvents.MVMStateMachineCore_main_region_PCV_r1_RM_time_event_0_raised)
			{ 
				exseq_main_region_PCV_r1_RM();
				rm_request = false;
				enseq_main_region_PCV_r1_Expiration_default();
				main_region_PCV_react(0);
				transitioned_after = 0;
			} 
		}
	} 
	/* If no transition was taken then execute local reactions */
	if ((transitioned_after) == (transitioned_before))
	{ 
		transitioned_after = main_region_PCV_react(transitioned_before);
	} 
	return transitioned_after;
}

sc_integer MVMStateMachineCore::main_region_PSV_react(const sc_integer transitioned_before) {
	/* The reactions of state PSV. */
	sc_integer transitioned_after = transitioned_before;
	if ((transitioned_after) < (0))
	{ 
		if (current.iface.poweroff_raised)
		{ 
			exseq_main_region_PSV();
			enseq_main_region__final__default();
			transitioned_after = 0;
		}  else
		{
			if (stopVentilation)
			{ 
				exseq_main_region_PSV();
				stopVentilation = false;
				enseq_main_region_VentilationOff_default();
				react(0);
				transitioned_after = 0;
			} 
		}
	} 
	/* If no transition was taken then execute local reactions */
	if ((transitioned_after) == (transitioned_before))
	{ 
		transitioned_after = react(transitioned_before);
	} 
	return transitioned_after;
}

sc_integer MVMStateMachineCore::main_region_PSV_r1_ExpiratoryPause_react(const sc_integer transitioned_before) {
	/* The reactions of state ExpiratoryPause. */
	sc_integer transitioned_after = transitioned_before;
	if ((transitioned_after) < (0))
	{ 
		if (current.timeEvents.MVMStateMachineCore_main_region_PSV_r1_ExpiratoryPause_time_event_0_raised)
		{ 
			exseq_main_region_PSV_r1_ExpiratoryPause();
			exp_pause = false;
			enseq_main_region_PSV_r1_Inspiration_default();
			main_region_PSV_react(0);
			transitioned_after = 0;
		}  else
		{
			if (!exp_pause)
			{ 
				exseq_main_region_PSV_r1_ExpiratoryPause();
				enseq_main_region_PSV_r1_Inspiration_default();
				main_region_PSV_react(0);
				transitioned_after = 0;
			} 
		}
	} 
	/* If no transition was taken then execute local reactions */
	if ((transitioned_after) == (transitioned_before))
	{ 
		transitioned_after = main_region_PSV_react(transitioned_before);
	} 
	return transitioned_after;
}

sc_integer MVMStateMachineCore::main_region_PSV_r1_Expiration_react(const sc_integer transitioned_before) {
	/* The reactions of state Expiration. */
	sc_integer transitioned_after = transitioned_before;
	if ((transitioned_after) < (0))
	{ 
		if (((current.timeEvents.MVMStateMachineCore_main_region_PSV_r1_Expiration_time_event_0_raised)) && (((ifaceOperationCallback->dropPAW_ITS_PSV()) && (!exp_pause))))
		{ 
			exseq_main_region_PSV_r1_Expiration();
			ifaceOperationCallback->autoBreath(true);
			enseq_main_region_PSV_r1_Inspiration_default();
			main_region_PSV_react(0);
			transitioned_after = 0;
		}  else
		{
			if (current.timeEvents.MVMStateMachineCore_main_region_PSV_r1_Expiration_time_event_1_raised)
			{ 
				exseq_main_region_PSV();
				ifaceOperationCallback->apneaAlarm();
				apnea_backup_mode = true;
				mode = P_CONTROLLED_V;
				ifaceOperationCallback->autoBreath(false);
				if(ifaceTraceObserver != sc_null) {
					ifaceTraceObserver->stateEntered(MVMStateMachineCore::main_region_PCV);
				}
				enseq_main_region_PCV_r1_Inspiration_default();
				react(0);
				transitioned_after = 0;
			}  else
			{
				if (((current.timeEvents.MVMStateMachineCore_main_region_PSV_r1_Expiration_time_event_2_raised)) && ((exp_pause)))
				{ 
					exseq_main_region_PSV_r1_Expiration();
					ifaceOperationCallback->autoBreath(false);
					enseq_main_region_PSV_r1_ExpiratoryPause_default();
					main_region_PSV_react(0);
					transitioned_after = 0;
				}  else
				{
					if (((current.timeEvents.MVMStateMachineCore_main_region_PSV_r1_Expiration_time_event_3_raised)) && (((mode) == (P_CONTROLLED_V))))
					{ 
						exseq_main_region_PSV();
						ifaceOperationCallback->autoBreath(false);
						if(ifaceTraceObserver != sc_null) {
							ifaceTraceObserver->stateEntered(MVMStateMachineCore::main_region_PCV);
						}
						enseq_main_region_PCV_r1_Inspiration_default();
						react(0);
						transitioned_after = 0;
					} 
				}
			}
		}
	} 
	/* If no transition was taken then execute local reactions */
	if ((transitioned_after) == (transitioned_before))
	{ 
		transitioned_after = main_region_PSV_react(transitioned_before);
	} 
	return transitioned_after;
}

sc_integer MVMStateMachineCore::main_region_PSV_r1_Inspiration_react(const sc_integer transitioned_before) {
	/* The reactions of state Inspiration. */
	sc_integer transitioned_after = transitioned_before;
	if ((transitioned_after) < (0))
	{ 
		if (((current.timeEvents.MVMStateMachineCore_main_region_PSV_r1_Inspiration_time_event_0_raised)) && ((ifaceOperationCallback->flowDropPSV())))
		{ 
			exseq_main_region_PSV_r1_Inspiration();
			react_main_region_PSV_r1__choice_0();
			transitioned_after = 0;
		}  else
		{
			if (current.timeEvents.MVMStateMachineCore_main_region_PSV_r1_Inspiration_time_event_1_raised)
			{ 
				exseq_main_region_PSV_r1_Inspiration();
				react_main_region_PSV_r1__choice_0();
				transitioned_after = 0;
			}  else
			{
				if (ifaceOperationCallback->pawGTMaxPinsp())
				{ 
					exseq_main_region_PSV_r1_Inspiration();
					enseq_main_region_PSV_r1_Expiration_default();
					main_region_PSV_react(0);
					transitioned_after = 0;
				} 
			}
		}
	} 
	/* If no transition was taken then execute local reactions */
	if ((transitioned_after) == (transitioned_before))
	{ 
		transitioned_after = main_region_PSV_react(transitioned_before);
	} 
	return transitioned_after;
}

sc_integer MVMStateMachineCore::main_region_PSV_r1_InspiratoryPause_react(const sc_integer transitioned_before) {
	/* The reactions of state InspiratoryPause. */
	sc_integer transitioned_after = transitioned_before;
	if ((transitioned_after) < (0))
	{ 
		if (!ins_pause)
		{ 
			exseq_main_region_PSV_r1_InspiratoryPause();
			enseq_main_region_PSV_r1_Expiration_default();
			main_region_PSV_react(0);
			transitioned_after = 0;
		}  else
		{
			if (current.timeEvents.MVMStateMachineCore_main_region_PSV_r1_InspiratoryPause_time_event_0_raised)
			{ 
				exseq_main_region_PSV_r1_InspiratoryPause();
				ins_pause = false;
				enseq_main_region_PSV_r1_Expiration_default();
				main_region_PSV_react(0);
				transitioned_after = 0;
			} 
		}
	} 
	/* If no transition was taken then execute local reactions */
	if ((transitioned_after) == (transitioned_before))
	{ 
		transitioned_after = main_region_PSV_react(transitioned_before);
	} 
	return transitioned_after;
}

sc_integer MVMStateMachineCore::main_region_PSV_r1_RM_react(const sc_integer transitioned_before) {
	/* The reactions of state RM. */
	sc_integer transitioned_after = transitioned_before;
	if ((transitioned_after) < (0))
	{ 
		if (!rm_request)
		{ 
			exseq_main_region_PSV_r1_RM();
			enseq_main_region_PSV_r1_Expiration_default();
			main_region_PSV_react(0);
			transitioned_after = 0;
		}  else
		{
			if (current.timeEvents.MVMStateMachineCore_main_region_PSV_r1_RM_time_event_0_raised)
			{ 
				exseq_main_region_PSV_r1_RM();
				rm_request = false;
				enseq_main_region_PSV_r1_Expiration_default();
				main_region_PSV_react(0);
				transitioned_after = 0;
			} 
		}
	} 
	/* If no transition was taken then execute local reactions */
	if ((transitioned_after) == (transitioned_before))
	{ 
		transitioned_after = main_region_PSV_react(transitioned_before);
	} 
	return transitioned_after;
}

sc_integer MVMStateMachineCore::main_region__final__react(const sc_integer transitioned_before) {
	/* The reactions of state null. */
	sc_integer transitioned_after = transitioned_before;
	if ((transitioned_after) < (0))
	{ 
	} 
	/* If no transition was taken then execute local reactions */
	if ((transitioned_after) == (transitioned_before))
	{ 
		transitioned_after = react(transitioned_before);
	} 
	return transitioned_after;
}

sc_integer MVMStateMachineCore::main_region_SelfTest_react(const sc_integer transitioned_before) {
	/* The reactions of state SelfTest. */
	sc_integer transitioned_after = transitioned_before;
	if ((transitioned_after) < (0))
	{ 
		if (current.iface.selfTestPassed_raised)
		{ 
			exseq_main_region_SelfTest();
			enseq_main_region_VentilationOff_default();
			react(0);
			transitioned_after = 0;
		}  else
		{
			if (current.iface.poweroff_raised)
			{ 
				exseq_main_region_SelfTest();
				enseq_main_region__final__default();
				transitioned_after = 0;
			}  else
			{
				if (current.iface.resume_raised)
				{ 
					exseq_main_region_SelfTest();
					enseq_main_region_VentilationOff_default();
					react(0);
					transitioned_after = 0;
				} 
			}
		}
	} 
	/* If no transition was taken then execute local reactions */
	if ((transitioned_after) == (transitioned_before))
	{ 
		transitioned_after = react(transitioned_before);
	} 
	return transitioned_after;
}

sc_integer MVMStateMachineCore::main_region_VentilationOff_react(const sc_integer transitioned_before) {
	/* The reactions of state VentilationOff. */
	sc_integer transitioned_after = transitioned_before;
	if ((transitioned_after) < (0))
	{ 
		if (((current.iface.startVentilation_raised)) && (((mode) == (P_CONTROLLED_V))))
		{ 
			exseq_main_region_VentilationOff();
			ifaceOperationCallback->start();
			ifaceOperationCallback->closeOutputValve();
			ifaceOperationCallback->autoBreath(false);
			if(ifaceTraceObserver != sc_null) {
				ifaceTraceObserver->stateEntered(MVMStateMachineCore::main_region_PCV);
			}
			enseq_main_region_PCV_r1_Inspiration_default();
			react(0);
			transitioned_after = 0;
		}  else
		{
			if (current.iface.poweroff_raised)
			{ 
				exseq_main_region_VentilationOff();
				enseq_main_region__final__default();
				transitioned_after = 0;
			}  else
			{
				if (((current.iface.startVentilation_raised)) && (((mode) == (P_SUPPORTED_V))))
				{ 
					exseq_main_region_VentilationOff();
					ifaceOperationCallback->start();
					ifaceOperationCallback->closeOutputValve();
					ifaceOperationCallback->autoBreath(false);
					enact_main_region_PSV();
					if(ifaceTraceObserver != sc_null) {
						ifaceTraceObserver->stateEntered(MVMStateMachineCore::main_region_PSV);
					}
					enseq_main_region_PSV_r1_Inspiration_default();
					react(0);
					transitioned_after = 0;
				}  else
				{
					if (((current.iface.startVentilation_raised)) && (((mode) == (A_SUPPORTED_V))))
					{ 
						exseq_main_region_VentilationOff();
						ifaceOperationCallback->start();
						ifaceOperationCallback->closeOutputValve();
						ifaceOperationCallback->autoBreath(false);
						numCycle = 0;
						enact_main_region_ASV();
						if(ifaceTraceObserver != sc_null) {
							ifaceTraceObserver->stateEntered(MVMStateMachineCore::main_region_ASV);
						}
						enseq_main_region_ASV_r1_InitialInspiration_default();
						react(0);
						transitioned_after = 0;
					} 
				}
			}
		}
	} 
	/* If no transition was taken then execute local reactions */
	if ((transitioned_after) == (transitioned_before))
	{ 
		transitioned_after = react(transitioned_before);
	} 
	return transitioned_after;
}

sc_integer MVMStateMachineCore::main_region_ASV_react(const sc_integer transitioned_before) {
	/* The reactions of state ASV. */
	sc_integer transitioned_after = transitioned_before;
	if ((transitioned_after) < (0))
	{ 
		if (current.iface.poweroff_raised)
		{ 
			exseq_main_region_ASV();
			enseq_main_region__final__default();
			transitioned_after = 0;
		}  else
		{
			if (stopVentilation)
			{ 
				exseq_main_region_ASV();
				stopVentilation = false;
				enseq_main_region_VentilationOff_default();
				react(0);
				transitioned_after = 0;
			} 
		}
	} 
	/* If no transition was taken then execute local reactions */
	if ((transitioned_after) == (transitioned_before))
	{ 
		transitioned_after = react(transitioned_before);
	} 
	return transitioned_after;
}

sc_integer MVMStateMachineCore::main_region_ASV_r1_InitialExpiration_react(const sc_integer transitioned_before) {
	/* The reactions of state InitialExpiration. */
	sc_integer transitioned_after = transitioned_before;
	if ((transitioned_after) < (0))
	{ 
		if (((current.timeEvents.MVMStateMachineCore_main_region_ASV_r1_InitialExpiration_time_event_0_raised)) && (((((ifaceOperationCallback->dropPAW_ITS_PSV()) || (ifaceOperationCallback->dropPAW_ITS_PCV()))) && ((numCycle) < (3)))))
		{ 
			exseq_main_region_ASV_r1_InitialExpiration();
			enseq_main_region_ASV_r1_InitialInspiration_default();
			main_region_ASV_react(0);
			transitioned_after = 0;
		}  else
		{
			if (((current.timeEvents.MVMStateMachineCore_main_region_ASV_r1_InitialExpiration_time_event_1_raised)) && (((numCycle) < (3))))
			{ 
				exseq_main_region_ASV_r1_InitialExpiration();
				enseq_main_region_ASV_r1_InitialInspiration_default();
				main_region_ASV_react(0);
				transitioned_after = 0;
			}  else
			{
				if (((current.timeEvents.MVMStateMachineCore_main_region_ASV_r1_InitialExpiration_time_event_2_raised)) && (((numCycle) >= (3))))
				{ 
					exseq_main_region_ASV_r1_InitialExpiration();
					enseq_main_region_ASV_r1_Inspiration_default();
					main_region_ASV_react(0);
					transitioned_after = 0;
				}  else
				{
					if (((current.timeEvents.MVMStateMachineCore_main_region_ASV_r1_InitialExpiration_time_event_3_raised)) && (((ifaceOperationCallback->dropPAW_ITS_PSV()) || (ifaceOperationCallback->dropPAW_ITS_PCV()))))
					{ 
						exseq_main_region_ASV_r1_InitialExpiration();
						enseq_main_region_ASV_r1_Inspiration_default();
						main_region_ASV_react(0);
						transitioned_after = 0;
					} 
				}
			}
		}
	} 
	/* If no transition was taken then execute local reactions */
	if ((transitioned_after) == (transitioned_before))
	{ 
		transitioned_after = main_region_ASV_react(transitioned_before);
	} 
	return transitioned_after;
}

sc_integer MVMStateMachineCore::main_region_ASV_r1_InitialInspiration_react(const sc_integer transitioned_before) {
	/* The reactions of state InitialInspiration. */
	sc_integer transitioned_after = transitioned_before;
	if ((transitioned_after) < (0))
	{ 
		if (((current.timeEvents.MVMStateMachineCore_main_region_ASV_r1_InitialInspiration_time_event_0_raised)) && ((ifaceOperationCallback->flowDropASV())))
		{ 
			exseq_main_region_ASV_r1_InitialInspiration();
			enseq_main_region_ASV_r1_InitialExpiration_default();
			main_region_ASV_react(0);
			transitioned_after = 0;
		}  else
		{
			if (current.timeEvents.MVMStateMachineCore_main_region_ASV_r1_InitialInspiration_time_event_1_raised)
			{ 
				exseq_main_region_ASV_r1_InitialInspiration();
				enseq_main_region_ASV_r1_InitialExpiration_default();
				main_region_ASV_react(0);
				transitioned_after = 0;
			}  else
			{
				if (ifaceOperationCallback->pawGTMaxPinsp())
				{ 
					exseq_main_region_ASV_r1_InitialInspiration();
					enseq_main_region_ASV_r1_InitialExpiration_default();
					main_region_ASV_react(0);
					transitioned_after = 0;
				}  else
				{
					if (current.timeEvents.MVMStateMachineCore_main_region_ASV_r1_InitialInspiration_time_event_2_raised)
					{ 
						exseq_main_region_ASV_r1_InitialInspiration();
						enseq_main_region_ASV_r1_InitialExpiration_default();
						main_region_ASV_react(0);
						transitioned_after = 0;
					} 
				}
			}
		}
	} 
	/* If no transition was taken then execute local reactions */
	if ((transitioned_after) == (transitioned_before))
	{ 
		transitioned_after = main_region_ASV_react(transitioned_before);
	} 
	return transitioned_after;
}

sc_integer MVMStateMachineCore::main_region_ASV_r1_Expiration_react(const sc_integer transitioned_before) {
	/* The reactions of state Expiration. */
	sc_integer transitioned_after = transitioned_before;
	if ((transitioned_after) < (0))
	{ 
		if (((current.timeEvents.MVMStateMachineCore_main_region_ASV_r1_Expiration_time_event_0_raised)) && ((ifaceOperationCallback->dropPAW_ITS_ASV())))
		{ 
			exseq_main_region_ASV_r1_Expiration();
			enseq_main_region_ASV_r1_Inspiration_default();
			main_region_ASV_react(0);
			transitioned_after = 0;
		} 
	} 
	/* If no transition was taken then execute local reactions */
	if ((transitioned_after) == (transitioned_before))
	{ 
		transitioned_after = main_region_ASV_react(transitioned_before);
	} 
	return transitioned_after;
}

sc_integer MVMStateMachineCore::main_region_ASV_r1_Inspiration_react(const sc_integer transitioned_before) {
	/* The reactions of state Inspiration. */
	sc_integer transitioned_after = transitioned_before;
	if ((transitioned_after) < (0))
	{ 
		if (((current.timeEvents.MVMStateMachineCore_main_region_ASV_r1_Inspiration_time_event_0_raised)) && ((ifaceOperationCallback->flowDropASV())))
		{ 
			exseq_main_region_ASV_r1_Inspiration();
			enseq_main_region_ASV_r1_Expiration_default();
			main_region_ASV_react(0);
			transitioned_after = 0;
		}  else
		{
			if (current.timeEvents.MVMStateMachineCore_main_region_ASV_r1_Inspiration_time_event_1_raised)
			{ 
				exseq_main_region_ASV_r1_Inspiration();
				enseq_main_region_ASV_r1_Expiration_default();
				main_region_ASV_react(0);
				transitioned_after = 0;
			}  else
			{
				if (ifaceOperationCallback->pawGTMaxPinsp())
				{ 
					exseq_main_region_ASV_r1_Inspiration();
					enseq_main_region_ASV_r1_Expiration_default();
					main_region_ASV_react(0);
					transitioned_after = 0;
				} 
			}
		}
	} 
	/* If no transition was taken then execute local reactions */
	if ((transitioned_after) == (transitioned_before))
	{ 
		transitioned_after = main_region_ASV_react(transitioned_before);
	} 
	return transitioned_after;
}

void MVMStateMachineCore::swapInEvents() {
	current.iface.startupEnded_raised = startupEnded_raised;
	startupEnded_raised = false;
	current.iface.resume_raised = resume_raised;
	resume_raised = false;
	current.iface.poweroff_raised = poweroff_raised;
	poweroff_raised = false;
	current.iface.selfTestPassed_raised = selfTestPassed_raised;
	selfTestPassed_raised = false;
	current.iface.startVentilation_raised = startVentilation_raised;
	startVentilation_raised = false;
	current.timeEvents.MVMStateMachineCore_main_region_PCV_r1_ExpiratoryPause_time_event_0_raised = timeEvents[0];
	timeEvents[0] = false;
	current.timeEvents.MVMStateMachineCore_main_region_PCV_r1_Expiration_time_event_0_raised = timeEvents[1];
	timeEvents[1] = false;
	current.timeEvents.MVMStateMachineCore_main_region_PCV_r1_Expiration_time_event_1_raised = timeEvents[2];
	timeEvents[2] = false;
	current.timeEvents.MVMStateMachineCore_main_region_PCV_r1_Inspiration_time_event_0_raised = timeEvents[3];
	timeEvents[3] = false;
	current.timeEvents.MVMStateMachineCore_main_region_PCV_r1_InspiratoryPause_time_event_0_raised = timeEvents[4];
	timeEvents[4] = false;
	current.timeEvents.MVMStateMachineCore_main_region_PCV_r1_RM_time_event_0_raised = timeEvents[5];
	timeEvents[5] = false;
	current.timeEvents.MVMStateMachineCore_main_region_PSV_r1_ExpiratoryPause_time_event_0_raised = timeEvents[6];
	timeEvents[6] = false;
	current.timeEvents.MVMStateMachineCore_main_region_PSV_r1_Expiration_time_event_0_raised = timeEvents[7];
	timeEvents[7] = false;
	current.timeEvents.MVMStateMachineCore_main_region_PSV_r1_Expiration_time_event_1_raised = timeEvents[8];
	timeEvents[8] = false;
	current.timeEvents.MVMStateMachineCore_main_region_PSV_r1_Expiration_time_event_2_raised = timeEvents[9];
	timeEvents[9] = false;
	current.timeEvents.MVMStateMachineCore_main_region_PSV_r1_Expiration_time_event_3_raised = timeEvents[10];
	timeEvents[10] = false;
	current.timeEvents.MVMStateMachineCore_main_region_PSV_r1_Inspiration_time_event_0_raised = timeEvents[11];
	timeEvents[11] = false;
	current.timeEvents.MVMStateMachineCore_main_region_PSV_r1_Inspiration_time_event_1_raised = timeEvents[12];
	timeEvents[12] = false;
	current.timeEvents.MVMStateMachineCore_main_region_PSV_r1_InspiratoryPause_time_event_0_raised = timeEvents[13];
	timeEvents[13] = false;
	current.timeEvents.MVMStateMachineCore_main_region_PSV_r1_RM_time_event_0_raised = timeEvents[14];
	timeEvents[14] = false;
	current.timeEvents.MVMStateMachineCore_main_region_ASV_r1_InitialExpiration_time_event_0_raised = timeEvents[15];
	timeEvents[15] = false;
	current.timeEvents.MVMStateMachineCore_main_region_ASV_r1_InitialExpiration_time_event_1_raised = timeEvents[16];
	timeEvents[16] = false;
	current.timeEvents.MVMStateMachineCore_main_region_ASV_r1_InitialExpiration_time_event_2_raised = timeEvents[17];
	timeEvents[17] = false;
	current.timeEvents.MVMStateMachineCore_main_region_ASV_r1_InitialExpiration_time_event_3_raised = timeEvents[18];
	timeEvents[18] = false;
	current.timeEvents.MVMStateMachineCore_main_region_ASV_r1_InitialInspiration_time_event_0_raised = timeEvents[19];
	timeEvents[19] = false;
	current.timeEvents.MVMStateMachineCore_main_region_ASV_r1_InitialInspiration_time_event_1_raised = timeEvents[20];
	timeEvents[20] = false;
	current.timeEvents.MVMStateMachineCore_main_region_ASV_r1_InitialInspiration_time_event_2_raised = timeEvents[21];
	timeEvents[21] = false;
	current.timeEvents.MVMStateMachineCore_main_region_ASV_r1_Expiration_time_event_0_raised = timeEvents[22];
	timeEvents[22] = false;
	current.timeEvents.MVMStateMachineCore_main_region_ASV_r1_Inspiration_time_event_0_raised = timeEvents[23];
	timeEvents[23] = false;
	current.timeEvents.MVMStateMachineCore_main_region_ASV_r1_Inspiration_time_event_1_raised = timeEvents[24];
	timeEvents[24] = false;
}

void MVMStateMachineCore::clearInEvents() {
	startupEnded_raised = false;
	resume_raised = false;
	poweroff_raised = false;
	selfTestPassed_raised = false;
	startVentilation_raised = false;
	timeEvents[0] = false;
	timeEvents[1] = false;
	timeEvents[2] = false;
	timeEvents[3] = false;
	timeEvents[4] = false;
	timeEvents[5] = false;
	timeEvents[6] = false;
	timeEvents[7] = false;
	timeEvents[8] = false;
	timeEvents[9] = false;
	timeEvents[10] = false;
	timeEvents[11] = false;
	timeEvents[12] = false;
	timeEvents[13] = false;
	timeEvents[14] = false;
	timeEvents[15] = false;
	timeEvents[16] = false;
	timeEvents[17] = false;
	timeEvents[18] = false;
	timeEvents[19] = false;
	timeEvents[20] = false;
	timeEvents[21] = false;
	timeEvents[22] = false;
	timeEvents[23] = false;
	timeEvents[24] = false;
}

void MVMStateMachineCore::microStep() {
	switch(stateConfVector[ 0 ])
	{
		case main_region_StartUp :
		{
			main_region_StartUp_react(-1);
			break;
		}
		case main_region_PCV_r1_ExpiratoryPause :
		{
			main_region_PCV_r1_ExpiratoryPause_react(-1);
			break;
		}
		case main_region_PCV_r1_Expiration :
		{
			main_region_PCV_r1_Expiration_react(-1);
			break;
		}
		case main_region_PCV_r1_Inspiration :
		{
			main_region_PCV_r1_Inspiration_react(-1);
			break;
		}
		case main_region_PCV_r1_InspiratoryPause :
		{
			main_region_PCV_r1_InspiratoryPause_react(-1);
			break;
		}
		case main_region_PCV_r1_RM :
		{
			main_region_PCV_r1_RM_react(-1);
			break;
		}
		case main_region_PSV_r1_ExpiratoryPause :
		{
			main_region_PSV_r1_ExpiratoryPause_react(-1);
			break;
		}
		case main_region_PSV_r1_Expiration :
		{
			main_region_PSV_r1_Expiration_react(-1);
			break;
		}
		case main_region_PSV_r1_Inspiration :
		{
			main_region_PSV_r1_Inspiration_react(-1);
			break;
		}
		case main_region_PSV_r1_InspiratoryPause :
		{
			main_region_PSV_r1_InspiratoryPause_react(-1);
			break;
		}
		case main_region_PSV_r1_RM :
		{
			main_region_PSV_r1_RM_react(-1);
			break;
		}
		case main_region__final_ :
		{
			main_region__final__react(-1);
			break;
		}
		case main_region_SelfTest :
		{
			main_region_SelfTest_react(-1);
			break;
		}
		case main_region_VentilationOff :
		{
			main_region_VentilationOff_react(-1);
			break;
		}
		case main_region_ASV_r1_InitialExpiration :
		{
			main_region_ASV_r1_InitialExpiration_react(-1);
			break;
		}
		case main_region_ASV_r1_InitialInspiration :
		{
			main_region_ASV_r1_InitialInspiration_react(-1);
			break;
		}
		case main_region_ASV_r1_Expiration :
		{
			main_region_ASV_r1_Expiration_react(-1);
			break;
		}
		case main_region_ASV_r1_Inspiration :
		{
			main_region_ASV_r1_Inspiration_react(-1);
			break;
		}
		default: break;
	}
}

void MVMStateMachineCore::runCycle() {
	/* Performs a 'run to completion' step. */
	if (isExecuting)
	{ 
		return;
	} 
	isExecuting = true;
	swapInEvents();
	microStep();
	isExecuting = false;
}

void MVMStateMachineCore::enter() {
	/* Activates the state machine. */
	if (isExecuting)
	{ 
		return;
	} 
	isExecuting = true;
	/* Default enter sequence for statechart MVMStateMachineCore */
	enseq_main_region_default();
	isExecuting = false;
}

void MVMStateMachineCore::exit() {
	/* Deactivates the state machine. */
	if (isExecuting)
	{ 
		return;
	} 
	isExecuting = true;
	/* Default exit sequence for statechart MVMStateMachineCore */
	exseq_main_region();
	isExecuting = false;
}



