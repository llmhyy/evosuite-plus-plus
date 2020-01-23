package com.example;

public class Student1 extends StudentAbstract implements StudentInterface {

	private int age;
	private int height;
	
	private Student1 friend;

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
	
	public void setAge(int age, int b, Student c) {
		this.age = age;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Student1 getFriend() {
		return friend;
	}

	public void setFriend(Student1 friend) {
		this.friend = friend;
	}
	
}
