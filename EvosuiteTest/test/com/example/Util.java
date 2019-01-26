package com.example;

public class Util {
	public boolean isOk(int a, int b){
		if(Math.pow(a, 2)<=625){
			if(Math.pow(b, 2)<=625){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isCornerCase(int a, int b){
		return a-b <= -49;
	}
}
