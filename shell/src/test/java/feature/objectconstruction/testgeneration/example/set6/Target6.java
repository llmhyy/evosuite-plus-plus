package feature.objectconstruction.testgeneration.example.set6;

public class Target6 {
    //targetMethod
    public static int checkLength(ListClass listClass) {
        return listClass.checkLength();
    }

//        if (listClass.getList().get(0) == 5)
//            return listClass.getList().size();
//        else
//            return -1;
//    }

    // targetMethod
//    public static int checkValueAtIndex4(ListClass listClass) {
//        if (checkLength(listClass) < 0)
//            return -1;
//
//        if (listClass.getList().get(4) >= 4)
//            return listClass.getList().get(4);
//        else
//            return -1;
//    }

    public static void main(String[] args) {
        ListClass listClass = new ListClass();
        listClass.add(0);
        listClass.add(1);
        listClass.add(2);
        listClass.add(3);
        listClass.add(4);

        System.out.println(Target6.checkLength(listClass));
        //System.out.println(Target6.checkValueAtIndex4(listClass));
    }

}
