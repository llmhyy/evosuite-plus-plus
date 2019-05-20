package org.evosuite.setup.var;

public class RelevantField extends RelevantVariable {

	private String declaringClassName;
	
	public RelevantField(String name, String type, String declaringClassName) {
		super(name, type);
		this.declaringClassName = declaringClassName;
	}

	public String getDeclaringClassName() {
		return declaringClassName;
	}

	public void setDeclaringClassName(String declaringClassName) {
		this.declaringClassName = declaringClassName;
	}
	
	

}
