import visualizer.RemoteRobot;

import java.rmi.RemoteException;
import java.util.Random;

public class Client {
    Random random = new Random();

    public void start() throws RemoteException {

        RemoteRobot robot = (RemoteRobot)RMIRegistry.retrieve("localhost", "robot_1");
        while(true) {
            if (random.nextInt(100) < 10) {
                robot.setColor(random.nextInt(255),random.nextInt(255),random.nextInt(255));
            } else if (random.nextInt(100) < 25) {
                robot.move(random.nextInt(100));
                robot.rotate(random.nextInt(360));
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
