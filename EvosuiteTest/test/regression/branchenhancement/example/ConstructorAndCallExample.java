package regression.branchenhancement.example;

public class ConstructorAndCallExample {

    private int field1;
    private int field2;
    private int field3;
    
    public ConstructorAndCallExample(int f1, int f2, int f3) throws Exception {
        if (f1 + f2 + f3 < 16000) {
            throw new Exception("Constructor1 exception");
        }
        this.field1 = f1;
        this.field2 = f2;
        this.field3 = f3;
    }
    
    public int targetM(int f1, int f2, int f3) throws Exception {
        
    	ConstructorAndCallExample test = new ConstructorAndCallExample(f1, f2, f3);
        
        m20(f2, f3);
        
        if (test.getF2() - f1 < 5) {
            if (f2 == test.getF1()) {
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

    private void m20(int f2, int f3) throws Exception {
        m21(f2, f3);
    }

    private void m21(int f2, int f3) throws Exception {
        if (Math.abs(f3 - f2) > 20) {
            throw new Exception("m21 exception");
        }
    }
}
