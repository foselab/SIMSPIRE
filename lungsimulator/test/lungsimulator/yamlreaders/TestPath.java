package lungsimulator.yamlreaders;
import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import lungsimulator.components.Patient;
import lungsimulator.yamlreaders.ResourceReader;
import lungsimulator.yamlreaders.YamlReader;

public class TestPath {

	@Test
	public void test() throws FileNotFoundException, IOException {
		YamlReader yaml = new YamlReader("Albanese");
		Patient patient = yaml.readPatientModel();
		assertNotNull(patient);
		assertEquals(patient.getElementsList().get(0).getX(),0);
		assertEquals(patient.getElementsList().get(0).getY(),2);
		assertEquals(patient.getElementsList().get(0).getY1(),2);
		assertEquals(patient.getElementsList().get(0).getX1(),1);
	}
	
	@Test
	public void test2() throws FileNotFoundException, IOException {
		Patient patient = ResourceReader.readPatientModel("Albanese");
		assertNotNull(patient);
	}

}
