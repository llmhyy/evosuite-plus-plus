package com.example;

import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.complex.ComplexField;
import org.apache.commons.math.util.OpenIntToFieldHashMap;
import org.junit.Test;

public class TmpTest {
	
	@Test
	public void test2()  throws Throwable  {
		double double0 = 1/0.0;
		double double1 = (-17.676440723604877);
		double double2 = 3381.246;
		double double3 = 3381.246;
		Complex complex0 = new Complex(double0, double2);
		ComplexField complexField0 = complex0.getField();
		Complex complex1 = new Complex(double3, double1);
		int int0 = 0;
		OpenIntToFieldHashMap<Complex> openIntToFieldHashMap0 = new OpenIntToFieldHashMap<Complex>(complexField0, int0);
		complex0.log();
		openIntToFieldHashMap0.iterator();
		int int1 = (-552);
		int int2 = 3626;
		openIntToFieldHashMap0.iterator();
		openIntToFieldHashMap0.put(int2, complex0);
		openIntToFieldHashMap0.put(int1, complex0);
		openIntToFieldHashMap0.remove(int1);
		int int3 = 3899;
		openIntToFieldHashMap0.get(int3);

	  }
}
