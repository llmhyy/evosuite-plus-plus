package feature.objectconstruction.testgeneration.example.wheelwebtool;

/*     */ public class Attribute {
/*     */    public final String type;
/*     */    byte[] value;
/*     */    Attribute next;
/*     */ 
/*     */    protected Attribute(String type) {
/*  61 */       this.type = type;
/*  62 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public boolean isUnknown() {
/*  71 */       return true;
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public boolean isCodeAttribute() {
/*  80 */       return false;
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    
///*     */    protected Label[] getLabels() {
///*  90 */       return null;
///*     */    }
/*     */    
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
///*     */    protected Attribute read(ClassReader cr, int off, int len, char[] buf, int codeOff, Label[] labels) {
///* 127 */       Attribute attr = new Attribute(this.type);
///* 128 */       attr.value = new byte[len];
///* 129 */       System.arraycopy(cr.b, off, attr.value, 0, len);
///* 130 */       return attr;
///*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
///*     */    protected ByteVector write(ClassWriter cw, byte[] code, int len, int maxStack, int maxLocals) {
///* 160 */       ByteVector v = new ByteVector();
///* 161 */       v.data = this.value;
///* 162 */       v.length = this.value.length;
///* 163 */       return v;
///*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    final int getCount() {
/* 172 */       int count = 0;
/* 173 */       for(Attribute attr = this; attr != null; attr = attr.next) {
/*     */ 
/* 175 */          ++count;
/*     */       }
/*     */ 
/* 178 */       return count;
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
///*     */    final int getSize(ClassWriter cw, byte[] code, int len, int maxStack, int maxLocals) {
///* 208 */       Attribute attr = this;      int size;
///* 209 */       for(size = 0; attr != null; attr = attr.next) {
///*     */ 
///* 211 */          cw.newUTF8(attr.type);
///* 212 */          size += attr.write(cw, code, len, maxStack, maxLocals).length + 6;
///*     */       }
///*     */ 
///* 215 */       return size;
///*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
///*     */    final void put(ClassWriter cw, byte[] code, int len, int maxStack, int maxLocals, ByteVector out) {
///* 246 */       for(Attribute attr = this; attr != null; attr = attr.next) {
///*     */ 
///* 248 */          ByteVector b = attr.write(cw, code, len, maxStack, maxLocals);
///* 249 */          out.putShort(cw.newUTF8(attr.type)).putInt(b.length);
///* 250 */          out.putByteArray(b.data, 0, b.length);
///*     */       }
///*     */ 
///* 253 */    }
/*     */ }
