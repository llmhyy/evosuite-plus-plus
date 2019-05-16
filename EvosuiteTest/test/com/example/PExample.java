package com.example;

public class PExample {
	public Integer get(int[] hashValues, int value) {
		if(!Util.checkPrecondition(hashValues, value)) {
			return null;
		}
		
		for(int i=0; i<hashValues.length; i++) {
			if(Util.checkValue(hashValues, i, value)) {
				return hashValues[i];
			}
		}
		
		return null;
	}
	
//	public static void main(String[] args) {
////		for(int i=0; i<100; i++) {
////			System.out.print(i + " ");
////		}
//		
//		for(int i=0; i<100; i++) {
//			if(i==Util.hash(i)) {
//				System.out.print(i + " " + Util.hash(i) + " ");
//				System.out.println();				
//			}
//		}
//	}
}
