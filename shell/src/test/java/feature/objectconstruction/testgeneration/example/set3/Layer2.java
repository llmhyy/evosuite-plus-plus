package feature.objectconstruction.testgeneration.example.set3;

public class Layer2 {
    private InnerMost innerMost;

    public void setInnerMost(InnerMost innerMost) {
        this.innerMost = innerMost;
    }

    public void setValue(int value) {
        innerMost.setValue(value);
    }

    public InnerMost getInnerMost() {
        return innerMost;
    }

    public int getValue() {
        return innerMost.getValue();
    }
}