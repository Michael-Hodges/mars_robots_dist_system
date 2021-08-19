package model.consensus;


import model.ActionPeerEvent;
import model.PeerEvent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class ConsensusImpl implements Consensus {
    private final List<ConsensusParticipant> participantList;
    private final ConsensusParticipant leader;
    private final List<ActionListener> listeners;

    public ConsensusImpl(ConsensusParticipant leader, List<ConsensusParticipant> participantList) {
        this.leader = leader;
        this.participantList = participantList;
        this.listeners = new ArrayList<>();
    }

    @Override
    public void run() {
        while (true) {
            try {
                for (ConsensusParticipant potentiallyUnresponsive : this.participantList) {
                   this.runConsensus(potentiallyUnresponsive, this.participantList);
                }
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    private void runConsensus(ConsensusParticipant potentiallyUnresponsiveParticipant, List<ConsensusParticipant> allParticipants) {
        List<Boolean> responses =  Collections.synchronizedList(new ArrayList<>());
        List<ConsensusParticipant> otherParticipantList = new ArrayList<>(allParticipants);
        otherParticipantList.remove(potentiallyUnresponsiveParticipant);
        ListIterator<ConsensusParticipant> iterator  = otherParticipantList.listIterator();

        // until we reach majority of the votes
        while (responses.size() < otherParticipantList.size() / 2) {
            if (iterator.hasNext()) {
                ConsensusParticipant friend = iterator.next();

                if (!friend.equals(potentiallyUnresponsiveParticipant)) {
                    responses.add(this.leader.requestPing(friend, potentiallyUnresponsiveParticipant));
                }
            } else {
                break;
            }
        }

        long responsiveCount = responses.stream().filter(response -> response).count(); // counts how many True => responsive
        long unResponsiveCount = responses.stream().filter(response -> !response).count(); // counts how many False
        if(unResponsiveCount > responsiveCount) {
            this.participantList.remove(potentiallyUnresponsiveParticipant);
            this.onConsensusReachedPositive(potentiallyUnresponsiveParticipant);
        } else {
            this.onConsensusReachedNegative(potentiallyUnresponsiveParticipant);
        }
    }

    private void onConsensusReachedNegative(ConsensusParticipant unResponsiveParticipant) {
        for (ActionListener listener : this.listeners) {
            listener.actionPerformed(new ConsensusAction(this, unResponsiveParticipant.getHostOrIp(),
                    unResponsiveParticipant.getPort(), PeerEvent.ConsensusNodeUnreachable));
        }
    }

    private void onConsensusReachedPositive(ConsensusParticipant responsiveParticipant) {
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
