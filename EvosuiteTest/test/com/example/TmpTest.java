package com.example;

import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.complex.ComplexField;
import org.apache.commons.math.util.OpenIntToFieldHashMap;
import org.junit.Test;

public class TmpTest {
	
	@Test
	public void test2()  throws Throwable  {
		double double0 = 2;
		double double2 = 3381.246;
		Complex complex0 = new Complex(double0, double2);
		ComplexField complexField0 = complex0.getField();
		int int0 = 3;
		OpenIntToFieldHashMap<Complex> openIntToFieldHashMap0 = new OpenIntToFieldHashMap<Complex>(complexField0, int0);
		int int3 = 0;
		openIntToFieldHashMap0.put(int3, complex0);
		openIntToFieldHashMap0.remove(int3);
		int int2 = 1;
		openIntToFieldHashMap0.put(int2, complex0);
		openIntToFieldHashMap0.remove(int2);
		openIntToFieldHashMap0.get(int3);
	  }
}
