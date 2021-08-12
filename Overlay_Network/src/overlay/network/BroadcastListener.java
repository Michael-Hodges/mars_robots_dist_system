package overlay.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

public class BroadcastListener implements Runnable
{
    private int x;
    private int y;
    private MulticastSocket s;
    private HashMap<UUID, Integer> seqNumMap = new HashMap<>();
    private HashMap<UUID, Integer> idPortMap;
    private UUID uID;
    private int localPort;
    private double multicastDist = 25;


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

    public boolean checkDist(int x, int y)
    {
        int calcX = Math.abs(this.x - x);
        int calcY = Math.abs(this.y - y);
        if ((calcX*calcY) < this.multicastDist)
        {
            return true;
        }
        else return false;
    }

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
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }


        }
    }

    public void pong(InetAddress responseAddress, int portNumber) throws IOException
    {
        String responseString = this.uID.toString() + " " + this.localPort;
        byte[] responseBytes = responseString.getBytes();
        DatagramPacket responseMessage = new DatagramPacket(responseBytes, responseBytes.length, responseAddress, portNumber);
        this.s.send(responseMessage);
    }

    public void updateCoords(int x, int y)
    {
        this.x = x;
        this.y = y;
        System.out.println("Updated coords: " + this.x + this.y);
    }

    public static void main(String[] args) throws InterruptedException, IOException
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
