package regression.objectconstruction.testgeneration.example.graphcontruction.RMIManagedConnectionAcceptor.close;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

final class RegistryHelper {
	private static final String BIND_SUFFIX = RMIInvokerFactory.class.getName();
	
	public static void unbind(RMIInvokerFactory factory, URI uri, Registry registry) throws ResourceException {
	    String name = getName(uri);
	    try {
	    	registry.unbind(name);
	    } catch (NotBoundException exception) {
	    	throw new ResourceException("No binding exists for " + name, exception);
	    } catch (RemoteException exception) {
	    	throw new ResourceException("Failed to unbind connection factory", exception);
	    } 
	}

	public static boolean hasBindings(Registry registry) throws RemoteException {
	    String[] names = registry.list();
	    return (names.length != 0);
	}
	
	public static String getName(URI uri) {
	    String path = uri.getPath();
	    if (path == null) {
	    	path = "/";
	    } else if (!path.startsWith("/")) {
	    	path = "/" + path;
	    } 
	    if (!path.endsWith("/"))
	    	path = path + "/"; 
	    return path + BIND_SUFFIX;
	}
}
