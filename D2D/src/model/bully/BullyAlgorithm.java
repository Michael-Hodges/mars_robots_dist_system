package model.bully;

import java.util.List;

//Following implementation from: https://en.wikipedia.org/wiki/Bully_algorithm

/**
 * Implements the bully algorithm.
 */
class BullyAlgorithm {

    List<BullyAlgorithmParticipant> participants;
    BullyAlgorithmParticipant self;

    /**
     * Constructs new bully algorithm using a given participant and list of other participants
     * @param self Participant to treat as self
     * @param participants other participants in algorithm
     */
    public BullyAlgorithm(BullyAlgorithmParticipant self, List<BullyAlgorithmParticipant> participants) {
        this.self = self;
        this.participants = participants;
    }

    /**
     * Starts the bully Algorithm, by sending out election messages, or a victory message if self
     * has the highest processid
     */
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

    /**
     * Tests to see if self has the highest process id
     * @return true if self has highest process id
     */
    public boolean isMaxProcessId() {
        for(BullyAlgorithmParticipant p : participants) {
            if (p.getProcessId() > self.getProcessId()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sends victory message to all other algorithm participants
     */
    private void sendVictory() {
        for(BullyAlgorithmParticipant p : participants) {
            if (p.getProcessId() != this.self.getProcessId()) {
                self.sendVictory(p);
            }
        }
    }
}
