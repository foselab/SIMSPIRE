/*
 * Serial.h
 *
 *  Created on: 24 giu 2020
 *      Author: AngeloGargantini
 */

#ifndef SERIAL_H_
#define SERIAL_H_
#include <string>
// clang-format off
#define DEC 10

class HardwareSerial{

public:
    void println(const std::string &){}
    void println(const char[]){}
    void println(unsigned long, int = DEC){}
    void println(const bool){}
    void println(const float){}
    void print(const std::string &){}
    void print(unsigned long, int = DEC){}


};

extern HardwareSerial Serial;



#endif /* SERIAL_H_ */
