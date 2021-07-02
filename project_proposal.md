states: (timestamp, Coordinates(relative), Altitude(relative), destination, task, leader, ) distributed among agents 
The state of the system should be distributed across multiple client 




# Summary Description

Semi-Autonomous robots on mars with a single leader communicates back to earth command center.

Robustness/failure scenarios
reliable communication

- Failure Scenarios and recovery
-- Loss of communication of robot/physical loss of device: how to refind the robot via communication queues? return towards closest Coordinate, how to re-establish call nasa? head back towards nearest coordinate etc.
-- 

- adding/merging fleets


which robots to keep path with


# Archichitecture overview diagram (figure) and design description

# Implementation Approach
what libraries used to implement
look into tapestry and pastry 

Robustness

# Key Algorithms 4 
(write overview for each algorithm)

Leader election (Chapter 15.3) - Alex
- coordinate tasks
concensus 15.5 - Nadiia
- agree upon commands Nasa
group membership (peer to peer): look into tapestry and pastry (Chapter 10) - Michael
- how to add and delete members and handle lost robots
multicast/reliable multicast (Chapter 6/18) - Matthew
- distributing commands to all the robots or casting states from/to leader.

maybe delete
Logical Clocks (Chapter 14) - 
how to store states shared among robots


# expected results




