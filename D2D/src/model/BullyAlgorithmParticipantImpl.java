package model;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public class BullyAlgorithmParticipantImpl implements BullyAlgorithmParticipant{

    enum Message {
        Election,
        Answer,
        Victory
    }

    private BullyAlgorithmParticipant coordinator;
    private int processId;
    private List<BullyAlgorithmParticipant> otherParticipants;
    private int countOfAnswers;

    public BullyAlgorithmParticipantImpl(int processId, List<BullyAlgorithmParticipant> otherParticipants) {
        this.processId = processId;
        this.otherParticipants = otherParticipants;
        coordinator = null;
        this.countOfAnswers = 0;
    }

    @Override
    public BullyAlgorithmParticipant getCoordinator() {
        return this.coordinator;
    }

    @Override
    public void startElection() {
        this.countOfAnswers = 0; //reset count
        BullyAlgorithm algorithm = new BullyAlgorithm(this, otherParticipants);
        algorithm.start();
    }

    @Override
    public int getProcessId() {
        return this.processId;
    }

    @Override
    public void sendVictory(BullyAlgorithmParticipant p) {
        this.coordinator = this;
        this.send(p, Message.Victory);
    }

    @Override
    public void sendElectionMessage(BullyAlgorithmParticipant p) {
        this.send(p, Message.Election);
    }

    @Override
    public void sendAnswer(BullyAlgorithmParticipant p) {
        this.send(p, Message.Answer);
    }

    public void onAnswerMessage(BullyAlgorithmParticipant p) {
        countOfAnswers++;
        if (p.getProcessId() > this.getProcessId()) {
            awaitVictoryMessage();
        }
    }

    public void onElectionMessage(BullyAlgorithmParticipant p) {
        if (p.getProcessId() < this.getProcessId()) {
            sendAnswer(p);
            startElection();
        }
    }

    public void onVictoryMessage(BullyAlgorithmParticipant p) {
        this.coordinator = p;
    }

    @Override
    public boolean didReceiveAnswerMessages() {
        return countOfAnswers > 0;
    }

    private void awaitVictoryMessage() {
        //we do nothing - if we don't get a victory message
        //by the time we're through then we'll start over.
    }


    private void send(BullyAlgorithmParticipant target, Message message) {

    }

    private void receive(String message) {
        Logger.log("Received: " + message);
        String[] elems = message.split(" ");
        Message messageType = Message.valueOf(elems[0]);
        int senderProcessId = Integer.parseInt(elems[1]);
        BullyAlgorithmParticipant p = new BullyAlgorithmParticipantImpl(senderProcessId, new ArrayList<>());
        switch(messageType) {
            case Election:
                onElectionMessage(p);
                break;
            case Answer:
                onAnswerMessage(p);
                break;
            case Victory:
                onVictoryMessage(p);
                break;
            default:
                break;
        }
    }

    private void log(String msg) {
        Logger.log(msg);
    }


}
