package org.evosuite.ga.metaheuristics.mosa;

import javafx.util.Pair;
import org.evosuite.Properties;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.comparators.NonduplicationComparator;
import org.evosuite.ga.metaheuristics.mosa.structural.MultiCriteriaManager;
import org.evosuite.ga.operators.ranking.CrowdingDistance;
import org.evosuite.lm.OpenAiLanguageModel;
import org.evosuite.seeding.smart.SensitivityMutator;
import org.evosuite.testcase.MutationPositionDiscriminator;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testcase.execution.TestCaseExecutor;
import org.evosuite.testcase.factories.RandomLengthTestFactory;
import org.evosuite.testcase.parser.Parser;
import org.evosuite.testcase.parser.ParserUtil;
import org.evosuite.testcase.synthesizer.TestCaseLegitimizer;
import org.evosuite.utils.LoggingUtils;
import org.evosuite.utils.Randomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class CodaMOSA<T extends Chromosome> extends AbstractMOSA<T> {

    private static final long serialVersionUID = -1267042940991321480L;

    private static final Logger logger = LoggerFactory.getLogger(CodaMOSA.class);

    public static long endTime;

    /** Manager to determine the test goals to consider at each generation */
    protected MultiCriteriaManager<T> goalsManager = null;

    protected CrowdingDistance<T> distance = new CrowdingDistance<>();

    protected ExceptionBranchEnhancer<T> exceptionBranchEnhancer = new ExceptionBranchEnhancer<>(goalsManager);

    private Parser.ParseResult targetSummary;

    private Map<FitnessFunction<T>, String> nlBranchesMap;

    /**
     * Constructor based on the abstract class {@link AbstractMOSA}.
     *
     * @param factory a {@link org.evosuite.ga.ChromosomeFactory} object
     */
    public CodaMOSA(ChromosomeFactory<T> factory) {
        super(factory);
        this.targetSummary = new Parser.ParseResult();
        this.nlBranchesMap = new LinkedHashMap<>();
    }

    /** {@inheritDoc} */
    @Override
    public void initializePopulation() {
        logger.info("executing initializePopulation function");

        this.notifySearchStarted();
        this.currentIteration = 0;

        String targetClassDef = ParserUtil.getClassDefinition(Properties.CP, Properties.TARGET_CLASS);
        Pair<String, String[]> targetMethodSignature = ParserUtil.getMethodSimpleSignature(Properties.TARGET_METHOD);
        String targetMethodName = targetMethodSignature.getKey();
        String[] targetMethodParaTypes = targetMethodSignature.getValue();

        Parser parser = new Parser(targetClassDef, targetMethodName, targetMethodParaTypes);
        this.targetSummary = parser.getSummary();
        this.initializeNLBranches(parser.getLineBranchMap(targetMethodName, targetMethodParaTypes));

        String targetClass = ParserUtil.getClassSimpleName(Properties.TARGET_CLASS);
        String targetMethodStr = ParserUtil.getMethodSimpleSignatureStr(Properties.TARGET_METHOD);
        String targetSummaryStr = this.targetSummary.toString();

        String populationStr = ""; // new OpenAiLanguageModel().getInitialPopulation(targetMethodStr, targetSummaryStr);
        // TODO: testing, to be removed
        try {
            populationStr = new String(Files.readAllBytes(Paths.get(
                    //"/home/nbvannhi/repo/evosuite-plus-plus/client/src/test/data/populationStr.txt"
                    "/home/nbvannhi/repo/evosuite-plus-plus/client/src/test/data/targetClass.txt"
            )));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        parser = new Parser(populationStr);
        parser.handleSetUpAndTearDown();
        parser.parse(10);

        System.out.println("LLM TEST:");
        for (TestCase testCase : parser.getTestCases()) {
            System.out.println(testCase.toCode());
            // Execute new test case
            ExecutionResult exeRes = TestCaseExecutor.runTest(testCase);
            TestChromosome newTest = new TestChromosome();
            newTest.setTestCase(testCase);
            newTest.setLastExecutionResult(exeRes);
            this.population.add((T) newTest);
        }

        // Determine fitness
        this.calculateFitness(true);
        this.notifyIteration();
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    protected void evolve() {
        List<T> offStringPopulation = this.breedNextGeneration();

        // Create the union of parents and offSpring
        List<T> union = new ArrayList<>(this.population.size() + offStringPopulation.size());
        union.addAll(this.population);
        union.addAll(offStringPopulation);

        logger.debug("Union Size = {}", union.size());

        // Rank the union using the best rank algorithm
        // (modified version of the non-dominated sorting algorithm)

        FitnessFunction<T> newCoveredGoal = null;
        int count = 0;
        for (FitnessFunction<T> goal : this.goalsManager.getCoveredGoals()) {
            if (count == this.goalsManager.getCoveredGoals().size() - 1) {
                newCoveredGoal = goal;
            }
            count++;
        }

        Set<FitnessFunction<T>> caredSet = new HashSet<>();
        if (newCoveredGoal == null) {
            caredSet = this.goalsManager.getCurrentGoals();
        } else {
            caredSet.add(newCoveredGoal);
        }

        // Rank with the previous covered goal

        this.rankingFunction.computeRankingAssignment(union, caredSet);

        // Form the next population using
        // "preference sorting and non-dominated sorting"
        // on the updated set of goals
        int remain = Math.max(Properties.POPULATION, this.rankingFunction.getSubfront(0).size());
        int index = 0;
        List<T> front = null;
        this.population.clear();

        // Obtain the next front
        front = this.rankingFunction.getSubfront(index);
        this.population.addAll(front);
        remain = remain - front.size();

        // Re-rank with the current goal

        this.rankingFunction.computeRankingAssignment(union, this.goalsManager.getCurrentGoals());

        while ((remain > 0) && (remain >= front.size()) && !front.isEmpty()) {
            // Assign crowding distance to individuals
            this.distance.fastEpsilonDominanceAssignment(front, this.goalsManager.getCurrentGoals());

            // Obtain the next front
            front = this.rankingFunction.getSubfront(index);

            // Add the individuals of this front
            this.population.addAll(front);

            // Decrement remain
            remain = remain - front.size();
            index++;
            front = this.rankingFunction.getSubfront(index);
        }

        // Increase only the best one when remain < front(index).size()
        if (remain > 0 && !front.isEmpty()) { // front contains individuals to insert
            this.distance.fastEpsilonDominanceAssignment(front, this.goalsManager.getCurrentGoals());
            front.sort(new NonduplicationComparator<>(this.population));
            for (int k = 0; k < remain; k++) {
                this.population.add(front.get(k));
            }
        }

        // Add new randomly generate tests
        for (int i = 0; i < remain; i++) {
            T tch = null;
            if (this.getCoveredGoals().size() == 0 || Randomness.nextBoolean()) {
                tch = this.chromosomeFactory.getChromosome();
                tch.setChanged(true);
            } else {
                tch = (T) Randomness.choice(this.getSolutions()).clone();
                tch.mutate();
                tch.mutate();
            }
            if (tch.isChanged()) {
                tch.updateAge(this.currentIteration);
                this.calculateFitness(tch);
                this.population.add(tch);
            }
        }

        // Get 50 populations based on ranking
        MutationPositionDiscriminator.discriminator.decreaseFrozenIteration();

        checkBestFitness();

        this.currentIteration++;
        logger.debug("Covered goals = {}", goalsManager.getCoveredGoals().size());
        logger.debug("Current goals = {}", goalsManager.getCurrentGoals().size());
        logger.debug("Uncovered goals = {}", goalsManager.getUncoveredGoals().size());

        System.out.println("total time: " + SensitivityMutator.total);
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public void generateSolution() {
        RandomLengthTestFactory.legitimizationSuccess = 0;
        RandomLengthTestFactory.legitimizationTrials = 0;

        logger.debug("Executing generateSolution function");

        this.goalsManager = new MultiCriteriaManager<>(this.fitnessFunctions);
        MutationPositionDiscriminator.discriminator.setPurpose(this.goalsManager.getCurrentGoals());

        this.goalsManager.getCoveredGoals().clear();
        LoggingUtils.getEvoLogger().info("* Initial number of goals in CodaMOSA = "
                + this.goalsManager.getCurrentGoals().size());

        logger.debug("Initial number of goals = " + this.goalsManager.getCurrentGoals().size());

        // Initialize population
        boolean turnOffSmart = false;
        if (Properties.APPLY_SMART_SEED) {
            Properties.APPLY_SMART_SEED = false;
            turnOffSmart = true;
        }

        long t1 = System.currentTimeMillis();
        TestCaseLegitimizer.startTime = t1;
        if (this.population.isEmpty()) {
            this.initializePopulation();
        }
        long t2 = System.currentTimeMillis();
        initializationOverhead = t2 - t1;

        if (turnOffSmart) {
            Properties.APPLY_SMART_SEED = true;
        }
        this.calculateFitness(true);
        T suite = getBestIndividual();
        this.initialCoverage = suite.getCoverage();
        logger.warn("initial coverage: " + this.initialCoverage);
        logger.warn("initialization overhead: " + initializationOverhead);

        // Calculate dominance ranks and crowding distance
        this.rankingFunction.computeRankingAssignment(this.population, this.goalsManager.getCurrentGoals());

        for (int i = 0; i < this.rankingFunction.getNumberOfSubfronts(); i++) {
            this.distance.fastEpsilonDominanceAssignment(
                    this.rankingFunction.getSubfront(i),
                    this.goalsManager.getCurrentGoals());
        }

        int maxStallLen = 5;
        int stallLen = 0;
        boolean wasTargeted = false;

        // Get next generations
        TestCaseLegitimizer.startTime = System.currentTimeMillis();
        while (!isFinished() && this.goalsManager.getUncoveredGoals().size() > 0) {
            MutationPositionDiscriminator.discriminator.setPurpose(this.goalsManager.getCurrentGoals());

            if (Properties.ENABLE_BRANCH_ENHANCEMENT) {
                exceptionBranchEnhancer.setGoalManager(goalsManager);
                exceptionBranchEnhancer.updatePopulation(population);
                exceptionBranchEnhancer.enhanceBranchGoals();
            }

            Set<FitnessFunction<T>> goalsBefore = this.goalsManager.getUncoveredGoals();
            List<T> populationBefore = this.population;

            if (stallLen > maxStallLen) {
                System.out.println("POPULATION WHEN STALL DETECTED:");
                System.out.println("=============================================");
                for (int i = 0; i < this.population.size(); i++) {
                    System.out.println("TEST " + i + ":");
                    System.out.println(this.population.get(i).toString());
                }
                System.out.println("=============================================");

                wasTargeted = true;
                t1 = System.currentTimeMillis();
                System.out.println("COVERAGE STALL DETECTED");

                try {
                    List<String> uncoveredBranches = this.getUncoveredBranches();
                    String newBranch = uncoveredBranches.get(1);

                    String targetClassName = ParserUtil.getClassSimpleName(Properties.TARGET_CLASS);
                    String newClassName = ParserUtil.getClassNameFromList(targetSummary.getImports(), "SearchUtility");
                    String targetClassDefinition = ParserUtil.getClassDefinition(Properties.CP, Properties.TARGET_CLASS);
                    String newClassDefinition = ParserUtil.getClassDefinition(Properties.CP, newClassName);

                    String populationStr = "";
                    populationStr = new OpenAiLanguageModel().coverNewBranch(
                            populationStr, newBranch,
                            targetClassName, targetClassDefinition,
                            newClassName, newClassDefinition);
                    // TODO: testing, to be removed
                    try {
                        populationStr = new String(Files.readAllBytes(Paths.get(
                                // "/home/nbvannhi/repo/evosuite-plus-plus/client/src/test/data/populationStr.txt"
                                "/home/nbvannhi/repo/evosuite-plus-plus/client/src/test/data/populationInit.txt"
                        )));
                    } catch (IOException e) {
                        logger.error(e.getMessage());
                    }

                    Parser parser = new Parser(populationStr);
                    parser.handleSetUpAndTearDown();
                    parser.parse(10);

                    System.out.println("LLM TEST:");
                    for (TestCase testCase : parser.getTestCases()) {
                        System.out.println(testCase.toCode());
                        // Execute new test case
                        ExecutionResult exeRes = TestCaseExecutor.runTest(testCase);
                        TestChromosome newTest = new TestChromosome();
                        newTest.setTestCase(testCase);
                        newTest.setLastExecutionResult(exeRes);
                        this.population.add((T) newTest);
                    }

                } catch (RuntimeException e) {
                    logger.error(e.getMessage());
                }

                this.evolve();
                t2 = System.currentTimeMillis();
                this.notifyIteration();
            } else {
                wasTargeted = false;
                t1 = System.currentTimeMillis();
                this.evolve();
                t2 = System.currentTimeMillis();
                this.notifyIteration();
            }

            if (wasTargeted && comparePopulation(populationBefore, this.population)) {
                maxStallLen *= 2;
            }

            Set<FitnessFunction<T>> goalsAfter = this.goalsManager.getUncoveredGoals();
            if (goalsAfter.equals(goalsBefore)) {
                 stallLen += 1;
                System.out.println("NO NEW GOAL COVERED");
            } else {
                stallLen = 0;
            }
        }

        logger.warn("legitimizationSuccess: " + RandomLengthTestFactory.legitimizationSuccess);
        logger.warn("legitimizationTrials: " + RandomLengthTestFactory.legitimizationTrials);

        this.notifySearchFinished();
    }

    /** {@inheritDoc} */
    @Override
    protected void calculateFitness(T c) {
        String oldGoalFingerPrint = this.goalsManager.getCurrentGoalFingerPrint();

        this.goalsManager.calculateFitness(c);
        this.notifyEvaluation(c);

        String newGoalFingerprint = this.goalsManager.getCurrentGoalFingerPrint();

        if (!oldGoalFingerPrint.equals(newGoalFingerprint)) {
            MutationPositionDiscriminator.discriminator.resetFrozenIteartion();
        }
    }

    /**
     * Checks if the population i

    public static String getClassDeclaration(String classDefinition) {
        String className = ParserUtil.getClassSimpleName(Properties.TARGET_CLASS);
        StringBuilder classDeclaration = new StringBuilder();
        String[] lines = classDefinition.split("\\r?\\n");
        for (String l : lines) {
            classDeclaration.append(l).append("\n");
            if (l.contains(className)) {
                break;
            }
        }
        return classDeclaration.toString();
    }s the same as before.
     *
     * @param populationBefore list of before test cases
     * @return true if the population is the same, false otherwise
     */
    private boolean comparePopulation(List<T> populationBefore, List<T> populationAfter) {
        int size;
        List<T> lPopulation, sPopulation;
        if (populationBefore.size() > populationAfter.size()) {
            size = populationBefore.size();
            lPopulation = populationBefore;
            sPopulation = populationAfter;
        } else {
            size = populationAfter.size();
            lPopulation = populationAfter;
            sPopulation = populationBefore;
        }
        for (int i = 0; i < size; i++) {
            if (!sPopulation.contains(lPopulation.get(i))) {
                return false;
            }
        }
        return true;
    }

    private void initializeNLBranches(Map<Integer, String> lineBranchMap) {
        Map<Integer, FitnessFunction<T>> branchCoverageTrueMap = goalsManager.getBranchCoverageTrueMap();
        Map<Integer, FitnessFunction<T>> branchCoverageFalseMap = goalsManager.getBranchCoverageFalseMap();
        assert branchCoverageTrueMap.keySet().equals(branchCoverageFalseMap.keySet()); // is this true?

        List<FitnessFunction<T>> trueBranches = new ArrayList<>(branchCoverageTrueMap.values());
        List<FitnessFunction<T>> falseBranches = new ArrayList<>(branchCoverageFalseMap.values());
        List<String> nlBranches = new ArrayList<>(lineBranchMap.values());
        assert trueBranches.size() == nlBranches.size() &&
                falseBranches.size() == nlBranches.size();

        Iterator<FitnessFunction<T>> trueIt = trueBranches.iterator();
        Iterator<FitnessFunction<T>> falseIt = falseBranches.iterator();
        Iterator<String> nlIt = nlBranches.iterator();
        while (trueIt.hasNext() && falseIt.hasNext() && nlIt.hasNext()) {
            String branch = nlIt.next();
            nlBranchesMap.put(trueIt.next(), branch);
            nlBranchesMap.put(falseIt.next(), branch);
        }
    }

    private List<String> getUncoveredBranches() {
        List<String> uncoveredBranches = new ArrayList<>();
        this.getUncoveredGoals().forEach(g -> uncoveredBranches.add(nlBranchesMap.get(g)));
        return uncoveredBranches.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
