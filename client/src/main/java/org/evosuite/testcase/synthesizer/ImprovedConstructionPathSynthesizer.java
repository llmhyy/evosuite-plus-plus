package org.evosuite.testcase.synthesizer;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.testcase.TestCase;

public class ImprovedConstructionPathSynthesizer extends ConstructionPathSynthesizer {
	public ImprovedConstructionPathSynthesizer(boolean isDebug) {
		super(isDebug);
	}

	@Override
	public void buildNodeStatementCorrespondence(TestCase test, Branch b, boolean allowNullValue)
			throws ConstructionFailedException, ClassNotFoundException {
		// TODO
	}
}
