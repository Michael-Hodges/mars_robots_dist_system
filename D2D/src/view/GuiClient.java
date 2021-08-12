package view;

import view.gui.RemoteRobot;
import view.gui.RemoteSimulation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Random;

public class GuiClient implements ActionListener {
    private static Random random = new Random();
    RemoteRobot robot;
    String identifier;
    public GuiClient(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("startServer")) {
            onStartServer();
        } else if (cmd.startsWith("bully")) {
            onBully(cmd);
        }
    }

    private void onStartServer() {
        try {
        RemoteSimulation s = (RemoteSimulation) RMIRegistry.retrieve("localhost", "simulation");
        this.robot = s.addRobot(500,500);
        this.robot.setLabel(this.identifier);
        this.robot.setColor(0,255, 0);
        this.robot.rotate(random.nextInt(360));
        this.robot.move(random.nextInt(200));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void onBully(String message) {
        try {
            if (message.contains("Victory")) {
                this.robot.setColor(0,0,255);
            } else {
                this.robot.setColor(0,255,0);
            }

            this.robot.setLabel(this.identifier + " - " + message);
            this.robot.rotate(random.nextInt(360));
            this.robot.move(random.nextInt(200));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


}
