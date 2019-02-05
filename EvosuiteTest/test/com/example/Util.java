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
}
