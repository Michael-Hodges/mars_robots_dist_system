package view.gui;

interface Robot extends Entity {
    void move(int amount);
    void rotate(int degrees);
    void setColor(int r, int g, int b);
    void setTextColor(int r, int g, int b);
    void setSize(int size);
    int getSize();
    void setLabel(String label);
    String getLabel();
    void setStatus(RobotStatus status);
    void addPeer(String peerLabel);
    void removePeer(String peerLabel);
    void setPeerStatus(String peerLabel, RobotStatus status);
}
