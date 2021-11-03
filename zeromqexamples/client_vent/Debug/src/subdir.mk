################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/client_vent.cpp 

OBJS += \
./src/client_vent.o 

CPP_DEPS += \
./src/client_vent.d 


# Each subdirectory must supply rules for building sources it contributes
src/%.o: ../src/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: Cygwin C++ Compiler'
	g++ -I"F:\Repository MVM\mvm-adapt\zeromqexamples\client_vent\zmq" -I"F:\Repository MVM\mvm-adapt\zeromqexamples\client_vent\libzmq" -I"F:\Repository MVM\mvm-adapt\zeromqexamples\client_vent\libzmq\include" -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


