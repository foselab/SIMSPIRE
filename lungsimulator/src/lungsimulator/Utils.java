package lungsimulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.experimental.UtilityClass;

/**
 * Contains methods for vectors initialization and data management
 */
@UtilityClass
public class Utils {

	/**
	 * Number of data shown in a chart
	 */
	public static final int MAXDATA = 50;

	/**
	 * Map initializer
	 * 
	 * @param ids map keys
	 * @return initialized map
	 */
	public static Map<String, List<Double>> initMap(final List<String> ids) {
		final Map<String, List<Double>> myMap = new ConcurrentHashMap<>();

		for (final String index : ids) {
			myMap.put(index, new ArrayList<>(Arrays.asList(0.0)));
		}

		return myMap;
	}

	/**
	 * Left shift values for each list of the map
	 * 
	 * @param myMap map with lists of values
	 * @param key   key of the list that has to be shifted
	 * @param value new value that has to be added to the ist
	 * @return map with shifted values
	 */
	public static Map<String, List<Double>> updateMap(final Map<String, List<Double>> myMap, final String key,
			final double value) {
		final List<Double> myList = myMap.get(key);

		if (myList.size() >= MAXDATA) {
			myList.remove(0);
		}

		myList.add(value);
		myMap.put(key, myList);

		return myMap;
	}

	/**
	 * Performs a left shift of a list with double values
	 * 
	 * @param myList   the list with double values
	 * @param newValue the new value that has to be added
	 * @return the updated list
	 */
	public static List<Double> updateDoubleList(final List<Double> myList, final double newValue) {
		if (myList.size() >= MAXDATA) {
			myList.remove(0);
		}

		myList.add(newValue);

		return myList;
	}
}
