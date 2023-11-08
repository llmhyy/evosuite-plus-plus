package feature.objectconstruction.testgeneration.example.set6;

import java.util.ArrayList;

public class ListClass {
    private ArrayList<Integer> list = new ArrayList<>();

    // target
    public int checkLength() {
        if (this.list.get(0) == 5)
            return 1;
        else
            return -1;
    }

    public void add(Integer i) {
        list.add(i);
    }
}


