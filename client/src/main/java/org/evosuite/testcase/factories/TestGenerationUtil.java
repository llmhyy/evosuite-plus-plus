package org.evosuite.testcase.factories;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.Properties.Criterion;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.utils.ArrayUtil;
import org.evosuite.utils.MethodUtil;
import org.evosuite.utils.generic.GenericAccessibleObject;
import org.evosuite.utils.generic.GenericMethod;

public class TestGenerationUtil {
	
	public static int getTargetMethodPosition(TestCase test, int lastMutatableStatement) {
		for (int position = 0; position <= lastMutatableStatement; position++) {
			Statement stat = test.getStatement(position);
			if(stat instanceof MethodStatement) {
				MethodStatement mStat = (MethodStatement)stat;
				String mSig = mStat.getMethodName() + MethodUtil.getSignature(mStat.getMethod().getMethod());
				if(mSig.equals(Properties.TARGET_METHOD)) {
//					System.currentTimeMillis();
					return position;
				}
			}
		}
		
		return -1;
	}
	
	public static boolean checkTwiceTargetMethodInvocation(TestCase test) {
		int count = 0;
		if(!Properties.TARGET_METHOD.isEmpty()) {
			for(int i=0; i<test.size(); i++) {
				Statement statement = test.getStatement(i);
				if(statement instanceof MethodStatement) {
					MethodStatement mStatement = (MethodStatement)statement;
					GenericMethod method = mStatement.getMethod();
					String sig = method.getName() + MethodUtil.getSignature(method.getMethod());
					if(sig.equals(Properties.TARGET_METHOD)) {
						count ++;
						if(count >=2) {
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	public static boolean checkTargetMethodInvocation(TestCase test) {
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
	
	public static boolean isTargetMethod(GenericAccessibleObject<?> obj, Set<GenericMethod> calledMethods) {
		if(obj instanceof GenericMethod) {
			GenericMethod method = (GenericMethod)obj;
//			String sig = method.getName() + MethodUtil.getSignature(method.getMethod());
			
			if(calledMethods.contains(method)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static Set<GenericMethod> getCalledMethods(TestCase test){
		Set<GenericMethod> calledMethods = new HashSet<>();
		for(Statement stat: test) {
			if(stat instanceof MethodStatement) {
				MethodStatement mStat = (MethodStatement)stat;
				GenericMethod m = mStat.getMethod();
				if(!calledMethods.contains(m)) {
					calledMethods.add(m);
				}
			}
		}
		
		return calledMethods;
	}

	public static void filterTargetMethod(TestCase test, List<GenericAccessibleObject<?>> candidateTestMethods) {
//		return;
		
		if(!Properties.TARGET_METHOD.isEmpty()) {
			Set<GenericMethod> calledMethods = getCalledMethods(test);
			
			Iterator<GenericAccessibleObject<?>> iter = candidateTestMethods.iterator();
			while(iter.hasNext()) {
				GenericAccessibleObject<?> obj = iter.next();
				if(isTargetMethod(obj, calledMethods)) {
					iter.remove();
				}
			}
		}
		
//		if(!Properties.TARGET_METHOD.isEmpty()) {
//			boolean targetCalled = checkTargetMethodInvocation(test);
//			if(targetCalled) {
//				Iterator<GenericAccessibleObject<?>> iter = candidateTestMethods.iterator();
//				while(iter.hasNext()) {
//					GenericAccessibleObject<?> obj = iter.next();
//					if(isTargetMethod(obj)) {
//						iter.remove();
//					}
//				}
//			}
//		}
		
	}
}
