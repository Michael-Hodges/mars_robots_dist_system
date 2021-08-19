package model.consensus;

import controller.MessageChannelFactory;

public interface ConsensusParticipant {

    /**
     * Returns true when this participant can reach given participant and false otherwise.
     * @param unresponsiveParticipant
     * @return
     */
    boolean ping(ConsensusParticipant unresponsiveParticipant);

    /**
     * Returns true if participant a can reach participant b and false otherwise.
     * @param friend
     * @param target
     * @return
     */
    boolean requestPing(ConsensusParticipant friend, ConsensusParticipant target);


    String getHostOrIp();

    int getPort();



}
