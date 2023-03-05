package feature.objectconstruction.testgeneration.example.admin;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Assignment {
    private Map<Student, Grade> map;

    public void setMap(Map<Student, Grade> map) {
        this.map = map;
    }

    public Grade getGrade(Student student) {
        return map.get(student);
    }

    public List<Student> getStudentsWithGrade(Grade grade) {
        List<Student> result = new ArrayList<>();
        for (Map.Entry<Student, Grade> entry : map.entrySet()) {
            if (entry.getValue() == grade)
                result.add(entry.getKey());
        }
        return result;
    }
}