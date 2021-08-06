package visualizer;

import java.awt.*;
import java.io.Serializable;
import java.rmi.RemoteException;

public class RemoteRobotImpl implements RemoteRobot {

    private Robot wrappedRobot;

    public RemoteRobotImpl(Robot robot) {
        this.wrappedRobot = robot;
    }

    @Override
    public void move(int amount) throws RemoteException {
        this.wrappedRobot.move(amount);
    }

    @Override
    public void rotate(int degrees) throws RemoteException {
        this.wrappedRobot.rotate(degrees);
    }

    @Override
    public void setColor(int r, int g, int b) throws RemoteException {
        this.wrappedRobot.setColor(r,g,b);
    }

    @Override
    public void setLabel(String label) {
        this.wrappedRobot.setLabel(label);
    }
}
