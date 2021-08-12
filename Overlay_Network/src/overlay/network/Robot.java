package overlay.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

public class Robot implements Runnable
{
    private InetAddress multiCastSubnet;
    private int localPort;
    private int multiCastPort;
    private DatagramSocket localSocket;
    private UUID uID;
    private HashMap<UUID, Integer> idPortMap;
    private int x;
    private int y;
    private BroadcastListener multiCastListener;

    public Robot(int localPort, UUID identify, int xCoords, int yCoords) throws IOException
    {
        super();
        this.uID = identify;
        this.localPort = localPort;
        this.localSocket = new DatagramSocket(this.localPort);
        this.multiCastPort = 5000;
        this.multiCastSubnet = InetAddress.getByName("228.5.6.7");
        this.idPortMap = new HashMap<>();
        this.x = xCoords;
        this.y = yCoords;
        this.multiCastListener = new BroadcastListener(this.x, this.y, this.multiCastPort, this.localPort, this.multiCastSubnet, this.uID, this.idPortMap);
        new Thread(this.multiCastListener).start();
    }

    public void ping() throws IOException
    {
        Date date = new Date();
        int seqNum = (int) date.getTime();
        String pingMessage = this.uID + " " + seqNum + " ping " + this.x + " " + this.y;


        DatagramPacket message = new DatagramPacket(pingMessage.getBytes(), pingMessage.length(), this.multiCastSubnet, this.multiCastPort);
        this.localSocket.send(message);

        // get responses
        // use Arraylist to keep track of UUID messages that come in
        // use retain all such that the messages can send to are the ones
        // received ping from
        ArrayList<UUID> uuidHolder = new ArrayList<>();
        while (true)
        {
            try
            {
                byte[] buf = new byte[1000];
                DatagramPacket recv = new DatagramPacket(buf, buf.length);
                this.localSocket.receive(recv);
                this.localSocket.setSoTimeout(1000); // timeout set to 500ms
                String recvData = new String(recv.getData(), 0, recv.getLength());
                String[] splitRecvData = recvData.split(" ");
                UUID receivedUUID = UUID.fromString(splitRecvData[0]);
                int receivedPort = Integer.parseInt(splitRecvData[1]);
                this.idPortMap.put(receivedUUID, receivedPort);
                uuidHolder.add(receivedUUID);
            }
            catch (Exception e)
            {
                // Update idPortMap to have keys can send to.
                this.idPortMap.keySet().retainAll(uuidHolder);
                System.out.println(this.idPortMap);
                break;
            }
        }
    }

    public HashMap<UUID, Integer> getIdPortMap()
    {
        return this.idPortMap;
    }

    public void setCoords(int x, int y)
    {
        this.x = x;
        this.y = y;
        this.multiCastListener.updateCoords(this.x, this.y);
    }

    public void run()
    {
        while (true)
        {
            try
            {
                ping();
                Thread.sleep(3000); // cast ping every 3 seconds to update
            }
            catch (IOException | InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException
    {
        UUID generatedUUID = UUID.randomUUID();
        System.out.println("local UUID: " + generatedUUID);
        Scanner myScanner = new Scanner(System.in);
        System.out.println("What local port to listen for UDP on?");
        int listen_port = Integer.parseInt(myScanner.nextLine());
        System.out.println("X coord?");
        int inputX = Integer.parseInt(myScanner.nextLine());
        System.out.println("Y coord?");
        int inputY = Integer.parseInt(myScanner.nextLine());
        Robot myRobot = new Robot(listen_port, generatedUUID, inputX, inputY);
        new Thread(myRobot).start();


//            myScanner.nextLine();
//            myRobot.ping();
//            System.out.println("printing local hashmap");
//            System.out.println(myRobot.idPortMap);
    }
}
