package view;

import view.gui.RemoteRobot;
import view.gui.RemoteSimulation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

public class GuiClient implements ActionListener {
    RemoteRobot robot;
    String identifier;
    public GuiClient(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("startServer")) {
            onStartServer();
        } else {
            onBar();
        }
    }

    private void onStartServer() {
        try {
        RemoteSimulation s = (RemoteSimulation) RMIRegistry.retrieve("localhost", "simulation");
        this.robot = s.addRobot(500,500);
        this.robot.setLabel(this.identifier);
        this.robot.setColor(0,255, 0);
        this.robot.rotate(145);
        this.robot.move(100);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void onBar() {
        try {
            this.robot.setColor(0,0,255);
            this.robot.setLabel(this.identifier + "-onBar");
            this.robot.rotate(180);
            this.robot.move(100);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


}
