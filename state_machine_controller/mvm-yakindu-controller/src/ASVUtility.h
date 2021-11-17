/*
 * ASVUtility.h
 *
 *  Created on: 2 nov 2021
 *      Author: Andrea_PC
 */

#ifndef ASVUTILITY_H_
#define ASVUTILITY_H_

#include <iostream>

float getMean(float* v, int n) {
	float mean = 0;
	float weights = 0;

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

float getWeightedMean(std::deque<float> v) {
	float mean = 0;
	float weights = 0;
	float n=v.size();

	for (int i=n-1; i>=0; i--) {
		if (i == n-1) {
			mean += (v[i] * 0.8);
			weights += 0.8;
		}
		else {
			mean += (v[i] * (i + 1) / n * (0.2*(n-1)));
			weights += (i + 1) / n * (0.2*(n-1));
		}
	}

	return mean/weights;
}

double getWeightedMean(std::deque<double> v) {
	double mean = 0;
	double weights = 0;
	double n=v.size();

	for (int i=n-1; i>=0; i--) {
		if (i == n-1) {
			mean += (v[i] * 0.8);
			weights += 0.8;
		}
		else {
			mean += (v[i] * (i + 1) / n * (0.2*(n-1)));
			weights += (i + 1) / n * (0.2*(n-1));
		}
	}

	return mean/weights;
}




#endif /* ASVUTILITY_H_ */
