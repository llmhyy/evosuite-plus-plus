package feature.objectconstruction.testgeneration.example.xbus;

/*     */ public class Trace {
/*     */    public static final int DEBUG = 4;
/*     */    public static final int INFO = 3;
/*     */    public static final int WARN = 2;
/*     */    public static final int ERROR = 1;
/*     */    public static final int ALWAYS = 0;
/*     */    private static final int NOT_INITIALIZED = 9999;
/*     */    private static int mTracelevel = 9999;
/*     */    private static TraceTarget mTarget = null;
/*     */ 
/*     */    public static void always(Object message) {
/*  31 */       trace(0, message, (Throwable)null);
/*  32 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public static void error(Object message) {
/*  41 */       if (mTracelevel >= 1) {
/*     */       }
/*  43 */    }
/*     */ 
///*  45 */          trace(1, message, (Throwable)null);
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
/*     */    public static void error(Throwable t) {
/*  59 */       if (mTracelevel >= 1) {
/*     */          }      // }
///*  61 */    }
/*     */ 
/*  63 */          if (t != null) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*  68 */             if (!(t instanceof XException)) {
/*     */ 
/*  70 */                trace(1, t.getMessage(), t);
/*     */ 
/*     */ 
/*     */             }
/*     */          } else {
/*  75 */             trace(1, "Exception <null> ???", (Throwable)null); } }
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
/*     */    public static void error(Object message, Throwable t) {
/*  88 */       if (mTracelevel >= 1) {
/*     */       }
/*  90 */    }
/*     */ 
///*  92 */          trace(1, message, t);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public static void warn(Object message) {
/* 102 */       if (mTracelevel >= 2) {
/*     */       }
/* 104 */    }
/*     */ 
///* 106 */          trace(2, message, (Throwable)null);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public static void info(Object message) {
/* 116 */       if (mTracelevel >= 3) {
/*     */       }
/* 118 */    }
/*     */ 
///* 120 */          trace(3, message, (Throwable)null);
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
///*     */    public static void initialize() {
///*     */       try {
///* 148 */          Configuration config = Configuration.getInstance();
///* 149 */          Integer traceLevel = new Integer(config.getValue("Base", "Trace", "Level"));
///* 150 */          mTracelevel = traceLevel;
///*     */ 
///* 152 */          String tracerShort = config.getValue("Base", "Trace", "Tracer");
///* 153 */          String tracerName = Configuration.getClass("Trace", tracerShort);
///*     */ 
///* 155 */          mTarget = (TraceTarget)Class.forName(tracerName).newInstance();
///* 156 */          System.out.println("Tracing with " + tracerName);
///*     */ 
///* 158 */       } catch (Exception var4) {
///*     */ 
///* 160 */          System.err.println(var4.getMessage());
///* 161 */          var4.printStackTrace();
///* 162 */          System.exit(1);
///*     */       }
///* 164 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
///*     */    public static String getTrace() {
///* 173 */       if (!isInitialized()) {
///*     */ 
///* 175 */          initialize();
///*     */       }
///* 177 */       String retString = mTarget.getTrace();
///* 178 */       return retString == null ? "No trace messages found." : retString;
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
/*     */    public static boolean isInitialized() {
/* 195 */       return mTracelevel != 9999;
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
/*     */    private static void trace(int priority, Object message, Throwable t) {
/* 207 */       if (mTracelevel == 9999) {
/*     */ 
///* 209 */          initialize();
/*     */       }
/* 211 */       if (mTracelevel >= priority) {
/*     */ 
/* 213 */          mTarget.trace(priority, message, t);
/*     */       }
/* 215 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public static int getTracelevel() {
/* 222 */       return mTracelevel;
/*     */    }
/*     */ }
