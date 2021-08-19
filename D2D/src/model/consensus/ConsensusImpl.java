package model.consensus;


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
                   this.start(potentiallyUnresponsive, this.participantList);
                }
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // ignore
            }
        }

    }

    @Override
    public void addListener(ActionListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public List<ConsensusParticipant> start(ConsensusParticipant potentiallyUnresponsiveParticipant, List<ConsensusParticipant> allParticipants) {
        // prior to running algorithm double check that the participant is indeed unresponsive
        if(this.isTimeOutOccurred(potentiallyUnresponsiveParticipant)) {
            return this.runConsensus(potentiallyUnresponsiveParticipant, allParticipants);
        } else {
            return allParticipants;
        }
    }

    private boolean isTimeOutOccurred(ConsensusParticipant p) {
//        Future<Boolean> future = CompletableFuture.supplyAsync(p::confirmSelfResponsiveness);
//        try {
//            future.get(5, TimeUnit.SECONDS);
//            // we got the response - the participant is not unresponsive
//            return false;
//        } catch (TimeoutException | InterruptedException | ExecutionException e) {
//            return  true;
//        }
        // TODO: ???
        return false;
    }

    private List<ConsensusParticipant> runConsensus(ConsensusParticipant potentiallyUnresponsiveParticipant, List<ConsensusParticipant> allParticipants) {
        List<Boolean> responses =  Collections.synchronizedList(new ArrayList<>());
        List<ConsensusParticipant> otherParticipantList = new ArrayList<>(allParticipants);
        otherParticipantList.remove(potentiallyUnresponsiveParticipant);
        ListIterator<ConsensusParticipant> iterator  = otherParticipantList.listIterator();

        // until we reach majority of the votes
        while (responses.size() < otherParticipantList.size() / 2) {
            if (iterator.hasNext()) {
                ConsensusParticipant friend = iterator.next();
                responses.add(this.leader.requestPing(friend, potentiallyUnresponsiveParticipant));
            } else {
                break;
            }
        }

        long responsiveCount = responses.stream().filter(response -> response).count(); // counts how many True => responsive
        long unResponsiveCount = responses.stream().filter(response -> !response).count(); // counts how many False
        if(unResponsiveCount > responsiveCount) {
            this.participantList.remove(potentiallyUnresponsiveParticipant);
            return otherParticipantList;
        } else {
            return allParticipants;
        }
    }

    @Override
    public void addParticipant(ConsensusParticipant participant) {
        this.participantList.add(participant);
    }

    @Override
    public List<ConsensusParticipant> getActiveParticipants() {
        return this.participantList;
    }
}
