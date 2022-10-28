import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import lungsimulator.ResourceReader;
import lungsimulator.components.Patient;
import lungsimulator.utils.YamlReader;

public class TestPath {

	@Test
	public void test() throws FileNotFoundException, IOException {
		YamlReader yaml = new YamlReader("Albanese");
		Patient patient = yaml.readPatientModel();
		assertNotNull(patient);
	}
	
	@Test
	public void test2() throws FileNotFoundException, IOException {
		Patient patient = ResourceReader.readPatientModel("Albanese");
		assertNotNull(patient);
	}

}
