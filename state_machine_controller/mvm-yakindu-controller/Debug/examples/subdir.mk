################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../examples/Serial.cpp \
../examples/ValvesController.cpp \
../examples/mvm-state-machine.cpp \
../examples/mvm-state-machine_generated.cpp \
../examples/state_machine.test.cpp 

OBJS += \
./examples/Serial.o \
./examples/ValvesController.o \
./examples/mvm-state-machine.o \
./examples/mvm-state-machine_generated.o \
./examples/state_machine.test.o 

CPP_DEPS += \
./examples/Serial.d \
./examples/ValvesController.d \
./examples/mvm-state-machine.d \
./examples/mvm-state-machine_generated.d \
./examples/state_machine.test.d 


# Each subdirectory must supply rules for building sources it contributes
examples/%.o: ../examples/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: Cygwin C++ Compiler'
	g++ -I"E:\GitHub\adapt-mvm\mvm-yakindu-controller\src" -I"E:\GitHub\adapt-mvm\mvm-yakindu-controller\src\src-gen" -I"E:\GitHub\adapt-mvm\mvm-yakindu-controller\examples" -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


