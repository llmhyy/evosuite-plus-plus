package com.example;

import java.util.List;

public class ObjectExample {
//	public boolean test(Student student){
//		int a = 0;
//		if(Util.isOk(student.getAge(a), 
//				student.getFriend().getAge(a))){
//			if(Util.isCornerCase(student.getAge(a), 
//					student.getFriend().getAge(a))){
//				return true;
//			}	
//		}
//		
//		return false;
//	}
		
		public boolean test(List<Integer> list, Student1 student) {
			for (Integer age : list) {
				if (student.getAge() == age) {
					return true;
				}
			}
			return false;
		}
		
		public boolean test1(List<Integer> list, StudentInterface student) {
			for (Integer age : list) {
				if (student.getAge() == age) {
					return true;
				}
			}
			return false;
		}
		
		public boolean test1(List<Integer> list, StudentAbstract student) {
			for (Integer age : list) {
				if (student.getAge() == age) {
					return true;
				}
			}
			return false;
		}
	
}
