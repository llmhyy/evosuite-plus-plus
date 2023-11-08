package feature.objectconstruction.testgeneration.example.set2;

public class Target2 {
    // targetMethod
    public int crossLayer(OuterMost outerMost) {
        int value = outerMost.getValue();
        if (value > 10) {
            return 10;
        } else if (value > 0) {
            return 0;
        } else {
            return -1;
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
