package feature.objectconstruction.testgeneration.example.set4;

public class Layer2 {
    private InnerMost innerMost;

    Layer2(InnerMost innerMost) {
        this.innerMost = innerMost;
    }

    public Layer2() {

    }

    public void setInnerMost(InnerMost innerMost) {
        this.innerMost = innerMost;
    }

//    public void setValue(int value) {
//        innerMost.setValue(value);
//    }

    public InnerMost getInnerMost() {
        return innerMost;
    }

//    public int getValue() {
//        return innerMost.getValue();
//    }
}