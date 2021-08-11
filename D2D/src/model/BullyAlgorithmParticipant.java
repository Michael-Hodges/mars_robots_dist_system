package model;

public interface BullyAlgorithmParticipant {

    BullyAlgorithmParticipant getCoordinator();
    int getProcessId();
    void startElection();
    void sendVictory(BullyAlgorithmParticipant p);
    void sendElectionMessage(BullyAlgorithmParticipant p);
    void sendAnswer(BullyAlgorithmParticipant p);
    void onAnswerMessage(BullyAlgorithmParticipant p);
    void onElectionMessage(BullyAlgorithmParticipant p);
    void onVictoryMessage(BullyAlgorithmParticipant p);
    boolean didReceiveAnswerMessages();
}
