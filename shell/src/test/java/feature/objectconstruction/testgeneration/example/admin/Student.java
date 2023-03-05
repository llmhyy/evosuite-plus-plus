package feature.objectconstruction.testgeneration.example.admin;

import java.util.List;
import java.util.ArrayList;

public class Student {
    private String matric; // unique id
    private List<Assignment> assignmentList;

    public void setMatric(String matric) {
        this.matric = matric;
    }

    public void setAssignmentList(List<Assignment> assignmentList) {
        this.assignmentList = assignmentList;
    }

    public String getMatric() {
        return matric;
    }

    public List<Assignment> getAssignmentList() {
        return assignmentList;
    }

    public Grade getGrade(Assignment assignment) {
        return assignment.getGrade(this);
    }

}