package feature.objectconstruction.testgeneration.example.set8;

public class AnotherClass {
    private int value;

    public AnotherClass(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void reverseValue() {
        this.value = (-1) * this.value;
    }
}
