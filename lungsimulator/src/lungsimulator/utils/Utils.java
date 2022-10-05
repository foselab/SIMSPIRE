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
	public static void shiftData(int maxData, double[][] initdataPressure, double[][] initdataVentilatorPressure,
			double[][] initdataFlow) {
		for (int i = 0; i < maxData - 1; i++) {
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
	public static void initVectors(int maxData, double[][] initdataPressure,
			double[][] initdataVentilatorPressure, double[][] initdataFlow) {
		for (int i = 0; i < maxData; i++) {
			for (int j = 0; j < maxData; j++) {
				initdataPressure[i][j] = 0;
				initdataVentilatorPressure[i][j] = 0;
				initdataFlow[i][j] = 0;
			}
		}
	}

	/*public static void updateMaxFlow(RealTimePlot rtp, double[][] initdataFlow) {
		if (initdataFlow[1][rtp.getMaxData() - 1] > LungSimulator.maxFlow)
			LungSimulator.maxFlow = initdataFlow[1][rtp.getMaxData() - 1];
	}*/

}
