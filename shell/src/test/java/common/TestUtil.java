package common;

import java.util.List;

import org.evosuite.coverage.branch.Branch;
import org.evosuite.testcase.DefaultTestCase;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestFactory;
import org.evosuite.testcase.statements.NullStatement;
import org.evosuite.testcase.statements.Statement;

public class TestUtil {
	public static void mutateNullStatements(TestCase test) {
		for (int i = 0; i < test.size(); i++) {
			Statement s = test.getStatement(i);
			if (s instanceof NullStatement) {
				TestFactory.getInstance().changeNullStatement(test, s);
				System.currentTimeMillis();
			}
		}
	}

	public static TestCase initializeTest(Branch b, TestFactory testFactory, boolean allowNullValue) {
		TestCase test = new DefaultTestCase();
		int success = -1;
		while (test.size() == 0 || success == -1) {
			test = new DefaultTestCase();
			success = testFactory.insertRandomStatement(test, 0);
			if (test.size() != 0 && success != -1 && !allowNullValue) {
				mutateNullStatements(test);
			}
		}

		return test;
	}
	
	public static Branch searchBranch(List<Branch> rankedList, int lineNumber) {
		for (Branch b : rankedList) {
			if (b.getInstruction().getLineNumber() == lineNumber) {
				return b;
			}
		}
		return null;
	}
}
