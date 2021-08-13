package view;

import view.gui.*;

public class GuiServer {

    public void start() {
        SimulationImpl s = new SimulationImpl();
        RemoteSimulation rs = new RemoteSimulationImpl(s);
        RMIRegistry.register(rs, "simulation");
        VisualizationPanel panel = new VisualizationPanel();
        panel.start(s);
    }
}
