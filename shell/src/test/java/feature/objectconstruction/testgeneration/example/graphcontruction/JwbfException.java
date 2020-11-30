package feature.objectconstruction.testgeneration.example.graphcontruction;

import java.io.PrintStream;
import java.io.PrintWriter;

public class JwbfException extends Exception {
  private static final long serialVersionUID = -2456904376052276104L;
  
  public JwbfException(String message) {
    super(message);
  }
  
  public JwbfException(Throwable t) {
    super(t);
  }
  
  public JwbfException(String message, Throwable t) {
    super(message, t);
  }
  
  public Class<?> getExceptionSrcClass() {
    return getStackTraceClass();
  }
  
  private Class<?> getStackTraceClass() {
    ClassLoader loader = getClass().getClassLoader();
    try {
      return loader.loadClass(getStackTrace()[0].getClassName());
    } catch (ClassNotFoundException e) {
      return Object.class;
    } 
  }
  
//  private String getModulInfo() {
//    Class<?> clazz = getStackTraceClass();
//    return "( " + JWBF.getPartId(clazz) + "-" + JWBF.getVersion(clazz) + " )";
//  }
  
//  public void printStackTrace(PrintWriter arg0) {
//    arg0.println(getModulInfo());
//    super.printStackTrace(arg0);
//  }
//  
//  public void printStackTrace(PrintStream s) {
//    s.println(getModulInfo());
//    super.printStackTrace(s);
//  }
}
