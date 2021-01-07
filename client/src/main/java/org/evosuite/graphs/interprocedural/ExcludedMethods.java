package org.evosuite.graphs.interprocedural;

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
