/**
 * Copyright (C) 2010-2017 Gordon Fraser, Andrea Arcuri and EvoSuite
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
package org.evosuite.coverage.fbranch;

import java.lang.reflect.Method;

import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.Properties.Criterion;
import org.evosuite.coverage.branch.BranchCoverageSuiteFitness;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.strategy.TestGenerationStrategy;
import org.evosuite.symbolic.TestCaseBuilder;
import org.evosuite.testcase.DefaultTestCase;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.evosuite.SystemTestBase;
import org.evosuite.TestGenerationContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.examples.with.different.packagename.coverage.IndirectlyCoverableBranches;

public class FBranchSystemTest extends SystemTestBase {
	
    private static final Criterion[] defaultCriterion = Properties.CRITERION;
    
    private static boolean defaultArchive = Properties.TEST_ARCHIVE;

	@After
	public void resetProperties() {
		Properties.CRITERION = defaultCriterion;
		Properties.TEST_ARCHIVE = defaultArchive;
	}

	@Before
	public void beforeTest() {
        Properties.CRITERION[0] = Criterion.CBRANCH;
	}

	@Test
	public void testCBranchFitnessWithArchive() {
		Properties.TEST_ARCHIVE = true;
		testBranchFitness();
	}

	@Test
	public void testCBranchFitnessWithoutArchive() {
		Properties.TEST_ARCHIVE = false;
		Properties.SEARCH_BUDGET = 50000;
		testBranchFitness();
	}

	public void testBranchFitness() {
//		Properties.CRITERION = new Properties.Criterion[] { Criterion.FBRANCH };
//		Properties.TARGET_CLASS = com.examples.with.different.packagename.coverage.CopiedFastMath.class.getName();
//		Properties.TARGET_METHOD = "pow(DD)D";
//		Properties.TIMEOUT = 1000000;
//		
//		TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(Properties.TARGET_CLASS);
//		
//		TestSuiteChromosome suite = new TestSuiteChromosome();
////		suite.addFitness(branchCoverageSuiteFitness);
//		
//		DefaultTestCase testCase0 = buildTestCase0();
//		TestChromosome testChromosome0 = new TestChromosome();
//		testChromosome0.setTestCase(testCase0);
//		suite.addTest(testChromosome0);
//		
//		BranchCoverageSuiteFitness branchCoverageSuiteFitness = new BranchCoverageSuiteFitness();
//		double fitness1 = branchCoverageSuiteFitness.getFitness(suite);
//		
//		FBranchSuiteFitness fBranchSuiteFitness = new FBranchSuiteFitness();
//		double fitness2 = fBranchSuiteFitness.getFitness(suite);
//		
//		assert(fitness1 == fitness2);
	}

	private DefaultTestCase buildTestCase0() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		TestCaseBuilder builder = new TestCaseBuilder();
		VariableReference x = builder.appendDoublePrimitive(0);
		VariableReference y = builder.appendDoublePrimitive(1);
		Class<?> triangleClass = TestGenerationContext.getInstance().getClassLoaderForSUT()
				.loadClass(Properties.TARGET_CLASS);

		Method barMethod = triangleClass.getMethod("pow", double.class, double.class);
		builder.appendMethod(null, barMethod, x, y);
		return builder.getDefaultTestCase();
	}
}
