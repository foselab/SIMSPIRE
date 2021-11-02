################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/src-gen_old/MVMStateMachineCore.cpp 

OBJS += \
./src/src-gen_old/MVMStateMachineCore.o 

CPP_DEPS += \
./src/src-gen_old/MVMStateMachineCore.d 


# Each subdirectory must supply rules for building sources it contributes
src/src-gen_old/%.o: ../src/src-gen_old/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: Cygwin C++ Compiler'
	g++ -I"E:\GitHub\adapt-mvm\mvm-yakindu-controller\src" -I"E:\GitHub\adapt-mvm\mvm-yakindu-controller\src\src-gen" -I"E:\GitHub\adapt-mvm\mvm-yakindu-controller\src\mocks" -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


