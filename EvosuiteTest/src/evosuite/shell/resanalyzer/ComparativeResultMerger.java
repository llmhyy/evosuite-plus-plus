package evosuite.shell.resanalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.map.HashedMap;

import evosuite.shell.ComparativeRecorder;
import evosuite.shell.excel.ExcelReader;
import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.experiment.SFConfiguration;

public class ComparativeResultMerger {

	public static String folderName = "report-fbranch";
	
	public static void main(String[] args) {
		ComparativeResultMerger merger = new ComparativeResultMerger();

		// String branchSummaryAddress = SFConfiguration.sfBenchmarkFolder +
		// File.separator + "summary.xlsx";
		String fbranchMaterialsAddress = SFConfiguration.sfBenchmarkFolder + File.separator + folderName;
		merger.runAnalyzer(fbranchMaterialsAddress);

	}

	public static final String BETTER_COVERAGE = "good coverage";
	public static final String BETTER_TIME = "good time";
	public static final String EQUAL = "equal";
	public static final String WORSE_COVERAGE = "worse coverage";
	public static final String WORSE_TIME = "worse time";
	public static final String ALL = "all";

	private ExcelWriter excelWriter;

	public ComparativeResultMerger() {
		excelWriter = new ExcelWriter(new File(SFConfiguration.sfBenchmarkFolder + File.separator + folderName
				+ File.separator + "overall_compare.xlsx"));
		excelWriter.getSheet(BETTER_COVERAGE, ComparativeRecorder.header, 0);
		excelWriter.getSheet(BETTER_TIME, ComparativeRecorder.header, 0);
		excelWriter.getSheet(EQUAL, ComparativeRecorder.header, 0);
		excelWriter.getSheet(WORSE_COVERAGE, ComparativeRecorder.header, 0);
		excelWriter.getSheet(WORSE_TIME, ComparativeRecorder.header, 0);
	}

	private void runAnalyzer(String fbranchMaterialsAddress) {
		List<List<Object>> betterCoverage = new ArrayList<>();
		List<List<Object>> betterTime = new ArrayList<>();
		List<List<Object>> equal = new ArrayList<>();
		List<List<Object>> worseTime = new ArrayList<>();
		List<List<Object>> worseCoverage = new ArrayList<>();

		File root = new File(fbranchMaterialsAddress);

		if (!root.exists()) {
			System.err.println(root + " does not exsit");
			return;
		}

		for (File file : root.listFiles()) {
			if (file.exists() && file.getName().endsWith("evotest_compare.xlsx") && !file.getName().startsWith("~$")) {
				ExcelReader reader = new ExcelReader(file, 0);
				List<List<Object>> bc = reader.listData(BETTER_COVERAGE);
				List<List<Object>> bt = reader.listData(BETTER_TIME);
				List<List<Object>> eq = reader.listData(EQUAL);
				List<List<Object>> wc = reader.listData(WORSE_COVERAGE);
				List<List<Object>> wt = reader.listData(WORSE_TIME);

				betterCoverage.addAll(bc);
				betterTime.addAll(bt);
				equal.addAll(eq);
				worseCoverage.addAll(wc);
				worseTime.addAll(wt);

			}
		}
		
		CleanResult results = clean(betterCoverage, betterTime, equal, worseTime, worseCoverage);

		List<List<Object>> all = new ArrayList<>();
		all.addAll(results.betterCoverage);
		all.addAll(results.betterTime);
		all.addAll(results.worseTime);
		all.addAll(results.worseCoverage);
		all.addAll(results.equal);
		
		try {
			excelWriter.writeSheet(BETTER_COVERAGE, results.betterCoverage);
			excelWriter.writeSheet(BETTER_TIME, results.betterTime);
			excelWriter.writeSheet(EQUAL, results.equal);
			excelWriter.writeSheet(WORSE_TIME, results.worseTime);
			excelWriter.writeSheet(WORSE_COVERAGE, results.worseCoverage);
			excelWriter.writeSheet(ALL, all);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	class Record {
		String className;
		String methodName;
		Long randomSeed;
		int timeF;
		int timeB;
		double coverageF;
		double coverageB;
		double IPConverageF;
		double IPConverageB;
		int ageF;
		int ageB;
		String uncoveredIPF;
		String uncoveredIPB;
		double callAvailability;
		String unavailableCall;

		public Record(String className, String methodName, Long randomSeed, int timeF, int timeB, double coverageF,
				double coverageB, double iPConverageF, double iPConverageB, int ageF, int ageB, String uncoveredIPF,
				String uncoveredIPB, double callAvailability, String unavailableCall) {
			super();
			this.className = className;
			this.methodName = methodName;
			this.randomSeed = randomSeed;
			this.timeF = timeF;
			this.timeB = timeB;
			this.coverageF = coverageF;
			this.coverageB = coverageB;
			IPConverageF = iPConverageF;
			IPConverageB = iPConverageB;
			this.ageF = ageF;
			this.ageB = ageB;
			this.uncoveredIPF = uncoveredIPF;
			this.uncoveredIPB = uncoveredIPB;
			this.callAvailability = callAvailability;
			this.unavailableCall = unavailableCall;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((className == null) ? 0 : className.hashCode());
			result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
			result = prime * result + ((randomSeed == null) ? 0 : randomSeed.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Record other = (Record) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (className == null) {
				if (other.className != null)
					return false;
			} else if (!className.equals(other.className))
				return false;
			if (methodName == null) {
				if (other.methodName != null)
					return false;
			} else if (!methodName.equals(other.methodName))
				return false;
			if (randomSeed == null) {
				if (other.randomSeed != null)
					return false;
			} else if (!randomSeed.equals(other.randomSeed))
				return false;
			return true;
		}

		private ComparativeResultMerger getOuterType() {
			return ComparativeResultMerger.this;
		}

		public String getMethodID() {
			return className + "#" + methodName;
		}
		
		public double getCoverageAdvantage() {
			return coverageF - coverageB;
		}
		
		public double getTimeAdvantage() {
			if(coverageB == coverageF && coverageB == 1) {
				return timeB - timeF;				
			}
			
			return 0;
		}

	}

	class CleanResult{
		List<List<Object>> betterCoverage;
		List<List<Object>> betterTime;
		List<List<Object>> equal;
		List<List<Object>> worseTime;
		List<List<Object>> worseCoverage;
		public CleanResult(List<List<Object>> betterCoverage, List<List<Object>> betterTime, List<List<Object>> equal,
				List<List<Object>> worseTime, List<List<Object>> worseCoverage) {
			super();
			this.betterCoverage = betterCoverage;
			this.betterTime = betterTime;
			this.equal = equal;
			this.worseTime = worseTime;
			this.worseCoverage = worseCoverage;
		}
		
		
	}
	
	private CleanResult clean(List<List<Object>> betterCoverage, List<List<Object>> betterTime, List<List<Object>> equal,
			List<List<Object>> worseTime, List<List<Object>> worseCoverage) {
		Set<Record> betterCoverageSet = transferListToSet(betterCoverage);
		Set<Record> betterTimeSet = transferListToSet(betterTime);
		Set<Record> equalSet = transferListToSet(equal);
		Set<Record> worseTimeSet = transferListToSet(worseTime);
		Set<Record> worseCoverageSet = transferListToSet(worseCoverage);

		Map<String, List<Record>> recordIteration = new HashMap<>();
		
		cleanSet(betterCoverageSet, recordIteration);
		cleanSet(betterTimeSet, recordIteration);
		cleanSet(equalSet, recordIteration);
		cleanSet(worseTimeSet, recordIteration);
		cleanSet(worseCoverageSet, recordIteration);
		
		CleanSets cleanSets = retrieveCleanSets(recordIteration);
		
		List<List<Object>> newBetterCoverage = transferSetToList(cleanSets.betterCoverageSet);
		List<List<Object>> newBetterTime = transferSetToList(cleanSets.betterTimeSet);
		List<List<Object>> newEqual = transferSetToList(cleanSets.equalSet);
		List<List<Object>> newWorseTime = transferSetToList(cleanSets.worseTimeSet);
		List<List<Object>> newWorseCoverage = transferSetToList(cleanSets.worseCoverageSet);
		
		return new CleanResult(newBetterCoverage, newBetterTime, newEqual, newWorseTime, newWorseCoverage);
	}

	private Set<Record> transferListToSet(List<List<Object>> list) {
		Set<Record> set = new HashSet<>();
		for(List<Object> item: list) {
			Record record = new Record(
					(String)item.get(0), 
					(String)item.get(1), 
					((Double)item.get(2)).longValue(), 
					((Double)item.get(3)).intValue(), 
					((Double)item.get(4)).intValue(), 
					(Double)item.get(5), 
					(Double)item.get(6), 
					item.get(7)==null? 1 : (Double)item.get(7), 
					item.get(7)==null? 1 : (Double)item.get(8), 
					((Double)item.get(9)).intValue(), 
					((Double)item.get(10)).intValue(), 
					(String)item.get(11), 
					(String)item.get(12), 
					(Double)item.get(13),
					(String)item.get(14)
					);
			set.add(record);
		}
		return set;
	}
	
	class RecordComparator implements Comparator<Record>{
		@Override
		public int compare(Record o1, Record o2) {
			if(o1.getCoverageAdvantage() < o2.getCoverageAdvantage()) {
				return -1;
			}
			else if(o1.getCoverageAdvantage() > o2.getCoverageAdvantage()) {
				return 1;
			}
			else{
				if(o1.getTimeAdvantage() < o2.getTimeAdvantage()){
					return -1;
				}
				else if(o1.getTimeAdvantage() > o2.getTimeAdvantage()) {
					return 1;
				}
				else {
					return 0;
				}
			}
		}
	}

	private void cleanSet(Set<Record> set, Map<String, List<Record>> recordIteration) {
		RecordComparator comparator = new RecordComparator();
		for(Record record: set) {
			String methodID = record.getMethodID();
			List<Record> list = recordIteration.get(methodID);
			if(list == null) {
				list = new ArrayList<>();
			}
			
			if(list.size() < 3) {
				list.add(record);
				Collections.sort(list, comparator);
			}
			else {
				Record worst = list.get(0);
				if(comparator.compare(record, worst) > 0) {
					list.set(0, record);
					Collections.sort(list, comparator);
				}				
			}
			
			recordIteration.put(methodID, list);
		}
		
	}
	
	class CleanSets{
		Set<Record> betterCoverageSet;
		Set<Record> betterTimeSet;
		Set<Record> equalSet;
		Set<Record> worseTimeSet;
		Set<Record> worseCoverageSet;
		public CleanSets(Set<Record> betterCoverageSet, Set<Record> betterTimeSet, Set<Record> equalSet,
				Set<Record> worseTimeSet, Set<Record> worseCoverageSet) {
			super();
			this.betterCoverageSet = betterCoverageSet;
			this.betterTimeSet = betterTimeSet;
			this.equalSet = equalSet;
			this.worseTimeSet = worseTimeSet;
			this.worseCoverageSet = worseCoverageSet;
		}
	}
	
	private CleanSets retrieveCleanSets(Map<String, List<Record>> recordIteration){
		Set<Record> betterCoverageSet = new HashSet<>();
		Set<Record> betterTimeSet = new HashSet<>();
		Set<Record> equalSet = new HashSet<>();
		Set<Record> worseTimeSet = new HashSet<>();
		Set<Record> worseCoverageSet = new HashSet<>();
		for(String key: recordIteration.keySet()) {
			List<Record> recordList = recordIteration.get(key);
			
			if(recordList.size()>3) {
				System.currentTimeMillis();
			}
			
			for(Record record: recordList) {
				if(record.getCoverageAdvantage() > 0) {
					betterCoverageSet.add(record);
				}
				else if(record.getCoverageAdvantage() < 0) {
					worseCoverageSet.add(record);
				}
				else {
					if(record.getTimeAdvantage() > 0) {
						betterTimeSet.add(record);
					}
					else if(record.getTimeAdvantage() < 0){
						worseTimeSet.add(record);
					}
					else {
						equalSet.add(record);
					}
				}
			}
		}
		
		return new CleanSets(betterCoverageSet, betterTimeSet, equalSet, worseTimeSet, worseCoverageSet);
	}

	private List<List<Object>> transferSetToList(Set<Record> set) {
		List<List<Object>> items = new ArrayList<>();
		for(Record record: set) {
			List<Object> item = new ArrayList<>();
			item.add(record.className);
			item.add(record.methodName);
			item.add(record.randomSeed);
			item.add(record.timeF);
			item.add(record.timeB);
			item.add(record.coverageF);
			item.add(record.coverageB);
			item.add(record.IPConverageF);
			item.add(record.IPConverageB);
			item.add(record.ageF);
			item.add(record.ageB);
			item.add(record.uncoveredIPF);
			item.add(record.uncoveredIPB);
			item.add(record.callAvailability);
			item.add(record.unavailableCall);
			
			items.add(item);
		}
		return items;
	}

}
