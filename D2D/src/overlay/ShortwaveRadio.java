package overlay;

import model.ActionPeerEvent;
import model.PeerEvent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * Shortwave radio to communicate with other robots
 */
public class ShortwaveRadio implements Runnable
{
    private InetAddress multiCastSubnet;
    private int localPort;
    private int multiCastPort;
    private DatagramSocket localSocket;
    private UUID uID;
    private HashMap<UUID, Integer> idPortMap;
    private List<Integer> msgIds;
    private int x;
    private int y;
    //private BroadcastListener multiCastListener;
    private BroadcastListener multiCastListener;
    private ActionListener listener;

    /**
     * Constructs new shortwave radio and starts listener on a new thread
     * @param localPort local port for sending and receiving messages
     * @param identify UUID for this object
     * @param xCoords x coord to start at
     * @param yCoords y coord to start at
     * @throws IOException Java socket/io exceptions
     */
    public ShortwaveRadio(int localPort, UUID identify, int xCoords, int yCoords) throws IOException
    {
        super();
        this.uID = identify;
        this.localPort = localPort;
        this.localSocket = new DatagramSocket(this.localPort);
        this.multiCastPort = 6000;
        this.multiCastSubnet = InetAddress.getByName("228.5.6.7");
        this.idPortMap = new HashMap<>();
        this.x = xCoords;
        this.y = yCoords;
        this.multiCastListener = new BroadcastListener(this.x, this.y, this.multiCastPort, this.localPort, this.multiCastSubnet, this.uID, this.idPortMap);
        this.msgIds = new ArrayList<>();
        this.listener = null;
        new Thread(this.multiCastListener).start();
    }

    /**
     * Set the actionListener for this object
     * @param listener listener to use for this class
     */
    public void setListener(ActionListener listener) {
        this.listener = listener;
    }

    /**
     * Send out pings to find neighbors
     * @throws IOException java socket/io exceptions
     */
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
                if (splitRecvData[0].equals("multicast")){
                    runMulticast(splitRecvData);
                } else {
                    UUID receivedUUID = UUID.fromString(splitRecvData[0]);
                    int receivedPort = Integer.parseInt(splitRecvData[1]);
                    this.idPortMap.put(receivedUUID, receivedPort);
                    this.sendEventToListener(PeerEvent.ShortwaveRadioPing);
                    uuidHolder.add(receivedUUID);
                }
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

    /**
     * Multicasts a message to peers, as a part of reliable multicast
     * @param msg message to pass on
     * @throws IOException java socket/io exceptions
     */
    public void runMulticast(String[] msg) throws IOException {

        if (!msgIds.contains(Integer.parseInt(msg[2]))) {


            String multicastMessage = "multicast " + this.uID + " " + msg[2] + " " + msg[3];
            // number 2 is message id number, number 3 is the actual message

            for (Map.Entry<UUID, Integer> ent : idPortMap.entrySet()) {
                DatagramPacket message = new DatagramPacket(multicastMessage.getBytes(), multicastMessage.length(),
                        InetAddress.getByName("localhost"), ent.getValue());
                this.localSocket.send(message);
            }
            //don't need to get responses because we've already sent the message
        }
    }

    /**
     * Return the idPort map from this object
     * @return HashMap of UID:Port of known neighbors
     */
    public HashMap<UUID, Integer> getIdPortMap()
    {
        return this.idPortMap;
    }

    /**
     * Set the coordinates of this radio
     * @param x x coord
     * @param y y coord
     */
    public void setCoords(int x, int y)
    {
        this.x = x;
        this.y = y;
        this.multiCastListener.updateCoords(this.x, this.y);
    }

    /**
     * Send an event to the ActionListener
     * @param peerEvent event to send to the listener
     */
    private void sendEventToListener(PeerEvent peerEvent) {
        if (this.listener != null) {
            ActionPeerEvent event = new ActionPeerEvent(this, 1, peerEvent);
            this.listener.actionPerformed(event);
        }
    }

    /**
     * Send pings every 3 seconds
     */
    public void run()
    {
        int count = 0;
        while (true)
        {
            try
            {
                ping();
                Thread.sleep(3000); // cast ping every 3 seconds to update
                //can add code here to send out multicast messages we want
                //runMulticast(new String[]{"multicast", this.uID.toString(), String.valueOf(count), "this is a message"});
                count++;
            }
            catch (IOException | InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }


    /**
     * Main function
     * @param args program arguments
     * @throws IOException java socket/io exceptions
     */
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
        ShortwaveRadio myShortwaveRadio = new ShortwaveRadio(listen_port, generatedUUID, inputX, inputY);
        new Thread(myShortwaveRadio).start();


//            myScanner.nextLine();
//            myRobot.ping();
//            System.out.println("printing local hashmap");
//            System.out.println(myRobot.idPortMap);
    }
}
