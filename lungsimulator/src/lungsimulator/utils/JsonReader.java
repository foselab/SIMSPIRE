package lungsimulator.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import lungsimulator.RealTimePlot;
import lungsimulator.components.Patient;
import lungsimulator.components.SimulatorParams;
import lungsimulator.components.Ventilator;

public class JsonReader {
	static String JSON_PATH = "config/lung.json";

	public void readConfig(RealTimePlot plotter) throws FileNotFoundException, IOException, ParseException {
		File f = new File(JSON_PATH);
		assert f.exists();

		// Set the patient parameters
		readPatientConfig(plotter);
		readPatientUncertaintyConfig(plotter);

		// Set the ventilator parameters
		readVentilatorConfig(plotter.getVentilator());

		// Set the simulation parameters
		readSimulationConfig(plotter.getSimulatorParams());
	}

	@SuppressWarnings("unchecked")
	public void readSimulationConfig(SimulatorParams sp) throws FileNotFoundException, IOException, ParseException {
		File f = new File(JSON_PATH);
		assert f.exists();

		// Read the JSON File
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = (JSONObject) parser.parse(new FileReader(JSON_PATH));

		// Set the simulation parameters
		JSONObject simulationParams = (JSONObject) jsonObj.get("simulationParams");
		sp.setSleepTime((long) simulationParams.getOrDefault("sleepTime", 0L));
		sp.setStep((double) simulationParams.getOrDefault("step", 0.0));
		sp.setUseCom((boolean) simulationParams.getOrDefault("useCOM", true));
		sp.setUseZMQ((boolean) simulationParams.getOrDefault("useZMQ", false));
		sp.setComName((String) simulationParams.getOrDefault("comName", ""));
	}

	@SuppressWarnings("unchecked")
	public void readVentilatorConfig(Ventilator v) throws FileNotFoundException, IOException, ParseException {
		File f = new File(JSON_PATH);
		assert f.exists();

		// Read the JSON File
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = (JSONObject) parser.parse(new FileReader(JSON_PATH));

		// Set the ventilator parameters
		JSONObject ventilatorParams = (JSONObject) jsonObj.get("ventilatorParams");
		v.setBatteryValue((double) ventilatorParams.getOrDefault("pMax", 0.0));
		v.setPeepValue((double) ventilatorParams.getOrDefault("peep", 0.0));
		v.setrVentilatorValue((double) ventilatorParams.getOrDefault("rVentilator", 1E-6));
		v.setT1((double) ventilatorParams.getOrDefault("t1", 0.0));
		v.setT2((double) ventilatorParams.getOrDefault("t2", 0.0));
		v.setT3((double) ventilatorParams.getOrDefault("t3", 0.0));
		v.setT4((double) ventilatorParams.getOrDefault("t4", 0.0));
	}

	@SuppressWarnings("unchecked")
	public void readPatientConfig(RealTimePlot plotter) throws FileNotFoundException, IOException, ParseException {
		File f = new File(JSON_PATH);
		assert f.exists();

		// Read the JSON File
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = (JSONObject) parser.parse(new FileReader(JSON_PATH));

		// Set the patient parameters
		JSONObject patientParams = (JSONObject) jsonObj.get("patientParams");
		plotter.setrPatValue((double) patientParams.getOrDefault("resistance", 0.0));
		plotter.setcPatValue((double) patientParams.getOrDefault("compliance", 0.0));
		plotter.setrDropValue((double) patientParams.getOrDefault("dropResistance", 0.0));
		plotter.setPressureDropEnabled((Boolean) patientParams.getOrDefault("pressureDropEnabled", true));
		plotter.setPressureDropAfterSec((double) patientParams.getOrDefault("pressureDropAfterSec", 0.0));
	}

	@SuppressWarnings("unchecked")
	public void readPatientUncertaintyConfig(RealTimePlot plotter)
			throws FileNotFoundException, IOException, ParseException {
		File f = new File(JSON_PATH);
		assert f.exists();

		// Read the JSON File
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = (JSONObject) parser.parse(new FileReader(JSON_PATH));

		// Set the patient parameters
		JSONObject patientParams = (JSONObject) jsonObj.get("patientParams");
		plotter.setCoughProbability((double) patientParams.getOrDefault("coughProbability", 0.0));
		plotter.setProbabilityChangeResistance((double) patientParams.getOrDefault("probabilityChangeResistance", 0.0));
		plotter.setMaxVariabilityResistance((double) patientParams.getOrDefault("maxVariabilityResistance", 0.0));
		plotter.setProbabilityChangeCompliance((double) patientParams.getOrDefault("probabilityChangeCompliance", 0.0));
		plotter.setMaxVariabilityCompliance((double) patientParams.getOrDefault("maxVariabilityCompliance", 0.0));
		plotter.setDropResistanceChangeProbability(
				(double) patientParams.getOrDefault("dropResistanceChangeProbability", 0.0));
		plotter.setMaxDropResistanceChange((double) patientParams.getOrDefault("maxDropResistanceChange", 0.0));
		plotter.setPressureDropMissProbability((double) patientParams.getOrDefault("pressureDropMissProbability", 0.0));
		plotter.setCoughEnabled((Boolean) patientParams.getOrDefault("coughEnabled", false));
		plotter.setResistanceChangeEnabled((Boolean) patientParams.getOrDefault("resistanceChangeEnabled", false));
		plotter.setComplianceChangeEnabled((Boolean) patientParams.getOrDefault("complianceChangeEnabled", false));
		plotter.setDropResistanceChangeEnabled(
				(Boolean) patientParams.getOrDefault("dropResistanceChangeEnabled", false));
		plotter.setPressureDropEnabledWithProbability(
				(Boolean) patientParams.getOrDefault("pressureDropEnabledWithProbability", false));
	}

	@SuppressWarnings("unchecked")
	public Object[] readGraphicsConfig() throws FileNotFoundException, IOException, ParseException {
		File f = new File(JSON_PATH);
		assert f.exists();

		// Read the JSON File
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = (JSONObject) parser.parse(new FileReader(JSON_PATH));

		// Set the chart parameters
		JSONObject chartParams = (JSONObject) jsonObj.get("chartParams");
		return new Object[] { chartParams.getOrDefault("numberOfDataShown", 0.0),
				chartParams.getOrDefault("width", 0.0), chartParams.getOrDefault("height", 0.0),
				chartParams.getOrDefault("showVentilatorPlot", true) };
	}
}
