package org.evosuite.testcase.factories;

import org.evosuite.Properties;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.utils.MethodUtil;
import org.evosuite.utils.generic.GenericMethod;

public class TestGenerationUtil {
	public static boolean checkStopInsertion(TestCase test) {
		boolean stopInsertion = false;
		if(!Properties.TARGET_METHOD.isEmpty()) {
			for(int i=0; i<test.size(); i++) {
				Statement statement = test.getStatement(i);
				if(statement instanceof MethodStatement) {
					MethodStatement mStatement = (MethodStatement)statement;
					GenericMethod method = mStatement.getMethod();
					String sig = method.getName() + MethodUtil.getSignature(method.getMethod());
					if(sig.equals(Properties.TARGET_METHOD)) {
						stopInsertion = true;
						break;
					}
				}
			}
		}
		
		return stopInsertion;
	}
}
