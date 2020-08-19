package regression.objectconstruction.testgeneration.example.graphcontruction;

public class ProcessException extends JwbfException {
  private static final long serialVersionUID = -3830701798846228121L;
  
  public ProcessException(String msg) {
    super(msg);
  }
  
  public ProcessException(String message, Throwable t) {
    super(message, t);
  }
  
  public ProcessException(Throwable t) {
    super(t);
  }
}
