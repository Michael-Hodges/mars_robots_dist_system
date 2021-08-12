package model;

import controller.TCPMessageEvent;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class BullyAlgorithmParticipantImpl implements BullyAlgorithmParticipant{

    enum Message {
        Election,
        Answer,
        Victory,
        Start
    }

    private static int EVENT_ID_COUNTER = 0;
    private BullyAlgorithmParticipant coordinator;
    private int processId;
    private List<BullyAlgorithmParticipant> otherParticipants;
    private int countOfAnswers;
    private int port;
    private String hostOrIp;
    private ActionListener listener;

    public BullyAlgorithmParticipantImpl(String hostOrIp, int port, int processId) {
        this.hostOrIp = hostOrIp;
        this.port = port;
        this.processId = processId;
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
        //we do nothing - if we don't get a victory message
        //by the time we're through then we'll start over.
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
            Socket conn = new Socket(target.getHostOrIp(), target.getPort());
            SocketChannel channel = new SocketChannel(conn);
            channel.out.writeUTF("bully");
            channel.out.writeUTF(message.name());
            channel.out.writeInt(this.processId);
            channel.out.flush();
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
