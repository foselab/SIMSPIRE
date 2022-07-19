package lungsimulator.utils;

import java.util.Random;

public class Randomizer {
	public static int generate(int min, int max) {
		return min + (int) (Math.random() * ((max - min) + 1));
	}

	public static double generate(double min, double max) {
		return min + (new Random()).nextDouble() * ((max - min));
	}
}
