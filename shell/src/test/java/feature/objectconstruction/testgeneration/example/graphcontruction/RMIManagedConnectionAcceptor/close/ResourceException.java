package feature.objectconstruction.testgeneration.example.graphcontruction.RMIManagedConnectionAcceptor.close;

public class ResourceException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private Throwable _target;
	
	public ResourceException(String detail) {
	    super(detail);
	}
	
	public ResourceException(String detail, Exception target) {
	    super(detail);
	    this._target = target;
	}	
}
