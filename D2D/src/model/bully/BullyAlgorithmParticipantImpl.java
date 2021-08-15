package model.bully;

import model.Logger;
import controller.MessageChannel;
import controller.MessageChannelFactory;
import model.PeerEvent;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of a member of the bully algorithm
 */
public class BullyAlgorithmParticipantImpl implements BullyAlgorithmParticipant{

    /**
     * Message types for the bully algorithm
     */
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

    /**
     * Constructs a new bully algorithm participant
     * @param hostOrIp host/ip to use for this participant
     * @param port port to use for this participant
     * @param processId processId to use for this participant
     * @param messageChannelFactory messagechannelFactory to use for this participant
     */
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
        sendEventToListener(PeerEvent.BullyStartElection);
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

    @Override
    public void onAnswerMessage(int receivedProcessId) {
        countOfAnswers++;
        if (receivedProcessId > this.getProcessId()) {
            awaitVictoryMessage();
        }
        sendEventToListener(PeerEvent.BullyReceiveAnswer);
    }

    @Override
    public void onElectionMessage(int receivedProcessId) {
        if (receivedProcessId < this.getProcessId()) {
            BullyAlgorithmParticipant p = lookup(receivedProcessId);
            sendAnswer(p);
            startElection();
        }
        sendEventToListener(PeerEvent.BullyReceiveElectionMessage);
    }

    @Override
    public void onVictoryMessage(int receivedProcessId) {
        log("Leader: " + receivedProcessId);
        this.coordinator = this.lookup(receivedProcessId);
        sendEventToListener(PeerEvent.BullyReceiveVictory);
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

    /**
     * Returns the participant object from list of other participants based on id
     * @param processId participant id to find
     * @return participant with given id
     */
    private BullyAlgorithmParticipant lookup(int processId) {
        for(BullyAlgorithmParticipant p : this.otherParticipants) {
            if (p.getProcessId() == processId) {
                return p;
            }
        }
        throw new IllegalArgumentException("Unable to find Bully participant with " + processId);
    }

    /**
     * Sends a given message to the given target, using message channels.
     * @param target participant to send a message to
     * @param message message to send to the participant
     */
    private void send(BullyAlgorithmParticipant target, Message message) {
        log(message.name());
        sendMessageToListener(message);
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

    /**
     * Sends a given message to the listener as a PeerEvent
     * @param message message to send to the listener
     */
    private void sendMessageToListener(Message message) {
        PeerEvent event = toPeerEvent(message);
        sendEventToListener(event);
    }

    /**
     * Sends a given peerevent to the listener
     * @param event event to send to the listener
     */
    private void sendEventToListener(PeerEvent event) {
        int id = BullyAlgorithmParticipantImpl.EVENT_ID_COUNTER++;
        if (this.listener != null) {
            ActionEvent e = new ActionEvent(this, id, event.toString());
            this.listener.actionPerformed(e);
        }
    }

    /**
     * Converts message into a peerEvent
     * @param message message to translate
     * @return peer event version of the message
     */
    private PeerEvent toPeerEvent(Message message) {
        switch(message) {
            case Answer:
                return PeerEvent.BullySendAnswer;
            case Election:
                return PeerEvent.BullySendElectionMessage;
            case Victory:
                return PeerEvent.BullySendVictory;
            default:
                return PeerEvent.Unknown;
        }
    }

    /**
     * Logs a given string
     * @param msg string to log
     */
    private void log(String msg) {
        Logger.log(msg);
    }

}
