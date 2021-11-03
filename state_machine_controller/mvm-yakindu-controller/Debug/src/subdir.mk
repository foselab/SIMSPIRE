################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/HAL.cpp \
../src/MVMTimerInterface.cpp \
../src/StateMachineOCBs.cpp \
../src/mvm_machine_run.cpp 

OBJS += \
./src/HAL.o \
./src/MVMTimerInterface.o \
./src/StateMachineOCBs.o \
./src/mvm_machine_run.o 

CPP_DEPS += \
./src/HAL.d \
./src/MVMTimerInterface.d \
./src/StateMachineOCBs.d \
./src/mvm_machine_run.d 


# Each subdirectory must supply rules for building sources it contributes
src/%.o: ../src/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: Cygwin C++ Compiler'
	g++ -I"F:\Repository MVM\mvm-adapt\state_machine_controller\mvm-yakindu-controller\src" -I"F:\Repository MVM\mvm-adapt\state_machine_controller\mvm-yakindu-controller\src\src-gen" -I"F:\Repository MVM\mvm-adapt\state_machine_controller\mvm-yakindu-controller\src\mocks" -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


