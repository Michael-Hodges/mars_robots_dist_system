import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIRegistry {


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

  private static void log(String msg) {
    Logger.log(msg);
  }


}
