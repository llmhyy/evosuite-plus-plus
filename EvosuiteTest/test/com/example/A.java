package com.example;

import com.example.passedExamples.ColtExample;

public class A {
	public static void main(String[] args) {
		int[] intArray0 = new int[18];
		int int0 = 867;
		intArray0[1] = int0;
		intArray0[8] = intArray0[0];
		int int1 = 216;
		intArray0[0] = int1;
		int int2 = 1004;
		intArray0[10] = intArray0[0];
		intArray0[14] = int0;
		intArray0[0] = int1;
		int int3 = (-1175);
		int int4 = (-2060);
		int int5 = (-2303);
		ColtExample.inplace_merge(intArray0, int3, int4, int5);
		ColtExample coltExample0 = new ColtExample();
		ColtExample.mergeSortInPlace(intArray0, intArray0[4], intArray0[8]);
	}
	
	
}
