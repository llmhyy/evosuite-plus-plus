package org.evosuite.statistics;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.Properties;
import org.evosuite.testcase.SensitivityMutator;


public class logToExcel {
	
	public static Map<Double, Double> TIME_COVERAGE_CACHE = new HashMap<>();
	public static List<List<Object>> data = new ArrayList<List<Object>>();
	
	public static void addTimeAndCoverage(double time,double coverage) {
		TIME_COVERAGE_CACHE.put(time, coverage);
	}
	
	public static void clear() {
		TIME_COVERAGE_CACHE.clear();
		data.clear();
	}
	
	public static void recordList(double time,double coverage) {
		List<Object> row = new ArrayList<Object>();
		String workingDir = System.getProperty("user.dir");
		String project = new File(workingDir).getName();
		String pid = project.split("_")[0];
		row.add(pid);
		row.add(Properties.TARGET_CLASS);
		row.add(Properties.TARGET_METHOD);
		row.add(time);
		row.add(coverage);
		data.add(row);
	}
	
}
