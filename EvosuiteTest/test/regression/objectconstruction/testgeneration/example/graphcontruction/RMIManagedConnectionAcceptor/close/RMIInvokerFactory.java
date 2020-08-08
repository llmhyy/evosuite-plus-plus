package regression.objectconstruction.testgeneration.example.graphcontruction.RMIManagedConnectionAcceptor.close;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.Principal;

public interface RMIInvokerFactory extends Remote {
	RMIInvoker createInvoker(Principal paramPrincipal, RMIInvoker paramRMIInvoker, String paramString) throws RemoteException;
}
