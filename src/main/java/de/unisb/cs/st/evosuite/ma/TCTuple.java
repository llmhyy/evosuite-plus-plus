/**
 * 
 */
package de.unisb.cs.st.evosuite.ma;

import java.util.HashSet;
import java.util.Set;

import de.unisb.cs.st.evosuite.testcase.TestCase;

/**
 * @author Yury Pavlov
 * 
 */
public class TCTuple implements Cloneable {

	private TestCase testCase;

	private Set<Integer> coverage = new HashSet<Integer>();

	public TCTuple(TestCase testCase, Set<Integer> coverage) {
		this.testCase = testCase;
		this.coverage = coverage;
	}

	/**
	 * @return the coverage
	 */
	public Set<Integer> getCoverage() {
		return coverage;
	}

	/**
	 * @return the testCase
	 */
	public TestCase getTestCase() {
		return testCase;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		return false;
	}

	/**
	 * Returns a shallow copy of this <tt>TestCaseTuple</tt> instance. (The
	 * elements themselves are not copied.)
	 * 
	 * @return a clone of this <tt>TestCaseTuple</tt> instance
	 */
	public TCTuple clone() {
		try {
			TCTuple v = (TCTuple) super.clone();
			v.testCase = testCase.clone();
			v.coverage = new HashSet<Integer>(coverage);
			return v;
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// main target of this representation just to identify diff.
		// TestCaseTuple
		String res = "TestCase: " + this.hashCode() + ". Coverage: " + coverage
				+ "\n" + "Source code: \n" + testCase.toCode();

		return res;
	}

}
