package feature.objectconstruction.testgeneration.example;

import java.util.List;

public class ObjectExample {
	public static boolean isOk(int a, int b){
		if(Math.pow(a, 2)<=10000){
			if(Math.pow(b, 2)<=9801){
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isCornerCase(int a, int b){
		return a+b >= 199;
	}
	
	public boolean test(Student student) {
		if (isOk(student.getAge(), 
				student.getFriend().getAge())) {
			if (isCornerCase(student.getAge(), 
					student.getFriend().getAge())) {
				return true;
			}
		}

		return false;
	}

	public boolean test(List<Integer> list, Student student) {
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

	public boolean test2(List<Integer> list, StudentAbstract student) {
		for (Integer age : list) {
			if (student.getAge() == age) {
				return true;
			}
		}
		return false;
	}

}
