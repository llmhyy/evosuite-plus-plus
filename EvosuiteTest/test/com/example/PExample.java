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
}
