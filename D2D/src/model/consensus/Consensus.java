package model.consensus;

import java.awt.event.ActionListener;
import java.util.List;

public interface Consensus extends Runnable{
    List<ConsensusParticipant> start(ConsensusParticipant potentiallyUnresponsiveParticipant, List<ConsensusParticipant> participantList);

    void run();

    void addListener(ActionListener listener);

    void addParticipant(ConsensusParticipant participant);
    List<ConsensusParticipant> getActiveParticipants();
}
