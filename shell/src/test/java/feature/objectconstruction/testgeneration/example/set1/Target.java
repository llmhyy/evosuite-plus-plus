package feature.objectconstruction.testgeneration.example.set1;

import java.util.List;

public class Target {
    // targetMethod
    public void checkGrade(Administrator administrator, Grade grade) {
        List<Student> studentList = administrator.getStudentsAtLeastOneGrade(grade);
        if (studentList.size() > 0) {
            System.out.println(grade + ">0");
            // System.currentTimeMillis();
        }
        if (studentList.size() > 5) {
            System.out.println(grade + ">5");
        }
    }

    // this is guaranteed coverage 0.5
    public boolean checkConvention(Administrator administrator, Student student, Assignment assignment) {
        Grade grade = student.getGrade(assignment);
        if (administrator.getGrade(student, assignment) == grade) {
            return true;
        }
        return false;
    }
}
