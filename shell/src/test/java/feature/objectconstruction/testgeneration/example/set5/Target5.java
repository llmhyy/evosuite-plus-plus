package feature.objectconstruction.testgeneration.example.set5;

public class Target5 {
    // targetMethod
    public static int checkLength(ListClass listClass) {
        if (listClass.getSize() >= 5)
            return 1;
        else
            return -1;
    }

    // targetMethod
    public static int checkValueAtIndex5(ListClass listClass) {
        if (listClass.getAt5() > 5)
            return 1;
        else
            return -1;
    }

    public static void main(String[] args) {
        ListClass listClass = new ListClass();
        listClass.add(0);
        listClass.add(1);
        listClass.add(2);
        listClass.add(3);
        listClass.add(4);
        listClass.add(5); // index 5

        listClass.setAt5(100); // optional
        // however, can't set index 5 without having values for indices 0-4

        System.out.println(listClass.getAt5());
        System.out.println(Target5.checkValueAtIndex5(listClass));
    }

}
