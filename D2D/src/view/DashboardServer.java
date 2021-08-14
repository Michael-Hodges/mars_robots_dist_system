package view;

import view.gui.*;

public class DashboardServer {

    public void start() {
        DashboardImpl s = new DashboardImpl();
        RemoteDashboard db = new RemoteDashboardImpl(s);
        RMIRegistry.register(db, "dashboard");
        VisualizationPanel panel = new VisualizationPanel();
        panel.start(s);
    }
}
