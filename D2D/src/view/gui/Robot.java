package view.gui;

/**
 * Interface for the Robot to wrap a peer for display
 */
interface Robot extends Entity {
    /**
     * Move the robot a given amount
     * @param amount amount to move the robot
     */
    void move(int amount);

    /**
     * Rotates the robot
     * @param degrees degrees to rotate the robot
     */
    void rotate(int degrees);

    /**
     * Sets the color of the robot
     * @param r red value
     * @param g green value
     * @param b blue value
     */
    void setColor(int r, int g, int b);

    /**
     * Set Color of text
     * @param r r value
     * @param g g value
     * @param b b value
     */
    void setTextColor(int r, int g, int b);

    /**
     * Sets size of a robot
     * @param size size to set entity
     */
    void setSize(int size);

    /**
     * gets size of a robot
     * @return size the robot
     */
    int getSize();

    /**
     * Sets the label of the robot
     * @param label label to give to the robot
     */
    void setLabel(String label);

    /**
     * Get the label of the robot
     * @return the label of the robot
     */
    String getLabel();

    /**
     * Sets the status of a robot
     * @param status status to set the robot
     */
    void setStatus(RobotStatus status);

    /**
     * Adds peer with a given label
     * @param peerLabel label of peer to add
     */
    void addPeer(String peerLabel);

    /**
     * Removes peer with a given label
     * @param peerLabel label of peer to remove
     */
    void removePeer(String peerLabel);

    /**
     * Remove all peers from a robot
     */
    void removeAllPeers();

    /**
     * Set the status of a certain peer of this robot
     * @param peerLabel label to assign to the peer
     * @param status to assign to that peer
     */
    void setPeerStatus(String peerLabel, RobotStatus status);
}
