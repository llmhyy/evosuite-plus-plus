package com.example;

public class Student {
	private int age;
	private int height;

	public Student(int age, int height) {
		super();
		this.age = age;
		this.height = height;
	}

	@Override
	public String toString() {
		return "Student [age=" + age + ", height=" + height + "]";
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
