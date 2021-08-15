package model;

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
