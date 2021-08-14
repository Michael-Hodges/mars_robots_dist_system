package view.gui;

import javax.swing.*;

public class VisualizationPanel {

    public void start(DashboardImpl dashboard) {
        JFrame frame = new JFrame();
        frame.setSize(1000,1000);
        frame.add(dashboard);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        dashboard.start();
        frame.setVisible(true);
    }
}
