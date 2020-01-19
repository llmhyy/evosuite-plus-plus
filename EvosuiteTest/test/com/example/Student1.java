package com.example;

public class Student1 {
	private int age;
	private int height;
	
	private Student friend;
	private static Student friend1;

//	public Student(int age, int height) {
//		super();
//		this.age = age;
//		this.height = height;
//	}

	@Override
	public String toString() {
		return "Student [age=" + age + ", height=" + height + "]";
	}

	public int getAge() {
		return age;
	}

	public int getfadfad() {
		return 0;
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

	public Student getFriend() {
		return friend;
	}

	public void setFriend(Student friend) {
		this.friend = friend;
	}
	
	public static Student getFriend1() {
		return friend1;
	}
	
	public void setFriend1(Student friend1) {
		this.friend1 = friend1;
	}

}
