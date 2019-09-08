package com.example;

public class ObjectExample {
	public boolean test(Student student){
		if(Util.isOk(student.getAge(), student.getHeight())){
			if(Util.isCornerCase(student.getAge(), student.getHeight())){
				return true;
			}	
		}
		
		return false;
	}
}
