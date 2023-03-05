package feature.objectconstruction.testgeneration.example.admin;

public class TargetAdmin {
	public void targetMethod(Administrator admin, Student stu, Assignment assign) {
		Grade grade = admin.getGrade(stu, assign);
		if(stu.getGrade(assign) == grade) {
			System.currentTimeMillis();
		}
	}
}	
