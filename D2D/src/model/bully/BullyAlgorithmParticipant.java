package model.bully;

import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * The interface of participants in the bully algorithm
 */
public interface BullyAlgorithmParticipant {
    /**
     * Returns the coordinator for this participant.
     * @return the object which is the coordinator upon completion of the algorithm
     */
    BullyAlgorithmParticipant getCoordinator();

    /**
     * Returns this participants processId
     * @return the processId
     */
    int getProcessId();

    /**
     * Adds a participant to this participant's list of other participants
     * @param participant participant to be added to the list
     */
    void add(BullyAlgorithmParticipant participant);

    /**
     * Starts bully algorithm election.
     */
    void startElection();

    /**
     * Sets this participant as the coordinator and informs other nodes of victory
     * @param p participant to send victory message to
     */
    void sendVictory(BullyAlgorithmParticipant p);

    /**
     * Sends message of election to the given participant
     * @param p participant to send election message to
     */
    void sendElectionMessage(BullyAlgorithmParticipant p);

    /**
     * Sends election answer to given participant
     * @param p participant to send answer to
     */
    void sendAnswer(BullyAlgorithmParticipant p);

    /**
     * Sleeps in order to wait for response messages.
     */
    void waitForAnswers();

    /**
     * When an answer message is received, send an event to the event listener.
     * @param receivedProcessId id of process answer was received from
     */
    void onAnswerMessage(int receivedProcessId);

    /**
     * When an election message has been received, if the sender has a lower process id, send an
     * answer and start another election, finally send event to the listener.
     * @param receivedProcessIdt process ID of sender
     * @throws IOException Java socket/io exception
     */
    void onElectionMessage(int receivedProcessIdt) throws IOException;

    /**
     * Sets the coordinator as the participant that sent the message, then sends event to the
     * listener.
     * @param receivedProcessId process Id of algorithm victor
     */
    void onVictoryMessage(int receivedProcessId);

    /**
     * Elect yourself as leader.
     */
    void markSelfAsLeader();

    /**
     * Returns whether an answers have been received.
     * @return true if received messages > 0
     */
    boolean didReceiveAnswerMessages();

    /**
     * Returns hostname/ip address of this participant
     * @return hostname or ip address
     */
    String getHostOrIp();

    /**
     * Returns port number of this participant
     * @return port number
     */
    int getPort();

    /**
     * Add the actionlistener to use for this participant
     * @param listener Listener to use for this participant
     */
    void addListener(ActionListener listener);
}
