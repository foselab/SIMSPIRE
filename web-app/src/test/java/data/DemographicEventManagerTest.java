package data;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.flow.component.combobox.ComboBox;

import lungsimulator.LungSimulator;

public class DemographicEventManagerTest {
	private LungSimulator lungSimulator;
	private DemographicEventManager dem;

	@Before
	public void setupData() throws FileNotFoundException, IOException {
		lungSimulator = new LungSimulator();
		lungSimulator.initSchema("Albanese");
		
		dem = new DemographicEventManager(lungSimulator);
	}
	
	@Test
	public void testGenderSetupWithValidValue() {
		String selectedValue = "Male";
		dem.setGender(selectedValue);
		ComboBox<String> gender = dem.getGender();
		assertTrue(gender.getValue().equalsIgnoreCase(selectedValue));
	}
	
	@Test
	public void testAgeSetupWithValidValue() {
		int age = 78;
		dem.setAge(18, 120, 1, age);
		assertTrue(dem.getAge().getValue() == age);
	}
	
	@Test
	public void testHeightSetupWithValidValue() {
		double height = 1.65;
		dem.setHeight(0.55, 2.60, 0.01, height);
		assertTrue(dem.getHeight().getValue() == height);
	}
	
	@Test
	public void testWeightSetupWithValidValue() {
		double weight = 90;
		dem.setWeight(50, 600, 0.1, weight);
		assertTrue(dem.getWeight().getValue() == weight);
	}

}
