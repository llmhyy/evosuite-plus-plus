package com.example;

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
		return checkArrayRange(keys) &&
				checkValueRange(value);
	}

	private static boolean checkValueRange(int value) {
		return Math.abs(value) < 100000;
	}

	private static boolean checkArrayRange(int[] keys) {
		return keys.length < 100000;
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
}
