package feature.objectconstruction.testgeneration.testcase;

import common.TestUtility;
import feature.objectconstruction.testgeneration.example.set1.Target;
import feature.objectconstruction.testgeneration.example.set2.Target2;
import feature.objectconstruction.testgeneration.example.set3.Target3;
import feature.objectconstruction.testgeneration.example.set4.Target4;
import feature.objectconstruction.testgeneration.example.set5.Target5;
import feature.objectconstruction.testgeneration.example.set6.ListClass;
import feature.objectconstruction.testgeneration.example.set6.Target6;
import org.evosuite.Properties;
import org.evosuite.Properties.StatisticsBackend;
import org.evosuite.utils.MethodUtil;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;


public class SelfDefinedTest extends TestUtility{

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
    public void testSet1Method1() {
        Class<?> clazz = Target.class;
        String methodName = "checkGrade";
        int parameterNum = 2;

        String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
        Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

        String targetMethod = method.getName() + MethodUtil.getSignature(method);
        String cp = "target/test-classes" + File.pathSeparator + "target/classes";

        // Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
        Properties.CLIENT_ON_THREAD = true;
        Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;


        Properties.TIMEOUT = 10;
        Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10;

//		Properties.TIMELINE_INTERVAL = 3000;

        String fitnessApproach = "branch";

        int timeBudget = 10;

        boolean aor = true;
        double coverage = TestUtility.evoTestSingleMethod(targetClass,
                targetMethod, timeBudget, true, aor, cp, fitnessApproach,
                "generateMOSuite", "MOSUITE", "DynaMOSA");

        System.out.println("coverage is:" + coverage);
        assert coverage > 0.1;

    }

    @Test
    public void testSet1Method1NoObjectRule() {
        Class<?> clazz = Target.class;
        String methodName = "checkGrade";
        int parameterNum = 2;

        String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
        Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

        String targetMethod = method.getName() + MethodUtil.getSignature(method);
        String cp = "target/test-classes" + File.pathSeparator + "target/classes";

        // Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
        Properties.CLIENT_ON_THREAD = true;
        Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

        Properties.TIMEOUT = 10;
        Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10;
//		Properties.TIMELINE_INTERVAL = 3000;

        String fitnessApproach = "branch";

        int timeBudget = 10;

        boolean aor = false;
        double coverage = TestUtility.evoTestSingleMethod(targetClass,
                targetMethod, timeBudget, true, aor, cp, fitnessApproach,
                "generateMOSuite", "MOSUITE", "DynaMOSA");

        System.out.println("coverage is:" + coverage);
        assert coverage > 0.1;

    }

    @Test
    public void testSet2Method1() {
        Class<?> clazz = Target2.class;
        String methodName = "crossLayer";
        int parameterNum = 1;

        String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
        Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

        String targetMethod = method.getName() + MethodUtil.getSignature(method);
        String cp = "target/test-classes" + File.pathSeparator + "target/classes";

        // Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
        Properties.CLIENT_ON_THREAD = true;
        Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

        Properties.TIMEOUT = 10;
        Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10;
//		Properties.TIMELINE_INTERVAL = 3000;

        String fitnessApproach = "branch";

        int timeBudget = 10;

        boolean aor = true;
        double coverage = TestUtility.evoTestSingleMethod(targetClass,
                targetMethod, timeBudget, true, aor, cp, fitnessApproach,
                "generateMOSuite", "MOSUITE", "DynaMOSA");

        System.out.println("coverage is:" + coverage);
        assert coverage > 0.1;

    }


    @Test
    public void testSet2Method1NoObjectRule() {
        Class<?> clazz = Target2.class;
        String methodName = "crossLayer";
        int parameterNum = 1;

        String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
        Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

        String targetMethod = method.getName() + MethodUtil.getSignature(method);
        String cp = "target/test-classes" + File.pathSeparator + "target/classes";

        // Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
        Properties.CLIENT_ON_THREAD = true;
        Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

        Properties.TIMEOUT = 10;
        Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10;
//		Properties.TIMELINE_INTERVAL = 3000;

        String fitnessApproach = "branch";

        int timeBudget = 10;

        boolean aor = false;
        double coverage = TestUtility.evoTestSingleMethod(targetClass,
                targetMethod, timeBudget, true, aor, cp, fitnessApproach,
                "generateMOSuite", "MOSUITE", "DynaMOSA");

        System.out.println("coverage is:" + coverage);
        assert coverage > 0.1;

    }

    @Test
    public void testSet2Method2() {
        Class<?> clazz = Target2.class;
        String methodName = "checkEqual";
        int parameterNum = 2;

        String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
        Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

        String targetMethod = method.getName() + MethodUtil.getSignature(method);
        String cp = "target/test-classes" + File.pathSeparator + "target/classes";

        // Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
        Properties.CLIENT_ON_THREAD = true;
        Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

        Properties.TIMEOUT = 10;
        Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10;
//		Properties.TIMELINE_INTERVAL = 3000;

        String fitnessApproach = "branch";

        int timeBudget = 10;

        boolean aor = true;
        double coverage = TestUtility.evoTestSingleMethod(targetClass,
                targetMethod, timeBudget, true, aor, cp, fitnessApproach,
                "generateMOSuite", "MOSUITE", "DynaMOSA");

        System.out.println("coverage is:" + coverage);
        assert coverage > 0.1;

    }

    @Test
    public void testSet2Method2NoObjectRule() {
        Class<?> clazz = Target2.class;
        String methodName = "checkEqual";
        int parameterNum = 2;

        String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
        Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

        String targetMethod = method.getName() + MethodUtil.getSignature(method);
        String cp = "target/test-classes" + File.pathSeparator + "target/classes";

        // Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
        Properties.CLIENT_ON_THREAD = true;
        Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

        Properties.TIMEOUT = 10;
        Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10;
//		Properties.TIMELINE_INTERVAL = 3000;

        String fitnessApproach = "branch";

        int timeBudget = 10;

        boolean aor = false;
        double coverage = TestUtility.evoTestSingleMethod(targetClass,
                targetMethod, timeBudget, true, aor, cp, fitnessApproach,
                "generateMOSuite", "MOSUITE", "DynaMOSA");

        System.out.println("coverage is:" + coverage);
        assert coverage > 0.1;

    }


    @Test
    public void testSet3Method1() {
        Class<?> clazz = Target3.class;
        String methodName = "crossLayer";
        int parameterNum = 1;

        String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
        Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

        String targetMethod = method.getName() + MethodUtil.getSignature(method);
        String cp = "target/test-classes" + File.pathSeparator + "target/classes";

        // Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
        Properties.CLIENT_ON_THREAD = true;
        Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

        Properties.TIMEOUT = 10;
        Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10;
//		Properties.TIMELINE_INTERVAL = 3000;

        String fitnessApproach = "branch";

        int timeBudget = 10;

        boolean aor = true;
        double coverage = TestUtility.evoTestSingleMethod(targetClass,
                targetMethod, timeBudget, true, aor, cp, fitnessApproach,
                "generateMOSuite", "MOSUITE", "DynaMOSA");

        System.out.println("coverage is:" + coverage);
        assert coverage > 0.1;

    }


    @Test
    public void testSet3Method1NoObjectRule() {
        Class<?> clazz = Target3.class;
        String methodName = "crossLayer";
        int parameterNum = 1;

        String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
        Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

        String targetMethod = method.getName() + MethodUtil.getSignature(method);
        String cp = "target/test-classes" + File.pathSeparator + "target/classes";

        // Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
        Properties.CLIENT_ON_THREAD = true;
        Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

        Properties.TIMEOUT = 10;
        Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10;
//		Properties.TIMELINE_INTERVAL = 3000;

        String fitnessApproach = "branch";

        int timeBudget = 10;

        boolean aor = false;
        double coverage = TestUtility.evoTestSingleMethod(targetClass,
                targetMethod, timeBudget, true, aor, cp, fitnessApproach,
                "generateMOSuite", "MOSUITE", "DynaMOSA");

        System.out.println("coverage is:" + coverage);
        assert coverage > 0.1;

    }

    @Test
    public void testSet3Method2() {
        Class<?> clazz = Target3.class;
        String methodName = "checkEqual";
        int parameterNum = 2;

        String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
        Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

        String targetMethod = method.getName() + MethodUtil.getSignature(method);
        String cp = "target/test-classes" + File.pathSeparator + "target/classes";

        // Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
        Properties.CLIENT_ON_THREAD = true;
        Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

        Properties.TIMEOUT = 10;
        Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10;
//		Properties.TIMELINE_INTERVAL = 3000;

        String fitnessApproach = "branch";

        int timeBudget = 10;

        boolean aor = true;
        double coverage = TestUtility.evoTestSingleMethod(targetClass,
                targetMethod, timeBudget, true, aor, cp, fitnessApproach,
                "generateMOSuite", "MOSUITE", "DynaMOSA");

        System.out.println("coverage is:" + coverage);
        assert coverage > 0.1;

    }

    @Test
    public void testSet2Method3NoObjectRule() {
        Class<?> clazz = Target3.class;
        String methodName = "checkEqual";
        int parameterNum = 2;

        String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
        Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

        String targetMethod = method.getName() + MethodUtil.getSignature(method);
        String cp = "target/test-classes" + File.pathSeparator + "target/classes";

        // Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
        Properties.CLIENT_ON_THREAD = true;
        Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

        Properties.TIMEOUT = 10;
        Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10;
//		Properties.TIMELINE_INTERVAL = 3000;

        String fitnessApproach = "branch";

        int timeBudget = 10;

        boolean aor = false;
        double coverage = TestUtility.evoTestSingleMethod(targetClass,
                targetMethod, timeBudget, true, aor, cp, fitnessApproach,
                "generateMOSuite", "MOSUITE", "DynaMOSA");

        System.out.println("coverage is:" + coverage);
        assert coverage > 0.1;

    }

    @Test
    public void testSet4Method1() {
        Class<?> clazz = Target4.class;
        String methodName = "crossLayer";
        int parameterNum = 1;

        String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
        Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

        String targetMethod = method.getName() + MethodUtil.getSignature(method);
        String cp = "target/test-classes" + File.pathSeparator + "target/classes";

        // Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
        Properties.CLIENT_ON_THREAD = true;
        Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

        Properties.TIMEOUT = 10;
        Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10;
//		Properties.TIMELINE_INTERVAL = 3000;

        String fitnessApproach = "branch";

        int timeBudget = 10;

        boolean aor = true;
        double coverage = TestUtility.evoTestSingleMethod(targetClass,
                targetMethod, timeBudget, true, aor, cp, fitnessApproach,
                "generateMOSuite", "MOSUITE", "DynaMOSA");

        System.out.println("coverage is:" + coverage);
        assert coverage > 0.1;

    }


    @Test
    public void testSet4Method1NoObjectRule() {
        Class<?> clazz = Target4.class;
        String methodName = "crossLayer";
        int parameterNum = 1;

        String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
        Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

        String targetMethod = method.getName() + MethodUtil.getSignature(method);
        String cp = "target/test-classes" + File.pathSeparator + "target/classes";

        // Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
        Properties.CLIENT_ON_THREAD = true;
        Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

        Properties.TIMEOUT = 10;
        Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10;
//		Properties.TIMELINE_INTERVAL = 3000;

        String fitnessApproach = "branch";

        int timeBudget = 10;

        boolean aor = false;
        double coverage = TestUtility.evoTestSingleMethod(targetClass,
                targetMethod, timeBudget, true, aor, cp, fitnessApproach,
                "generateMOSuite", "MOSUITE", "DynaMOSA");

        System.out.println("coverage is:" + coverage);
        assert coverage > 0.1;

    }

    @Test
    public void testSet5Method1() {
        Class<?> clazz = Target5.class;
        String methodName = "checkLength";
        int parameterNum = 1;

        String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
        Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

        String targetMethod = method.getName() + MethodUtil.getSignature(method);
        String cp = "target/test-classes" + File.pathSeparator + "target/classes";

        // Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
        Properties.CLIENT_ON_THREAD = true;
        Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

        Properties.TIMEOUT = 10;
        Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10;
//		Properties.TIMELINE_INTERVAL = 3000;

        String fitnessApproach = "branch";

        int timeBudget = 10;

        boolean aor = true;
        double coverage = TestUtility.evoTestSingleMethod(targetClass,
                targetMethod, timeBudget, true, aor, cp, fitnessApproach,
                "generateMOSuite", "MOSUITE", "DynaMOSA");

        System.out.println("coverage is:" + coverage);
        assert coverage > 0.1;

    }


    @Test
    public void testSet5Method1NoObjectRule() {
        Class<?> clazz = Target5.class;
        String methodName = "checkLength";
        int parameterNum = 1;

        String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
        Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

        String targetMethod = method.getName() + MethodUtil.getSignature(method);
        String cp = "target/test-classes" + File.pathSeparator + "target/classes";

        // Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
        Properties.CLIENT_ON_THREAD = true;
        Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

        Properties.TIMEOUT = 10;
        Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10;
//		Properties.TIMELINE_INTERVAL = 3000;

        String fitnessApproach = "branch";

        int timeBudget = 10;

        boolean aor = false;
        double coverage = TestUtility.evoTestSingleMethod(targetClass,
                targetMethod, timeBudget, true, aor, cp, fitnessApproach,
                "generateMOSuite", "MOSUITE", "DynaMOSA");

        System.out.println("coverage is:" + coverage);
        assert coverage > 0.1;

    }

    @Test
    public void testSet5Method2() {
        Class<?> clazz = Target5.class;
        String methodName = "checkValueAtIndex5";
        int parameterNum = 1;

        String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
        Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

        String targetMethod = method.getName() + MethodUtil.getSignature(method);
        String cp = "target/test-classes" + File.pathSeparator + "target/classes";

        // Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
        Properties.CLIENT_ON_THREAD = true;
        Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

        Properties.TIMEOUT = 10;
        Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10;
//		Properties.TIMELINE_INTERVAL = 3000;

        String fitnessApproach = "branch";

        int timeBudget = 10;

        boolean aor = true;
        double coverage = TestUtility.evoTestSingleMethod(targetClass,
                targetMethod, timeBudget, true, aor, cp, fitnessApproach,
                "generateMOSuite", "MOSUITE", "DynaMOSA");

        System.out.println("coverage is:" + coverage);
        assert coverage > 0.1;

    }


    @Test
    public void testSet5Method2NoObjectRule() {
        Class<?> clazz = Target5.class;
        String methodName = "checkValueAtIndex5";
        int parameterNum = 1;

        String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
        Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

        String targetMethod = method.getName() + MethodUtil.getSignature(method);
        String cp = "target/test-classes" + File.pathSeparator + "target/classes";

        // Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
        Properties.CLIENT_ON_THREAD = true;
        Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

        Properties.TIMEOUT = 10;
        Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10;
//		Properties.TIMELINE_INTERVAL = 3000;

        String fitnessApproach = "branch";

        int timeBudget = 10;

        boolean aor = false;
        double coverage = TestUtility.evoTestSingleMethod(targetClass,
                targetMethod, timeBudget, true, aor, cp, fitnessApproach,
                "generateMOSuite", "MOSUITE", "DynaMOSA");

        System.out.println("coverage is:" + coverage);
        assert coverage > 0.1;

    }


    @Test
    public void testSet6Method1() {
        Class<?> clazz = ListClass.class;
        String methodName = "checkLength";
        int parameterNum = 0;

        String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
        Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

        String targetMethod = method.getName() + MethodUtil.getSignature(method);
        String cp = "target/test-classes" + File.pathSeparator + "target/classes";

        // Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
        Properties.CLIENT_ON_THREAD = true;
        Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

        Properties.TIMEOUT = 10;
        Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10;
//		Properties.TIMELINE_INTERVAL = 3000;

        String fitnessApproach = "branch";

        int timeBudget = 10;

        boolean aor = true;
        double coverage = TestUtility.evoTestSingleMethod(targetClass,
                targetMethod, timeBudget, true, aor, cp, fitnessApproach,
                "generateMOSuite", "MOSUITE", "DynaMOSA");

        System.out.println("coverage is:" + coverage);
        assert coverage > 0.1;

    }


    @Test
    public void testSet6Method1NoObjectRule() {
        Class<?> clazz = ListClass.class;
        String methodName = "checkLength";
        int parameterNum = 1;

        String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
        Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

        String targetMethod = method.getName() + MethodUtil.getSignature(method);
        String cp = "target/test-classes" + File.pathSeparator + "target/classes";

        // Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
        Properties.CLIENT_ON_THREAD = true;
        Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

        Properties.TIMEOUT = 10;
        Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10;
//		Properties.TIMELINE_INTERVAL = 3000;

        String fitnessApproach = "branch";

        int timeBudget = 10;

        boolean aor = false;
        double coverage = TestUtility.evoTestSingleMethod(targetClass,
                targetMethod, timeBudget, true, aor, cp, fitnessApproach,
                "generateMOSuite", "MOSUITE", "DynaMOSA");

        System.out.println("coverage is:" + coverage);
        assert coverage > 0.1;

    }

    @Test
    public void testSet6Method2() {
        Class<?> clazz = Target6.class;
        String methodName = "checkValueAtIndex4";
        int parameterNum = 1;

        String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
        Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

        String targetMethod = method.getName() + MethodUtil.getSignature(method);
        String cp = "target/test-classes" + File.pathSeparator + "target/classes";

        // Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
        Properties.CLIENT_ON_THREAD = true;
        Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

        Properties.TIMEOUT = 10;
        Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10;
//		Properties.TIMELINE_INTERVAL = 3000;

        String fitnessApproach = "branch";

        int timeBudget = 10;

        boolean aor = true;
        double coverage = TestUtility.evoTestSingleMethod(targetClass,
                targetMethod, timeBudget, true, aor, cp, fitnessApproach,
                "generateMOSuite", "MOSUITE", "DynaMOSA");

        System.out.println("coverage is:" + coverage);
        assert coverage > 0.1;

    }


    @Test
    public void testSet6Method2NoObjectRule() {
        Class<?> clazz = Target6.class;
        String methodName = "checkValueAtIndex4";
        int parameterNum = 1;

        String targetClass = clazz.getCanonicalName();
//		Method method = clazz.getMethods()[0];
        Method method = TestUtility.getTargetMethod(methodName, clazz, parameterNum);

        String targetMethod = method.getName() + MethodUtil.getSignature(method);
        String cp = "target/test-classes" + File.pathSeparator + "target/classes";

        // Properties.LOCAL_SEARCH_RATE = 1;
//		Properties.DEBUG = true;
//		Properties.PORT = 8000;
        Properties.CLIENT_ON_THREAD = true;
        Properties.STATISTICS_BACKEND = StatisticsBackend.DEBUG;

        Properties.TIMEOUT = 10;
        Properties.INDIVIDUAL_LEGITIMIZATION_BUDGET = 10;
//		Properties.TIMELINE_INTERVAL = 3000;

        String fitnessApproach = "branch";

        int timeBudget = 10;

        boolean aor = false;
        double coverage = TestUtility.evoTestSingleMethod(targetClass,
                targetMethod, timeBudget, true, aor, cp, fitnessApproach,
                "generateMOSuite", "MOSUITE", "DynaMOSA");

        System.out.println("coverage is:" + coverage);
        assert coverage > 0.1;

    }

}
