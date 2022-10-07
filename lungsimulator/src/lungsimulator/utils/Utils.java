package lungsimulator.utils;

import lombok.experimental.UtilityClass;

/**
 * Contains methods for vectors initialization and data management
 */
@UtilityClass
public class Utils {

	/**
	 * Shift data when a new one has to be included
	 *
	 * @param rtp
	 * @param initdataPressure
	 * @param initdataVentilatorPressure
	 * @param initdataFlow
	 */
	public static void shiftData(final int maxData, double[][] initdataPressure, double[][] initdataVentilatorPressure,
			double[][] initdataFlow) {
		int flowSize = initdataFlow.length;
		for (int i = 0; i < maxData - 1; i++) {
			for(int j=0; j<flowSize; j++) {
				initdataFlow[j][i] = initdataFlow[j][i + 1];
			}

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
	public static void initVectors(final int maxData, double[][] initdataPressure,
			double[][] initdataVentilatorPressure, double[][] initdataFlow) {
		int flowSize = initdataFlow.length;
		for (int i = 0; i < maxData; i++) {
			for (int j = 0; j < maxData; j++) {
				initdataPressure[i][j] = 0;

				initdataVentilatorPressure[0][j] = 0;
				initdataVentilatorPressure[1][j] = 0;

				if (i < flowSize) {
					initdataFlow[i][j] = 0;
				}
			}
		}
	}
}
