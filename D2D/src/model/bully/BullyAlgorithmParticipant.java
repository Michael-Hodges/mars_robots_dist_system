package model.bully;

import java.awt.event.ActionListener;
import java.io.IOException;

public interface BullyAlgorithmParticipant {

    BullyAlgorithmParticipant getCoordinator();
    int getProcessId();
    void add(BullyAlgorithmParticipant participant);
    void startElection();
    void sendVictory(BullyAlgorithmParticipant p);
    void sendElectionMessage(BullyAlgorithmParticipant p);
    void sendAnswer(BullyAlgorithmParticipant p);
    void waitForAnswers();
    void onAnswerMessage(int receivedProcessId);
    void onElectionMessage(int receivedProcessIdt) throws IOException;
    void onVictoryMessage(int receivedProcessId);
    boolean didReceiveAnswerMessages();
    String getHostOrIp();
    int getPort();
    void setListener(ActionListener listener);
}
