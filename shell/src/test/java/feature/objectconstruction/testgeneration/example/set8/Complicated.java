package feature.objectconstruction.testgeneration.example.set8;

import java.util.ArrayList;


public class Complicated {
    private ArrayList<AnotherClass> values = new ArrayList<>();

    public void setDirectly(int value) {
        AnotherClass anotherClass = new AnotherClass(value);
        anotherClass.reverseValue();
        values.add(anotherClass);
    }

    public int check() {
        if (values.get(0).getValue() > 0)
            return 1;
        else return 0;
    }
}

