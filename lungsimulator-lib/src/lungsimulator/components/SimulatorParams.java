package lungsimulator.components;

import lungsimulator.exceptions.InspireException;

/**
 * Shows demographic data related to the patient
 */
public class SimulatorParams {
	/**
	 * Gender of the patient
	 */
	private String gender;
	
	/**
	 * Age of the patient
	 */
	private int age;
	
	/**
	 * Height of the patient in meters
	 */
	private double height;
	
	/**
	 * Weight of the patient in kilograms
	 */
	private double weight;
	
	/**
	 * Ideal Body Weight of the patient
	 * It is calculated as 22*(height^2)
	 */
	private double ibw;

	/**
	 * Constructor for a custom patient
	 * @param gender gender of the patient
	 * @param age age of the patient
	 * @param height height of the patient
	 * @param weight weight of the patient
	 */
	public SimulatorParams(final String gender, final int age, final double height, final double weight) {
		this.gender = gender;
		this.age = age;
		this.height = height;
		this.weight = weight;
		this.ibw = 22 * Math.pow(height, 2);
	}

	/**
	 * Constructor to properly use YAML file
	 */
	public SimulatorParams() {
	}

	/**
	 * Checks if patient parameters are legit
	 */
	public void validate() {
		if (!"Male".equalsIgnoreCase(gender) && !"Female".equalsIgnoreCase(gender)) {
			throw new InspireException("The gender has to be male or female");
		}

		if (age < 17 || age > 126) {
			throw new InspireException("Age must be between 18 and 125 years old");
		}

		if (height < 0.54 || height > 2.61) {
			throw new InspireException("Height must be betwenn 0.55 m and 2.6 m");
		}

		if (weight < 24 || weight > 601) {
			throw new InspireException("Weight must be between 25 kg and 600 kg");
		}
	}

	public String getGender() {
		return gender;
	}

	public void setGender(final String gender) {
		this.gender = gender;
	}

	public int getAge() {
		return age;
	}

	public void setAge(final int age) {
		this.age = age;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(final double height) {
		this.height = height;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(final double weight) {
		this.weight = weight;
	}

	public double getIbw() {
		this.ibw = 22 * Math.pow(height, 2);
		return ibw;
	}

	public void setIbw(final double ibw) {
		this.ibw = ibw;
	}
}
