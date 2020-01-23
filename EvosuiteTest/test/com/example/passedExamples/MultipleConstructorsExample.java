package com.example.passedExamples;

/**
 * This example involves constructor from two classes
 *
 */
public class MultipleConstructorsExample {

    private int field1;
    private int field2;
    private int field3;
    private static InevitableConstructorExample test;
    
    public MultipleConstructorsExample(int f1, int f2, int f3) throws Exception {
        if (f1 + f2 + f3 < 15000) {
            throw new Exception("Constructor1 exception");
        }
        this.field1 = f1;
        this.field2 = f2;
        this.field3 = f3;
    }
    
    public MultipleConstructorsExample(int f1, int f2) throws Exception {
        if (f1 + f2 < 14000) {
            throw new Exception("Constructor2 exception");
        }
        this.field1 = f1;
        this.field2 = f2;
    }
    
    public static int targetM(int f1, int f2, int f3) throws Exception {
        
        test = new InevitableConstructorExample(f1, f2, f3);
        MultipleConstructorsExample testV2 = new MultipleConstructorsExample(f1, f2);
        
        if (test.getF2() - testV2.getF1() < 5) {
            if (f2 == testV2.getF1()) {
                return 1;
            }
        }
        return -1;
    }

    public int getF1() {
        return field1;
    }

    public void setF1(int f1) {
        this.field1 = f1;
    }
    
    public int getF2() {
        return field2;
    }

    public void setF2(int f2) {
        this.field2 = f2;
    }
    
    public int getF3() {
        return field3;
    }

    public void setF3(int f3) {
        this.field3 = f3;
    }
}
