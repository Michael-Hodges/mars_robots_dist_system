package model.consensus;

import java.awt.event.ActionListener;
import java.util.List;

public interface Consensus extends Runnable{
    void run();

    void addListener(ActionListener listener);
}
