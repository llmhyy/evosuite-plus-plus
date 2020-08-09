package regression.objectconstruction.testgeneration.example.graphcontruction.RMIManagedConnectionAcceptor.close;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RMIManagedConnectionAcceptor {
	private URI _uri;
	private Registry _registry;
	private boolean _created = false;
	private RMIInvokerFactory _factory;
	private static final Log _log = LogFactory.getLog(RMIManagedConnectionAcceptor.class);

	public synchronized void close() throws ResourceException {
		if (this._registry != null)
			try {
				RegistryHelper.unbind(this._factory, this._uri, this._registry);
				if (!UnicastRemoteObject.unexportObject(this._factory, true))
					_log.warn("Failed to unexport invoker factory");
				if (this._created && !RegistryHelper.hasBindings(this._registry))
					if (!UnicastRemoteObject.unexportObject(this._registry, true))
						_log.warn("Failed to unexport registry");
			} catch (RemoteException exception) {
				throw new ResourceException("Failed to close connection acceptor", exception);
			} finally {
				this._factory = null;
				this._registry = null;
			}
	}
}
