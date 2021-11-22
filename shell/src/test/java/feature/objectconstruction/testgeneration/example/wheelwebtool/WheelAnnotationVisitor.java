package feature.objectconstruction.testgeneration.example.wheelwebtool;

public class WheelAnnotationVisitor implements AnnotationVisitor {
/*    */    private AnnotationVisitor annotationVisitor;
/*    */    private Scope scope;
/*    */    private boolean rebuild;
/*    */ 
/*    */    public WheelAnnotationVisitor(AnnotationVisitor annotationVisitor) {
/* 36 */       this.annotationVisitor = annotationVisitor;
/* 37 */    }
/*    */ 
/*    */    public void visit(String name, Object value) {
/* 40 */       if (name.equals("rebuild")) {
/* 41 */          this.rebuild = (Boolean)value;
/*    */       }
/* 43 */    }
/*    */ 
/*    */    public void visitEnum(String name, String desc, String value) {
/* 46 */       this.scope = Scope.valueOf(value);
/* 47 */    }
/*    */ 
/*    */    public AnnotationVisitor visitAnnotation(String string, String string1) {
/* 50 */       return this.annotationVisitor.visitAnnotation(string, string1);
/*    */    }
/*    */ 
/*    */    public AnnotationVisitor visitArray(String string) {
/* 54 */       return this.annotationVisitor.visitArray(string);
/*    */    }
/*    */ 
/*    */    public void visitEnd() {
/* 58 */       this.annotationVisitor.visitEnd();
/* 59 */    }
/*    */ 
/*    */ 
/*    */    public Scope getScope() {
/* 63 */       return this.scope;
/*    */    }
/*    */ 
/*    */    public boolean isRebuild() {
/* 67 */       return this.rebuild;
/*    */    }
}
