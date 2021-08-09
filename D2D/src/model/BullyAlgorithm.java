package model;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

//Following implementation from: https://en.wikipedia.org/wiki/Bully_algorithm

public class BullyAlgorithm {

    int coordinatorProcessId;
    int processId;
    List<Process> allProcesses;
    int timeoutInMilliseconds = 100;

    public BullyAlgorithm(int processId) {
        this.processId = processId;
        this.coordinatorProcessId = -1;
        this.allProcesses = new ArrayList<>();
    }

    public void start() {
        if (isMaxProcessId()) {
            sendVictory();
        } else {
            for(Process p : allProcesses) {
                if (p.processId > this.processId) {
                    sendElectionMessage(p);
                }
            }
        }
    }

    public boolean isMaxProcessId() {
        for(Process p : allProcesses) {
            if (p.processId > this.processId) {
                return false;
            }
        }
        return true;
    }

    private void sendVictory() {
        for(Process p : allProcesses) {
            if (p.processId != this.processId) {
                sendVictory(p);
            }
        }
        this.coordinatorProcessId = this.processId;
    }

    private void sendVictory(Process p) {
        throw new NotImplementedException();
    }

    private void sendElectionMessage(Process p) {
        throw new NotImplementedException();
    }

    private void onAnswerMessage(Process p) {
        if (p.processId > this.processId) {
            awaitVictoryMessage();
        }
    }

    private void onNoAnswers() {
        sendVictory();
    }

    private void awaitVictoryMessage() {
        //if we timeout waiting we start at the beginning
        throw new NotImplementedException();
    }

    private void onElectionMessage(Process p) {
        if (p.processId < this.processId) {
            sendAnswer(p);
            start();
        }
    }

    private void onCoordinator(Process p) {
        this.coordinatorProcessId = p.processId;
    }


    private void sendAnswer(Process targetProcess) {
        throw new NotImplementedException();
    }


    private class Process {
        int processId;
        String hostnameOrIp;
        int port;
    }

}
