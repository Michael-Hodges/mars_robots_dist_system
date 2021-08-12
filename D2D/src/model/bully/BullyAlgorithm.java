package model.bully;

import java.util.List;

//Following implementation from: https://en.wikipedia.org/wiki/Bully_algorithm

class BullyAlgorithm {

    List<BullyAlgorithmParticipant> participants;
    BullyAlgorithmParticipant self;

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
            this.self.waitForAnswers();
            if (!self.didReceiveAnswerMessages()) {
                sendVictory();
            }
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
