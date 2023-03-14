package feature.objectconstruction.testgeneration.example.set1;

import java.util.List;

public class Student {
    private String matric; // unique id
    private List<Assignment> assignmentList;

    public Student(String matric, List<Assignment> assignmentList) {
        this.matric = matric;
        this.assignmentList = assignmentList;
    }

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