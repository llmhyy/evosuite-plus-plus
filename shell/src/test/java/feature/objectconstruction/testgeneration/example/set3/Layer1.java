package feature.objectconstruction.testgeneration.example.set3;

class Layer1 {
    private Layer2 layer2;

    public void setLayer2(Layer2 layer2) {
        this.layer2 = layer2;
    }

    public void setValue(int value) {
        layer2.setValue(value);
    }

    public Layer2 getLayer2() {
        return layer2;
    }

    public int getValue() {
        return layer2.getValue();
    }
}