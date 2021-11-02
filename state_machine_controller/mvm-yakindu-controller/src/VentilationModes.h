/*
 * VentilationModes.h
 *
 *  Created on: 9 mag 2020
 *      Author: AngeloGargantini
 */

#ifndef VENTILATIONMODES_H_
#define VENTILATIONMODES_H_


typedef enum {
    P_CONTROLLED_V, P_SUPPORTED_V, A_SUPPORTED_V
} MVM_mode;

// pressure open for input valve
typedef enum {
    PCV, PSV, RM, ASV
} MVM_PIO;

// pressure closed for input valve
typedef enum {
    ZERO,SMART_PEEP
} MVM_PIC;



#endif /* VENTILATIONMODES_H_ */
