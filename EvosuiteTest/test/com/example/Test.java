package com.example;

public class Test {

	int a;
	private int b;

	public Test(int a, int b) {
		this.a = a;
		this.b = b;
	}

	public int getA() {
		return this.a;
	}

	public int helper(int num1) {
		int d = a + num1;
		return d;
	}

	public void helper1(int[] nums) {
		nums[1]++;
		if (nums[1] > 5) {
			return;
		}
		nums[2]++;
	}

	public boolean isOk(int num1, int num2) {
		int sum = num1 + num2;
		if (this.a > sum) {
			return true;
		} else {
//		return this.a > num1 + num2;
			return false;
		}
	}
}
