package feature.objectconstruction.testgeneration.example.set2;

class OuterMost {
    private Layer1 layer1;

    public void setLayer1(Layer1 layer1) {
        this.layer1 = layer1;
    }

    public Layer1 getLayer1() {
        return layer1;
    }

    // fast retrieval method
    public int getValue() {
        return layer1.getValue();
    }

    // fast retrieval method
    public void setValue(int value) {
        layer1.setValue(value);
    }

    public boolean isValueGreaterThan(int x) {
        return getValue() > x;
    }

    public boolean isValueGreaterThan100() {
        return getValue() > 100;
    }
}