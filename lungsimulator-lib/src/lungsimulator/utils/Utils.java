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
	 * @param initdataPressure
	 * @param initdataVentilatorPressure
	 * @param initdataFlow
	 */
	public static void shiftData(final int maxData, String[] timeline, double[][] initdataPressure, double[][] initdataVentilatorPressure,
			double[][] initdataFlow) {
		int flowSize = initdataFlow.length-1;
		int pressureSize = initdataPressure.length-1;
		
		for (int i = 0; i < maxData - 1; i++) {
			timeline[i] = timeline[i+1];
			
			for (int j = 0; j < flowSize; j++) {
				initdataFlow[j][i] = initdataFlow[j][i + 1];
			}

			for(int k = 0; k < pressureSize; k++) {
				initdataPressure[k][i] = initdataPressure[k][i + 1];
			}

			initdataVentilatorPressure[0][i] = initdataVentilatorPressure[0][i + 1];
			initdataVentilatorPressure[1][i] = initdataVentilatorPressure[1][i + 1];
		}
	}

	/**
	 * Init data vectors with zeros
	 *
	 * @param initdataPressure
	 * @param initdataVentilatorPressure
	 * @param initdataFlow
	 */
	public static void initVectors(final int maxData, String[] timeline, double[][] initdataPressure,
			double[][] initdataVentilatorPressure, double[][] initdataFlow) {
		final int flowSize = initdataFlow.length;
		final int pressureSize = initdataPressure.length;

		final int maxLength = flowSize > pressureSize ? flowSize : pressureSize;

		for (int i = 0; i < maxLength; i++) {
			for (int j = 0; j < maxData; j++) {
				timeline[j] = "0";
				
				if (i < pressureSize) {
					initdataPressure[i][j] = 0;
				}

				initdataVentilatorPressure[0][j] = 0;
				initdataVentilatorPressure[1][j] = 0;

				if (i < flowSize) {
					initdataFlow[i][j] = 0;
				}
			}
		}
	}
}
