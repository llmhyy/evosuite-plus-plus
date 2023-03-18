package feature.regression;

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.result.TestGenerationResult;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitHistoryRefactoringMiner;
import org.refactoringminer.api.GitService;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringHandler;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;
import java.util.Collections;
import sootup.callgraph.CallGraph;
import sootup.callgraph.CallGraphAlgorithm;
import sootup.callgraph.ClassHierarchyAnalysisAlgorithm;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.signatures.MethodSignature;
import sootup.core.typehierarchy.ViewTypeHierarchy;
import sootup.core.types.ClassType;
import sootup.core.types.ReferenceType;
import sootup.core.types.VoidType;
import sootup.java.bytecode.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaIdentifierFactory;
import sootup.java.core.JavaProject;
import sootup.java.core.JavaSootClass;
import sootup.java.core.language.JavaLanguage;
import sootup.java.core.views.JavaView;
import java.io.*;
import java.util.*;

import common.TestUtility;
import feature.regression.example1.RegressionExample;

public class RegressionTest extends TestUtility{
	
//	@Before
//	public void beforeTest() {
//		Properties.CLIENT_ON_THREAD = true;
//		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;
//
//		Properties.ENABLE_BRANCH_ENHANCEMENT = false;
//		Properties.APPLY_OBJECT_RULE = true;
//		Properties.APPLY_INTERPROCEDURAL_GRAPH_ANALYSIS = true;
//		Properties.ADOPT_SMART_MUTATION = false;
//		
//		Properties.INSTRUMENT_CONTEXT = true;
//		Properties.CHROMOSOME_LENGTH = 200;
//		
////		Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 20;
////		Properties.TOTAL_LEGITIMIZATION_BUDGET = 50;
//		Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 0;
//		Properties.TOTAL_LEGITIMIZATION_BUDGET = 0;
//		Properties.TIMEOUT = 10;
////		Properties.SANDBOX_MODE = Sandbox.SandboxMode.OFF;
//	}
	@Test
	public void testPipelineIntegration() throws IOException {
        //Step 1: Setting up and Obtaining entries from Res4j
		String storageFile = "/Users/diwuyi/Documents/GitHub/refactoring-miner/src/benchmarkEntry.txt";
		File file = new File(storageFile);
		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		String str;
		Map<String, List<String>> relevantProjectWithRic = new HashMap<>();
		while ((str = bufferedReader.readLine()) != null) {
			String[] tempLine = str.split(", ");
			String proj = tempLine[tempLine.length - 1];
			String ric = tempLine[1];
			if (relevantProjectWithRic.containsKey(proj)) {
				List<String> currRics = relevantProjectWithRic.get(proj);
				currRics.add(ric);
			} else {
				relevantProjectWithRic.put(proj, new ArrayList<>(10));
			}
		}
        //Step 2: Applying Refactoring Miner
		GitService gitService = new GitServiceImpl();
		GitHistoryRefactoringMiner miner = new GitHistoryRefactoringMinerImpl();

		Repository repo = null;

		try {
			repo = gitService.cloneIfNotExists(
					"jhy/jsoup",
					"https://github.com/jhy/jsoup.git");
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (String key: relevantProjectWithRic.keySet()) {
			//API 2: Report all refactoring changes in commits between 2 commits (ric & rfc in this case)
//        miner.detectBetweenCommits(repo,
//                "d23fc99a99ac44f2a4352899e2a6d12d26a74503", "c716d1de3fe1bde6a330629939c45745e9e65e95",
//                new RefactoringHandler() {
//                    @Override
//                    public void handle(String commitId, List<Refactoring> refactorings) {
//                        System.out.println("Refactorings at " + commitId);
//                        for (Refactoring ref : refactorings) {
//                            System.out.println(ref.toString());
//                        }
//                    }
//                });

			//API 1: Report refactoring changes in one single commit (ric)
			try {
				repo = gitService.openRepository(key);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			System.out.println(key);
			List<String> rics = relevantProjectWithRic.get(key);
			FileWriter myWriter = new FileWriter("/Users/diwuyi/Documents/GitHub/refactoring-miner/src/refactoring.txt");
			for (String ric : rics) {
				miner.detectAtCommit(repo, ric, new RefactoringHandler() {
					@Override
					public void handle(String commitId, List<Refactoring> refactorings) {
						System.out.println("Refactorings at " + commitId);
						try {
							myWriter.write(key + ", "+"Refactorings at " + commitId + "\n");
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
						for (Refactoring ref : refactorings) {
							System.out.println(ref.toString());
							try {
								myWriter.write(ref.toString() + "\n");
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						}
					}
				});
			}
		}

		//Step 3: Construct of Call Graph with SootUp library --> code
		System.out.println("start construction of call graph");
		System.out.println(System.getProperty("java.home"));
		AnalysisInputLocation<JavaSootClass> inputLocation =
				new JavaClassPathAnalysisInputLocation(
						"/Users/diwuyi/Documents/GitHub/refactoring-miner/src/main/resources");

		JavaLanguage language = new JavaLanguage(8);

		JavaProject project =
				JavaProject.builder(language)
						.addInputLocation(inputLocation)
						.addInputLocation(
								new JavaClassPathAnalysisInputLocation(
										"/Library/Java/JavaVirtualMachines/jdk1.8.0_333.jdk/Contents/Home/jre/lib/rt.jar"))
						.build();

		JavaView view = project.createFullView();

		ViewTypeHierarchy typeHierarchy = new ViewTypeHierarchy(view);
		ClassType classTypeA = project.getIdentifierFactory().getClassType("A");
		ClassType classTypeB = project.getIdentifierFactory().getClassType("B");

		ClassType classType1 = project.getIdentifierFactory().getClassType("Jsoup");
		ClassType classType2 = project.getIdentifierFactory().getClassType("org.jsoup.nodes.Document");
		ReferenceType type1 = project.getIdentifierFactory().getClassType("java.lang.String");
		ArrayList<String> params = new ArrayList<>(3);
		params.add("java.nio.file.Files");
		params.add("java.lang.String");
		params.add("java.lang.String");
		MethodSignature entryMethodSignature =
				JavaIdentifierFactory.getInstance()
						.getMethodSignature(
								classTypeA,
								JavaIdentifierFactory.getInstance()
										.getMethodSubSignature(
												"calc", VoidType.getInstance(), Collections.singletonList(classTypeA)));
		CallGraphAlgorithm cha = new ClassHierarchyAnalysisAlgorithm(view, typeHierarchy);

		CallGraph cg = cha.initialize(Collections.singletonList(entryMethodSignature));

		cg.callsFrom(entryMethodSignature).forEach(System.out::println);

		//attempt of self-built call graph, naive approach
//        String graphFiles = "/Users/diwuyi/Documents/GitHub/refactoring-miner/src/main/resources";
//        Map<String, String> var = new HashMap<>(10);
//        Map<String, String> method = new HashMap<>(10);
//        BufferedReader bf = new BufferedReader(new FileReader(file));
//        String str;
//        Map<String, List<String>> relevantProjectWithRic = new HashMap<>();
//        while ((str = bufferedReader.readLine()) != null) {
//            String[] tempLine = str.split(", ");
//            String proj = tempLine[tempLine.length - 1];
//            String ric = tempLine[1];
//            if (relevantProjectWithRic.containsKey(proj)) {
//                List<String> currRics = relevantProjectWithRic.get(proj);
//                currRics.add(ric);
//            } else {
//                relevantProjectWithRic.put(proj, new ArrayList<>(10));
//            }
//        }
		//Step 3.5 refactoring miner 报出来的 refactor 是否包含target method (引起 bug 的修改)
		//Step 4: Differential Testing with evosuite
		// Experiment : target method identification -->
		// Differential test case 报了一个错 , 看跟 ric 是不是也是一样的 bug
		// 覆盖率：同时走过 fixing 的地方，
		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		String[] arguments = new String[] {"/bin/bash", "-c", "java -jar evosuite-1.0.6.jar -regressionSuite -projectCP jsoup-1.13.1-SNAPSHOT_correct.jar -Dregressioncp=\"jsoup-1.11.3-SNAPSHOT.jar\" -class org.jsoup.parser.CharacterReader"};
		// 调evosuite 的 api 接口
		// current code below is not working

//		Process process = new ProcessBuilder(arguments).start();
//		BufferedReader procReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//		String ln = "";
//		while ((ln = procReader.readLine()) != null) {
//			System.out.println(ln + "\n");
//		}
//		try {
//			process.waitFor();
//		} catch (InterruptedException e) {
//			throw new RuntimeException(e);
//		}
//		Runtime.getRuntime().exec("/bin/bash -c java -jar evosuite-1.0.6.jar -Dtools_jar_location=/Library/Java/JavaVirtualMachines/jdk1.8.0_333.jdk/Contents/Home/lib/tools.jar -regressionSuite -projectCP jsoup-1.13.1-SNAPSHOT_correct.jar -Dregressioncp=\"jsoup-1.11.3-SNAPSHOT.jar\" -class org.jsoup.parser.CharacterReader");
//		//process.waitFor();

		String targetClass = "com.RegressionExample";
		System.out.println(targetClass);

//		Method method = clazz.getMethods()[0];
//		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

//		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		//path to origin and regression
//		String original = "/Users/diwuyi/Library/Mobile Documents/com~apple~CloudDocs/Documents/GitHub/evosuite-plus-plus/shell/src/test/java/feature/regression/example1";
//		String regression = "/Users/diwuyi/Library/Mobile Documents/com~apple~CloudDocs/Documents/GitHub/evosuite-plus-plus/shell/src/test/java/feature/regression/example2";
		String original = "/Users/diwuyi/Desktop/target-example/original/bin";
		String regression = "/Users/diwuyi/Desktop/target-example/regression/bin";
		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.TIMEOUT = 100;
		Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10;
//		Properties.TIMELINE_INTERVAL = 3000;

		int timeBudget = 10;

		double coverage = evoTestRegressionSingleMethod(targetClass,
				null, timeBudget, true, original, regression);

		System.out.println("coverage is:" + coverage);

		System.out.println("Execution of differential testing is done");


	}
	
	@Test
	public void testBasicRulesObj() {
		Class<?> clazz = RegressionExample.class;
		// where to find target class
		//String targetClass = clazz.getCanonicalName();
		String targetClass = "com.RegressionExample";
		System.out.println(targetClass);

//		Method method = clazz.getMethods()[0];
//		Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

//		String targetMethod = method.getName() + MethodUtil.getSignature(method);
		//path to origin and regression
//		String original = "/Users/diwuyi/Library/Mobile Documents/com~apple~CloudDocs/Documents/GitHub/evosuite-plus-plus/shell/src/test/java/feature/regression/example1";
//		String regression = "/Users/diwuyi/Library/Mobile Documents/com~apple~CloudDocs/Documents/GitHub/evosuite-plus-plus/shell/src/test/java/feature/regression/example2";
        String original = "/Users/diwuyi/Desktop/target-example/original/bin";
		String regression = "/Users/diwuyi/Desktop/target-example/regression/bin";
		// Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
		Properties.CLIENT_ON_THREAD = true;
		Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

		Properties.TIMEOUT = 100;
		Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10;
//		Properties.TIMELINE_INTERVAL = 3000;
		
		int timeBudget = 10;
		
		double coverage = evoTestRegressionSingleMethod(targetClass,  
				null, timeBudget, true, original, regression);
		
		System.out.println("coverage is:" + coverage);
//		assert coverage > 0.1;
		
	}
	
	public static double evoTestRegressionSingleMethod(String targetClass, String targetMethod, 
			int timeBudget, 
			boolean instrumentContext,
			String originalCP,
			String regressionCP) {
		EvoSuite evo = new EvoSuite();
		Properties.TARGET_CLASS = targetClass;
		Properties.TRACK_COVERED_GRADIENT_BRANCHES = true;
		// Properties.CRITERION = new Criterion[] { Criterion.BRANCH };
		// Properties.STRATEGY = Strategy.RANDOM;
		String[] command = new String[] {
				"-regressionSuite",
				"-class", targetClass, 
				"-projectCP", originalCP,
				"-Dregressioncp", regressionCP
//				"-Dtarget_method", targetMethod
				};

		@SuppressWarnings("unchecked")
		List<List<TestGenerationResult>> list = (List<List<TestGenerationResult>>) evo.parseCommandLine(command);
		for (List<TestGenerationResult> l : list) {
			for (TestGenerationResult r : l) {
				System.out.println(r.getProgressInformation());
				if (r.getDistribution() != null) {
					for (int i = 0; i < r.getDistribution().length; i++) {
						System.out.println(r.getDistribution()[i]);
					}
				}

				int age = 0;
				if (r.getGeneticAlgorithm() != null) {
					age = r.getGeneticAlgorithm().getAge();
					System.out.println("Generations: " + age);
				}

				System.out.println("Used time: " + r.getElapseTime());
				System.out.println("Age: " + r.getAge());

				System.out.println("Available calls: " + getAvailableCalls());
				System.out.println("Unavailable calls: " + getUnavailableCalls());
				
				return r.getCoverage();
			}
		}
		
		return 0;

	}
}
