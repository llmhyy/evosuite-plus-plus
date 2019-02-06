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
import java.util.Arrays;

import org.evosuite.EvoSuite;
import org.evosuite.Properties;
import org.evosuite.Properties.Criterion;
import org.evosuite.SystemTestBase;
import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.BranchCoverageSuiteFitness;
import org.evosuite.coverage.fbranch.FBranchSuiteFitness;
import org.evosuite.ga.metaheuristics.GeneticAlgorithm;
import org.evosuite.strategy.TestGenerationStrategy;
import org.evosuite.symbolic.TestCaseBuilder;
import org.evosuite.testcase.DefaultTestCase;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.variable.VariableReference;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.examples.with.different.packagename.ncs.Triangle;

public class FastMathBranchSystemTest extends SystemTestBase {

	private Properties.Criterion[] oldCriteria = Arrays.copyOf(Properties.CRITERION, Properties.CRITERION.length);
	private Properties.StoppingCondition oldStoppingCondition = Properties.STOPPING_CONDITION;
	private double oldPrimitivePool = Properties.PRIMITIVE_POOL;

	@Before
	public void beforeTest() {
		oldCriteria = Arrays.copyOf(Properties.CRITERION, Properties.CRITERION.length);
		oldStoppingCondition = Properties.STOPPING_CONDITION;
		oldPrimitivePool = Properties.PRIMITIVE_POOL;
		// Properties.MINIMIZE = false;
	}

	@After
	public void restoreProperties() {
		Properties.CRITERION = oldCriteria;
		Properties.STOPPING_CONDITION = oldStoppingCondition;
		Properties.PRIMITIVE_POOL = oldPrimitivePool;
	}

	@Test
	public void testBranchFitess() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		Properties.CRITERION = new Properties.Criterion[] { Criterion.BRANCH };
		Properties.TARGET_CLASS = com.examples.with.different.packagename.coverage.CopiedFastMath.class.getName();
		Properties.TARGET_METHOD = "pow(DD)D";
		Properties.TIMEOUT = 1000000;
		
		TestGenerationContext.getInstance().getClassLoaderForSUT().loadClass(Properties.TARGET_CLASS);
		
		TestSuiteChromosome suite = new TestSuiteChromosome();
//		suite.addFitness(branchCoverageSuiteFitness);
		
		suite.addTest(buildTestCase(0, 1));
		suite.addTest(buildTestCase(-1, 0));
		suite.addTest(buildTestCase(0, 0));
		suite.addTest(buildTestCase(-1.23, 2.3456));
		suite.addTest(buildTestCase(Double.POSITIVE_INFINITY, 2.3456));
		suite.addTest(buildTestCase(Double.NEGATIVE_INFINITY, 0));
		suite.addTest(buildTestCase(3, Double.NEGATIVE_INFINITY));
		
		BranchCoverageSuiteFitness branchCoverageSuiteFitness = new BranchCoverageSuiteFitness();
		double fitness1 = branchCoverageSuiteFitness.getFitness(suite);
		
		FBranchSuiteFitness fBranchSuiteFitness = new FBranchSuiteFitness();
		double fitness2 = fBranchSuiteFitness.getFitness(suite);
		
		assert(fitness1 == fitness2);
		
		System.currentTimeMillis();
	}

	private TestChromosome buildTestCase(double a, double b) throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		TestCaseBuilder builder = new TestCaseBuilder();
		VariableReference x = builder.appendDoublePrimitive(a);
		VariableReference y = builder.appendDoublePrimitive(b);
		Class<?> triangleClass = TestGenerationContext.getInstance().getClassLoaderForSUT()
				.loadClass(Properties.TARGET_CLASS);

		Method barMethod = triangleClass.getMethod("pow", double.class, double.class);
		builder.appendMethod(null, barMethod, x, y);
		DefaultTestCase tc = builder.getDefaultTestCase();
		
		TestChromosome testChromosome = new TestChromosome();
		testChromosome.setTestCase(tc);
		
		return testChromosome;
	}
	

}
