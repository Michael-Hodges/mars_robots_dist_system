# Mars Robots

1. Build artifacts to generate .jar file
2. Run helper_scripts in the following order
    - start_rmi_for_gui.bar
      - this starts the rmiregistry in the directory where
        it can see the java.class files
    - start_gui_server.bat
      - this creates the JPanel and registers the stubs that
       will be used by the client code/listeners.
    - run_coordinator.bat
      - this is "Houston" or headquarters which all nodes will
       register with at initialization time
    - run_client.bat
        - this our peer-to-peer node. It is currently wired up
        just with PoC functionality.
   