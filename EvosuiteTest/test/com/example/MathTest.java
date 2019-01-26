package com.example;

import java.math.BigInteger;

import org.apache.commons.math.Field;
import org.apache.commons.math.complex.ComplexField;
import org.apache.commons.math.dfp.Dfp;
import org.apache.commons.math.dfp.DfpField;
import org.apache.commons.math.fraction.BigFraction;
import org.apache.commons.math.fraction.BigFractionField;
import org.apache.commons.math.fraction.Fraction;
import org.apache.commons.math.util.OpenIntToFieldHashMap;

public class MathTest {
	public static void main(String[] args) {
		BigFractionField bigFractionField0 = BigFractionField.getInstance();
		int int0 = 0;
		OpenIntToFieldHashMap<BigFraction> openIntToFieldHashMap0 = new OpenIntToFieldHashMap<BigFraction>(bigFractionField0, int0);
		int int1 = 1;
		byte[] byteArray0 = new byte[2];
		byte byte0 = (byte)13;
		byteArray0[0] = byte0;
		byte byte1 = (byte) (-65);
		byteArray0[1] = byte1;
		BigInteger bigInteger0 = new BigInteger(byteArray0);
		BigFraction bigFraction0 = new BigFraction(bigInteger0);
		openIntToFieldHashMap0.put(int1, bigFraction0);
		int int2 = 0;
		BigFraction bigFraction1 = new BigFraction(int2);
		openIntToFieldHashMap0.put(int0, bigFraction1);
		openIntToFieldHashMap0.remove(int0);
		openIntToFieldHashMap0.get(int0);
		int int3 = 0;
		openIntToFieldHashMap0.iterator();
		Field<Fraction> field0 = null;
		OpenIntToFieldHashMap<Fraction> openIntToFieldHashMap1 = new OpenIntToFieldHashMap<Fraction>(field0);
		OpenIntToFieldHashMap<Fraction> openIntToFieldHashMap2 = new OpenIntToFieldHashMap<Fraction>(openIntToFieldHashMap1);
		openIntToFieldHashMap2.iterator();
		openIntToFieldHashMap2.get(int3);
		ComplexField.getInstance();
		int int4 = 0;
		openIntToFieldHashMap0.remove(int4);
		DfpField dfpField0 = new DfpField(int4);
		Dfp dfp0 = dfpField0.getLn2();
		DfpField dfpField1 = dfp0.getField();
		OpenIntToFieldHashMap<Dfp> openIntToFieldHashMap3 = new OpenIntToFieldHashMap<Dfp>(dfpField1, dfp0.RADIX);
		int int5 = (-1701);
		openIntToFieldHashMap3.get(int5);
		int int6 = 1939;
		int int7 = 20;
		openIntToFieldHashMap0.get(int7);

	}
}
