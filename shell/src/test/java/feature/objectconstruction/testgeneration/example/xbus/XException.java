package feature.objectconstruction.testgeneration.example.xbus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;

import javax.naming.NamingException;

import org.xml.sax.SAXParseException;

/*     */ public class XException extends Exception {
/*     */    private static Hashtable mExceptionInformation = new Hashtable();
/*     */    private String mMessageText = null;
/*     */ 
/*     */    public XException(String location, String layer, String Package, String number) {
/*  40 */       this.traceException(location, layer, Package, number, (List)null);
/*  41 */    }
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
/*     */    public XException(String location, String layer, String Package, String number, List params) {
/*  57 */       this.traceException(location, layer, Package, number, params);
/*  58 */    }
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
/*     */    public XException(String location, String layer, String Package, String number, Throwable t, List params) {
/*  75 */       super(t.getMessage());
/*     */ 
/*  77 */       this.traceException(location, layer, Package, number, t, params);
/*  78 */    }
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
/*     */    public XException(String location, String layer, String Package, String number, Throwable t) {
/*  94 */       super(t.getMessage());
/*     */ 
/*  96 */       this.traceException(location, layer, Package, number, t, (List)null);
/*  97 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public String getMessage() {
/* 104 */       return this.mMessageText;
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public static String getExceptionInformation() {
/* 112 */       return (String)mExceptionInformation.get(Thread.currentThread().getName());
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
/*     */    public static void setExceptionInformation(String message, Throwable t) {
/* 124 */       if (t == null) {
/*     */ 
/* 126 */          mExceptionInformation.put(Thread.currentThread().getName(), message);
/*     */ 
/*     */ 
/*     */ 
/*     */       } else {
/* 131 */          mExceptionInformation.put(Thread.currentThread().getName(), message + Constants.LINE_SEPERATOR + Constants.LINE_SEPERATOR + getStackTraceAsString(t));
/*     */ 
/*     */ 
/*     */ 
/*     */       }
/*     */ 
/* 137 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    public static void clearExceptionInformation() {
/* 144 */       mExceptionInformation = new Hashtable();
/* 145 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    private static String getStackTraceAsString(Throwable t) {
/* 154 */       String retString = null;
/*     */ 
/*     */ 
/*     */       try {
/* 158 */          ByteArrayOutputStream outStream = new ByteArrayOutputStream();
/* 159 */          PrintStream printStream = new PrintStream(outStream);
/* 160 */          t.printStackTrace(printStream);
/*     */ 
/* 162 */          retString = outStream.toString();
/* 163 */          printStream.close();
/* 164 */          outStream.close();
/*     */ 
/* 166 */       } catch (IOException var4) {
/*     */ 
/*     */ 
/*     */ 
/*     */          ;
/*     */       }
/*     */ 
/* 173 */       return retString;
/*     */    }
/*     */ 
/*     */ 
/*     */ 
/*     */    private void traceException(String Location, String Layer, String Package, String Number, List params) {
/* 179 */       String basename = "errors";
/* 180 */       String key = Location + "_" + Layer + "_" + Package + "_" + Number;
/*     */ 
/* 182 */       MessageHandler msg = MessageHandler.getInstance(basename);
/* 183 */       String messageText = msg.getMessage(key, params);
/*     */ 
/* 185 */       if (Trace.isInitialized()) {
/*     */ 
/* 187 */          Trace.error("Exception caught: " + key + " " + messageText, this);
/*     */ 
/*     */ 
/*     */       } else {
/* 191 */          System.out.println("Exception caught: " + key + " " + messageText);
/* 192 */          this.printStackTrace();
/*     */       }
/*     */ 
/* 195 */       messageText = key + " " + messageText;
/* 196 */       setExceptionInformation(messageText, this);
/* 197 */       this.mMessageText = messageText;
/* 198 */    }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */    private void traceException(String Location, String Layer, String Package, String Number, Throwable t, List params) {
/* 206 */       if (!this.getClass().equals(t.getClass())) {
/*     */ 
/* 208 */          String basename = "errors";
/* 209 */          String key = Location + "_" + Layer + "_" + Package + "_" + Number;
/*     */ 
/* 211 */          MessageHandler msg = MessageHandler.getInstance(basename);
/* 212 */          String messageText = msg.getMessageOptional(key, params);
/*     */ 
/* 214 */          if (messageText == null) {
/*     */ 
/* 216 */             messageText = t.getMessage();
/* 217 */             if (messageText == null && t.getCause() != null) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 223 */                messageText = t.getCause().getMessage();
/*     */             }
/* 225 */             if (messageText == null) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 231 */                messageText = t.getClass().getName();
/*     */             }
/*     */          }
/*     */ 
/* 235 */          if (Trace.isInitialized()) {
/*     */ 
/* 237 */             Trace.error("Exception caught: " + key + " " + messageText, t);
/* 238 */             this.traceAdditionalInfo(t);
/*     */ 
/*     */ 
/*     */          } else {
/* 242 */             System.out.println("Exception caught: " + key + " " + messageText);
/*     */ 
/* 244 */             t.printStackTrace();
/*     */          }
/*     */ 
/* 247 */          messageText = key + " " + messageText;
/* 248 */          setExceptionInformation(messageText, t);
/* 249 */          this.mMessageText = messageText;
/*     */ 
/*     */ 
/*     */       } else {
/* 253 */          this.mMessageText = t.getMessage();
/*     */       }
/* 255 */    }
/*     */ 
/*     */ 
/*     */    private void traceAdditionalInfo(Throwable t) {
/* 259 */       if (t instanceof NamingException) {
/*     */ 
/* 261 */          Trace.error("Resolved Name\t : " + ((NamingException)t).getResolvedName());
/*     */ 
/* 263 */          Trace.error("Resolved Object : " + ((NamingException)t).getResolvedObj());
/*     */ 
/* 265 */          Trace.error("Remaining Name  : " + ((NamingException)t).getRemainingName());
/*     */ 
/* 267 */          Trace.error("Explanation : " + ((NamingException)t).getExplanation());
/*     */ 
/*     */ 
/* 270 */       } else if (t instanceof SQLException) {
/*     */ 
/* 272 */          Trace.error("SQLState : " + ((SQLException)t).getSQLState());
/* 273 */          Trace.error("Errorcode: " + ((SQLException)t).getErrorCode());
/*     */ 
/* 275 */       } else if (t instanceof SAXParseException) {
/*     */ 
/* 277 */          Trace.error("Parsing error occurred at line " + ((SAXParseException)t).getLineNumber() + ", column " + ((SAXParseException)t).getColumnNumber());
/*     */ 
/*     */       }
/*     */ 
/* 281 */    }
/*     */ }
