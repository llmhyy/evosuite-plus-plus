package org.evosuite.testcase.statements;

public interface ValueStatement extends Statement{
	public void setAssignmentValue(Object obj);
	
	public Object getAssignmentValue();
	
	public void mutateValue();
}
