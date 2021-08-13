package model.bully;

import model.Logger;
import controller.MessageChannel;
import controller.MessageChannelFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BullyAlgorithmParticipantImpl implements BullyAlgorithmParticipant{

    enum Message {
        Election,
        Answer,
        Victory
    }

    private static int EVENT_ID_COUNTER = 0;
    private BullyAlgorithmParticipant coordinator;
    private int processId;
    private List<BullyAlgorithmParticipant> otherParticipants;
    private int countOfAnswers;
    private int port;
    private String hostOrIp;
    private ActionListener listener;
    int timeoutInMilliseconds = 200;
    private MessageChannelFactory messageChannelFactory;

    public BullyAlgorithmParticipantImpl(String hostOrIp, int port, int processId,
                                         MessageChannelFactory messageChannelFactory) {
        this.hostOrIp = hostOrIp;
        this.port = port;
        this.processId = processId;
        this.messageChannelFactory = messageChannelFactory;
        this.otherParticipants = new ArrayList<>();
        this.coordinator = null;
        this.countOfAnswers = 0;
        this.listener = null;
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
    public void add(BullyAlgorithmParticipant participant) {
        this.otherParticipants.add(participant);
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

    @Override
    public void waitForAnswers() {
        int sleepTime = 10;
        int totalSleepTime = 0;
        while (totalSleepTime < timeoutInMilliseconds){
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            totalSleepTime += sleepTime;
            sleepTime *= 2;
        }
    }

    public void onAnswerMessage(int receivedProcessId) {
        countOfAnswers++;
        if (receivedProcessId > this.getProcessId()) {
            awaitVictoryMessage();
        }
    }


    public void onElectionMessage(int receivedProcessId) {
        if (receivedProcessId < this.getProcessId()) {
            BullyAlgorithmParticipant p = lookup(receivedProcessId);
            sendAnswer(p);
            startElection();
        }
    }

    public void onVictoryMessage(int receivedProcessId) {
        log("Leader: " + receivedProcessId);
        this.coordinator = this.lookup(receivedProcessId);
    }


    @Override
    public boolean didReceiveAnswerMessages() {
        return countOfAnswers > 0;
    }

    @Override
    public String getHostOrIp() {
        return this.hostOrIp;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    @Override
    public void setListener(ActionListener listener) {
        this.listener = listener;
    }



    private void awaitVictoryMessage() {
        //we just await our victory
    }

    private BullyAlgorithmParticipant lookup(int processId) {
        for(BullyAlgorithmParticipant p : this.otherParticipants) {
            if (p.getProcessId() == processId) {
                return p;
            }
        }
        throw new IllegalArgumentException("Unable to find Bully participant with " + processId);
    }


    private void send(BullyAlgorithmParticipant target, Message message) {
        log(message.name());
        this.sendToListener(message);
        try {
            MessageChannel channel = messageChannelFactory.getChannel(target.getHostOrIp(), target.getPort());
            channel.writeString("bully");
            channel.writeString(message.name());
            channel.writeInt(this.processId);
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendToListener(Message message) {
        int id = BullyAlgorithmParticipantImpl.EVENT_ID_COUNTER++;
        if (this.listener != null) {
            BullyEvent event = new BullyEvent(this, id, "bully " + message.name());
            this.listener.actionPerformed(event);
        }
    }


    private void log(String msg) {
        Logger.log(msg);
    }

    class BullyEvent extends ActionEvent {

        public BullyEvent(Object source, int id, String command) {
            super(source, id, command);
        }
    }
}
