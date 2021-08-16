package view.gui;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Testbed for the views
 */
public class ViewTestArea {

    static Random random = new Random();

    public static void main(String[] args) {
        DashboardImpl dashboard = new DashboardImpl();
        int n = 8;
        List<Robot> robots = new ArrayList<>();
        for (int i = 0 ; i < n; i++) {
            Robot r = new RobotImpl("R" + i, 100 + (100 * i),100 + (100 * i));
            r.setSize(80);
            r.setTextColor(0,0,0);
            for(int j = 0; j < n; j++) {
                if (j == i) {
                    continue;
                }
                r.addPeer("R" + j);
            }
            robots.add(r);
            dashboard.addEntity(r);
        }

        VisualizationPanel panel = new VisualizationPanel();
        panel.start(dashboard);
    }
}
