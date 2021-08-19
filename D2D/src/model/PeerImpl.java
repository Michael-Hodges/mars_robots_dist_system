package model;

import controller.*;
import controller.tcp.TCPServer;
import model.bully.BullyAlgorithmParticipant;
import model.bully.BullyAlgorithmParticipantImpl;
import model.bully.BullyMessageListenerFactoryImpl;
import model.consensus.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PeerImpl implements Peer {

    public enum Operation {
        Register,
        ElectLeader,
        MulticastRegister,
        MulticastElectLeader,
    }

    ActionListener listener;
    String host;
    int port;
    List<Peer> peers;
    BullyAlgorithmParticipant selfBullyParticipant;
    MessageChannelFactory messageChannelFactory;
    MulticastSession multicastSession;
    ConsensusParticipant selfConsensusParticipant;

    public PeerImpl(String hostOrIP, int port, MessageChannelFactory messageChannelFactory) {

        this.host = hostOrIP;
        this.port = port;
        this.messageChannelFactory = messageChannelFactory;
        this.peers = new ArrayList<>();
        this.selfBullyParticipant = new BullyAlgorithmParticipantImpl("localhost", port, port,
                messageChannelFactory);
        this.multicastSession = new MulticastSession();
        this.selfConsensusParticipant = new ConsensusParticipantImpl(this.getHostOrIp(), this.getPort(), messageChannelFactory);
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

    private void startServer() {

        //implement it here - send to new interruptable thread
        //set this.serverThread to new thread
        sendEventToListener(PeerEvent.StartServer);

        BullyMessageListenerFactoryImpl bullyDelegate = new BullyMessageListenerFactoryImpl(this.selfBullyParticipant);
        bullyDelegate.setListener(listener);

        RouteStrategy routeStrategy = new RouteStrategyImpl();
        TCPChaosMessageRouteImpl chaosRouteStrategy = new TCPChaosMessageRouteImpl(routeStrategy);

        MessageRouterImpl messageRouter = new MessageRouterImpl(chaosRouteStrategy);
        messageRouter.registerRoute(chaosRouteStrategy.getRoute());

        TCPServer server = new TCPServer(this.port, messageRouter);

        server.register("peer", new PeerMessageListenerFactory());
        server.register("bully", bullyDelegate);
        server.register("consensus", new ConsensusListenerFactory(this.selfConsensusParticipant));
        try {
            registerWithPeers();
            server.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerWithPeers() {
        for(Peer p : peers) {
            sendRegisterRequestTo(p);
        }
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
        }
    }

    @Override
    public List<Peer> getPeers() {
        return this.peers;
    }

    @Override
    public void identifyUnresponsiveNodes() {
        Consensus consensus = new ConsensusImpl(selfConsensusParticipant, this.convertPeersToConsensusParticipants());
        // TODO: consensus.addListener(this);
        new Thread(consensus).start();
    }

    private List<ConsensusParticipant> convertPeersToConsensusParticipants() {
        List<ConsensusParticipant> consensusParticipants = new ArrayList<>();
        for (Peer peer : peers) {
            consensusParticipants.add(new ConsensusParticipantImpl(peer.getHostOrIp(), peer.getPort()));
        }

        return consensusParticipants;
    }

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


    private void stopServer() {
        //interrupt and stop this.serverThread
    }

    private void sendEventToListener(PeerEvent event) {
        ActionEvent ev = new ActionEvent(this, 1, event.toString());
        listener.actionPerformed(ev);
    }


    // get and return the TCP socket
    private Socket connect(String hostOrIP, int port) {
        return null;
    }

    public void start() {
        startServer();
    }


    @Override
    public String toString() {
        return this.getHostAndPort();
    }

    class PeerMessageListenerFactory implements MessageListenerFactory {

        @Override
        public ActionListener getMessageListener() {
            return new PeerProcess();
        }
    }

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
                default:
                    break;
            }
        }

        private void onRegister(MessageChannel channel) {
            try {
                String hostOrIp = channel.readNextString();
                int port = channel.readNextInt();
                Peer p = new PeerImpl(hostOrIp, port, messageChannelFactory);
                PeerImpl.this.add(p);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void onElectLeader(MessageChannel channel) {
            PeerImpl.this.electLeader();
            channel.close();
        }

        // on multicast takes channel
        // turn it into an action event
        // store id's in list
        // peer multicastelectleader/multicastregister 234423 register/electleader data1 data2
        //

        private void onMulticastElectLeader(MessageChannel channel){
            try {
                int id = channel.readNextInt();
                if(PeerImpl.this.multicastSession.isIDUsed(id)){
                    channel.close();
                    return;
                }
                PeerImpl.this.multicastSession.addUsedId(id);
                Operation m = Operation.valueOf(channel.readNextString());
                PeerImpl.this.electLeader();
                channel.close();
                for (Peer p : PeerImpl.this.peers){
                    MessageChannel newChannel = getMessageChannel(p);
                    newChannel.writeString("peer");
                    newChannel.writeString(Operation.MulticastElectLeader.name());
                    newChannel.writeInt(id);
                    newChannel.writeString(m.name());
                    newChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // onmulticastelectleader


        // onmulticastregister
        private void onMulticastRegister(MessageChannel channel){
            try {
                int id = channel.readNextInt();
                if(PeerImpl.this.multicastSession.isIDUsed(id)){
                    channel.close();
                    return;
                }
                PeerImpl.this.multicastSession.addUsedId(id);
                Operation m = Operation.valueOf(channel.readNextString());
                String hostOrIp = channel.readNextString();
                int port = channel.readNextInt();
                Peer p = new PeerImpl(hostOrIp, port, messageChannelFactory);
                PeerImpl.this.add(p);
                for (Peer peer: PeerImpl.this.peers) {
                    MessageChannel newChannel = getMessageChannel(peer);
                    newChannel.writeString("peer");
                    newChannel.writeString(Operation.MulticastElectLeader.name());
                    newChannel.writeInt(id);
                    newChannel.writeString(m.name());
                    newChannel.writeString(hostOrIp);
                    newChannel.writeInt(port);
                    newChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //

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


    class MulticastSession {
        private int id = 0;
        Set<Integer> usedIds = new HashSet<>();

        // Since each node will have its own id counter, check to make sure current id is
        // not in the usedId set
        public synchronized int getNextId() {
            while(this.usedIds.contains(this.id)) {
                this.id++;
            }
            return this.id;
        }

        public synchronized void addUsedId(int id) {
            this.usedIds.add(id);
        }

        public synchronized boolean isIDUsed(int id){
            return usedIds.contains(id);
        }

    }
}
