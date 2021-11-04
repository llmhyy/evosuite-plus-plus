package feature.objectconstruction.testgeneration.example.xbus;

/*     */ public class XStringSupport {
/*     */    public static String getNumberString(int number, int length) {
/*  25 */       StringBuffer numString = new StringBuffer();
/*  26 */       numString.append(number);
/*     */ 
/*  28 */       if (numString.length() > length) {
/*  29 */          throw new IllegalArgumentException("Number too long");
/*     */       } else {
/*  31 */          StringBuffer buffer = null;
/*  32 */          if (length > numString.length()) {
/*     */ 
/*  34 */             buffer = new StringBuffer(length - numString.length());
/*     */ 
/*  36 */             if (number < 0) {
/*     */ 
/*  38 */                buffer.append('-');
/*  39 */                numString.deleteCharAt(0);
/*     */ 
/*     */             }
/*     */ 
/*  43 */             for(int i = 0; i < length - numString.length(); ++i) {
/*  44 */                buffer.append('0');
/*     */             }
/*  46 */             buffer.append(numString);
/*     */ 
/*     */          } else {
/*  49 */             buffer = numString;         }
/*  50 */          return buffer.toString();
/*     */       }
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
/*     */    public static String successorUsualChars(String s, boolean lengthFix) {
/*  67 */       char[] content = s.toCharArray();
/*  68 */       boolean overflow = true;
/*     */ 
/*  70 */       for(int i = content.length - 1; overflow && i > -1; --i) {
/*     */ 
/*     */ 
/*  73 */          if (content[i] < ' ') {
/*     */ 
/*  75 */             content[i] = ' ';
/*  76 */             overflow = false;
/*     */ 
/*     */ 
/*  79 */          } else if (content[i] > '}') {
/*  80 */             content[i] = ' ';
/*     */ 
/*     */          } else {
/*  83 */             ++content[i];
/*  84 */             overflow = false;
/*     */          }
/*     */       }
/*     */ 
/*  88 */       s = new String(content);
/*  89 */       if (!lengthFix && overflow) {
/*  90 */          s = ' ' + s;      }
/*  91 */       return s;
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
/*     */    public static String replaceAll(String text, String marker, String replacement) {
/*     */       int pos;
/*     */       StringBuffer work;
/* 112 */       for(work = new StringBuffer(text); (pos = text.indexOf(marker)) >= 0; text = new String(work)) {
/*     */ 
/*     */ 
/* 115 */          work.replace(pos, pos + marker.length(), replacement);
/*     */       }
/*     */ 
/* 118 */       return new String(work);
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public static String replaceFirst(String text, String marker, String replacement, int fromPosition) {
/* 125 */       StringBuffer work = new StringBuffer(text);      int pos;
/* 126 */       if ((pos = text.indexOf(marker, fromPosition)) >= 0) {
/*     */ 
/* 128 */          work.replace(pos, pos + marker.length(), replacement);
/*     */          new String(work);      }
/*     */ 
/* 131 */       return new String(work);
/*     */    }
/*     */ }