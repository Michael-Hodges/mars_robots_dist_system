package view.gui;

import javax.swing.*;

public class VisualizationPanel {

    public void start(SimulationImpl simulation) {
        JFrame frame = new JFrame();
        frame.setSize(1000,1000);
        frame.add(simulation);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        simulation.start();
        frame.setVisible(true);
    }
}
