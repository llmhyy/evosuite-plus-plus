package com.example;

public class C {
	private int x;
	private int y;
	private A z;
	
	public C(int x, int y, A z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public boolean test(A a, int b) {
		if(a.getC().getX() >= 1 && b > 10){
			return true;
		}
		return false;
	}
	
	public int getX() {
		return x;
	}

}
