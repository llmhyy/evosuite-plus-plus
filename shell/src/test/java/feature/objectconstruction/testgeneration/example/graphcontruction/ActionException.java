package feature.objectconstruction.testgeneration.example.graphcontruction;

public class ActionException extends JwbfException {
  private static final long serialVersionUID = 1L;
  
  public ActionException(String message, Throwable t) {
    super(message, t);
  }
  
  public ActionException(String message) {
    super(message);
  }
  
  public ActionException(Throwable t) {
    super(t);
  }
}
