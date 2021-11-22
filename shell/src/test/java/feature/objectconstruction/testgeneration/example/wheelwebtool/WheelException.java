package feature.objectconstruction.testgeneration.example.wheelwebtool;

public class WheelException extends RuntimeException {
/*    */    private Component source;
/*    */ 
/*    */    public WheelException(String string, Throwable throwable, Component source) {
/* 39 */       super(string, throwable);
/* 40 */       this.source = source;
/* 41 */    }
/*    */ 
/*    */ 
/*    */    public WheelException(Component source) {
/* 45 */       this.source = source;
/* 46 */    }
/*    */ 
/*    */    public WheelException(String string, Component source) {
/* 49 */       super(string);
/* 50 */       this.source = source;
/* 51 */    }
/*    */ 
/*    */    public WheelException(Throwable throwable, Component source) {
/* 54 */       super(throwable);
/* 55 */       this.source = source;
/* 56 */    }
/*    */ 
/*    */ 
/*    */    public Component getSource() {
/* 60 */       return this.source;
/*    */    }
}
