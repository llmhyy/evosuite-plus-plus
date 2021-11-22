package feature.objectconstruction.testgeneration.example.wheelwebtool;

import java.util.List;

public class WheelFieldVisitor {
/*    */	private FieldVisitor fieldVisitor;
/*    */    private WheelAnnotationVisitor wheelAnnotationVisitor;
/*    */    private List<WheelAnnotatedField> wheelAnnotatedFields;
/*    */    private int access;
/*    */    private String name;
/*    */    private String desc;
/*    */    private String signature;
/*    */ 
/*    */    public WheelFieldVisitor(FieldVisitor fieldVisitor, List<WheelAnnotatedField> wheelAnnotatedFields, int access, String name, String desc, String signature) {
/* 45 */       this.fieldVisitor = fieldVisitor;
/* 46 */       this.wheelAnnotatedFields = wheelAnnotatedFields;
/* 47 */       this.access = access;
/* 48 */       this.name = name;
/* 49 */       this.desc = desc;
/* 50 */       this.signature = signature;
/* 51 */    }
/*    */ 
/*    */    public AnnotationVisitor visitAnnotation(String name, boolean visible) {
/* 54 */       if (name.equals("Lwheel/annotations/Persist;")) {
/* 55 */          this.wheelAnnotationVisitor = new WheelAnnotationVisitor(this.fieldVisitor.visitAnnotation(name, visible));
/* 56 */          return this.wheelAnnotationVisitor;
/*    */ 
/*    */       } else {
/* 59 */          return this.fieldVisitor.visitAnnotation(name, visible);
/*    */       }
/*    */    }
/*    */    public void visitAttribute(Attribute attribute) {
/* 63 */       this.fieldVisitor.visitAttribute(attribute);
/* 64 */    }
/*    */    public void visitEnd() {
/*    */       WheelAnnotatedField field;
/* 67 */       if (this.wheelAnnotationVisitor != null) {
/*    */ 
/* 69 */          if (this.access != 2) {
/* 70 */             throw new WheelException("Persistent field " + this.name + " must have private access.", (Component)null);
/*    */ 
/*    */ 
/*    */          }
/*    */ 
/* 75 */          field = new WheelAnnotatedField(this.name, this.desc, this.signature, this.wheelAnnotationVisitor.getScope());
/*    */ 
/* 77 */          if (this.wheelAnnotationVisitor.getScope() == null) {
/* 78 */             field.setScope(Scope.component);
/*    */          }
/* 80 */          this.wheelAnnotatedFields.add(field);
/*    */ 
/*    */ 
/* 83 */       } else if (this.access == 2) {
/*    */ 
/*    */ 
/*    */ 
/* 87 */          field = new WheelAnnotatedField(this.name, this.desc, this.signature, Scope.request);
/* 88 */          this.wheelAnnotatedFields.add(field);
/*    */ 
/*    */       }
/*    */ 
/* 92 */       this.fieldVisitor.visitEnd();
/* 93 */    }
/*    */ 
/*    */    public WheelAnnotationVisitor getPersistAnnotationVisitor() {
/* 96 */       return this.wheelAnnotationVisitor;
/*    */    }
}
