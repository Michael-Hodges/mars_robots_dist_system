package view.gui;


import java.rmi.RemoteException;

public class RemoteRobotImpl implements RemoteRobot {

    private view.gui.Robot wrappedRobot;

    public RemoteRobotImpl(Robot robot) {
        this.wrappedRobot = robot;
    }

    @Override
    public void setStatus(RobotStatus status) {
        this.wrappedRobot.setStatus(status);
    }
}
