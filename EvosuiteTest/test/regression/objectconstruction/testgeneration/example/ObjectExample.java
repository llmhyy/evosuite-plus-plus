package regression.objectconstruction.testgeneration.example;

import java.util.List;

import com.example.Student;
import com.example.Student1;
import com.example.StudentAbstract;
import com.example.StudentInterface;
import com.example.Util;

public class ObjectExample {
	public boolean test(Student student) {
		int a = 0;
		if (Util.isOk(student.getAge(), 
				student.getFriend().getAge())) {
			if (Util.isCornerCase(student.getAge(), 
					student.getFriend().getAge())) {
				return true;
			}
		}

		return false;
	}

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

	public boolean test2(List<Integer> list, StudentAbstract student) {
		for (Integer age : list) {
			if (student.getAge() == age) {
				return true;
			}
		}
		return false;
	}

}
