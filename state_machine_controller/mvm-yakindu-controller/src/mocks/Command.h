/*
 * Command.h
 *
 *  Created on: 1 giu 2020
 *      Author: AngeloGargantini
 */

#ifndef COMMAND_H_
#define COMMAND_H_
#include "AsyncDelay.h"
#include "state_machine.h"
#include <functional>
#include <iostream>
#include <string>
#include <vector>
#define TOTAL_TIME_MS 21000

class Command
{
 public:
  static std::vector<Command*> all_comands;

  AsyncDelay stop_watch;
  std::function<void(mvm::StateMachine*)> command;
  std::string message;
  Command(int ms, std::function<void(mvm::StateMachine*)> cmd, std::string msg)
      : stop_watch(ms, AsyncDelay::MILLIS)
      , command(cmd)
      , message(msg)
  {
    all_comands.push_back(this);
  };
  virtual ~Command(){};

  void execute(mvm::StateMachine* x)
  {
    if (stop_watch.isExpired()) {
      stop_watch.start(TOTAL_TIME_MS, AsyncDelay::MILLIS);
      command(x);
      std::cout << message << std::endl;
    }
  }

  static void executeAll(mvm::StateMachine* sm)
  {
    for (auto const& value : all_comands) {
      value->execute(sm);
    }
  }
};

std::vector<Command*> Command::all_comands;

#endif /* COMMAND_H_ */
