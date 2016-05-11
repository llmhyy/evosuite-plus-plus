/**
 * Copyright (C) 2010-2016 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * This file is part of EvoSuite.
 *
 * EvoSuite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * EvoSuite is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EvoSuite. If not, see <http://www.gnu.org/licenses/>.
 */
package org.evosuite.junit.writer;

import org.apache.commons.lang3.StringEscapeUtils;
import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.instrumentation.BytecodeInstrumentation;
import org.evosuite.runtime.GuiSupport;
import org.evosuite.runtime.InitializingListener;
import org.evosuite.runtime.LoopCounter;
import org.evosuite.runtime.RuntimeSettings;
import org.evosuite.runtime.agent.InstrumentingAgent;
import org.evosuite.runtime.annotation.EvoSuiteClassExclude;
import org.evosuite.runtime.classhandling.ClassResetter;
import org.evosuite.runtime.classhandling.ClassStateSupport;
import org.evosuite.runtime.classhandling.JDKClassResetter;
import org.evosuite.runtime.classhandling.ResetManager;
import org.evosuite.runtime.javaee.db.DBManager;
import org.evosuite.runtime.jvm.ShutdownHookHandler;
import org.evosuite.runtime.sandbox.Sandbox;
import org.evosuite.runtime.thread.KillSwitchHandler;
import org.evosuite.runtime.thread.ThreadStopper;
import org.evosuite.runtime.util.SystemInUtil;
import org.evosuite.runtime.vnet.NonFunctionalRequirementRule;
import org.evosuite.testcase.execution.ExecutionResult;
import org.junit.rules.Timeout;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.evosuite.junit.writer.TestSuiteWriterUtils.*;

/**
 * Class used to generate all the scaffolding code that ends up in methods like @After/@Before
 * and that are used to setup the EvoSuite framework (eg mocking of classes, reset of static
 * state)
 *
 * @author arcuri
 */
public class Scaffolding {

    public static final String EXECUTOR_SERVICE = "executor";

    private static final String DEFAULT_PROPERTIES = "defaultProperties";

    private static final String THREAD_STOPPER = "threadStopper";

    /**
     * Return full JUnit code for scaffolding file for the give test
     *
     * @param testName
     * @return
     */
    public static String getScaffoldingFileContent(String testName, List<ExecutionResult> results, boolean wasSecurityException) {

        String name = getFileName(testName);

        StringBuilder builder = new StringBuilder();

        builder.append(getHeader(name, results, wasSecurityException));
        builder.append(new Scaffolding().getBeforeAndAfterMethods(name, wasSecurityException, results));
        builder.append(getFooter());

        return builder.toString();
    }

    protected static String getFooter() {
        return "}\n";
    }

    protected static String getHeader(String name, List<ExecutionResult> results, boolean wasSecurityException) {
        StringBuilder builder = new StringBuilder();
        builder.append("/**\n");
        builder.append(" * Scaffolding file used to store all the setups needed to run \n");
        builder.append(" * tests automatically generated by EvoSuite\n");
        builder.append(" * " + new Date() + "\n");
        builder.append(" */\n\n");

        if (!Properties.CLASS_PREFIX.equals("")) {
            builder.append("package ");
            builder.append(Properties.CLASS_PREFIX);
            builder.append(";\n");
        }
        builder.append("\n");

        for (String imp : getScaffoldingImports(wasSecurityException, results)) {
            builder.append("import ");
            builder.append(imp);
            builder.append(";\n");
        }
        builder.append("\n");

        builder.append("@EvoSuiteClassExclude\n");
        builder.append(TestSuiteWriterUtils.getAdapter().getClassDefinition(name));
        builder.append(" {\n");

        return builder.toString();
    }

    public static String getFileName(String testName) throws IllegalArgumentException {
        if (testName == null || testName.trim().isEmpty()) {
            throw new IllegalArgumentException("Empty test name");
        }
        return testName + "_" + Properties.SCAFFOLDING_SUFFIX;
    }

    /**
     * Return all classes for which we need an import statement
     *
     * @param wasSecurityException
     * @param results
     * @return
     */
    public static List<String> getScaffoldingImports(boolean wasSecurityException, List<ExecutionResult> results) {
        List<String> list = new ArrayList<String>();

        list.add(EvoSuiteClassExclude.class.getCanonicalName());
        
        if (TestSuiteWriterUtils.needToUseAgent() || wasSecurityException
                || SystemInUtil.getInstance().hasBeenUsed() || !Properties.NO_RUNTIME_DEPENDENCY) {
            list.add(org.junit.BeforeClass.class.getCanonicalName());
            list.add(org.junit.Before.class.getCanonicalName());
            list.add(org.junit.After.class.getCanonicalName());
        }

        if (wasSecurityException || TestSuiteWriterUtils.shouldResetProperties(results)) {
            list.add(org.junit.AfterClass.class.getCanonicalName());
        }


        if (Properties.RESET_STATIC_FIELDS || wasSecurityException) {
            /*
                for simplicity, when doing static reset, we always activate the sandbox,
                as anyway its code is only going to be in the scaffolding
             */
            list.add(Sandbox.class.getCanonicalName());
            list.add(Sandbox.SandboxMode.class.getCanonicalName());
        }

        if (wasSecurityException) {
            list.add(java.util.concurrent.ExecutorService.class.getCanonicalName());
            list.add(java.util.concurrent.Executors.class.getCanonicalName());
            list.add(java.util.concurrent.Future.class.getCanonicalName());
            list.add(java.util.concurrent.TimeUnit.class.getCanonicalName());
        }

        return list;
    }

    /**
     * Get the code of methods for @BeforeClass, @Before, @AfterClass and
     *
     * @return
     * @After. <p>
     * In those methods, the EvoSuite framework for running the
     * generated test cases is handled (e.g., use of customized
     * SecurityManager and runtime bytecode replacement)
     */
    public String getBeforeAndAfterMethods(String name, boolean wasSecurityException,
                                           List<ExecutionResult> results) {

		/*
         * Usually, we need support methods (ie @BeforeClass,@Before,@After and @AfterClass)
		 * only if there was a security exception (and so we need EvoSuite security manager,
		 * and test runs on separated thread) or if we are doing bytecode replacement (and
		 * so we need to activate JavaAgent).
		 *
		 * But there are cases that we might always want: eg, setup logging
		 */

        StringBuilder bd = new StringBuilder("");
        bd.append("\n");

		/*
         * Because this method is perhaps called only once per SUT,
		 * not much of the point to try to optimize it
		 */


        /*
           As of JUnit 4.12, Timeout Rule is broken, as it does not execute @After methods.
           TODO: put this back (and change @Test) once this issue is resolved, and new version of JUnit is released.
           Issue is reported at:

           https://github.com/junit-team/junit/issues/1231
         */
        //generateTimeoutRule(bd);

        generateNFRRule(bd);

        generateFields(bd, wasSecurityException, results);

        generateBeforeClass(bd, wasSecurityException);

        generateAfterClass(bd, wasSecurityException, results);

        generateBefore(bd, wasSecurityException, results);

        generateAfter(bd, wasSecurityException);

        generateSetSystemProperties(bd, results);

        generateInitializeClasses(name, bd);

        if (Properties.RESET_STATIC_FIELDS) {
            generateResetClasses(name, bd);
        }

        return bd.toString();
    }


    private void generateNFRRule(StringBuilder bd){
        bd.append(METHOD_SPACE);
        bd.append("@org.junit.Rule \n");
        bd.append(METHOD_SPACE);
        bd.append("public "+NonFunctionalRequirementRule.class.getName()+" nfr = new "+
                NonFunctionalRequirementRule.class.getName()+ "();\n\n");
    }

    /**
     * Hanging tests have very, very high negative impact.
     * They can mess up everything (eg when running "mvn test").
     * As such, we should always have timeouts.
     * Adding timeouts only in certain conditions is too risky
     *
     * @param bd
     */
    private void generateTimeoutRule(StringBuilder bd) {
        bd.append(METHOD_SPACE);
        bd.append("@org.junit.Rule \n");
        bd.append(METHOD_SPACE);
        int timeout = Properties.TIMEOUT + 1000;
        bd.append("public "+Timeout.class.getName()+" globalTimeout = new "+Timeout.class.getName()+ "(" + timeout);

        boolean useNew = false;
        try {
            //   FIXME: this check does not seem to work properly :(
            Class<?> timeoutOfSUTJunit = TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(Timeout.class.getName());
            Constructor c = timeoutOfSUTJunit.getDeclaredConstructor(Long.TYPE, TimeUnit.class);
            useNew = true;
        } catch (ClassNotFoundException e) {
            logger.error("Failed to load Timeout rule from SUT classloader: {}", e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            logger.warn("SUT is using an old version of JUnit");
            useNew = false;
        }

        if(useNew) {
            //TODO: put back once above check works
            //bd.append(", " + TimeUnit.class.getName() + ".MILLISECONDS");
        }
        bd.append("); \n");
        bd.append("\n");
    }

    private void generateResetClasses(String testClassName, StringBuilder bd) {
        List<String> classesToReset = ResetManager.getInstance().getClassResetOrder();

        bd.append("\n");
        bd.append(METHOD_SPACE);
        bd.append("private static void resetClasses() {\n");

        if (classesToReset.size() != 0) {

            bd.append(BLOCK_SPACE);
            bd.append(ClassResetter.class.getName() + ".getInstance().setClassLoader(");
            bd.append(testClassName + ".class.getClassLoader()); \n\n");

            bd.append(BLOCK_SPACE);
            bd.append(ClassStateSupport.class.getName() + ".resetClasses(");

            for (int i = 0; i < classesToReset.size(); i++) {
                String className = classesToReset.get(i);
                bd.append("\n" + INNER_BLOCK_SPACE + "\"" + className + "\"");
                if (i < classesToReset.size() - 1) {
                    bd.append(",");
                }
            }

            bd.append("\n");
            bd.append(BLOCK_SPACE);
            bd.append(");\n");
        }

        bd.append(METHOD_SPACE);
        bd.append("}" + "\n");
    }

    /**
     * Here we need to filter out all classes that cannot/should not be loaded, like for
     * example tmp tests generated by EvoSuite
     *
     * @return a new instantiated list
     */
    private List<String> getClassesToInit(List<String> allInstrumentedClasses) {

        List<String> classes = new ArrayList<>();

        for (String name : allInstrumentedClasses) {
            //check for generated tests
            if (name.contains(Properties.TARGET_CLASS) && (name.endsWith(Properties.JUNIT_SUFFIX) || name.endsWith(Properties.SCAFFOLDING_SUFFIX))) {
                continue;
            }

            classes.add(name);

        }

        return classes;
    }

    private void generateInitializeClasses(String testClassName, StringBuilder bd) {
        
        //if (Properties.NO_RUNTIME_DEPENDENCY) // Jose: this makes the test suite not compile
        //    return;							// when test_scaffolding=false and no_runtime_dependency=true 

        List<String> allInstrumentedClasses = TestGenerationContext.getInstance().getClassLoaderForSUT().getViewOfInstrumentedClasses();
        List<String> classesToInit = getClassesToInit(allInstrumentedClasses);


        bd.append("\n");
        bd.append(METHOD_SPACE);
        bd.append("private static void " + InitializingListener.INITIALIZE_CLASSES_METHOD + "() {\n");

        if (classesToInit.size() != 0) {
            bd.append(BLOCK_SPACE);
            bd.append(ClassStateSupport.class.getName() + ".initializeClasses(");
            bd.append(testClassName + ".class.getClassLoader() ");

            for (int i = 0; i < classesToInit.size(); i++) {
                String className = classesToInit.get(i);
                if (!BytecodeInstrumentation.checkIfCanInstrument(className)) {
                    continue;
                }
                bd.append(",\n" + INNER_BLOCK_SPACE + "\"" + className + "\"");
            }
            bd.append("\n");
            bd.append(BLOCK_SPACE);
            bd.append(");\n");
        }


		/* Not needed any longer, since the issue was fixed with a customized @RunWith
		 *
		bd.append("\n");

		List<String> allInstrumentedClasses = TestGenerationContext.getInstance().getClassLoaderForSUT().getViewOfInstrumentedClasses();

		//this have to be done AFTER the classes have been loaded in a specific order
		bd.append(BLOCK_SPACE);
		bd.append(ClassStateSupport.class.getName()+".retransformIfNeeded(");
		bd.append(testClassName+ ".class.getClassLoader()");

		for(int i=0; i<allInstrumentedClasses.size(); i++){
			String s = allInstrumentedClasses.get(i);
			bd.append(",\n");
			bd.append(INNER_BLOCK_SPACE);
			bd.append("\""+s+"\"");
		}
		bd.append("\n");
		bd.append(BLOCK_SPACE);
		bd.append(");\n");
		*/

        bd.append(METHOD_SPACE);
        bd.append("} \n");
    }

    private void generateAfter(StringBuilder bd, boolean wasSecurityException) {

        if (Properties.NO_RUNTIME_DEPENDENCY)
            return;

		/*
		 * Likely always at least ThreadStopper
		 *
		if (!Properties.RESET_STANDARD_STREAMS && !wasSecurityException
				&& !Properties.REPLACE_CALLS && !Properties.VIRTUAL_FS
				&& !Properties.RESET_STATIC_FIELDS) {
			return;
		}
		*/

        bd.append(METHOD_SPACE);
        bd.append("@After \n");
        bd.append(METHOD_SPACE);
        bd.append("public void doneWithTestCase(){ \n");

        bd.append(BLOCK_SPACE);
        bd.append(THREAD_STOPPER + ".killAndJoinClientThreads();\n");


        if (Properties.REPLACE_CALLS) {
            bd.append(BLOCK_SPACE);
            bd.append(ShutdownHookHandler.class.getName() + ".getInstance().safeExecuteAddedHooks(); \n");
        }


        if (Properties.RESET_STANDARD_STREAMS) {
            bd.append(BLOCK_SPACE);
            bd.append("java.lang.System.setErr(systemErr); \n");

            bd.append(BLOCK_SPACE);
            bd.append("java.lang.System.setOut(systemOut); \n");

            bd.append(BLOCK_SPACE);
            bd.append("DebugGraphics.setLogStream(logStream); \n");
        }

        if (Properties.RESET_STATIC_FIELDS) {
            bd.append(BLOCK_SPACE);
            bd.append(JDKClassResetter.class.getName() + ".reset(); \n");
            bd.append(BLOCK_SPACE);
            bd.append("resetClasses(); \n");
        }

        if (Properties.RESET_STATIC_FIELDS || wasSecurityException) {
            bd.append(BLOCK_SPACE);
            bd.append(Sandbox.class.getName() + ".doneWithExecutingSUTCode(); \n");
        }

        if (TestSuiteWriterUtils.needToUseAgent()) {
            bd.append(BLOCK_SPACE);
            bd.append(InstrumentingAgent.class.getName() + ".deactivate(); \n");
        }

        //TODO: see comment in @Before
        if (Properties.HEADLESS_MODE) {
            bd.append(BLOCK_SPACE);
            bd.append(org.evosuite.runtime.GuiSupport.class.getName() + ".restoreHeadlessMode(); \n");
        }


        bd.append(METHOD_SPACE);
        bd.append("} \n");

        bd.append("\n");
    }

    private void generateBefore(StringBuilder bd, boolean wasSecurityException,
                                List<ExecutionResult> results) {

        if (Properties.NO_RUNTIME_DEPENDENCY)
            return;

		/*
		 * Most likely, should always have at least ThreadStopper
		 *
		if (!Properties.RESET_STANDARD_STREAMS && !TestSuiteWriterUtils.shouldResetProperties(results)
				&& !wasSecurityException && !Properties.REPLACE_CALLS
				&& !Properties.VIRTUAL_FS && !Properties.RESET_STATIC_FIELDS
				&& !SystemInUtil.getInstance().hasBeenUsed()) {
			return;
		}
		 */

        bd.append(METHOD_SPACE);
        bd.append("@Before \n");
        bd.append(METHOD_SPACE);
        bd.append("public void initTestCase(){ \n");


        bd.append(BLOCK_SPACE);
        bd.append(THREAD_STOPPER + ".storeCurrentThreads();\n");
        bd.append(BLOCK_SPACE);
        bd.append(THREAD_STOPPER + ".startRecordingTime();\n");

        if (Properties.REPLACE_CALLS) {
            bd.append(BLOCK_SPACE);
            bd.append(ShutdownHookHandler.class.getName() + ".getInstance().initHandler(); \n");
        }

        if (Properties.RESET_STATIC_FIELDS || wasSecurityException) {
            bd.append(BLOCK_SPACE);
            bd.append(Sandbox.class.getName() + ".goingToExecuteSUTCode(); \n");
        }

        //FIXME those should be handled in the mocked classes,eg mock for java.lang.System
        if (Properties.RESET_STANDARD_STREAMS) {
            bd.append(BLOCK_SPACE);
            bd.append("systemErr = java.lang.System.err;");
            bd.append(" \n");

            bd.append(BLOCK_SPACE);
            bd.append("systemOut = java.lang.System.out;");
            bd.append(" \n");

            bd.append(BLOCK_SPACE);
            bd.append("logStream = DebugGraphics.logStream();");
            bd.append(" \n");
        }

        if (TestSuiteWriterUtils.shouldResetProperties(results)) {
            bd.append(BLOCK_SPACE);
            bd.append("setSystemProperties();");
            bd.append(" \n");
        }

		/*
		 * We do not mock GUI yet, but still we need to make the JUnit tests to
		 * run in headless mode. Checking if SUT needs headless is tricky: check
		 * for headless exception is brittle if those exceptions are caught before
		 * propagating to test.
		 *
		 * TODO: These things would be handled once we mock GUI. For the time being
		 * we just always include a reset call if @Before/@After methods are
		 * generated
		 */
        if (Properties.HEADLESS_MODE) {
            bd.append(BLOCK_SPACE);
            bd.append(org.evosuite.runtime.GuiSupport.class.getName() + ".setHeadless(); \n");
        }


        if (TestSuiteWriterUtils.needToUseAgent()) {
            bd.append(BLOCK_SPACE);
            bd.append(org.evosuite.runtime.Runtime.class.getName() + ".getInstance().resetRuntime(); \n");
            bd.append(BLOCK_SPACE);
            bd.append(InstrumentingAgent.class.getName() + ".activate(); \n");
        }

        if (SystemInUtil.getInstance().hasBeenUsed()) {
            bd.append(BLOCK_SPACE);
            bd.append(SystemInUtil.class.getName() + ".getInstance().initForTestCase(); \n");
        }

        bd.append(METHOD_SPACE);
        bd.append("} \n");

        bd.append("\n");
    }


    private String getResetPropertiesCommand() {
        return "java.lang.System.setProperties((java.util.Properties)" + " "
                + DEFAULT_PROPERTIES + ".clone());";
    }

    private void generateAfterClass(StringBuilder bd, boolean wasSecurityException,
                                    List<ExecutionResult> results) {

        if (wasSecurityException || TestSuiteWriterUtils.shouldResetProperties(results)) {
            bd.append(METHOD_SPACE);
            bd.append("@AfterClass \n");
            bd.append(METHOD_SPACE);
            bd.append("public static void clearEvoSuiteFramework(){ \n");

            if (Properties.RESET_STATIC_FIELDS || wasSecurityException) {
                bd.append(BLOCK_SPACE);
                bd.append("Sandbox.resetDefaultSecurityManager(); \n");
            }

            if(wasSecurityException){
                bd.append(BLOCK_SPACE);
                bd.append(EXECUTOR_SERVICE + ".shutdownNow(); \n");
            }

            if (TestSuiteWriterUtils.shouldResetProperties(results)) {
                bd.append(BLOCK_SPACE);
                bd.append(getResetPropertiesCommand());
                bd.append(" \n");
            }

            bd.append(METHOD_SPACE);
            bd.append("} \n");

            bd.append("\n");
        }

    }

    private void generateSetSystemProperties(StringBuilder bd,
                                             List<ExecutionResult> results) {

        if (!Properties.REPLACE_CALLS) {
            return;
        }

        bd.append(METHOD_SPACE);
        bd.append("public void setSystemProperties() {\n");
        bd.append(" \n");
        if (TestSuiteWriterUtils.shouldResetProperties(results)) {
			/*
			 * even if we set all the properties that were read, we still need
			 * to reset everything to handle the properties that were written
			 */
            bd.append(BLOCK_SPACE);
            bd.append(getResetPropertiesCommand());
            bd.append(" \n");

            Set<String> readProperties = TestSuiteWriterUtils.mergeProperties(results);
            for (String prop : readProperties) {
                String currentValue = java.lang.System.getProperty(prop);
                String escaped_prop = StringEscapeUtils.escapeJava(prop);
                if (currentValue != null) {
                    String escaped_currentValue = StringEscapeUtils.escapeJava(currentValue);
                    bd.append(BLOCK_SPACE);
                    bd.append("java.lang.System.setProperty(\"" + escaped_prop + "\", \""
                            + escaped_currentValue + "\"); \n");
                } else {
					/*
					 * In theory, we do not need to clear properties, as that is done with the reset to default.
					 * Avoiding doing the clear is not only good for readability (ie, less commands) but also
					 * to avoid crashes when properties are set based on SUT inputs. Eg, in classes like
					 *  SassToCssBuilder in 108_liferay we ended up with hundreds of thousands set properties...
					 */
                    //bd.append("java.lang.System.clearProperty(\"" + escaped_prop + "\"); \n");
                }
            }
        } else {
            bd.append(BLOCK_SPACE + "/*No java.lang.System property to set*/\n");
        }

        bd.append(METHOD_SPACE);
        bd.append("}\n");

    }

    private void generateBeforeClass(StringBuilder bd, boolean wasSecurityException) {

        if (!wasSecurityException && !TestSuiteWriterUtils.needToUseAgent()) {
            return;
        }

        bd.append(METHOD_SPACE);
        bd.append("@BeforeClass \n");

        bd.append(METHOD_SPACE);
        bd.append("public static void initEvoSuiteFramework() { \n");

        // FIXME: This is just commented out for experiments
        //bd.append("org.evosuite.utils.LoggingUtils.setLoggingForJUnit(); \n");

        bd.append(BLOCK_SPACE);
        bd.append("" + RuntimeSettings.class.getName() + ".className = \""+ Properties.TARGET_CLASS+"\"; \n");

        bd.append(BLOCK_SPACE);
        bd.append("" + GuiSupport.class.getName() + ".initialize(); \n");

        if(Properties.REPLACE_CALLS) {
            bd.append(BLOCK_SPACE);
            bd.append("" + RuntimeSettings.class.getName() + ".maxNumberOfThreads = "+ Properties.MAX_STARTED_THREADS+"; \n");
        }

        bd.append(BLOCK_SPACE);
        bd.append("" + RuntimeSettings.class.getName() + ".maxNumberOfIterationsPerLoop = " + Properties.MAX_LOOP_ITERATIONS+"; \n");

        if (Properties.REPLACE_SYSTEM_IN) {
            bd.append(BLOCK_SPACE);
            bd.append(RuntimeSettings.class.getName() + ".mockSystemIn = true; \n");
        }

        if (Properties.REPLACE_GUI) {
            bd.append(BLOCK_SPACE);
            bd.append(RuntimeSettings.class.getName() + ".mockGUI = true; \n");
        }
   

        if(Properties.RESET_STATIC_FIELDS || wasSecurityException){
            //need to setup the Sandbox mode
            bd.append(BLOCK_SPACE);
            bd.append(RuntimeSettings.class.getName() + ".sandboxMode = " +
                    Sandbox.SandboxMode.class.getCanonicalName() + "." + Properties.SANDBOX_MODE + "; \n");

            bd.append(BLOCK_SPACE);
            bd.append(Sandbox.class.getName() + ".initializeSecurityManagerForSUT(); \n");
        }

        if (wasSecurityException) {
            bd.append(BLOCK_SPACE);
            bd.append(EXECUTOR_SERVICE + " = Executors.newCachedThreadPool(); \n");
        }

        if (Properties.RESET_STATIC_FIELDS) {
            bd.append(BLOCK_SPACE);
            bd.append(JDKClassResetter.class.getName() + ".init(); \n");
            bd.append(BLOCK_SPACE);
            bd.append(InitializingListener.INITIALIZE_CLASSES_METHOD+"();" + "\n");
        }

        if (TestSuiteWriterUtils.needToUseAgent()) {
            bd.append(BLOCK_SPACE);
            bd.append(org.evosuite.runtime.Runtime.class.getName() + ".getInstance().resetRuntime(); \n");
        } else {
            //it is done inside Runtime, but, if that is not called, we need an explicit call here
            bd.append(BLOCK_SPACE);
            bd.append(LoopCounter.class.getName() + ".getInstance().reset(); \n");
        }
        if(DBManager.getInstance().isWasAccessed()) {
            //be sure it is called before any test is run, as to avoid timeout if init during a test case run
            bd.append(BLOCK_SPACE);
            bd.append(DBManager.class.getName() + ".getInstance().initDB(); \n");
        }


        bd.append(METHOD_SPACE);
        bd.append("} \n");

        bd.append("\n");
    }

    private void generateFields(StringBuilder bd, boolean wasSecurityException,
                                List<ExecutionResult> results) {

        if (Properties.RESET_STANDARD_STREAMS) {
            bd.append(METHOD_SPACE);
            bd.append("private PrintStream systemOut = null;" + '\n');

            bd.append(METHOD_SPACE);
            bd.append("private PrintStream systemErr = null;" + '\n');

            bd.append(METHOD_SPACE);
            bd.append("private PrintStream logStream = null;" + '\n');
        }

        if (wasSecurityException) {
            bd.append(METHOD_SPACE);
            bd.append("protected static ExecutorService " + EXECUTOR_SERVICE + "; \n");

            bd.append("\n");
        }

        if (TestSuiteWriterUtils.shouldResetProperties(results)) {
			/*
			 * some System properties were read/written. so, let's be sure we ll have the same
			 * properties in the generated JUnit file, regardless of where it will be executed
			 * (eg on a remote CI server). This is essential, as generated assertions might
			 * depend on those properties
			 */
            bd.append(METHOD_SPACE);
            bd.append("private static final java.util.Properties " + DEFAULT_PROPERTIES);
            bd.append(" = (java.util.Properties) java.lang.System.getProperties().clone(); \n");

            bd.append("\n");
        }

        bd.append(METHOD_SPACE);
        bd.append("private " + ThreadStopper.class.getName() + " " + THREAD_STOPPER + " = ");
        bd.append(" new " + ThreadStopper.class.getName() + " (");
        bd.append("" + KillSwitchHandler.class.getName() + ".getInstance(), ");
        bd.append("" + Properties.TIMEOUT + "");
        Set<String> threadsToIgnore = new LinkedHashSet<>();
        // this shouldn't appear among the threads in the generated tests
        //threadsToIgnore.add(TestCaseExecutor.TEST_EXECUTION_THREAD);
        threadsToIgnore.addAll(Arrays.asList(Properties.IGNORE_THREADS));
        for (String s : threadsToIgnore) {
            bd.append(", " + s);
        }
        bd.append(");\n\n");
    }
}
