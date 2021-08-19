# Mars Robots

Steps for running simulation

1. Build artifacts to generate .jar file
2. Run helper_scripts in the following order
    - step_1_start_rmi_for_gui.bat
      - This starts the rmiregistry in the directory where
        it can see the java.class files
      - RMI is only used by the GUI Server / GUI Client and the Coordinator 
    - step_2_start_gui_server.bat
      - This creates the JPanel and registers the stubs that
       will be used by the client code/listeners.
    - step_3_run_coordinator.bat
      - This represents "Houston" or the headquarters which all nodes will
       register with at initialization time. It is also responsible for providing
        port numbers for all the clients.
    - step_4_add_client.bat
      - This our peer-to-peer node. Run this multiple times to create multiple peer nodes.
            Suggesting running size for simulation is 4 clients.
   - select simulation
        - step_5_run_simulation_move_away.bat
            - This moves a few random peers away to random locations
        - step_5_run_simulation_move_home.bat
            - This moves everyone back to the same position via a multicast starting
                from a random node. Please note that the simulation doesn't know
                which servers are dead - so it might choose a dead server at random,
                if this happens just run the file again.
        - step_5_run_simulation_kill_client.bat
            - This kills a client a random.
   