################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/src-gen/MVMStateMachineCore.cpp 

OBJS += \
./src/src-gen/MVMStateMachineCore.o 

CPP_DEPS += \
./src/src-gen/MVMStateMachineCore.d 


# Each subdirectory must supply rules for building sources it contributes
src/src-gen/%.o: ../src/src-gen/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: Cygwin C++ Compiler'
	g++ -I"F:\Repository MVM\mvm-adapt\state_machine_controller\mvm-yakindu-controller\src" -I"F:\Repository MVM\mvm-adapt\state_machine_controller\mvm-yakindu-controller\src\src-gen" -I"F:\Repository MVM\mvm-adapt\state_machine_controller\mvm-yakindu-controller\src\mocks" -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


