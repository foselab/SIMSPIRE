cmake_minimum_required(VERSION 3.10)

project(mvm-sm VERSION 0.1)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED True)
set(CMAKE_CXX_EXTENSIONS False)

option(MVM_ENABLE_CLANG_TIDY "Enable clang-tidy" ON)
if(MVM_ENABLE_CLANG_TIDY)
  find_program(CLANG_TIDY_BIN clang-tidy)
  if(CLANG_TIDY_BIN)
    message(STATUS "clang-tidy enabled: ${CLANG_TIDY_BIN}")
    set(CMAKE_CXX_CLANG_TIDY ${CLANG_TIDY_BIN}
        "-extra-arg=-Wno-unknown-warning-option")
  else()
    message(SEND_ERROR "clang-tidy requested but executable not found")
  endif()
endif()

add_library(mvm_sm
            src-gen/CPPTimerInterface.cpp
            src-gen/MVMStateMachine.cpp
            src/HAL.cpp
            src/MVMStateMachineOCBs.cpp
            examples/ValvesController.cpp)
target_include_directories(mvm_sm
                           PUBLIC ${PROJECT_SOURCE_DIR}/src
                                  ${PROJECT_SOURCE_DIR}/src-gen)

add_executable(mvm_run src/mvm_machine_run.cpp)
target_link_libraries(mvm_run PRIVATE mvm_sm)
