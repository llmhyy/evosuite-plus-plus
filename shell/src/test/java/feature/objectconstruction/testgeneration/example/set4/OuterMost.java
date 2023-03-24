package feature.objectconstruction.testgeneration.example.set4;

public class OuterMost {
    private Layer1 layer1;
    private Layer2 layer2;

    public OuterMost(Layer1 layer1) {
        this.layer1 = layer1;
        this.layer2 = layer1.getLayer2();
    }

    public OuterMost() {
    }

    public int getValue() {
        return layer2.getInnerMost().getValue();
    }

    // fast retrieval method
    public Layer2 getLayer2() {
        return layer1.getLayer2();
    }

    // fast retrieval method
    public void setLayer2(Layer2 layer2) {
        layer1.setLayer2(layer2);
    }

    public void createLayer2(InnerMost innerMost) {
        layer2 = new Layer2(innerMost);
    }

}

