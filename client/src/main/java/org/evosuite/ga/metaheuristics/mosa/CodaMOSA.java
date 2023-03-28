package org.evosuite.ga.metaheuristics.mosa;

import org.evosuite.Properties;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.ga.FitnessFunction;
import org.evosuite.ga.comparators.NonduplicationComparator;
import org.evosuite.ga.metaheuristics.mosa.structural.MultiCriteriaManager;
import org.evosuite.ga.operators.ranking.CrowdingDistance;
import org.evosuite.seeding.smart.SensitivityMutator;
import org.evosuite.testcase.MutationPositionDiscriminator;
import org.evosuite.testcase.factories.RandomLengthTestFactory;
import org.evosuite.testcase.synthesizer.TestCaseLegitimizer;
import org.evosuite.utils.LoggingUtils;
import org.evosuite.utils.Randomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CodaMOSA<T extends Chromosome> extends AbstractMOSA<T> {

    private static final long serialVersionUID = -1267042940991321480L;

    private static final Logger logger = LoggerFactory.getLogger(CodaMOSA.class);

    public static long endTime;

    /** Manager to determine the test goals to consider at each generation */
    protected MultiCriteriaManager<T> goalsManager = null;

    protected CrowdingDistance<T> distance = new CrowdingDistance<>();

    protected ExceptionBranchEnhancer<T> exceptionBranchEnhancer = new ExceptionBranchEnhancer<>(goalsManager);

    /**
     * Constructor based on the abstract class {@link AbstractMOSA}.
     *
     * @param factory
     */
    public CodaMOSA(ChromosomeFactory<T> factory) {
        super(factory);
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

        int maxStallLen = 50;
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

            if (stallLen > maxStallLen) {
                wasTargeted = true;
                // TODO(vani):
                // query Codex for new test case
            } else {
                wasTargeted = false;
                t1 = System.currentTimeMillis();
                this.evolve();
                t2 = System.currentTimeMillis();
                this.notifyIteration();
            }

            if (wasTargeted /* && add check if the test suite is the same */) {
                maxStallLen *= 2;
            }

            Set<FitnessFunction<T>> goalsAfter = this.goalsManager.getUncoveredGoals();
            if (goalsAfter.equals(goalsBefore)) {
                stallLen += 1;
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
}
