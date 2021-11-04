package feature.objectconstruction.testgeneration.example.wheelwebtool;

public interface AnnotationVisitor {
   void visit(String var1, Object var2);

   void visitEnum(String var1, String var2, String var3);

   AnnotationVisitor visitAnnotation(String var1, String var2);

   AnnotationVisitor visitArray(String var1);

   void visitEnd();
}
