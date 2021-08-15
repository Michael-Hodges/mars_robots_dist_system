package model;

public enum PeerEvent {
    StartServer,
    Register,
    ElectLeader,
    BullyStartElection,
    BullyReceiveElectionMessage,
    BullySendElectionMessage,
    BullySendAnswer,
    BullyReceiveAnswer,
    BullySendVictory,
    BullyReceiveVictory,
    Unknown
}
