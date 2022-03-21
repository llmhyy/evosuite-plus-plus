package org.evosuite.testcase.synthesizer.matrix;

import java.util.ArrayList;

public class AccessMatrix {
	private AccessEntry[][] matrix;
	
	public AccessMatrix(int size) {
		matrix = new AccessEntry[size][size];
		
		for(int i=0; i<size; i++) {
			for(int j=0; j<size; j++) {
				AccessEntry entry = new AccessEntry(new ArrayList<>());
				matrix[i][j] = entry;
			}
		}
	}
	
	public void set(int i, int j, AccessEntry entry) {
		matrix[i][j] = entry;
	}
	
	public AccessEntry get(int i, int j) {
		return matrix[i][j];
	}
}
