package lungsimulator.utils;

import lungsimulator.LungSimulator;
import lungsimulator.RealTimePlot;

public class Utils {

	/**
	 * Shift data when a new one has to be included
	 *
	 * @param rtp
	 * @param initdataPressure
	 * @param initdataVentilatorPressure
	 * @param initdataFlow
	 */
	public static void shiftData(int max_data, double[][] initdataPressure, double[][] initdataVentilatorPressure,
			double[][] initdataFlow) {
		for (int i = 0; i < max_data - 1; i++) {
			initdataFlow[0][i] = initdataFlow[0][i + 1];
			initdataFlow[1][i] = initdataFlow[1][i + 1];
			initdataPressure[0][i] = initdataPressure[0][i + 1];
			initdataPressure[1][i] = initdataPressure[1][i + 1];
			initdataVentilatorPressure[0][i] = initdataVentilatorPressure[0][i + 1];
			initdataVentilatorPressure[1][i] = initdataVentilatorPressure[1][i + 1];
		}
	}

	/**
	 * Init data vectors with zeros
	 *
	 * @param rtp
	 * @param initdataPressure
	 * @param initdataVentilatorPressure
	 * @param initdataFlow
	 */
	public static void initVectors(int max_data, double[][] initdataPressure,
			double[][] initdataVentilatorPressure, double[][] initdataFlow) {
		for (int i = 0; i < max_data; i++) {
			for (int j = 0; j < max_data; j++) {
				initdataPressure[i][j] = 0;
				initdataVentilatorPressure[i][j] = 0;
				initdataFlow[i][j] = 0;
			}
		}
	}

	public static void updateMaxFlow(RealTimePlot rtp, double[][] initdataFlow) {
		if (initdataFlow[1][rtp.getMax_data() - 1] > LungSimulator.maxFlow)
			LungSimulator.maxFlow = initdataFlow[1][rtp.getMax_data() - 1];
	}

}
