package overlay;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

/**
 * Broadcast listener for receiving messages from a shortwave radio
 */
public class BroadcastListener implements Runnable
{
    private int x;
    private int y;
    private MulticastSocket s;
    private HashMap<UUID, Integer> seqNumMap = new HashMap<>();
    private HashMap<UUID, Integer> idPortMap;
    private UUID uID;
    private int localPort;
    private double multicastDist = 300;

    /**
     * Constructs a new Broadcast listener
     * @param x x coordinate of robot
     * @param y y coordinate of robot
     * @param mulitcastPortNum multicast port number to listen on
     * @param localPortNum local port number to use to send packets
     * @param subnet subnet to join for multicast messages
     * @param localUUID ID of this object
     * @param idPortMap map of id:port for neighbor objects
     * @throws IOException Java IO/socket exceptions
     */
    public BroadcastListener(int x, int y, int mulitcastPortNum, int localPortNum, InetAddress subnet, UUID localUUID, HashMap<UUID, Integer> idPortMap) throws IOException
    {
        this.x = x;
        this.y = y;
        this.s = new MulticastSocket(mulitcastPortNum);
        this.s.joinGroup(subnet);
        this.uID = localUUID;
        this.localPort = localPortNum;
        this.idPortMap = idPortMap;
    }

    /**
     * Checks the distance from this robot to a given point
     * @param x x coord to check distance to
     * @param y y coord to check distance to
     * @return true if we are within multicast range
     */
    public boolean checkDist(int x, int y)
    {
        int calcX = Math.abs(this.x - x);
        int calcY = Math.abs(this.y - y);
        double dist = Math.sqrt((calcX * calcX) + (calcY * calcY));
        System.out.println(String.format("(%d,%d) to (%d,%d) is %.2f", this.x, this.y, x, y, dist));
        if (dist < this.multicastDist)
        {
            return true;
        }
        return false;
    }

    /**
     * Runs a receiver to receive pings from other robots
     */
    public void run()
    {
        while (true)
        {

            byte[] buffer = new byte[1000];
            DatagramPacket recv = new DatagramPacket(buffer, buffer.length);
            try
            {
                this.s.receive(recv);
                String receivedString = new String(recv.getData(), 0, recv.getLength());
                String[] splitString = receivedString.split(" ");
                UUID receivedUUID = UUID.fromString(splitString[0]);
                Integer receivedSeqNum = Integer.parseInt(splitString[1]);
                Integer receivedX = Integer.parseInt(splitString[3]);
                Integer receivedY = Integer.parseInt(splitString[4]);

                if (!receivedSeqNum.equals(this.seqNumMap.get(receivedUUID)) & checkDist(receivedX, receivedY))
                {
                    System.out.println("Received ping from: " + receivedUUID);
                    this.seqNumMap.put(receivedUUID, receivedSeqNum);
                    pong(recv.getAddress(), recv.getPort());
                } else if (!checkDist(receivedX, receivedY)) {
                    System.out.println(receivedUUID + "is too far away.");
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }


        }
    }

    /**
     * Replies to pings from other robots
     * @param responseAddress address to respond to
     * @param portNumber port number to respond to
     * @throws IOException Java socket/io exceptions
     */
    public void pong(InetAddress responseAddress, int portNumber) throws IOException
    {
        String responseString = this.uID.toString() + " " + this.localPort;
        byte[] responseBytes = responseString.getBytes();
        DatagramPacket responseMessage = new DatagramPacket(responseBytes, responseBytes.length, responseAddress, portNumber);
        this.s.send(responseMessage);
    }

    /**
     * Update this objects coordinates
     * @param x x coord to move to
     * @param y y coord to move to
     */
    public void updateCoords(int x, int y)
    {
        this.x = x;
        this.y = y;
        System.out.println("Updated coords: " + this.x + "," + this.y);
    }

    /**
     * Main function
     * @param args Program arguments
     * @throws IOException Java socket/io exception
     */
    public static void main(String[] args) throws IOException
    {
        InetAddress group = InetAddress.getByName("228.5.6.7");
        UUID localID = UUID.randomUUID();
        Scanner myScanner = new Scanner(System.in);
        System.out.println("What port to listen on?");
        HashMap<UUID, Integer> idPortMap = new HashMap<>();
        int localPort = Integer.parseInt(myScanner.nextLine());
        BroadcastListener myListener = new BroadcastListener(0, 0, 5000, localPort, group, localID, idPortMap);
        new Thread(myListener).start();
    }
}
