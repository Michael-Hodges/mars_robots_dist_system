package model.bully;

import model.ActionPeerEvent;
import model.PeerEvent;

/**
 * Implementation of ActionPeerEvent interface, to deal with ActionEvents in the bully algorithm.
 */
public class BullyActionEvent extends ActionPeerEvent {

    BullyAlgorithmParticipant respondent;
    BullyAlgorithmParticipantImpl.Status respondentStatus;

    /**
     * Constructs a new BullyActionEvent with a given source, id, and PeerEvent
     * @param source source of event
     * @param id id of event
     * @param event PeerEvent to set as event
     */
    public BullyActionEvent(Object source, int id, PeerEvent event) {
        super(source, id, event);
        respondent = null;
        respondentStatus = BullyAlgorithmParticipantImpl.Status.Unknown;
    }

    /**
     * Returns the respondent of the event
     * @return respondent for event
     */
    public BullyAlgorithmParticipant getRespondent() {
        return this.respondent;
    }

    /**
     * Returns status of the respondent of the event
     * @return Respondent status
     */
    public BullyAlgorithmParticipantImpl.Status getRespondentStatus() {
        return this.respondentStatus;
    }
}
