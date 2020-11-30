package regression.objectconstruction.testgeneration.example;

import java.util.List;


import regression.fbranch.example.ValueUtil;

public class ObjectExample {
	public boolean test(Student student) {
		int a = 0;
		if (ValueUtil.isOk(student.getAge(), 
				student.getFriend().getAge())) {
			if (ValueUtil.isCornerCase(student.getAge(), 
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
