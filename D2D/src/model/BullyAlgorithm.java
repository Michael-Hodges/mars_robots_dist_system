package model;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

//Following implementation from: https://en.wikipedia.org/wiki/Bully_algorithm

class BullyAlgorithm {

    List<BullyAlgorithmParticipant> participants;
    BullyAlgorithmParticipant self;
    int timeout = 200;

    public BullyAlgorithm(BullyAlgorithmParticipant self, List<BullyAlgorithmParticipant> participants) {
        this.self = self;
        this.participants = participants;
    }

    public void start() {
        if (isMaxProcessId()) {
            sendVictory();
        } else {
            for(BullyAlgorithmParticipant p : participants) {
                if (p.getProcessId() > self.getProcessId()) {
                    self.sendElectionMessage(p);
                }
            }
            waitForAnswers();
            if (!self.didReceiveAnswerMessages()) {
                sendVictory();
            }
        }
    }

    private void waitForAnswers() {
        //wait for answers
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isMaxProcessId() {
        for(BullyAlgorithmParticipant p : participants) {
            if (p.getProcessId() > self.getProcessId()) {
                return false;
            }
        }
        return true;
    }

    private void sendVictory() {
        for(BullyAlgorithmParticipant p : participants) {
            if (p.getProcessId() != this.self.getProcessId()) {
                self.sendVictory(p);
            }
        }
    }
}
