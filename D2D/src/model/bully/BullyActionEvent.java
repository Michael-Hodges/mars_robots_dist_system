package model.bully;

import model.ActionPeerEvent;
import model.PeerEvent;

public class BullyActionEvent extends ActionPeerEvent {

    BullyAlgorithmParticipant respondent;
    BullyAlgorithmParticipantImpl.Status respondentStatus;

    public BullyActionEvent(Object source, int id, PeerEvent event) {
        super(source, id, event);
        respondent = null;
        respondentStatus = BullyAlgorithmParticipantImpl.Status.Unknown;
    }

    public BullyAlgorithmParticipant getRespondent() {
        return this.respondent;
    }

    public BullyAlgorithmParticipantImpl.Status getRespondentStatus() {
        return this.respondentStatus;
    }
}
