import javax.swing.*;
import java.awt.*;

public class Program {

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        PlanetViewer pv = new PlanetViewer();
        frame.setSize(1000,1000);
        frame.add(pv);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        pv.start();
        frame.setVisible(true);
    }
}
