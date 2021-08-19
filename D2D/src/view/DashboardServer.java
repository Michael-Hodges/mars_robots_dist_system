package view;

import view.gui.*;

/**
 * Server used to start a dashboard.
 */
public class DashboardServer {

    /**
     * Starts the dashboard, RemoteDashboard, VisualizationPanel, and then starts it all
     */
    public void start() {
        DashboardImpl s = new DashboardImpl();
        RemoteDashboard db = new RemoteDashboardImpl(s);
        RMIRegistry.register(db, "dashboard");
        VisualizationPanel panel = new VisualizationPanel();
        panel.start(s);
    }
}
