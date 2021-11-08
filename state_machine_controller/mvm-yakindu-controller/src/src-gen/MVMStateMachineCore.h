/* generated - DO NOT EDIT - MIT license. @authors: Angelo Gargantini, Silvia Bonfanti, Elvinia Riccobene, Andrea Bombarda */

#ifndef MVMSTATEMACHINECORE_H_
#define MVMSTATEMACHINECORE_H_

/*!
 * Forward declaration for the MVMStateMachineCore state machine.
 */
 class MVMStateMachineCore;


#include <VentilationModes.h>
#include "sc_types.h"
#include "sc_statemachine.h"
#include "sc_cyclebased.h"
#include "sc_timer.h"
#include "sc_tracing.h"

/*! \file Header of the state machine 'MVMStateMachineCore'.
*/


/*! Define indices of states in the StateConfVector */
#define SCVI_MAIN_REGION_STARTUP 0
#define SCVI_MAIN_REGION_PCV 0
#define SCVI_MAIN_REGION_PCV_R1_EXPIRATORYPAUSE 0
#define SCVI_MAIN_REGION_PCV_R1_EXPIRATION 0
#define SCVI_MAIN_REGION_PCV_R1_INSPIRATION 0
#define SCVI_MAIN_REGION_PCV_R1_INSPIRATORYPAUSE 0
#define SCVI_MAIN_REGION_PCV_R1_RM 0
#define SCVI_MAIN_REGION_PSV 0
#define SCVI_MAIN_REGION_PSV_R1_EXPIRATORYPAUSE 0
#define SCVI_MAIN_REGION_PSV_R1_EXPIRATION 0
#define SCVI_MAIN_REGION_PSV_R1_INSPIRATION 0
#define SCVI_MAIN_REGION_PSV_R1_INSPIRATORYPAUSE 0
#define SCVI_MAIN_REGION_PSV_R1_RM 0
#define SCVI_MAIN_REGION__FINAL_ 0
#define SCVI_MAIN_REGION_SELFTEST 0
#define SCVI_MAIN_REGION_VENTILATIONOFF 0
#define SCVI_MAIN_REGION_ASV 0
#define SCVI_MAIN_REGION_ASV_R1_INITIALEXPIRATION 0
#define SCVI_MAIN_REGION_ASV_R1_INITIALINSPIRATION 0
#define SCVI_MAIN_REGION_ASV_R1_EXPIRATION 0
#define SCVI_MAIN_REGION_ASV_R1_INSPIRATION 0


class MVMStateMachineCore : public sc::timer::TimedInterface, public sc::CycleBasedInterface, public sc::StatemachineInterface
{
	public:
		MVMStateMachineCore();
		
		virtual ~MVMStateMachineCore();
		
		/*! Enumeration of all states */ 
		typedef enum
		{
			MVMStateMachineCore_last_state,
			main_region_StartUp,
			main_region_PCV,
			main_region_PCV_r1_ExpiratoryPause,
			main_region_PCV_r1_Expiration,
			main_region_PCV_r1_Inspiration,
			main_region_PCV_r1_InspiratoryPause,
			main_region_PCV_r1_RM,
			main_region_PSV,
			main_region_PSV_r1_ExpiratoryPause,
			main_region_PSV_r1_Expiration,
			main_region_PSV_r1_Inspiration,
			main_region_PSV_r1_InspiratoryPause,
			main_region_PSV_r1_RM,
			main_region__final_,
			main_region_SelfTest,
			main_region_VentilationOff,
			main_region_ASV,
			main_region_ASV_r1_InitialExpiration,
			main_region_ASV_r1_InitialInspiration,
			main_region_ASV_r1_Expiration,
			main_region_ASV_r1_Inspiration
		} MVMStateMachineCoreStates;
					
		static const sc_integer numStates = 21;
		
		/*! Raises the in event 'startupEnded' that is defined in the default interface scope. */
		void raiseStartupEnded();
		
		/*! Raises the in event 'resume' that is defined in the default interface scope. */
		void raiseResume();
		
		/*! Raises the in event 'poweroff' that is defined in the default interface scope. */
		void raisePoweroff();
		
		/*! Raises the in event 'selfTestPassed' that is defined in the default interface scope. */
		void raiseSelfTestPassed();
		
		/*! Raises the in event 'startVentilation' that is defined in the default interface scope. */
		void raiseStartVentilation();
		
		/*! Gets the value of the variable 'stopVentilation' that is defined in the default interface scope. */
		sc_boolean getStopVentilation() const;
		
		/*! Sets the value of the variable 'stopVentilation' that is defined in the default interface scope. */
		void setStopVentilation(sc_boolean value);
		
		/*! Gets the value of the variable 'mode' that is defined in the default interface scope. */
		MVM_mode getMode() const;
		
		/*! Sets the value of the variable 'mode' that is defined in the default interface scope. */
		void setMode(MVM_mode value);
		
		/*! Gets the value of the variable 'max_exp_pause' that is defined in the default interface scope. */
		int32_t getMax_exp_pause() const;
		
		/*! Gets the value of the variable 'max_ins_pause' that is defined in the default interface scope. */
		int32_t getMax_ins_pause() const;
		
		/*! Gets the value of the variable 'max_rm_time' that is defined in the default interface scope. */
		int32_t getMax_rm_time() const;
		
		/*! Sets the value of the variable 'max_rm_time' that is defined in the default interface scope. */
		void setMax_rm_time(int32_t value);
		
		/*! Gets the value of the variable 'exp_pause' that is defined in the default interface scope. */
		sc_boolean getExp_pause() const;
		
		/*! Sets the value of the variable 'exp_pause' that is defined in the default interface scope. */
		void setExp_pause(sc_boolean value);
		
		/*! Gets the value of the variable 'ins_pause' that is defined in the default interface scope. */
		sc_boolean getIns_pause() const;
		
		/*! Sets the value of the variable 'ins_pause' that is defined in the default interface scope. */
		void setIns_pause(sc_boolean value);
		
		/*! Gets the value of the variable 'rm_request' that is defined in the default interface scope. */
		sc_boolean getRm_request() const;
		
		/*! Sets the value of the variable 'rm_request' that is defined in the default interface scope. */
		void setRm_request(sc_boolean value);
		
		/*! Gets the value of the variable 'ibwASV' that is defined in the default interface scope. */
		float getIbwASV() const;
		
		/*! Sets the value of the variable 'ibwASV' that is defined in the default interface scope. */
		void setIbwASV(float value);
		
		/*! Gets the value of the variable 'normalMinuteVentilationASV' that is defined in the default interface scope. */
		float getNormalMinuteVentilationASV() const;
		
		/*! Sets the value of the variable 'normalMinuteVentilationASV' that is defined in the default interface scope. */
		void setNormalMinuteVentilationASV(float value);
		
		/*! Gets the value of the variable 'targetMinuteVentilationASV' that is defined in the default interface scope. */
		float getTargetMinuteVentilationASV() const;
		
		/*! Sets the value of the variable 'targetMinuteVentilationASV' that is defined in the default interface scope. */
		void setTargetMinuteVentilationASV(float value);
		
		/*! Gets the value of the variable 'vTidalASV' that is defined in the default interface scope. */
		float getVTidalASV() const;
		
		/*! Sets the value of the variable 'vTidalASV' that is defined in the default interface scope. */
		void setVTidalASV(float value);
		
		/*! Gets the value of the variable 'rrASV' that is defined in the default interface scope. */
		float getRrASV() const;
		
		/*! Sets the value of the variable 'rrASV' that is defined in the default interface scope. */
		void setRrASV(float value);
		
		/*! Gets the value of the variable 'rcASV' that is defined in the default interface scope. */
		float getRcASV() const;
		
		/*! Sets the value of the variable 'rcASV' that is defined in the default interface scope. */
		void setRcASV(float value);
		
		/*! Gets the value of the variable 'numCycle' that is defined in the default interface scope. */
		int32_t getNumCycle() const;
		
		/*! Sets the value of the variable 'numCycle' that is defined in the default interface scope. */
		void setNumCycle(int32_t value);
		
		/*! Gets the value of the variable 'inspiration_duration_ms' that is defined in the default interface scope. */
		int32_t getInspiration_duration_ms() const;
		
		/*! Sets the value of the variable 'inspiration_duration_ms' that is defined in the default interface scope. */
		void setInspiration_duration_ms(int32_t value);
		
		/*! Gets the value of the variable 'expiration_duration_ms' that is defined in the default interface scope. */
		int32_t getExpiration_duration_ms() const;
		
		/*! Sets the value of the variable 'expiration_duration_ms' that is defined in the default interface scope. */
		void setExpiration_duration_ms(int32_t value);
		
		/*! Gets the value of the variable 'triggerWindowDelay_ms' that is defined in the default interface scope. */
		int32_t getTriggerWindowDelay_ms() const;
		
		/*! Gets the value of the variable 'min_insp_time_ms' that is defined in the default interface scope. */
		int32_t getMin_insp_time_ms() const;
		
		/*! Gets the value of the variable 'max_insp_time_psv' that is defined in the default interface scope. */
		int32_t getMax_insp_time_psv() const;
		
		/*! Sets the value of the variable 'max_insp_time_psv' that is defined in the default interface scope. */
		void setMax_insp_time_psv(int32_t value);
		
		/*! Gets the value of the variable 'max_insp_time_asv' that is defined in the default interface scope. */
		int32_t getMax_insp_time_asv() const;
		
		/*! Sets the value of the variable 'max_insp_time_asv' that is defined in the default interface scope. */
		void setMax_insp_time_asv(int32_t value);
		
		/*! Gets the value of the variable 'apnealag' that is defined in the default interface scope. */
		int32_t getApnealag() const;
		
		/*! Sets the value of the variable 'apnealag' that is defined in the default interface scope. */
		void setApnealag(int32_t value);
		
		/*! Gets the value of the variable 'apnea_backup_mode' that is defined in the default interface scope. */
		sc_boolean getApnea_backup_mode() const;
		
		/*! Sets the value of the variable 'apnea_backup_mode' that is defined in the default interface scope. */
		void setApnea_backup_mode(sc_boolean value);
		
		//! Inner class for default interface scope operation callbacks.
		class OperationCallback
		{
			public:
				virtual ~OperationCallback() = 0;
				
				virtual void finish() = 0;
				
				virtual void start() = 0;
				
				virtual void closeInputValve() = 0;
				
				virtual void openInputValve(MVM_PIO p) = 0;
				
				virtual void closeOutputValve() = 0;
				
				virtual void openOutputValve() = 0;
				
				virtual void autoBreath(sc_boolean b) = 0;
				
				virtual sc_boolean pawGTMaxPinsp() = 0;
				
				virtual sc_boolean dropPAW_ITS_PCV() = 0;
				
				virtual sc_boolean dropPAW_ITS_ASV() = 0;
				
				virtual int32_t min_exp_time_psv() = 0;
				
				virtual sc_boolean flowDropPSV() = 0;
				
				virtual sc_boolean flowDropASV() = 0;
				
				virtual sc_boolean dropPAW_ITS_PSV() = 0;
				
				virtual void apneaAlarm() = 0;
				
				
		};
		
		/*! Set the working instance of the operation callback interface 'OperationCallback'. */
		void setOperationCallback(OperationCallback* operationCallback);
		
		/*
		 * Functions inherited from StatemachineInterface
		 */
		virtual void enter();
		
		virtual void exit();
		
		virtual void runCycle();
		
		/*!
		 * Checks if the state machine is active (until 2.4.1 this method was used for states).
		 * A state machine is active if it has been entered. It is inactive if it has not been entered at all or if it has been exited.
		 */
		virtual sc_boolean isActive() const;
		
		
		/*!
		* Checks if all active states are final. 
		* If there are no active states then the state machine is considered being inactive. In this case this method returns false.
		*/
		virtual sc_boolean isFinal() const;
		
		/*! 
		 * Checks if member of the state machine must be set. For example an operation callback.
		 */
		sc_boolean check();
		
		/*
		 * Functions inherited from TimedStatemachineInterface
		 */
		virtual void setTimerService(sc::timer::TimerServiceInterface* timerService);
		
		virtual sc::timer::TimerServiceInterface* getTimerService();
		
		virtual void raiseTimeEvent(sc_eventid event);
		
		virtual sc_integer getNumberOfParallelTimeEvents();
		
		
		void setTraceObserver(sc::trace::TraceObserver<MVMStateMachineCoreStates>* tracingcallback);
		
		sc::trace::TraceObserver<MVMStateMachineCoreStates>* getTraceObserver();
		
		
		/*! Checks if the specified state is active (until 2.4.1 the used method for states was calles isActive()). */
		sc_boolean isStateActive(MVMStateMachineCoreStates state) const;
		
		//! number of time events used by the state machine.
		static const sc_integer timeEventsCount = 26;
		
		//! number of time events that can be active at once.
		static const sc_integer parallelTimeEventsCount = 4;
		
		
	protected:
		
		
	private:
		MVMStateMachineCore(const MVMStateMachineCore &rhs);
		MVMStateMachineCore& operator=(const MVMStateMachineCore&);
		
		sc_boolean startupEnded_raised;
		sc_boolean resume_raised;
		sc_boolean poweroff_raised;
		sc_boolean selfTestPassed_raised;
		sc_boolean startVentilation_raised;
		sc_boolean stopVentilation;
		MVM_mode mode;
		static const int32_t max_exp_pause;
		static const int32_t max_ins_pause;
		int32_t max_rm_time;
		sc_boolean exp_pause;
		sc_boolean ins_pause;
		sc_boolean rm_request;
		float ibwASV;
		float normalMinuteVentilationASV;
		float targetMinuteVentilationASV;
		float vTidalASV;
		float rrASV;
		float rcASV;
		int32_t numCycle;
		int32_t inspiration_duration_ms;
		int32_t expiration_duration_ms;
		static const int32_t triggerWindowDelay_ms;
		static const int32_t min_insp_time_ms;
		int32_t max_insp_time_psv;
		int32_t max_insp_time_asv;
		int32_t apnealag;
		sc_boolean apnea_backup_mode;
		
		
		//! the maximum number of orthogonal states defines the dimension of the state configuration vector.
		static const sc_ushort maxOrthogonalStates = 1;
		
		sc::timer::TimerServiceInterface* timerService;
		sc_boolean timeEvents[timeEventsCount];
		
		sc::trace::TraceObserver<MVMStateMachineCoreStates>* ifaceTraceObserver;
		
		MVMStateMachineCoreStates stateConfVector[maxOrthogonalStates];
		
		
		OperationCallback* ifaceOperationCallback;
		
		typedef struct {
			sc_boolean startupEnded_raised;
			sc_boolean resume_raised;
			sc_boolean poweroff_raised;
			sc_boolean selfTestPassed_raised;
			sc_boolean startVentilation_raised;
		}MVMStateMachineCoreIfaceEvBuf;
		typedef struct {
			sc_boolean MVMStateMachineCore_main_region_PCV_r1_ExpiratoryPause_time_event_0_raised;
			sc_boolean MVMStateMachineCore_main_region_PCV_r1_Expiration_time_event_0_raised;
			sc_boolean MVMStateMachineCore_main_region_PCV_r1_Expiration_time_event_1_raised;
			sc_boolean MVMStateMachineCore_main_region_PCV_r1_Inspiration_time_event_0_raised;
			sc_boolean MVMStateMachineCore_main_region_PCV_r1_InspiratoryPause_time_event_0_raised;
			sc_boolean MVMStateMachineCore_main_region_PCV_r1_RM_time_event_0_raised;
			sc_boolean MVMStateMachineCore_main_region_PSV_r1_ExpiratoryPause_time_event_0_raised;
			sc_boolean MVMStateMachineCore_main_region_PSV_r1_Expiration_time_event_0_raised;
			sc_boolean MVMStateMachineCore_main_region_PSV_r1_Expiration_time_event_1_raised;
			sc_boolean MVMStateMachineCore_main_region_PSV_r1_Expiration_time_event_2_raised;
			sc_boolean MVMStateMachineCore_main_region_PSV_r1_Expiration_time_event_3_raised;
			sc_boolean MVMStateMachineCore_main_region_PSV_r1_Inspiration_time_event_0_raised;
			sc_boolean MVMStateMachineCore_main_region_PSV_r1_Inspiration_time_event_1_raised;
			sc_boolean MVMStateMachineCore_main_region_PSV_r1_InspiratoryPause_time_event_0_raised;
			sc_boolean MVMStateMachineCore_main_region_PSV_r1_RM_time_event_0_raised;
			sc_boolean MVMStateMachineCore_main_region_ASV_r1_InitialExpiration_time_event_0_raised;
			sc_boolean MVMStateMachineCore_main_region_ASV_r1_InitialExpiration_time_event_1_raised;
			sc_boolean MVMStateMachineCore_main_region_ASV_r1_InitialExpiration_time_event_2_raised;
			sc_boolean MVMStateMachineCore_main_region_ASV_r1_InitialExpiration_time_event_3_raised;
			sc_boolean MVMStateMachineCore_main_region_ASV_r1_InitialInspiration_time_event_0_raised;
			sc_boolean MVMStateMachineCore_main_region_ASV_r1_InitialInspiration_time_event_1_raised;
			sc_boolean MVMStateMachineCore_main_region_ASV_r1_InitialInspiration_time_event_2_raised;
			sc_boolean MVMStateMachineCore_main_region_ASV_r1_Expiration_time_event_0_raised;
			sc_boolean MVMStateMachineCore_main_region_ASV_r1_Expiration_time_event_1_raised;
			sc_boolean MVMStateMachineCore_main_region_ASV_r1_Inspiration_time_event_0_raised;
			sc_boolean MVMStateMachineCore_main_region_ASV_r1_Inspiration_time_event_1_raised;
		}MVMStateMachineCoreTimeEventsEvBuf;
		typedef struct {
			MVMStateMachineCoreIfaceEvBuf iface;
			MVMStateMachineCoreTimeEventsEvBuf timeEvents;
		}MVMStateMachineCoreEvBuf;
		
		MVMStateMachineCoreEvBuf current;
		sc_boolean isExecuting;
		
		
		// prototypes of all internal functions
		
		sc_boolean check_main_region_PCV_r1__choice_0_tr0_tr0();
		sc_boolean check_main_region_PCV_r1__choice_1_tr0_tr0();
		sc_boolean check_main_region_PCV_r1__choice_1_tr1_tr1();
		sc_boolean check_main_region_PCV_r1__choice_2_tr0_tr0();
		sc_boolean check_main_region_PSV_r1__choice_0_tr0_tr0();
		sc_boolean check_main_region_PSV_r1__choice_1_tr1_tr1();
		void effect_main_region_PCV_r1__choice_0_tr0();
		void effect_main_region_PCV_r1__choice_0_tr1();
		void effect_main_region_PCV_r1__choice_1_tr0();
		void effect_main_region_PCV_r1__choice_1_tr1();
		void effect_main_region_PCV_r1__choice_1_tr2();
		void effect_main_region_PCV_r1__choice_2_tr0();
		void effect_main_region_PCV_r1__choice_2_tr1();
		void effect_main_region_PSV_r1__choice_0_tr0();
		void effect_main_region_PSV_r1__choice_0_tr1();
		void effect_main_region_PSV_r1__choice_1_tr1();
		void effect_main_region_PSV_r1__choice_1_tr0();
		void enact_main_region_PCV_r1_ExpiratoryPause();
		void enact_main_region_PCV_r1_Expiration();
		void enact_main_region_PCV_r1_Inspiration();
		void enact_main_region_PCV_r1_InspiratoryPause();
		void enact_main_region_PCV_r1_RM();
		void enact_main_region_PSV();
		void enact_main_region_PSV_r1_ExpiratoryPause();
		void enact_main_region_PSV_r1_Expiration();
		void enact_main_region_PSV_r1_Inspiration();
		void enact_main_region_PSV_r1_InspiratoryPause();
		void enact_main_region_PSV_r1_RM();
		void enact_main_region_VentilationOff();
		void enact_main_region_ASV();
		void enact_main_region_ASV_r1_InitialExpiration();
		void enact_main_region_ASV_r1_InitialInspiration();
		void enact_main_region_ASV_r1_Expiration();
		void enact_main_region_ASV_r1_Inspiration();
		void exact_main_region_PCV_r1_ExpiratoryPause();
		void exact_main_region_PCV_r1_Expiration();
		void exact_main_region_PCV_r1_Inspiration();
		void exact_main_region_PCV_r1_InspiratoryPause();
		void exact_main_region_PCV_r1_RM();
		void exact_main_region_PSV_r1_ExpiratoryPause();
		void exact_main_region_PSV_r1_Expiration();
		void exact_main_region_PSV_r1_Inspiration();
		void exact_main_region_PSV_r1_InspiratoryPause();
		void exact_main_region_PSV_r1_RM();
		void exact_main_region_SelfTest();
		void exact_main_region_ASV_r1_InitialExpiration();
		void exact_main_region_ASV_r1_InitialInspiration();
		void exact_main_region_ASV_r1_Expiration();
		void exact_main_region_ASV_r1_Inspiration();
		void enseq_main_region_StartUp_default();
		void enseq_main_region_PCV_r1_ExpiratoryPause_default();
		void enseq_main_region_PCV_r1_Expiration_default();
		void enseq_main_region_PCV_r1_Inspiration_default();
		void enseq_main_region_PCV_r1_InspiratoryPause_default();
		void enseq_main_region_PCV_r1_RM_default();
		void enseq_main_region_PSV_r1_ExpiratoryPause_default();
		void enseq_main_region_PSV_r1_Expiration_default();
		void enseq_main_region_PSV_r1_Inspiration_default();
		void enseq_main_region_PSV_r1_InspiratoryPause_default();
		void enseq_main_region_PSV_r1_RM_default();
		void enseq_main_region__final__default();
		void enseq_main_region_SelfTest_default();
		void enseq_main_region_VentilationOff_default();
		void enseq_main_region_ASV_r1_InitialExpiration_default();
		void enseq_main_region_ASV_r1_InitialInspiration_default();
		void enseq_main_region_ASV_r1_Expiration_default();
		void enseq_main_region_ASV_r1_Inspiration_default();
		void enseq_main_region_default();
		void exseq_main_region_StartUp();
		void exseq_main_region_PCV();
		void exseq_main_region_PCV_r1_ExpiratoryPause();
		void exseq_main_region_PCV_r1_Expiration();
		void exseq_main_region_PCV_r1_Inspiration();
		void exseq_main_region_PCV_r1_InspiratoryPause();
		void exseq_main_region_PCV_r1_RM();
		void exseq_main_region_PSV();
		void exseq_main_region_PSV_r1_ExpiratoryPause();
		void exseq_main_region_PSV_r1_Expiration();
		void exseq_main_region_PSV_r1_Inspiration();
		void exseq_main_region_PSV_r1_InspiratoryPause();
		void exseq_main_region_PSV_r1_RM();
		void exseq_main_region__final_();
		void exseq_main_region_SelfTest();
		void exseq_main_region_VentilationOff();
		void exseq_main_region_ASV();
		void exseq_main_region_ASV_r1_InitialExpiration();
		void exseq_main_region_ASV_r1_InitialInspiration();
		void exseq_main_region_ASV_r1_Expiration();
		void exseq_main_region_ASV_r1_Inspiration();
		void exseq_main_region();
		void exseq_main_region_PCV_r1();
		void exseq_main_region_PSV_r1();
		void exseq_main_region_ASV_r1();
		void react_main_region_PCV_r1__choice_0();
		void react_main_region_PCV_r1__choice_1();
		void react_main_region_PCV_r1__choice_2();
		void react_main_region_PSV_r1__choice_0();
		void react_main_region_PSV_r1__choice_1();
		void react_main_region__entry_Default();
		sc_integer react(const sc_integer transitioned_before);
		sc_integer main_region_StartUp_react(const sc_integer transitioned_before);
		sc_integer main_region_PCV_react(const sc_integer transitioned_before);
		sc_integer main_region_PCV_r1_ExpiratoryPause_react(const sc_integer transitioned_before);
		sc_integer main_region_PCV_r1_Expiration_react(const sc_integer transitioned_before);
		sc_integer main_region_PCV_r1_Inspiration_react(const sc_integer transitioned_before);
		sc_integer main_region_PCV_r1_InspiratoryPause_react(const sc_integer transitioned_before);
		sc_integer main_region_PCV_r1_RM_react(const sc_integer transitioned_before);
		sc_integer main_region_PSV_react(const sc_integer transitioned_before);
		sc_integer main_region_PSV_r1_ExpiratoryPause_react(const sc_integer transitioned_before);
		sc_integer main_region_PSV_r1_Expiration_react(const sc_integer transitioned_before);
		sc_integer main_region_PSV_r1_Inspiration_react(const sc_integer transitioned_before);
		sc_integer main_region_PSV_r1_InspiratoryPause_react(const sc_integer transitioned_before);
		sc_integer main_region_PSV_r1_RM_react(const sc_integer transitioned_before);
		sc_integer main_region__final__react(const sc_integer transitioned_before);
		sc_integer main_region_SelfTest_react(const sc_integer transitioned_before);
		sc_integer main_region_VentilationOff_react(const sc_integer transitioned_before);
		sc_integer main_region_ASV_react(const sc_integer transitioned_before);
		sc_integer main_region_ASV_r1_InitialExpiration_react(const sc_integer transitioned_before);
		sc_integer main_region_ASV_r1_InitialInspiration_react(const sc_integer transitioned_before);
		sc_integer main_region_ASV_r1_Expiration_react(const sc_integer transitioned_before);
		sc_integer main_region_ASV_r1_Inspiration_react(const sc_integer transitioned_before);
		void swapInEvents();
		void clearInEvents();
		void microStep();
		
		
		
		
};


inline MVMStateMachineCore::OperationCallback::~OperationCallback() {}


#endif /* MVMSTATEMACHINECORE_H_ */
