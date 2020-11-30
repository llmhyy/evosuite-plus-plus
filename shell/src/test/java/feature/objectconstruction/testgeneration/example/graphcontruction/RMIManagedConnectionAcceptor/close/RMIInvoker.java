package feature.objectconstruction.testgeneration.example.graphcontruction.RMIManagedConnectionAcceptor.close;

import java.rmi.MarshalledObject;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIInvoker extends Remote {
	MarshalledObject invoke(MarshalledObject paramMarshalledObject) throws RemoteException;
  
  	void ping() throws RemoteException;
  
  	void disconnect() throws RemoteException;
}