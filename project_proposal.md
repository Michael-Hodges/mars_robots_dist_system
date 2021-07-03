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

We will need to use FIFO ordering with multicast, in order to ensure that all messages and commands from the leader to the rest of the robots are received in the proper execution order. Within this system, order of message receipt by the robots is crucial, as executing commands out of order could have disastorous consequences, including the loss of a robot. Our multicast system will maintain reliability by ensuring integrity, validity, and agreement. To ensure the system's integrity, we will ensure that a process only delivers each message at most one time. For validity, we must ensure that if process multicasts a message, then that message will eventually be delivered. Finally, to guarantee agreement, we will ensure that if one process delivers a message, then all other members of the group will also eventually deliver that message.

This reliable multicast can be accomplished by building on top of basic multicast. When a process sends a message, it basic-multicasts it to the group, including itself. Whenever a message is delivered to a process using basic-deliver, the process checks to make sure the message is not a duplicate, and if not, it basic-multicasts the message again to the group, including itself. Once this has been done, it finally delivers the message. This methodology ensures that every process in the group will receive a message, and by checking if it has already been received, we can ensure there are no duplicate messages.

maybe delete
Logical Clocks (Chapter 14) - 
how to store states shared among robots


# expected results




