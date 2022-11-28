package lungsimulator.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public static Map<String, List<Double>> initMap(List<String> ids) {
		Map<String, List<Double>> myMap = new HashMap<>();
		
		for(String index: ids) {
			myMap.put(index, new ArrayList<>(Arrays.asList(0.0)));
		}
		
		return myMap;
	}
	
	public static Map<String, List<Double>> updateMap(Map<String, List<Double>> myMap, String key, double value){
		List<Double> myList = myMap.get(key);
		
		if(myList.size() < MAXDATA) {
			myList.add(value);
			myMap.put(key, myList);
		}else {
			myList.remove(0);
			myList.add(value);
			myMap.put(key, myList);
		}
		
		return myMap;
	}
	
	public static List<Double> updateDoubleList(List<Double> myList, double newValue){
		if(myList.size() >= MAXDATA) {
			myList.remove(0);	
		}
		
		myList.add(newValue);
		
		return myList;
	}
}
