import visualizer.RemoteRobot;
import visualizer.RemoteSimulation;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Client {
    Random random = new Random();

    public void start() throws RemoteException {
        RemoteSimulation s = (RemoteSimulation) RMIRegistry.retrieve("localhost", "simulation");
        List<RemoteRobot> robots = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            RemoteRobot robot = s.addRobot(500,500);
            robot.setLabel("R_" + i);
            robots.add(robot);
        }

        while(true) {
            for (RemoteRobot robot : robots) {
                if (random.nextInt(100) < 10) {
                    robot.setColor(random.nextInt(255), random.nextInt(255), random.nextInt(255));
                } else if (random.nextInt(100) < 25) {
                    robot.move(random.nextInt(100));
                    robot.rotate(random.nextInt(360));
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
