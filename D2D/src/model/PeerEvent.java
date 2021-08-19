package model;

/**
 * List of Events for peers to use
 */
public enum PeerEvent {
    StartServer,
    Register,
    ShortwaveRadioPing,
    ElectLeader,
    BullyStartElection,
    BullyReceiveElectionMessage,
    BullySendElectionMessage,
    BullySendAnswer,
    BullyReceiveAnswer,
    BullySendVictory,
    BullyReceiveVictory,
    PeerAdded,
    PeerRemoved,
    PeerStatusUpdated,
    Unknown
}
