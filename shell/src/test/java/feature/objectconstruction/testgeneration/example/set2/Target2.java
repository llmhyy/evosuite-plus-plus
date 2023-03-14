package feature.objectconstruction.testgeneration.example.set2;

public class Target2 {
    // targetMethod
    public void crossLayer(OuterMost outerMost) {
        int value = outerMost.getValue();
        if (value > 10) {
            System.out.println(">10");
        } else if (value > 0) {
            System.out.println(">0");
        } else {
            System.out.println("<=0");
        }
    }

    // targetMethod
    public boolean checkEqual(OuterMost outerMost1, OuterMost outerMost2) {
        int value1 = outerMost1.getValue();
        int value2 = outerMost2.getValue();
        if (value1 == value2)
            return true;
        return false;
    }
}
