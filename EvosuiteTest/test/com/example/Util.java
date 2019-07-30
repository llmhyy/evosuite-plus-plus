package com.example;

import java.math.BigInteger;

public class Util {
	public static boolean isOk(int a, int b){
		if(Math.pow(a, 2)<=10000){
			if(Math.pow(b, 2)<=9801){
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isCornerCase(int a, int b){
		return a+b >= 199;
	}

	public static boolean checkPrecondition(int[] keys, int value) {
		return checkValueRange(value) &&
				checkArrayRange(keys);
	}

	private static boolean checkValueRange(int value) {
		return Math.abs(value) < 10000;
	}

	private static boolean checkArrayRange(int[] keys) {
		return keys.length < 10000;
	}

	public static boolean checkValue(int[] hasValues, int index, int value) {
		int hashValue = hash(value);
		if((hasValues[index] == 0
	        || hashValue >= Math.pow(2, index)+100)
	            && hasValues[index] == hashValue)
			return true;
		return false;
	}
	
	public static int hash(int key) {
	  final int h = key ^ ((key >>> 20) ^ (key >>> 12));
	  return h ^ (h >>> 7) ^ (h >>> 4) + 100;
	}

	public static boolean checkB(int b) {
		return b==1000000;
	}
	
	public static BigInteger accumulation(int n) {
		if(n==1) return BigInteger.ONE;
		else {
			BigInteger bi=BigInteger.valueOf(n);
			return bi.add(accumulation(n-1));
		}
	}
	
	public static boolean checkN(int n) {
		if(n<1) return false;
		BigInteger bi=new BigInteger("10");
		if(accumulation(n).compareTo(bi)==1) return true;
		else return false;
	}
	public static int accumulate(int n) {
		if(n==1) return 1;
		else {
			return n+accumulate(n-1);
		}
	}
	
	public static boolean checkC(int n) {
		if(n<1||n>10) return false;
		int e=10;
		if(accumulate(n)>e) return true;
		else return false;
	}
	
	public static boolean checkD(int a) {
		return a>10000;
	}
	
	public static int checkF(int a) {
		while(a < 0) {
			a ++;
		}
		
		if(a<5) return 10;
		else if(a>5&&a<10) return 5;
		else if(a==12345) return 12345;
		else return -5;
	}
	

}
