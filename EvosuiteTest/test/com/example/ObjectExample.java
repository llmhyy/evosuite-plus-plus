package com.example;

public class ObjectExample {
	public boolean test(Student student){
		if(Util.isOk(student.getAge(), student.getFriend().getAge())){
			if(Util.isCornerCase(student.getAge(), student.getFriend().getAge())){
				return true;
			}	
		}
		
		return false;
	}
}
