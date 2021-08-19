package model.consensus;


import model.ActionPeerEvent;
import model.Logger;
import model.Peer;
import model.PeerEvent;

import java.awt.event.ActionListener;
import java.util.*;

public class ConsensusImpl implements Consensus {
    private List<ConsensusParticipant> participantList;
    private final ConsensusParticipant leader;
    private final Peer selfPeer;
    private final List<ActionListener> listeners;

    public ConsensusImpl(ConsensusParticipant leader, Peer selfPeer, List<ConsensusParticipant> participantList) {
        this.leader = leader;
        this.participantList = participantList;
        this.selfPeer = selfPeer;
        this.listeners = new ArrayList<>();
    }

    @Override
    public void run() {
        while (true) {
            selfPeer.electLeader();
            int port = selfPeer.getLeader().getPort();
            if (port == leader.getPort()) {
                Logger.log("CONSENSUS: Beginning Consensus");

                this.updateParticipantList();
                for (ConsensusParticipant potentiallyUnresponsive : this.participantList) {
                    this.runConsensus(potentiallyUnresponsive, this.participantList);
                }
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateParticipantList() {
        //Logger.log("CONSENSUS: Updating participants list. Current " + this.participantList);
        List<ConsensusParticipant> consensusParticipants = new ArrayList<>();
        for (Peer peer : this.selfPeer.getPeers()) {
            consensusParticipants.add(new ConsensusParticipantImpl(peer.getHostOrIp(), peer.getPort()));
        }

        this.participantList = consensusParticipants;
        //Logger.log("CONSENSUS: Updating participants list. New : " + this.participantList);
    }


    private void runConsensus(ConsensusParticipant potentiallyUnresponsiveParticipant, List<ConsensusParticipant> allParticipants) {
        int voteCounterReachable = 0;

        for (ConsensusParticipant friend : allParticipants) {
            if (!friend.equals(potentiallyUnresponsiveParticipant)) {
                boolean vote = this.leader.requestPing(friend, potentiallyUnresponsiveParticipant);
                voteCounterReachable += vote ? 1 : 0;

//                Logger.log("CONSENSUS: Friend " + friend.getHostOrIp() + " " + friend.getPort() + " voted " + vote
//                        + " for " + potentiallyUnresponsiveParticipant.getHostOrIp() + " "
//                        + potentiallyUnresponsiveParticipant.getPort());
            }
        }

//        Logger.log("CONSENSUS: finished. Votes for " + potentiallyUnresponsiveParticipant.getHostOrIp() + " "
//                + potentiallyUnresponsiveParticipant.getPort() + " is reachable "
//                + voteCounterReachable);

        if (voteCounterReachable >= allParticipants.size() / 2) {
            this.onConsensusNodeReachable(potentiallyUnresponsiveParticipant);
        } else {
            this.onConsensusNodeUnreachable(potentiallyUnresponsiveParticipant);
        }
    }

    private void onConsensusNodeUnreachable(ConsensusParticipant unResponsiveParticipant) {
        for (ActionListener listener : this.listeners) {
            listener.actionPerformed(new ConsensusAction(this, unResponsiveParticipant.getHostOrIp(),
                    unResponsiveParticipant.getPort(), PeerEvent.ConsensusNodeUnreachable));
        }
    }

    private void onConsensusNodeReachable(ConsensusParticipant responsiveParticipant) {
        for (ActionListener listener : this.listeners) {
            listener.actionPerformed(new ConsensusAction(this, responsiveParticipant.getHostOrIp(),
                    responsiveParticipant.getPort(), PeerEvent.ConsensusNodeReachable));
        }
    }

    @Override
    public void addListener(ActionListener listener) {
        this.listeners.add(listener);
    }

    public class ConsensusAction extends ActionPeerEvent {
        private String hostOrIp;
        private int port;

        public ConsensusAction(Object source, String hostOrIp, int port, PeerEvent peerEvent) {
            super(source, 1, peerEvent);
            this.hostOrIp = hostOrIp;
            this.port = port;
        }

        public String getHostOrIp() {
            return hostOrIp;
        }

        public int getPort() {
            return port;
        }
    }
}
