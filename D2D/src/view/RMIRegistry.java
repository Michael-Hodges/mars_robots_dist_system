package view;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * RMI registry for remote invocations
 */
public class RMIRegistry {

  /**
   * Register a remote object with the Registry
   * @param obj Remote object to register
   * @param remoteObjectName name of remote object to register
   */
  public static void register(Remote obj, String remoteObjectName) {
    try {
      log(String.format("Registering %s in RMI.", remoteObjectName));
      Remote stub = UnicastRemoteObject.exportObject(obj, 0);
      Registry registry = LocateRegistry.getRegistry();
      registry.bind(remoteObjectName, stub);
    } catch (RemoteException exception) {
      exception.printStackTrace();
    } catch (AlreadyBoundException e) {
      e.printStackTrace();
    }
  }

  /**
   * Retrieve a remote object by the host and name
   * @param hostOrIP host of object to retrieve
   * @param remoteObjectName name of object to find
   * @return the remote object at the host with the given name
   */
  public static Remote retrieve(String hostOrIP, String remoteObjectName) {
    Remote obj = null;
    try {
      log(String.format("Locating %s in RMI at %s.", remoteObjectName, hostOrIP));
      Registry r = LocateRegistry.getRegistry(hostOrIP);
      obj = r.lookup(remoteObjectName);
    } catch (AccessException e) {
      e.printStackTrace();
    } catch (RemoteException exception) {
      exception.printStackTrace();
    } catch (NotBoundException e) {
      e.printStackTrace();
    }
    return obj;
  }

  /**
   * Log a string to the console
   * @param msg string to log to the console
   */
  private static void log(String msg) {
    Logger.log(msg);
  }


}
