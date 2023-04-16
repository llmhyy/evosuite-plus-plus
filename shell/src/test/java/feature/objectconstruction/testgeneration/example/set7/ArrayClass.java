package feature.objectconstruction.testgeneration.example.set7;

public class ArrayClass {
    private static int MAX = 20;
    private int[] array = new int[MAX];

    public void setAtIndex(int index, int value) {
        array[index] = value;
    }

    // target
    public int checkValueAtIndex1() {
        if (array[1] > 20)
            return 1;
        else
            return 0;
    }
}

