package view.gui;

import javax.swing.*;

/**
 * Runs the visualisation panel using a dashboard
 */
public class VisualizationPanel {

    public void start(DashboardImpl dashboard) {
        JFrame frame = new JFrame();
        frame.setSize(1000,1000);
        frame.add(dashboard);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        dashboard.start();
    }
}
