package com.example;

public class ObjectExample {
	public boolean test(Student student){
		int a = 0;
		if(Util.isOk(student.getAge(a), 
				student.getFriend().getAge(a))){
			if(Util.isCornerCase(student.getAge(a), 
					student.getFriend().getAge(a))){
				return true;
			}	
		}
		
		return false;
	}
	
}
