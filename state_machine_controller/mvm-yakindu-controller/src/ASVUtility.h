/*
 * ASVUtility.h
 *
 *  Created on: 2 nov 2021
 *      Author: Andrea_PC
 */

#ifndef ASVUTILITY_H_
#define ASVUTILITY_H_

float getMean(float* v, int n) {
	float mean = 0;

	for (int i=0; i<n; i++) {
		mean += v[i];
	}

	return mean/n;
}

double getMean(double* v, int n) {
	double mean = 0;

	for (int i=0; i<n; i++) {
		mean += v[i];
	}

	return mean/n;
}



#endif /* ASVUTILITY_H_ */
