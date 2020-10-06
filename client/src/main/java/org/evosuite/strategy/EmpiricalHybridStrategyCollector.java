package org.evosuite.strategy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.poi.ss.usermodel.CellCopyPolicy;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.evosuite.Properties;
import org.evosuite.Properties.Algorithm;
import org.evosuite.Properties.Criterion;
import org.evosuite.Properties.HybridOption;
import org.evosuite.ShutdownTestWriter;
import org.evosuite.coverage.FitnessFunctions;
import org.evosuite.coverage.TestFitnessFactory;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.coverage.branch.BranchCoverageGoal;
import org.evosuite.coverage.fbranch.FBranchSuiteFitness;
import org.evosuite.coverage.fbranch.FBranchTestFitness;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.ga.metaheuristics.Hybridable;
import org.evosuite.ga.stoppingconditions.MaxStatementsStoppingCondition;
import org.evosuite.ga.stoppingconditions.MaxTimeStoppingCondition;
import org.evosuite.ga.stoppingconditions.StoppingCondition;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.ControlDependency;
import org.evosuite.graphs.cfg.RawControlFlowGraph;
import org.evosuite.rmi.ClientServices;
import org.evosuite.rmi.service.ClientState;
import org.evosuite.statistics.RuntimeVariable;
import org.evosuite.symbolic.DSEAlgorithm;
import org.evosuite.symbolic.expr.Constraint;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFitnessFunction;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testcase.execution.ExecutionTracer;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.evosuite.testsuite.TestSuiteFitnessFunction;
import org.evosuite.testsuite.TestSuiteMinimizer;
import org.evosuite.testsuite.factories.FixedSizeTestSuiteChromosomeFactory;
import org.evosuite.utils.ArrayUtil;
import org.evosuite.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author linyun
 *
 */
public class EmpiricalHybridStrategyCollector extends TestGenerationStrategy {

	private static final Logger logger = LoggerFactory.getLogger(EmpiricalHybridStrategyCollector.class);

	public static final int DEFAULT_PATH_TESTING_NUM = 1;

	class Segmentation {
		List<Branch> branchSegmentation;
		List<Constraint<?>> constraintSegmentation;
		long timeout;
		String strategy;
		TestChromosome bestIndividual;
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public TestSuiteChromosome generateTests() {
		// In order to improve strategy's performance, in here we explicitly disable
		// EvoSuite's
		// archive, as it is not used anyway by this strategy
		Properties.TEST_ARCHIVE = false;

		// Set up search algorithm
		LoggingUtils.getEvoLogger().info("* Setting up search algorithm for individual test generation");
		ExecutionTracer.enableTraceCalls();

		PropertiesTestGAFactory factory = new PropertiesTestGAFactory();

		// Get list of goals
		List<TestFitnessFactory<? extends TestFitnessFunction>> goalFactories = getFitnessFactories();
		// long goalComputationStart = System.currentTimeMillis();
		List<TestFitnessFunction> goals = new ArrayList<TestFitnessFunction>();
		LoggingUtils.getEvoLogger().info("* Total number of test goals: ");
		for (TestFitnessFactory<? extends TestFitnessFunction> goalFactory : goalFactories) {
			goals.addAll(goalFactory.getCoverageGoals());
			LoggingUtils.getEvoLogger()
					.info("  - " + goalFactory.getClass().getSimpleName().replace("CoverageFactory", "") + " "
							+ goalFactory.getCoverageGoals().size());
		}

		if (!canGenerateTestsForSUT()) {
			LoggingUtils.getEvoLogger()
					.info("* Found no testable methods in the target class " + Properties.TARGET_CLASS);
			ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.Total_Goals, goals.size());

			return new TestSuiteChromosome();
		}

		// Need to shuffle goals because the order may make a difference
		ClientServices.getInstance().getClientNode().trackOutputVariable(RuntimeVariable.Total_Goals, goals.size());

		LoggingUtils.getEvoLogger().info("* Total number of test goals: " + goals.size());

		// Bootstrap with random testing to cover easy goals
		// statistics.searchStarted(suiteGA);
		ClientServices.getInstance().getClientNode().changeState(ClientState.SEARCH);

//		TestSuiteChromosome suite = (TestSuiteChromosome) bootstrapRandomSuite(fitnessFunctions.get(0), goalFactories.get(0));
		TestSuiteChromosome suite = new TestSuiteChromosome();

//		StoppingCondition stoppingCondition = getStoppingCondition();
//		stoppingCondition.setLimit(strategyWiseBudget);

		for (TestFitnessFunction fitnessFunction : goals) {

			List<ArrayList<BranchGoal>> paths = achieveAllPaths(fitnessFunction);

			for (List<BranchGoal> path : paths) {

				int lasthybridStrategy = 999;

				for (int i = 0; i < DEFAULT_PATH_TESTING_NUM; i++) {
					logger.warn("working on " + fitnessFunction);

					List<TestChromosome> seeds = new ArrayList<>();
					suite.addTests(seeds);

					List<Segmentation> segList = new ArrayList<>();

//					Segmentation prevSeg = new Segmentation();

					long start = System.currentTimeMillis();
					long end = System.currentTimeMillis();

//					int lasthybridStrategy = 999;

					int timeout = Properties.OVERALL_HYBRID_STRATEGY_TIMEOUT * 1000 + path.size() * 10 * 1000;
					for (; end - start < timeout; end = System.currentTimeMillis()) {
						/**
						 * randomly select a strategy and run it for a while.
						 */
//						long remainBranchWiseBudget = end - start;
//						long timeBudget = strategyWiseBudget > remainBranchWiseBudget ? remainBranchWiseBudget : strategyWiseBudget;

						List<Hybridable> strategyList = getTotalStrategies(factory, fitnessFunction);
						int index = 0;
						Hybridable hybridStrategy;
						do {
							index = new Random().nextInt(strategyList.size());
							hybridStrategy = strategyList.get(index);
						} while (lasthybridStrategy == index);
						
						GeneticAlgorithm<TestChromosome> strategy = (GeneticAlgorithm<TestChromosome>) hybridStrategy;

						String strategyName = strategy.getClass().getCanonicalName();

						logger.warn("applying " + strategy.getClass().getCanonicalName());

						if (ShutdownTestWriter.isInterrupted()) {
							continue;
						}

						// Perform search
						logger.info("Starting evolution for goal " + fitnessFunction);
						long t1 = System.currentTimeMillis();
						hybridStrategy.generateSolution(suite);

						long t2 = System.currentTimeMillis();
						long realTimeout = t2 - t1;

						/**
						 * if this strategy can cover the branch
						 */
						TestChromosome bestIndividual = null;
						if (strategy.getBestIndividual() instanceof TestChromosome) {
							bestIndividual = (TestChromosome) strategy.getBestIndividual();
						} else {
							bestIndividual = findIndividualChromosomeFromSuite(strategy.getBestIndividual(),
									fitnessFunction);
						}

						Segmentation newSeg = parsePathSegmentation(segList, bestIndividual, realTimeout,
								fitnessFunction, strategyName, path);
						newSeg.bestIndividual = bestIndividual;
						segList.add(newSeg);

						if (fitnessFunction.getFitness(bestIndividual) == 0.0) {
							if (Properties.PRINT_COVERED_GOALS)
								LoggingUtils.getEvoLogger().info("* Covered!"); // : " +
							logger.warn("Found solution, adding to test suite at "
									+ MaxStatementsStoppingCondition.getNumExecutedStatements());
							break;
						} else {
							logger.warn("Found no solution for " + fitnessFunction + " at "
									+ MaxStatementsStoppingCondition.getNumExecutedStatements());
							seeds = hybridStrategy.getSeeds();
							suite.addTests(seeds);

							String segment = "";
							String goal = fitnessFunction.toString();

							int index1 = goal.indexOf(":");
							int index2 = goal.indexOf("-");

							goal = goal.substring(index1 + 2, index2 - 1);

							if (newSeg.branchSegmentation != null || newSeg.branchSegmentation.size() > 0) {
								for (int j = 0; j < newSeg.branchSegmentation.size(); j++) {
									Branch b = newSeg.branchSegmentation.get(j);
									segment = segment + b.toString() + ",";
								}
								if (segment.length() > 0) {
									segment = segment.substring(0, segment.length() - 1);
								}
							}

							if (segment.contains(goal))
								segList.remove(segList.indexOf(newSeg));
						}
						lasthybridStrategy = index;
					}

					// TODO add it back
					recordSegmentationList(segList, fitnessFunction);

				}

			}

		}

		return suite;
	}

	class BranchGoal {
		Branch branch;
		boolean value;

		public BranchGoal(Branch branch, boolean value) {
			super();
			this.branch = branch;
			this.value = value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getEnclosingInstance().hashCode();
			result = prime * result + ((branch == null) ? 0 : branch.hashCode());
			result = prime * result + (value ? 1231 : 1237);
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
			BranchGoal other = (BranchGoal) obj;
			if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
				return false;
			if (branch == null) {
				if (other.branch != null)
					return false;
			} else if (!branch.equals(other.branch))
				return false;
			if (value != other.value)
				return false;
			return true;
		}

		private EmpiricalHybridStrategyCollector getEnclosingInstance() {
			return EmpiricalHybridStrategyCollector.this;
		}

	}

	/**
	 * 
	 * 
	 * @param fitnessFunction
	 * @return
	 */
	private List<ArrayList<BranchGoal>> achieveAllPaths(TestFitnessFunction fitnessFunction) {
		List<ArrayList<BranchGoal>> pathList = new ArrayList<>();
		if (fitnessFunction instanceof FBranchTestFitness) {
			FBranchTestFitness fBranch = (FBranchTestFitness) fitnessFunction;
			BranchCoverageGoal goal = fBranch.getBranchGoal();
			ArrayList<BranchGoal> path = new ArrayList<>();
			path.add(new BranchGoal(goal.getBranch(), goal.getValue()));

			recursiveAchievePaths(pathList, path);
		}

		return pathList;
	}

	/**
	 * assume path has at least one branch goal
	 * 
	 * @param pathList
	 * @param path
	 */
	@SuppressWarnings("unchecked")
	private void recursiveAchievePaths(List<ArrayList<BranchGoal>> pathList, ArrayList<BranchGoal> path) {
		BranchGoal branchGoal = path.get(0);

		if (branchGoal.branch.getInstruction().getControlDependencies().isEmpty()) {
			pathList.add(path);
			return;
		}

		for (ControlDependency cd : branchGoal.branch.getInstruction().getControlDependencies()) {
			ArrayList<BranchGoal> newPath = (ArrayList<BranchGoal>) path.clone();
			BranchGoal newGoal = new BranchGoal(cd.getBranch(), cd.getBranchExpressionValue());

			if (!newPath.contains(newGoal)) {
				newPath.add(0, newGoal);
				recursiveAchievePaths(pathList, newPath);
			}
		}

	}


	/**
	 * TODO 1. check out which path segmentation is reached. 2. set strategy and
	 * timeout for the path segmentation 3. pass all the seeds to next strategy
	 * 
	 * @param fitnessFunction
	 * @param realTimeout
	 * @param strategyName
	 * @param path
	 */
	private Segmentation parsePathSegmentation(List<Segmentation> segList, TestChromosome bestIndividual,
			long realTimeout, TestFitnessFunction fitnessFunction, String strategyName, List<BranchGoal> path) {

		Segmentation currSeg = new Segmentation();
		currSeg.timeout = realTimeout;
		currSeg.strategy = strategyName;

		if (fitnessFunction instanceof FBranchTestFitness) {
			FBranchTestFitness fBranchFitness = (FBranchTestFitness) fitnessFunction;

			/**
			 * get bestindividual control path
			 */
			ExecutionResult er = fBranchFitness.runTest(bestIndividual.getTestCase());
			Set<Integer> btrue = er.getTrace().getCoveredTrueBranches();
			Set<Integer> bfalse = er.getTrace().getCoveredFalseBranches();

			/**
			 * get goal control path
			 */
			List<Branch> coverBranch = new ArrayList<Branch>();
			RawControlFlowGraph cfg = fBranchFitness.getBranch().getInstruction().getRawCFG();
			// controlDependencies branch
			for (int i = 0; i < path.size(); i++) {
				Branch b = path.get(i).branch;
//				if (b == fBranchFitness.getBranch())
//					break;
				boolean isCovered = false;
				boolean value = path.get(i).value;
				int branchId = b.getActualBranchId();
				if (value == true) {
					if (btrue.contains(branchId))
						isCovered = true;
				} else {
					if (bfalse.contains(branchId))
						isCovered = true;
				}

				if (isCovered)
					coverBranch.add(b);
				else
					break;
			}
			// ? List<constraints>

			/**
			 * compute segment difference with regard to existing segment list
			 */
			for (Segmentation seg : segList) {
				List<Branch> prevBranchList = seg.branchSegmentation;
				if (prevBranchList != null) {
					for (Branch b : prevBranchList) {
						if (coverBranch.contains(b))
							coverBranch.remove(b);
					}
				}

			}

			currSeg.branchSegmentation = coverBranch;
		}
		return currSeg;
	}

	private List<BytecodeInstruction> getControlDependentBranch(BytecodeInstruction branchGoalBytecodeInstruction,
			List<BytecodeInstruction> bytecodeInstructionList) {
		// TODO Auto-generated method stub
		int index = bytecodeInstructionList.indexOf(branchGoalBytecodeInstruction);
		while (bytecodeInstructionList.size() - 1 > index) {
			bytecodeInstructionList.remove(bytecodeInstructionList.size() - 1);
		}
		return bytecodeInstructionList;
	}

	private void recordSegmentationList(List<Segmentation> segList, TestFitnessFunction fitnessFunction) {
		String path = "D:\\linyun\\experiment\\";
		String targetfileName = "segmentationList.xlsx";
		String tmpfileNme = "segmentationList_tmp.xlsx";

		File files = new File(path);
		File[] filesList = files.listFiles();

		boolean isExisted = false;
		for (File f : filesList) {
			if (f.getName().equals(targetfileName)) {
				isExisted = true;
			}
			if (f.getName() == tmpfileNme) {
				File tmpFile = new File(path + tmpfileNme);
				tmpFile.delete();
			}
		}

		if (!isExisted) {
			initSegmentationListExcel(path + targetfileName);
		}

		if (segList != null) {
			recordTmpSegmentationList(path + tmpfileNme, segList, fitnessFunction);
			mergeSegmentationListResult(path + targetfileName, path + tmpfileNme);
		}

	}

	private void mergeSegmentationListResult(String targetFile, String tmpFile) {
		try {
			FileInputStream targetFileIn = new FileInputStream(targetFile);
			FileInputStream tmpFileIn = new FileInputStream(tmpFile);
			XSSFWorkbook targetwb = new XSSFWorkbook(targetFileIn);
			XSSFWorkbook tmpwb = new XSSFWorkbook(tmpFileIn);
			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet targetws = targetwb.getSheet("segmentationList");
			XSSFSheet tmpws = tmpwb.getSheet("segmentationList");
			XSSFSheet ws = wb.createSheet("segmentationList");
			int index = 0;
			for (int i = 0; i <= targetws.getLastRowNum(); i++) {
				XSSFRow Row = ws.createRow(index++);
				for (int j = 0; j < targetws.getRow(i).getLastCellNum(); j++) {
					XSSFCell cell = Row.createCell(j);
					copyCell(cell, targetws.getRow(i).getCell(j));
//					cell.setCellValue(ws0.getRow(i).getCell(j).getStringCellValue());
				}
			}
			for (int i = 0; i <= tmpws.getLastRowNum(); i++) {
				XSSFRow Row = ws.createRow(index++);
				for (int j = 0; j < tmpws.getRow(i).getLastCellNum(); j++) {
					XSSFCell cell = Row.createCell(j);
					copyCell(cell, tmpws.getRow(i).getCell(j));
//					cell.setCellValue(ws1.getRow(i).getCell(j).getStringCellValue());
				}
			}
			targetwb.close();
			tmpwb.close();
			targetFileIn.close();
			tmpFileIn.close();
			File file = new File(targetFile);
			file.delete();
			file = new File(tmpFile);
			file.delete();
			FileOutputStream fileOut = new FileOutputStream(targetFile);
			wb.write(fileOut);
			wb.close();
			fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void copyCell(XSSFCell destCell, XSSFCell srcCell) {
		if (srcCell == null) {
			destCell.setCellType(CellType.BLANK);
			return;
		}
		CellCopyPolicy policy = new CellCopyPolicy();
		policy.setCopyCellStyle(false);
		destCell.copyCellFrom(srcCell, policy);
	}

	private void recordTmpSegmentationList(String tmpFileName, List<Segmentation> segList,
			TestFitnessFunction fitnessFunction) {
		try {
			FileOutputStream fileOut = new FileOutputStream(tmpFileName);
			XSSFWorkbook seg_wb = new XSSFWorkbook();
			XSSFSheet seg_ws = seg_wb.createSheet("segmentationList");

			XSSFRow Row = seg_ws.createRow(0);
			XSSFCell goalCell = Row.createCell(0);
			String goal = "?";
			if (fitnessFunction instanceof FBranchTestFitness) {
				FBranchTestFitness fBranchFitness = (FBranchTestFitness) fitnessFunction;
				goal = fBranchFitness.getBranchGoal().toString();
			}
//			goalCell.setCellValue(goal);

			int index1 = goal.indexOf(":");
			int index2 = goal.indexOf("-");
			String goalBranch = goal.substring(index1 + 2, goal.length());
			goalCell.setCellValue(goalBranch);

			goalBranch = goal.substring(index1 + 2, index2 - 1);
			Segmentation segLast = segList.get(segList.size() - 1);
			String segment = "";

			if (segLast.branchSegmentation != null || segLast.branchSegmentation.size() > 0) {
				for (int j = 0; j < segLast.branchSegmentation.size(); j++) {
					Branch b = segLast.branchSegmentation.get(j);
					segment = segment + b.toString() + ",";
				}
				if (segment.length() > 0) {
					segment = segment.substring(0, segment.length() - 1);
				}
			}

			if (segment.contains(goalBranch)) {
				for (Segmentation seg : segList) {
					recordSegmentation(seg, seg_ws);
				}
			}

			seg_wb.write(fileOut);
			fileOut.close();
			seg_wb.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void initSegmentationListExcel(String file) {
		XSSFWorkbook segmentationList_wb = new XSSFWorkbook();
		XSSFSheet segmentationList_ws = segmentationList_wb.createSheet("segmentationList");
		String[] headers = { "Goals", "Segmentation", "Strategy", "Timeout", "Suite" };
		XSSFRow row = segmentationList_ws.createRow(0);
		addHeader(row, headers);

		try {
			OutputStream fileOut = new FileOutputStream(file);
			segmentationList_wb.write(fileOut);
			fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void addHeader(XSSFRow Row, String[] headers) {
		for (int i = 0; i < headers.length; i++) {
			XSSFCell cell = Row.createCell((short) i);
			XSSFRichTextString text = new XSSFRichTextString(headers[i]);
			cell.setCellValue(text.toString());

		}
	}

	private void recordSegmentation(Segmentation seg, XSSFSheet seg_ws) {

		XSSFRow row = seg_ws.getRow(0);
		short newCellNum = row.getLastCellNum();

		String segment = "", suite = "";

//		XSSFCell branch_cell = row.createCell(newCellNum);
//		String branch = "";
		// branch
		if (seg.branchSegmentation != null || seg.branchSegmentation.size() > 0) {
			for (int j = 0; j < seg.branchSegmentation.size(); j++) {
				Branch b = seg.branchSegmentation.get(j);
				segment = segment + b.toString() + ",";
			}
			if (segment.length() > 0) {
				segment = segment.substring(0, segment.length() - 1);
			}
		}
		if (segment != "") {

//			XSSFCell branch = row.getCell(0);
//			String s = branch.toString();
//			
//			int index2 = s.indexOf("-");
//			
//			s = s.substring(0, index2-1);
//			
//			if(segment.contains(s))
//			{
			XSSFCell segment_cell = row.createCell(newCellNum);
			segment_cell.setCellValue(segment);
			newCellNum++;

			XSSFCell strategy_cell = row.createCell((short) newCellNum);
			strategy_cell.setCellValue(seg.strategy);
			newCellNum++;

			XSSFCell timeout_cell = row.createCell((short) newCellNum);
			timeout_cell.setCellValue(Long.toString(seg.timeout));
			newCellNum++;

			XSSFCell suite_cell = row.createCell((short) newCellNum);
			TestChromosome bestIndividual = null;
			bestIndividual = seg.bestIndividual;
			suite = bestIndividual.toString();
			suite_cell.setCellValue(suite);
//			}
		}

	}

	private TestChromosome findIndividualChromosomeFromSuite(Object bestIndividual,
			TestFitnessFunction fitnessFunction) {

		TestChromosome best = null;
		double fitness = -1;

		if (bestIndividual instanceof TestSuiteChromosome) {
			TestSuiteChromosome suite = (TestSuiteChromosome) bestIndividual;
			for (TestChromosome test : suite.getTestChromosomes()) {
				if (best == null) {
					best = test;
					fitness = fitnessFunction.getFitness(test);
				} else {
					double tmpFitness = fitnessFunction.getFitness(test);
					if (tmpFitness < fitness) {
						best = test;
						fitness = fitnessFunction.getFitness(test);
					}

				}

			}
		}

		return best;
	}

	private void deriveSingleGoal(FBranchSuiteFitness ff, TestFitnessFunction fitnessFunction) {
		ff.totalBranches = 1;
		ff.totalGoals = 1;

		/**
		 * TODO it is better to refactor, no need to be specific for FBranchTestFitness
		 */
		if (fitnessFunction instanceof FBranchTestFitness) {
			FBranchTestFitness function = (FBranchTestFitness) fitnessFunction;
			int branchID = function.getBranch().getActualBranchId();

			ff.getBranchesId().clear();
			ff.getBranchesId().add(branchID);

			Map<Integer, TestFitnessFunction> mapToModify = function.getBranchExpressionValue()
					? ff.getBranchCoverageTrueMap()
					: ff.getBranchCoverageFalseMap();
			Map<Integer, TestFitnessFunction> mapToClear = !function.getBranchExpressionValue()
					? ff.getBranchCoverageTrueMap()
					: ff.getBranchCoverageFalseMap();

			mapToClear.clear();

			Iterator<Integer> iter = mapToModify.keySet().iterator();
			while (iter.hasNext()) {
				Integer i = iter.next();
				if (i != branchID) {
					iter.remove();
				}
			}

			System.currentTimeMillis();
		}

//		ff.get

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<Hybridable> getTotalStrategies(PropertiesTestGAFactory factory,
			TestFitnessFunction fitnessFunction) {
		List<Hybridable> list = new ArrayList<>();

		for(int i=0; i<Properties.HYBRID_OPTION.length; i++) {
			if(Properties.HYBRID_OPTION[i].equals(HybridOption.DSE)) {
				DSEAlgorithm dse = new DSEAlgorithm();
				setStrategyWiseBudget((GeneticAlgorithm) dse);
				TestSuiteFitnessFunction function = FitnessFunctions.getFitnessFunction(Properties.CRITERION[0]);
				if (function instanceof FBranchSuiteFitness) {
					FBranchSuiteFitness ff = (FBranchSuiteFitness) function;
					deriveSingleGoal(ff, fitnessFunction);
				}
				((GeneticAlgorithm) dse).addFitnessFunction(function);
				list.add((Hybridable) dse);
			}
			else if(Properties.HYBRID_OPTION[i].equals(HybridOption.RANDOM)) {
				Properties.ALGORITHM = Algorithm.RANDOM_SEARCH;
				GeneticAlgorithm<TestChromosome> random = factory.getSearchAlgorithm();
				setStrategyWiseBudget(random);
				random.addFitnessFunction(fitnessFunction);
				list.add((Hybridable) random);
			}
			else if(Properties.HYBRID_OPTION[i].equals(HybridOption.SEARCH)) {
				Properties.ALGORITHM = Algorithm.MONOTONIC_GA;
				GeneticAlgorithm<TestChromosome> ga = factory.getSearchAlgorithm();
				setStrategyWiseBudget(ga);
				ga.addFitnessFunction(fitnessFunction);
				list.add((Hybridable) ga);
			}
		}
		
		
		

		

		

//		Properties.ALGORITHM = Algorithm.MOSA;
//		GeneticAlgorithm<TestChromosome> mosa = factory.getSearchAlgorithm();
//		setStrategyWiseBudget(mosa);
//		ga.addFitnessFunction(fitnessFunction);
//		list.add((Hybridable)mosa);
//		
//		Properties.ALGORITHM = Algorithm.DYNAMOSA;
//		GeneticAlgorithm<TestChromosome> dynamosa = factory.getSearchAlgorithm();
//		setStrategyWiseBudget(dynamosa);
//		ga.addFitnessFunction(fitnessFunction);
//		list.add((Hybridable)dynamosa);

		return list;
	}

	
	private void setStrategyWiseBudget(GeneticAlgorithm<TestChromosome> ga) {
		for (StoppingCondition condition : ga.getStoppingConditions()) {
			if (condition instanceof MaxTimeStoppingCondition) {
				MaxTimeStoppingCondition mCondition = (MaxTimeStoppingCondition) condition;
				mCondition.setLimit(Properties.INDIVIDUAL_STRATEGY_TIMEOUT);
			}
		}
	}

	private Set<Integer> getAdditionallyCoveredGoals(List<? extends TestFitnessFunction> goals, Set<Integer> covered,
			TestChromosome best) {

		Set<Integer> r = new HashSet<Integer>();
		ExecutionResult result = best.getLastExecutionResult();
		assert (result != null);
		// if (result == null) {
		// result = TestCaseExecutor.getInstance().execute(best.test);
		// }
		int num = -1;
		for (TestFitnessFunction goal : goals) {
			num++;
			if (covered.contains(num))
				continue;
			if (goal.isCovered(best, result)) {
				r.add(num);
				if (Properties.PRINT_COVERED_GOALS)
					LoggingUtils.getEvoLogger().info("* Additionally covered: " + goal.toString());
			}
		}
		return r;
	}

	private TestSuiteChromosome bootstrapRandomSuite(FitnessFunction<?> fitness, TestFitnessFactory<?> goals) {

		if (ArrayUtil.contains(Properties.CRITERION, Criterion.DEFUSE)
				|| ArrayUtil.contains(Properties.CRITERION, Criterion.ALLDEFS)) {
			LoggingUtils.getEvoLogger().info("* Disabled random bootstraping for dataflow criterion");
			Properties.RANDOM_TESTS = 0;
		}

		if (Properties.RANDOM_TESTS > 0) {
			LoggingUtils.getEvoLogger().info("* Bootstrapping initial random test suite");
		} // else
			// LoggingUtils.getEvoLogger().info("* Bootstrapping initial random test suite
			// disabled!");

		FixedSizeTestSuiteChromosomeFactory factory = new FixedSizeTestSuiteChromosomeFactory(Properties.RANDOM_TESTS);

		TestSuiteChromosome suite = factory.getChromosome();
		if (Properties.RANDOM_TESTS > 0) {
			TestSuiteMinimizer minimizer = new TestSuiteMinimizer(goals);
			minimizer.minimize(suite, true);
			LoggingUtils.getEvoLogger().info("* Initial test suite contains " + suite.size() + " tests");
		}

		return suite;
	}
}
