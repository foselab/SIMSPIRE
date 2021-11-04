################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../src/client_vent2.cpp 

OBJS += \
./src/client_vent2.o 

CPP_DEPS += \
./src/client_vent2.d 


# Each subdirectory must supply rules for building sources it contributes
src/%.o: ../src/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: Cygwin C++ Compiler'
	g++ -I"C:\Users\Andrea_PC\YAKINDU_SCTPRO\ws\client_vent2\libzmq" -I"C:\Users\Andrea_PC\YAKINDU_SCTPRO\ws\client_vent2\libzmq\include" -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


