package org.evosuite.graphs.dataflow;

/*
 * 
 */
public enum ExcludedMethods {

	instanceOf ("instanceOf"),
	getClass ("getClass()"),
	fileExists("file.exists()");
	
	public final String name;
	
	private ExcludedMethods(String name) {
		this.name = name;
	}
	
}
