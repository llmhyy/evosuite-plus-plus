package feature.objectconstruction.testgeneration.example.set4;

public class Target4 {
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

    public static void main(String[] args) {
        Target4 target4 = new Target4();
        OuterMost outerMost = new OuterMost();
        //Layer1 layer1 = new Layer1();
        //Layer2 layer2 = new Layer2();
        InnerMost innerMost = new InnerMost();
        int int0 = 100;
        innerMost.setValue(int0);
        //layer1.setLayer2(layer2)
        //layer2.setInnerMost(innerMost);
        outerMost.createLayer2(innerMost);
        int result = target4.crossLayer(outerMost);
        System.out.println(result);
    }

}
