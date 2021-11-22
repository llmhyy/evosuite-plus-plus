package evosuite.shell;

import static evosuite.shell.EvosuiteForMethod.projectId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageGoal;
import org.evosuite.coverage.branch.BranchCoverageTestFitness;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.result.BranchInfo;
import org.evosuite.result.ExceptionResult;
import org.evosuite.result.ExceptionResultBranch;
import org.evosuite.result.ExceptionResultIteration;
import org.evosuite.testcase.TestChromosome;
import org.slf4j.Logger;

import evosuite.shell.excel.ExcelWriter;
import evosuite.shell.utils.LoggerUtils;

public class ExceptionIterationRecorder extends ExperimentRecorder {
	private Logger log = LoggerUtils.getLogger(ExceptionIterationRecorder.class);

	private ExcelWriter excelWriter;
	private String[] dataColumnHeaders;
	private String[] testCaseCodeColumnHeaders;
	private String[] statisticsColumnHeaders;
	private String[] coverageColumnHeaders;
	private Map<String, Integer> branchIdentifierToIterationNumber = new HashMap<>();
	private Map<String, Integer> classAndMethodNameToIterationNumber = new HashMap<>();

	private static final String DEFAULT_SHEET = "Data";
	private static final String TEST_CASE_CODE_SHEET = "Test case code";
	private static final String STATISTICS_SHEET = "Statistics";
	private static final String COVERAGE_SHEET = "Coverage";

	public ExceptionIterationRecorder() throws IOException {
		super();
	}

	private String generateFileName() {
		return projectId + "_exception_data.xlsx";
	}

	private void setupExcelWriter() {
		excelWriter = new ExcelWriter(FileUtils.newFile(Settings.getReportFolder(), generateFileName()));
	}

	private void setupColumnHeadersForSheet(String sheetName, String[] columnHeaders) {
		if (excelWriter == null) {
			log.error("Excel writer was not initialised.");
			return;
		}
		excelWriter.getSheet(sheetName, columnHeaders, 0);
	}

	private String[] generateTestCaseCodeColumnHeaders() {
		List<String> columnHeaders = new ArrayList<>();
		columnHeaders.add("Evosuite iteration");
		columnHeaders.add("Class");
		columnHeaders.add("Method");
		columnHeaders.add("Branch");
		columnHeaders.add("Is branch covered?");
		columnHeaders.add("Is constant-reading branch?");
		columnHeaders.add("Did the last iteration incur exception?");
		columnHeaders.add("Classification of last exception");
		columnHeaders.add("Last exception type");

		return columnHeaders.toArray(new String[] {});
	}

	private String[] generateDataColumnHeaders() {
		List<String> columnHeaders = new ArrayList<>();
		columnHeaders.add("Evosuite iteration");
		columnHeaders.add("Class");
		columnHeaders.add("Method");
		columnHeaders.add("Branch");
		columnHeaders.add("Is branch covered?");
		columnHeaders.add("Is constant-reading branch?");
		columnHeaders.add("Did the last iteration incur exception?");
		columnHeaders.add("Classification of last exception");
		columnHeaders.add("Last exception type");

		return columnHeaders.toArray(new String[] {});
	}

	private String[] generateStatisticsColumnHeaders() {
		List<String> columnHeaders = new ArrayList<>();
		columnHeaders.add("Evosuite iteration");
		columnHeaders.add("Class");
		columnHeaders.add("Method");
		columnHeaders.add("Branch");
		columnHeaders.add("Is branch covered?");
		columnHeaders.add("Is constant-reading branch?");
		columnHeaders.add("Did the last iteration incur exception?");
		columnHeaders.add("Classification of last exception");
		columnHeaders.add("Last exception type");
		columnHeaders.add("Total iterations");
		columnHeaders.add("Total exceptions");
		columnHeaders.add("Total in-method exceptions");
		columnHeaders.add("Breakdown of in-method exception types");
		columnHeaders.add("Total out-method exceptions");
		columnHeaders.add("Breakdown of out-method exception types");

		return columnHeaders.toArray(new String[] {});
	}

	private String[] generateCoverageColumnHeaders() {
		List<String> columnHeaders = new ArrayList<>();
		columnHeaders.add("Evosuite iteration");
		columnHeaders.add("Class");
		columnHeaders.add("Method");
		columnHeaders.add("Total branches");
		columnHeaders.add("Number of uncovered branches");
		columnHeaders.add("Number of covered branches");

		return columnHeaders.toArray(new String[] {});
	}

	@Override
	public void record(String className, String methodName, EvoTestResult r) {
		if (excelWriter == null) {
			setupExcelWriter();
		}

		dataColumnHeaders = generateDataColumnHeaders();
		testCaseCodeColumnHeaders = generateTestCaseCodeColumnHeaders();
		statisticsColumnHeaders = generateStatisticsColumnHeaders();
		coverageColumnHeaders = generateCoverageColumnHeaders();

		setupColumnHeadersForSheet(DEFAULT_SHEET, dataColumnHeaders);
		setupColumnHeadersForSheet(TEST_CASE_CODE_SHEET, testCaseCodeColumnHeaders);
		setupColumnHeadersForSheet(STATISTICS_SHEET, statisticsColumnHeaders);
		setupColumnHeadersForSheet(COVERAGE_SHEET, coverageColumnHeaders);

		List<List<Object>> rows = new ArrayList<>();
		List<List<Object>> comments = new ArrayList<>();
		List<List<Object>> statistics = new ArrayList<>();
		// Yes it's ungrammatical, but it indicates the plural state more clearly.
		List<List<Object>> coverages = new ArrayList<>();

		// Coverage related data.
		List<Object> rowCoverage = new ArrayList<>();
		boolean isFirstIteration = !classAndMethodNameToIterationNumber.containsKey(className + methodName);
		if (isFirstIteration) {
			classAndMethodNameToIterationNumber.put(className + methodName, 0);
		}
		Integer evosuiteIteration = classAndMethodNameToIterationNumber.get(className + methodName);
		classAndMethodNameToIterationNumber.put(className + methodName, evosuiteIteration + 1);

		rowCoverage.add(evosuiteIteration);
		rowCoverage.add(className);
		rowCoverage.add(methodName);
		int numUncoveredBranches = r.getMissingBranches().size();
		int numCoveredBranches = r.getCoveredBranchWithTest().size();
		int numTotalBranches = numUncoveredBranches + numCoveredBranches;
		rowCoverage.add(numTotalBranches);
		rowCoverage.add(numUncoveredBranches);
		rowCoverage.add(numCoveredBranches);
		coverages.add(rowCoverage);

		ExceptionResult<TestChromosome> exceptionResult = r.getExceptionResult();
		for (ExceptionResultBranch<TestChromosome> exceptionResultBranch : exceptionResult.getAllResults()) {
			BranchCoverageTestFitness fitnessFunction = ((BranchCoverageTestFitness) exceptionResultBranch
					.getFitnessFunction());
			String branchName = fitnessFunction.getBranch().toString();
			String branchGoal = fitnessFunction.getBranchGoal().getValue() ? "true" : "false";
			boolean isBranchCovered = isBranchCovered(r, exceptionResultBranch);
			boolean isConstantReadingBranch = exceptionResultBranch.isConstantReadingBranch();
			boolean isLastIterationIncurredException = exceptionResultBranch.doesLastIterationHaveException();
			String classificationOfLastException = "-";
			if (isLastIterationIncurredException) {
				boolean isLastExceptionInMethod = exceptionResultBranch.doesLastIterationHaveInMethodException();
				boolean isLastExceptionOutMethod = exceptionResultBranch.doesLastIterationHaveOutMethodException();

				if (isLastExceptionInMethod && isLastExceptionOutMethod) {
					log.debug("Assertion violated; iteration exception is both in- and out-method.");
				}

				if (isLastExceptionInMethod) {
					classificationOfLastException = "In-method";
				}

				if (isLastExceptionOutMethod) {
					classificationOfLastException = "Out-method";
				}
			}
			String lastExceptionType = "-";
			if (isLastIterationIncurredException) {
				lastExceptionType = exceptionResultBranch.getLastIteration().getException().getClass()
						.getCanonicalName();
			}

			List<Object> rowData = new ArrayList<>();
			List<Object> rowComments = new ArrayList<>();
			List<Object> rowStatistics = new ArrayList<>();

			rowData.add(evosuiteIteration);
			rowData.add(className);
			rowData.add(methodName);
			rowData.add(branchName + ";" + branchGoal);
			rowData.add(isBranchCovered);
			rowData.add(isConstantReadingBranch);
			rowData.add(isLastIterationIncurredException);
			rowData.add(classificationOfLastException);
			rowData.add(lastExceptionType);

			rowComments.add(evosuiteIteration);
			rowComments.add(className);
			rowComments.add(methodName);
			rowComments.add(branchName + ";" + branchGoal);
			rowComments.add(isBranchCovered);
			rowComments.add(isConstantReadingBranch);
			rowComments.add(isLastIterationIncurredException);
			rowComments.add(classificationOfLastException);
			rowComments.add(lastExceptionType);

			rowStatistics.add(evosuiteIteration);
			rowStatistics.add(className);
			rowStatistics.add(methodName);
			rowStatistics.add(branchName + ";" + branchGoal);
			rowStatistics.add(isBranchCovered);
			rowStatistics.add(isConstantReadingBranch);
			rowStatistics.add(isLastIterationIncurredException);
			rowStatistics.add(classificationOfLastException);
			rowStatistics.add(lastExceptionType);

			for (ExceptionResultIteration<TestChromosome> exceptionResultIteration : exceptionResultBranch
					.getAllResults()) {
				boolean isExceptionOccurred = exceptionResultIteration.isExceptionOccurred();
				boolean isInMethodException = exceptionResultIteration.isInMethodException();
				String exceptionClass = "";
				if (isExceptionOccurred) {
					exceptionClass = exceptionResultIteration.getException().getClass().getCanonicalName();
				}

				String testCaseCode = exceptionResultIteration.getTestCase().toString();
				if (testCaseCode == null || testCaseCode.isEmpty()) {
					testCaseCode = "-";
				}

				// We store all the information in a single cell per iteration.
				StringBuilder iterationEntryBuilder = new StringBuilder();
				iterationEntryBuilder.append("Exception occurred? " + (isExceptionOccurred ? "Yes" : "No"));
				iterationEntryBuilder.append("\n");
				iterationEntryBuilder.append("In/out-method exception? "
						+ (isExceptionOccurred ? (isInMethodException ? "In-method" : "Out-method") : "-"));
				iterationEntryBuilder.append("\n");
				iterationEntryBuilder.append("Exception class: " + exceptionClass);
				iterationEntryBuilder.append("\n");
				iterationEntryBuilder.append("Stack trace: ");
				iterationEntryBuilder.append("\n");

				// To save the stack trace, we have to manually create a string from the stack
				// trace.
				StringBuilder stackTraceStringBuilder = new StringBuilder();
				if (isExceptionOccurred) {
					for (StackTraceElement stackTraceElement : exceptionResultIteration.getException()
							.getStackTrace()) {
						stackTraceStringBuilder.append(stackTraceElement.toString()).append("\n");
					}
				}

				iterationEntryBuilder.append(stackTraceStringBuilder.toString());
				rowData.add(iterationEntryBuilder.toString());

				rowComments.add(testCaseCode);
			}

			// Add statistics about breakdown of exceptions, etc.
			int numberOfIterations = exceptionResultBranch.getNumberOfIterations();
			int numberOfExceptions = exceptionResultBranch.getNumberOfExceptions();
			int numberOfInMethodExceptions = exceptionResultBranch.getNumberOfInMethodExceptions();
			int numberOfOutMethodExceptions = exceptionResultBranch.getNumberOfOutMethodExceptions();
			List<Throwable> exceptions = exceptionResultBranch.getExceptions();
			List<Throwable> inMethodExceptions = exceptionResultBranch.getInMethodExceptions();
			List<Throwable> outMethodExceptions = exceptionResultBranch.getOutMethodExceptions();
			Map<String, Integer> inMethodExceptionsClassified = getExceptionTypesAndCount(inMethodExceptions);
			Map<String, Integer> outMethodExceptionsClassified = getExceptionTypesAndCount(outMethodExceptions);

			// Verification checks
			boolean isExceptionSumEqualTotal = (numberOfExceptions == (numberOfInMethodExceptions
					+ numberOfOutMethodExceptions));
			boolean isListSizeEqualExceptionCount = (exceptions.size() == numberOfExceptions);
			boolean isListSizeEqualInMethodExceptionCount = (inMethodExceptions.size() == numberOfInMethodExceptions);
			boolean isListSizeEqualOutMethodExceptionCount = (outMethodExceptions
					.size() == numberOfOutMethodExceptions);
			int sumOfInMethodExceptionCounts = 0;
			boolean isInMethodExceptionSumEqualTotal = false;
			for (Map.Entry<String, Integer> entry : inMethodExceptionsClassified.entrySet()) {
				boolean isValueNull = (entry.getValue() == null);
				if (isValueNull) {
					log.debug("Encountered a null value in the in-method exception classification map.");
					break;
				}

				sumOfInMethodExceptionCounts += entry.getValue();
			}
			isInMethodExceptionSumEqualTotal = (sumOfInMethodExceptionCounts == numberOfInMethodExceptions);

			int sumOfOutMethodExceptionCounts = 0;
			boolean isOutMethodExceptionSumEqualTotal = false;
			for (Map.Entry<String, Integer> entry : outMethodExceptionsClassified.entrySet()) {
				boolean isValueNull = (entry.getValue() == null);
				if (isValueNull) {
					log.debug("Encountered a null value in the out-method exception classification map.");
					break;
				}

				sumOfOutMethodExceptionCounts += entry.getValue();
			}
			isOutMethodExceptionSumEqualTotal = (sumOfOutMethodExceptionCounts == numberOfOutMethodExceptions);

			if (!isListSizeEqualExceptionCount) {
				log.debug("Exception count assertion was violated.");
			}

			if (!isListSizeEqualInMethodExceptionCount) {
				log.debug("In-method exception count assertion was violated.");
			}

			if (!isListSizeEqualOutMethodExceptionCount) {
				log.debug("Out-method exception count assertion was violated.");
			}

			if (!isExceptionSumEqualTotal) {
				log.debug("Exception total assertion was violated.");
			}

			if (!isInMethodExceptionSumEqualTotal) {
				log.debug("In-method exception total assertion was violated.");
			}

			if (!isOutMethodExceptionSumEqualTotal) {
				log.debug("Out-method exception total assertion was violated.");
			}

			StringBuilder inMethodExceptionClassificationStringBuilder = new StringBuilder();
			for (Map.Entry<String, Integer> entry : entrySetToOrderedList(inMethodExceptionsClassified.entrySet())) {
				String exceptionClass = entry.getKey();
				Integer count = entry.getValue();

				inMethodExceptionClassificationStringBuilder.append(exceptionClass).append(": ").append(count)
						.append("\n");
			}

			StringBuilder outMethodExceptionClassificationStringBuilder = new StringBuilder();
			for (Map.Entry<String, Integer> entry : entrySetToOrderedList(outMethodExceptionsClassified.entrySet())) {
				String exceptionClass = entry.getKey();
				Integer count = entry.getValue();

				outMethodExceptionClassificationStringBuilder.append(exceptionClass).append(": ").append(count)
						.append("\n");
			}

			rowStatistics.add(numberOfIterations);
			rowStatistics.add(numberOfExceptions);
			rowStatistics.add(numberOfInMethodExceptions);
			rowStatistics.add(inMethodExceptionClassificationStringBuilder.toString());
			rowStatistics.add(numberOfOutMethodExceptions);
			rowStatistics.add(outMethodExceptionClassificationStringBuilder.toString());

			rows.add(rowData);
			comments.add(rowComments);
			statistics.add(rowStatistics);
		}

		try {
			excelWriter.writeSheet(DEFAULT_SHEET, rows);
			excelWriter.writeSheet(TEST_CASE_CODE_SHEET, comments);
			excelWriter.writeSheet(STATISTICS_SHEET, statistics);
			excelWriter.writeSheet(COVERAGE_SHEET, coverages);
		} catch (IOException ioe) {
			log.error("IO Error\n", ioe);
		}
	}

	@Override
	public void recordError(String className, String methodName, Exception e) {
		Integer evosuiteIterationNumber = branchIdentifierToIterationNumber.get(className + methodName);
		if (evosuiteIterationNumber == null) {
			evosuiteIterationNumber = 0;
		}
		// Don't know the branch name, information not provided here.
		String unknownBranchName = "?";

		List<Object> rowData = new ArrayList<Object>();
		rowData.add(evosuiteIterationNumber);
		rowData.add(className);
		rowData.add(methodName);
		rowData.add(unknownBranchName);

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < e.getStackTrace().length; i++) {
			StackTraceElement ste = e.getStackTrace()[i];
			String s = "Class Name: " + ste.getClassName() + ", line number:  " + ste.getLineNumber() + "\n";
			sb.append(s);
		}

		rowData.add("Error: " + e.getClass().getCanonicalName() + " (" + e.toString() + ")\n" + sb.toString());
		try {
			excelWriter.writeSheet(DEFAULT_SHEET, Arrays.asList(rowData));
		} catch (IOException ex) {
			log.error("IO Error\n", ex);
		}
	}

	public String getFinalReportFilePath() {
		return excelWriter.getFile().getAbsolutePath();
	}

	private static Map<String, Integer> getExceptionTypesAndCount(List<Throwable> exceptions) {
		Map<String, Integer> toReturn = new HashMap<>();

		for (Throwable exception : exceptions) {
			String exceptionType = exception.getClass().getCanonicalName();
			Integer count = toReturn.get(exceptionType);
			if (count == null) {
				toReturn.put(exceptionType, 1);
			} else {
				toReturn.put(exceptionType, count + 1);
			}
		}

		return toReturn;
	}

	private static boolean isBranchCovered(EvoTestResult result,
			ExceptionResultBranch<TestChromosome> exceptionResultBranch) {
		Set<BranchInfo> coveredBranches = result.getCoveredBranchWithTest().keySet();
		BranchCoverageGoal branchCoverageGoal = ((BranchCoverageTestFitness) exceptionResultBranch.getFitnessFunction())
				.getBranchGoal();
		Branch branch = branchCoverageGoal.getBranch();
		BytecodeInstruction bytecodeInstruction = branch.getInstruction();
		for (BranchInfo branchInfo : coveredBranches) {
			boolean isClassNameSame = (branchInfo.getClassName().equals(branchCoverageGoal.getClassName()));
			boolean isMethodNameSame = (branchInfo.getMethodName().equals(branchCoverageGoal.getMethodName()));
			boolean isLineNumberSame = (branchInfo.getLineNo() == bytecodeInstruction.getLineNumber());
			boolean isTruthValueSame = (branchInfo.getTruthValue() == branchCoverageGoal.getValue());

			if (isClassNameSame && isMethodNameSame && isLineNumberSame && isTruthValueSame) {
				return true;
			}
		}

		return false;
	}

	private static List<Map.Entry<String, Integer>> entrySetToOrderedList(Set<Map.Entry<String, Integer>> entrySet) {
		List<Map.Entry<String, Integer>> orderedList = new ArrayList<>(entrySet);
		// Sort by decreasing number of exceptions
		Collections.sort(orderedList, (entry, anotherEntry) -> {
			boolean entryValueIsNull = (entry.getValue() == null);
			boolean anotherEntryValueIsNull = (anotherEntry.getValue() == null);
			if (entryValueIsNull && anotherEntryValueIsNull) {
				return 0;
			}

			if (entryValueIsNull) {
				return -1;
			}

			if (anotherEntryValueIsNull) {
				return 1;
			}

			return -entry.getValue().compareTo(anotherEntry.getValue());
		});
		return orderedList;
	}
}
