/**
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and EvoSuite
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
package org.evosuite.testcase.factories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.coverage.branch.Branch;
import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.graphs.dataflow.Dataflow;
import org.evosuite.graphs.dataflow.DepVariable;
import org.evosuite.testcase.DefaultTestCase;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.execution.ExecutionTracer;
import org.evosuite.testcase.synthesizer.ConstructionPathSynthesizer;
import org.evosuite.testcase.synthesizer.TestCaseLegitimizer;
import org.evosuite.utils.Randomness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * RandomLengthTestFactory class.
 * </p>
 * 
 * @author Gordon Fraser
 */
public class RandomLengthTestFactory implements ChromosomeFactory<TestChromosome> {

	private static final long serialVersionUID = -5202578461625984100L;

	/** Constant <code>logger</code> */
	protected static final Logger logger = LoggerFactory.getLogger(RandomLengthTestFactory.class);

	/**
	 * Create a random individual
	 * 
	 * @param size
	 */
	private TestCase getRandomTestCase(int size) {
		boolean tracerEnabled = ExecutionTracer.isEnabled();
		if (tracerEnabled)
			ExecutionTracer.disable();

		TestCase test = getNewTestCase();
		int num = 0;

		// Choose a random length in 0 - size
		int length = Randomness.nextInt(size);
		while (length == 0)
			length = Randomness.nextInt(size);

		
//		List<Method> setterMethods = findSetterMethod(Properties.TARGET_CLASS);
		System.currentTimeMillis();
		
		TestFactory testFactory = TestFactory.getInstance();

		// Then add random stuff
		while (test.size() < length && num < Properties.MAX_ATTEMPTS) {
			int position = test.size() - 1;
			
			int targetMethodCallPosition = -1;
			if(!Properties.TARGET_METHOD.isEmpty()) {
				targetMethodCallPosition = TestGenerationUtil.getTargetMethodPosition(test, test.size() - 1);
				if(targetMethodCallPosition != -1) {
					position = targetMethodCallPosition - 1;						
				}
			}
			
			/**
			 * the first call must be the target method, we add difficult branch support after the target method
			 * is called in test case.
			 */
//			Properties.APPLY_OBJECT_RULE = false;
			if(num == 1 && targetMethodCallPosition != -1 && Properties.APPLY_OBJECT_RULE) {
//				Map<Branch, List<ConstructionPath>> difficulties = Dataflow.checkObjectDifficultPath();
				
				Map<Branch, Set<DepVariable>> interestedBranches = Dataflow.branchDepVarsMap.get(Properties.TARGET_METHOD);
				ArrayList<Branch> rankedList = new ArrayList<>(interestedBranches.keySet());
				Collections.sort(rankedList, new Comparator<Branch>() {
					@Override
					public int compare(Branch o1, Branch o2) {
						return o1.getInstruction().getLineNumber() - o2.getInstruction().getLineNumber();
					}
				});
				
				Branch b = Randomness.choice(interestedBranches.keySet());
//				Branch b = rankedList.get(19);
//				logger.warn("Selected branch:" + b + "\n");
				
//				List<ConstructionPath> paths = difficulties.get(b);
				try {
					ConstructionPathSynthesizer cpSynthesizer = new ConstructionPathSynthesizer(testFactory);
					cpSynthesizer.constructDifficultObjectStatement(test, b);
					
					test = TestCaseLegitimizer.legitimize(test);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				break;
			}
			else {
				testFactory.insertRandomStatement(test, position);						
			}
			
			num++;
		}
		if (logger.isDebugEnabled())
			logger.debug("Randomized test case:" + test.toCode());

		if (tracerEnabled)
			ExecutionTracer.enable();

		return test;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Generate a random chromosome
	 */
	@Override
	public TestChromosome getChromosome() {
		TestChromosome c = new TestChromosome();
		c.setTestCase(getRandomTestCase(Properties.CHROMOSOME_LENGTH));
		return c;
	}

	/**
	 * Provided so that subtypes of this factory type can modify the returned
	 * TestCase
	 * 
	 * @return a {@link org.evosuite.testcase.TestCase} object.
	 */
	protected TestCase getNewTestCase() {
		return new DefaultTestCase();
	}

}
