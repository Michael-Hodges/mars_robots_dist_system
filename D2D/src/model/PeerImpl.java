package model;

import controller.*;
import controller.tcp.TCPServer;
import model.bully.BullyActionEvent;
import model.bully.BullyAlgorithmParticipant;
import model.bully.BullyAlgorithmParticipantImpl;
import model.bully.BullyMessageListenerFactoryImpl;
import overlay.ShortwaveRadio;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.util.*;


/**
 * The main implementation class for peers, the entities which communicate with each other to
 * accomplish leader election and consensus.
 */
public class PeerImpl implements Peer, ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        ActionPeerEvent event = (ActionPeerEvent) e;

        switch(event.eventType) {
            case ShortwaveRadioPing:
                discoverLocalGroup();
                break;
            case BullyReceiveElectionMessage:
            case BullyReceiveVictory:
            case BullyReceiveAnswer:
                onBullyResponses((BullyActionEvent)event);
                break;
        }
    }

    /**
     * The 4 types of operations which the peer will execute.
     */
    public enum Operation {
        Register,
        ElectLeader,
        UpdatePosition,
        IdentifyLocalGroup,
        MulticastRegister,
        MulticastElectLeader,
        MulticastUpdatePosition,
        InitiateMulticastUpdatePosition
    }

    /**
     * Various statuses that a peer can have
     */
    public enum Status {
        Up,
        Down,
        Leader,
        Unknown
    }

    ActionListener listener;
    String host;
    int port;
    List<Peer> peers;
    BullyAlgorithmParticipant selfBullyParticipant;
    MessageChannelFactory messageChannelFactory;
    MulticastSession multicastSession;
    ShortwaveRadio shortwaveRadio;
    Status status;
    UUID identity;
    int positionX;
    int positionY;


    /**
     * Constructor for a PeerImpl, which takes in the ip and port to listen on, as well as a
     * message channel factory to use to produce MessageChannels to communicate with.
     * @param hostOrIP the host name or ip address to listen on
     * @param port the port to listen on
     * @param messageChannelFactory factory used to produce message channels
     */
    public PeerImpl(String hostOrIP, int port, MessageChannelFactory messageChannelFactory) {

        this.host = hostOrIP;
        this.port = port;
        this.messageChannelFactory = messageChannelFactory;
        this.peers = new ArrayList<>();
        this.selfBullyParticipant = new BullyAlgorithmParticipantImpl("localhost", port, port,
                messageChannelFactory);
        this.multicastSession = new MulticastSession();
        this.status = Status.Unknown;
        this.identity = UUID.randomUUID();
        this.shortwaveRadio = null;
    }

    /**
     * Returns a new shortwave radio for the peer to use
     * @return new Shortwaveradio object
     */
    private ShortwaveRadio getShortwaveRadio() {
        try {
            return new ShortwaveRadio(convertToShortwavePort(port), this.identity, 100, 100);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Update the position of this peer
     * @param x x coordinate to move to
     * @param y y coordinate to move to
     */
    private void updatePosition(int x, int y) {
        this.positionX = x;
        this.positionY = y;
        this.shortwaveRadio.setCoords(x, y);
        for (Peer p : peers) {
            p.setStatus(Status.Unknown);
        }
        sendEventToListener(PeerEvent.PeerStatusUpdated);
    }

    /**
     * Converts this peers port number to a usable shortwave radio port number
     * @param port port number to convert
     * @return new port number
     */
    private int convertToShortwavePort(int port) {
        return port + 1000;
    }

    /**
     * Converts the shortwave radio port back to a peer port
     * @param port port to convert
     * @return new poert number
     */
    private int convertToPeerPort(int port) {
        return port - 1000;
    }

    @Override
    public String getHostAndPort() {
        return host + ":" + port;
    }

    @Override
    public String getHostOrIp() {
        return this.host;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public void setListener(ActionListener listener) {
        this.listener = listener;
    }

    /**
     * Starts TCPServer instance, to listen to incoming messages.
     */
    private void startServer() {

        //implement it here - send to new interruptable thread
        //set this.serverThread to new thread
        sendEventToListener(PeerEvent.StartServer);

        this.selfBullyParticipant.addListener(this);

        BullyMessageListenerFactoryImpl bullyDelegate = new BullyMessageListenerFactoryImpl(this.selfBullyParticipant);

        RouteStrategy routeStrategy = new RouteStrategyImpl();
        TCPChaosMessageRouteImpl chaosRouteStrategy = new TCPChaosMessageRouteImpl(routeStrategy);

        MessageRouterImpl messageRouter = new MessageRouterImpl(chaosRouteStrategy);
        messageRouter.registerRoute(chaosRouteStrategy.getRoute());

        TCPServer server = new TCPServer(this.port, messageRouter);

        server.register("peer", new PeerMessageListenerFactory());
        server.register("bully", bullyDelegate);
        try {
            registerWithPeers();
            this.status = Status.Up;
            startListeningOnShortwaveRadio();
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
            this.status = Status.Down;
        }
    }

    /**
     * Set up short wave radio to listen on separate thread
     */
    void startListeningOnShortwaveRadio() {
        this.shortwaveRadio = getShortwaveRadio();
        this.shortwaveRadio.setListener(this);
        Thread t = new Thread(this.shortwaveRadio);
        t.start();
    }
    /**
     * Registers this peer with its neighbors
     */
    private void registerWithPeers() {
        for(Peer p : peers) {
            sendRegisterRequestTo(p);
        }
    }

    /**
     * When a bully response is received, update our view of peer status
     * @param event response to bully algorithm received
     */
    void onBullyResponses(BullyActionEvent event) {
        BullyAlgorithmParticipant respondent = event.getRespondent();
        BullyAlgorithmParticipantImpl.Status respondentStatus = event.getRespondentStatus();
        Peer p = locatePeer(respondent.getHostOrIp(), respondent.getPort());
        if (p != null) {
            switch(respondentStatus) {
                case Leader:
                    clearPreviousLeader();
                    updatePeerStatus(p, Status.Leader);
                    break;
                default:
                    updatePeerStatus(p, Status.Up);
                    break;
            }
        }
    }

    /**
     * Update the status of a given peer
     * @param p peers status to update
     * @param status status to send to peer
     */
    void updatePeerStatus(Peer p, Status status) {
        p.setStatus(status);
        sendEventToListener(PeerEvent.PeerStatusUpdated);
    }

    /**
     * Removes the previous leader of the bully election
     */
    void clearPreviousLeader() {
        if (this.status == Status.Leader) {
            this.status = Status.Up;
        }

        for(Peer p : peers) {
            if (p.getStatus() == Status.Leader) {
                p.setStatus(Status.Unknown);
            }
        }
    }

    /**
     * Locates a peer given a host/ip and port
     * @param hostOrIp host/ip to find a peer for
     * @param port port number to find the peer for
     * @return the peer with given host/ip and port
     */
    Peer locatePeer(String hostOrIp, int port) {
        if (hostOrIp.equals(hostOrIp) && this.port == port) {
            return this;
        }

        for (Peer p : peers) {
            if (p.getHostOrIp().equals(hostOrIp) && p.getPort() == port)
                return p;
        }
        return null;
    }

    @Override
    public void sendRegisterRequestTo(Peer peer) {
        sendEventToListener(PeerEvent.Register);
        try {
            MessageChannel channel = this.messageChannelFactory.getChannel(peer.getHostOrIp(), peer.getPort());
            channel.writeString("peer");
            channel.writeString(Operation.Register.name());
            channel.writeString(this.getHostOrIp());
            channel.writeInt(this.getPort());
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void discoverLocalGroup() {

        for(Peer p : peers) {
            p.setStatus(Status.Unknown);
        }

        Collection<Integer> ports = this.shortwaveRadio.getIdPortMap().values();

        for(Integer port : ports) {
            port = convertToPeerPort(port);
            Peer p = locatePeer("localhost", port);
            if (p != null) {
                p.setStatus(Status.Up);
            }
        }
        sendEventToListener(PeerEvent.PeerStatusUpdated);
    }

    @Override
    public void electLeader() {
        this.selfBullyParticipant.startElection();
    }

    @Override
    public Peer getLeader() {
        BullyAlgorithmParticipant p = this.selfBullyParticipant.getCoordinator();
        if (p == null) {
            electLeader();
            p = this.selfBullyParticipant.getCoordinator();
        }

        return new PeerImpl(p.getHostOrIp(), p.getPort(),this.messageChannelFactory);
    }

    @Override
    public synchronized void add(Peer peer) {
        if (!peer.equals(this) && !peers.contains(peer)) {
            Logger.log("Added Peer: " + peer);
            peers.add(peer);
            addAsBullyParticipant(peer);
            sendEventToListener(PeerEvent.PeerAdded);
        }
    }

    @Override
    public List<Peer> getPeers() {
        return this.peers;
    }

    @Override
    public Status getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(PeerImpl.Status status) {
        this.status = status;
        sendEventToListener(PeerEvent.PeerStatusUpdated);
    }

    /**
     * Wraps a given peer as a bully participant
     * @param peer peer to express as a bully participant
     */
    void addAsBullyParticipant(Peer peer) {
        BullyAlgorithmParticipant p = new BullyAlgorithmParticipantImpl(peer.getHostOrIp(),
                peer.getPort(), peer.getPort(), this.messageChannelFactory);
        selfBullyParticipant.add(p);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Peer)) {
            return false;
        }

        Peer p = (Peer)obj;

        return this.getHostAndPort().equals(p.getHostAndPort());
    }


    /**
     * Stops the TCPServer running.
     */
    private void stopServer() {
        //interrupt and stop this.serverThread
    }

    /**
     * Sends a given event to the action listener
     * @param event event to send to listener
     */
    private void sendEventToListener(PeerEvent event) {
        ActionEvent ev = new ActionEvent(this, 1, event.toString());
        if (listener != null) {
            listener.actionPerformed(ev);
        }
    }


    // get and return the TCP socket
    private Socket connect(String hostOrIP, int port) {
        return null;
    }

    /**
     * Starts this peer's server.
     */
    public void start() {
        startServer();
    }


    @Override
    public String toString() {
        return this.getHostAndPort();
    }

    /**
     * Returns a new message listener for this peer to use with message channels
     */
    class PeerMessageListenerFactory implements MessageListenerFactory {

        @Override
        public ActionListener getMessageListener() {
            return new PeerProcess();
        }
    }

    /**
     * Handles actions for the peer, based on the messages the peer receives from the message
     * channels.
     */
    class PeerProcess implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            MessageEvent event = (MessageEvent)e;
            MessageChannel channel = event.getChannel();
            Operation m = Operation.valueOf(event.getActionCommand());
            switch(m) {
                case Register:
                    onRegister(channel);
                    break;
                case ElectLeader:
                    onElectLeader(channel);
                    break;
                case MulticastElectLeader:
                    onMulticastElectLeader(channel);
                    break;
                case MulticastRegister:
                    onMulticastRegister(channel);
                    break;
                case IdentifyLocalGroup:
                    onDiscoverLocalGroup(channel);
                    break;
                case UpdatePosition:
                    onUpdatePosition(channel);
                    break;
                case MulticastUpdatePosition:
                    onMulticastUpdatePosition(channel);
                    break;
                case InitiateMulticastUpdatePosition:
                    onInitiateMulticastUpdatePosition(channel);
                    break;
                default:
                    break;
            }
        }

        /**
         * When a register event is called, add the requesting peer to the current peer's list
         * @param channel message channel to listen on
         */
        private void onRegister(MessageChannel channel) {
            try {
                String hostOrIp = channel.readNextString();
                int port = channel.readNextInt();
                Peer p = new PeerImpl(hostOrIp, port, messageChannelFactory);
                PeerImpl.this.add(p);
                PeerImpl.this.updatePeerStatus(p, Status.Up);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * When an election is happening, trigger this peer to take part in the election.
         * @param channel channel to listen on
         */
        private void onElectLeader(MessageChannel channel) {
            PeerImpl.this.electLeader();
            channel.close();
        }

        /**
         * Discovers the local group for this peer
         * @param channel channel command came in on
         */
        private void onDiscoverLocalGroup(MessageChannel channel) {
            PeerImpl.this.discoverLocalGroup();
            channel.close();
        }

        /**
         * Updates position of this peer
         * @param channel channel to receive message from
         */
        private void onUpdatePosition(MessageChannel channel) {
            try {
                int x = channel.readNextInt();
                int y = channel.readNextInt();
                channel.close();
                PeerImpl.this.updatePosition(x, y);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * On multicast update position, updates position, then tells all other peers to do the
         * same (reliable multicast)
         * @param channel channel to receive commands on
         */
        private void onMulticastUpdatePosition(MessageChannel channel) {
            try {
                int id = channel.readNextInt();
                if(PeerImpl.this.multicastSession.isIDUsed(id)) {
                    channel.close();
                    return;
                }
                PeerImpl.this.multicastSession.addUsedId(id);
                int x = channel.readNextInt();
                int y = channel.readNextInt();
                channel.close();
                PeerImpl.this.updatePosition(x, y);
                for (Peer p : PeerImpl.this.peers){
                    MessageChannel newChannel = getMessageChannel(p);
                    newChannel.writeString("peer");
                    newChannel.writeString(Operation.MulticastUpdatePosition.name());
                    newChannel.writeInt(id);
                    newChannel.writeInt(x);
                    newChannel.writeInt(y);
                    newChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * On initiate multicast update position, updates position, then tells all other peers
         * (including itself) to do the same (reliable multicast)
         * @param channel channel to receive commands on
         */
        private void onInitiateMulticastUpdatePosition(MessageChannel channel) {
            try {
                int x = channel.readNextInt();
                int y = channel.readNextInt();
                channel.close();
                int id = PeerImpl.this.multicastSession.getNextId();
                MessageChannel newChannel = getMessageChannel(PeerImpl.this);
                newChannel.writeString("peer");
                newChannel.writeString(Operation.MulticastUpdatePosition.name());
                newChannel.writeInt(id);
                newChannel.writeInt(x);
                newChannel.writeInt(y);
                newChannel.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // on multicast takes channel
        // turn it into an action event
        // store id's in list
        // peer multicastelectleader/multicastregister 234423 data1 data2
        //

        /**
         * Multicast implementation of onElectLeader, once this peer votes, rebroadcast election
         * request to peers to ensure reception by all peers
         * @param channel channel to listen on
         */
        private void onMulticastElectLeader(MessageChannel channel){
            try {
                int id = channel.readNextInt();
                if(PeerImpl.this.multicastSession.isIDUsed(id)){
                    channel.close();
                    return;
                }
                PeerImpl.this.multicastSession.addUsedId(id);
                PeerImpl.this.electLeader();
                channel.close();
                for (Peer p : PeerImpl.this.peers){
                    MessageChannel newChannel = getMessageChannel(p);
                    newChannel.writeString("peer");
                    newChannel.writeString(Operation.MulticastElectLeader.name());
                    newChannel.writeInt(id);
                    newChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        /**
         * Reliable multicast implementation of onRegister, registers a new node with the current
         * peer, then rebroadcasts the request to all known peers, to ensure reception by all
         * neighbors
         * @param channel channel to receive commands on
         */
        private void onMulticastRegister(MessageChannel channel){
            try {
                int id = channel.readNextInt();
                if(PeerImpl.this.multicastSession.isIDUsed(id)){
                    channel.close();
                    return;
                }
                PeerImpl.this.multicastSession.addUsedId(id);
                String hostOrIp = channel.readNextString();
                int port = channel.readNextInt();
                Peer p = new PeerImpl(hostOrIp, port, messageChannelFactory);
                PeerImpl.this.add(p);
                for (Peer peer: PeerImpl.this.peers) {
                    MessageChannel newChannel = getMessageChannel(peer);
                    newChannel.writeString("peer");
                    newChannel.writeString(Operation.MulticastElectLeader.name());
                    newChannel.writeInt(id);
                    newChannel.writeString(hostOrIp);
                    newChannel.writeInt(port);
                    newChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //

        /**
         * Returns new message channel directed to a given peer
         * @param peer peer to connect to with new channel
         * @return new channel to given peer
         */
        private MessageChannel getMessageChannel(Peer peer){
            try {
                return PeerImpl.this.messageChannelFactory.getChannel(peer.getHostOrIp(),
                        peer.getPort());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }


    /**
     * Class to ensure that a Peer can track IDs of received multicast messages.
     */
    class MulticastSession {
        private int id = 0;
        Set<Integer> usedIds = new HashSet<>();

        // Since each node will have its own id counter, check to make sure current id is
        // not in the usedId set

        /**
         * Gets the next unused id
         * @return new id for multicast
         */
        public synchronized int getNextId() {
            while(this.usedIds.contains(this.id)) {
                this.id++;
            }
            return this.id;
        }

        /**
         * Adds a seen id to the list
         * @param id id that has been used
         */
        public synchronized void addUsedId(int id) {
            this.usedIds.add(id);
        }

        /**
         * Check if an id has been seen yet
         * @param id id to check
         * @return true if the id has already been used
         */
        public synchronized boolean isIDUsed(int id){
            return usedIds.contains(id);
        }

    }
}
