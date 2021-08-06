import java.rmi.RemoteException;

public class Program {

    public static void main(String[] args) {
        if (args[0].equals("client")) {
            Client client = new Client();
            try {
                client.start();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (args[0].equals("server")) {
            Server server = new Server();
            server.start();
        }

    }
}
