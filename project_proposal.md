Nadiia Ramthun

# Summary Description

In this paper we propose a distributed system for a fleet of semi-autonomous robots exploring a planet. In our hypothetical scenario a group of robots are placed onto a planet and begin exploring the surface. The robots goal is to maintain and agree upon their state, maintain communication, and distribute tasks. In so doing, the robots must be able to keep track of their own state as well as access information about other robots states, coordinate tasks broadcasted to them, agree upon data, and communicate reliably with one another.

#### Authors
- Michael Hodges
- Matthew Kuhn
- Nadiia Ramthun
- Alexander Stults

### Key features of our distributed system:

- No centralized servers
- Nodes can enter and leave certain reachable parts of the network.
- Managing a global state
- Coordinating tasks
- Reliable communication to the group

### Expected scenarios to handle
We also list out some failure scenarios that we hope to be able to tackle in our implementation

- Unreachable robot. How can we handle a robot going offline for an extended period and reentering the network.
- Dropped messages. We need to ensure that our nodes reliably deliver messages to the leader and other members of the fleet.

In general, we can abstract our problem out to a higher level and describe it as a peer-to-peer network used to maintain the state of a distributed system. Within this description the commands we send will merely be updated in the state of a specific robot.


### Features out of scope
To eliminate scope creep we will only implement a subset of algorithms and focus only on the distributed system aspect of our application. Therefore the following will not be included in our implementation:

- Robot Navigation: Developing an algorithm for how to control the robots would be necessary in a real application but does not aid in the learning and development of distributed systems.
- Robotic mesh network: we will assume that this network exists and allows our robots to talk to each other.
- Altitude and coordinate calculation: obviously on a planet there are no existing satellites and therefore, no way to utilize such satellites to calculate coordinates. Thus, we will generate our data in a relative way and not focus on what the data actually is.
- Issuing commands: To control the fleet of robots some centralized command will be issued from the likes of a NASA. This is out of our scope and will assume we can broadcast commands and we will focus on developing a way to handle simulated commands

# Archichitecture overview diagram (figure) and design description
<div class="mxgraph" style="max-width:100%;border:1px solid transparent;" data-mxgraph="{&quot;highlight&quot;:&quot;#0000ff&quot;,&quot;nav&quot;:true,&quot;resize&quot;:true,&quot;toolbar&quot;:&quot;zoom layers lightbox&quot;,&quot;edit&quot;:&quot;_blank&quot;,&quot;xml&quot;:&quot;&lt;mxfile host=\&quot;app.diagrams.net\&quot; modified=\&quot;2021-07-05T14:29:47.320Z\&quot; agent=\&quot;5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1 Safari/605.1.15\&quot; etag=\&quot;wMjv_CjplAhJjaOr3L8F\&quot; version=\&quot;14.8.4\&quot;&gt;&lt;diagram id=\&quot;_tpdV9ZdxflvKZcWmpkt\&quot; name=\&quot;Page-1\&quot;&gt;7Ztbc6M2FMc/DY/1IMT1MXHSdqbZzjbpTDdPOywotqYYeUC+7aevBOImsI29MlrPNA8JOgghfvzPkXREDDhf7X/LwvXyE4lRYlhmvDfgk2FZth2w39xwKA2uDUvDIsNxaQKN4Q1/R8JoCusGxyjvVKSEJBSvu8aIpCmKaMcWZhnZdat9kKR713W4QD3DWxQmfes/OKbL0uo7ZmP/HeHFsrozMMWZVVhVFoZ8GcZk1zLBZwPOM0JoebTaz1HC2VVcyut+PXK27liGUjrmAveP7ev6Odr5c899fc/95NPXv34BVtnMNkw24olFb+mhQsCaYbRZ4XG3xBS9rcOIn9mx981sS7pKWAmwww+cJHOSkIyVU5LyKzJCQ4pJyms4rCzuhjKK9kefA9R0mKoQWSGaHViV6gLTnVlCVUJUwDZnwCtNu+YtgQr9svWGAmELhTAW9Q0aduxA4LsEJbw/lLbZ4Vix0cfQnoyhqQZh7d6VFE2nz9CelKEzGcNAFUR7BERvAGIdbdVTVAgxzNfl0PSB9yiWqBoWNIsfRS4NJZTegE8PkrwRSJUDzKQg/S7ImpAukCqHl0lBupIidYNUOcZMCtKTQDqaQaocaKYECUEXpK+Zo3uvHOWxBmoG6d0rSPMnG2v8ewXpSIocWtFMCTK4U5D18qQC6WoGWTV8fyR/tiAJ+sPNSw8le14qMUvwgq/5IvbciHF65FRwFCYP4sQKx3FSLBBRjr+H34qmOMQ1wSktnsJ5NJwn3taGkrxM9fGmc5qRf5G0zFSAHvjBzHW7kdXs0x/KcFg3oz8UWt2Ew47xlh0u+OGLAR9YpRcUxoy1OM/u16oycBX7G6641NNv+boomz1T0e6fJEZKWx02Ffd6JRuK0wU7+rvQxJHbSvrLyCaNuWsWCro8SVGpNUEfVI2YYAA7ShqcNHp9JTk3U9K9ji0ySTDgk9OmKFQme6bJmMHA6TIcmjE6E8Y1a8TcO9pk28KnOSeUxg98f4YPKUmY5zjqckR7TL8I/+fH7/x45ojS07516ulgtPO5KO5t6UhYWa/IJovQqecZxt/C6wxItLJlKGFvfNvtxhBzcYfPfIg0jubl6zlE1UTZfXFV8+bONlQntaqGaJgtEO01xN5MeGhVE0P48Q4Hwx2+tF+NAsseNHqs38EPJNBGTCEvk2jKOlRrlBfe22cajRalQ0uxXzpifm9reRJhW2b54k/Us3U6gCVlaHq7JWMdwJKSj/VOg2oH8KV47J9xAA9ocIAR49xVMRroELJWgfaS2u61EVpO6sojtCqBer0t88v6BacQqDWBQMFJgQ7HdBGrT0R11cIeEaGBaWj0gF5ew7s2RPsTeYCc0rKnUPSIXTsFcw7zqjnHaU9QqOhqND8r6eB/RV+gaOvIhuBtFT1i+/THJxFnFnrXeYEORbtaFS2HvGsVDeX12q0ULXdYTFlvq+gR+9j3My3WKjh5Zx3Iy63RgpM+q5PXf4r0BqE0KwanZ8Vyt6rU5W31OeL7gOtSazPfb08HZsA9HXVZ4TPKMHsgvgPUjcRAUyR2RwZiX6df2JJuersCY/3CloZ8S/4A8laO4Z5xDHnj3ZvCMW6Wc75gmqx9KhKMdABPqwPIX7hcOxOxJaH1djSUzUSkhF5wxgFMcKr+pQ7Ais1/hpTVm3+vgc//AQ==&lt;/diagram&gt;&lt;/mxfile&gt;&quot;}"></div>
<script type="text/javascript" src="https://viewer.diagrams.net/js/viewer-static.min.js"></script>
# Implementation Approach

what libraries used to implement
look into tapestry and pastry

We will be writing our own Java implementation of the Bully algorithm for leader election.

Robustness

# Key Algorithms 4

(write overview for each algorithm)
In this section we review the algorithms we will implement for our semi-autonomous robotic system. In so doing we will review the following algoritms: Leader election, Concensus, group membership, and multicast/reliable multicast.

### Leader election (Bully Algorithm)
Within the fleet of robots a leader will need to be elected. The leader will be responsible for assigning tasks within the fleet and ensuring their completion

In order to elect a leader we will use the Bully algorithm. This algorithm can handle crashes within the election process, and unlike the ring-algorithm, doesn't require the candidates to know their direct neighbors. The Bully algorithm requires a unique id for each member that can ordered by each member and for this I suggest either a MAC address or some sort of hardware level unique identifier. This will allow us to avoid the Bully algorithm's failure case around two candidates with identical priority numbers.

### concensus 15.5 - Nadiia
Another algorithm that we plan to use in our solution is the one that handles coordinate task consensus. Since robots will receive the messages from the port on Earth as to where to go to explore items such as rocks, there might be delay, wrong ordering of messages, or even tampering with messages received by malicious party. Furthermore, the robot may lose part of its state if it crashes. Upon receival of messages, the robots must conclude as to how to proceed with their task. 

In general, the agreement algorithms work by each process starting in undecided state and proposing a value from a set of possible values for the solution. After that, each process decided upon the value in question and enters a decided state in which they no longer can change their decision. Each algorithm solving consensus problem must have following properties. First, in order to be useful at all, an algorithm must terminate with each process setting its value. Second, all correct processes must agree and choose the same value upon entering a decided state. Third, the algorithm must have an “integrity” that is if a correct process proposed a value upon entering a decided state, it must choose that value.

In asynchronous distributed system reaching consensus cannot be guaranteed. Therefore, our solution must be a synchronous system, in which we can use simple multicast to broadcast all values we consider to be correct by each process and assume the number of the failure out of all processes we expect to occur. After completing one more round than assumed number, we can pick the minimum value.


- agree upon commands Nasa
  ###group membership (peer to peer): look into tapestry and pastry (Chapter 10) - Michael
  To implement a peer to peer system the main focus will be on the routing algorithm. We will follow the algorithm as implemented by Pastry, a message routing infrastructure. In so doing, each node of our algorithm will be given a GUID (globally unique identifier). The goal of pastry is to continually transport a message to a node that is closer to the destination node. Utilizing the GUID the message is transported to a node who's GUID is closer to the destination ID. The underlying transport protocol for these messages is normally UDP. To avoid excessive amounts of hops a routing table is developed to reduce the hop-count or round trip latency. With the use of a routing table and appropriate routing algorithm the message can be delivered in O(log N) steps.

We will now get into more details on how the algorithm is implemented. Each node maintains a tree-structured routing table of GUIDs and IP addresses. This routing table has as many rows as there are hexadecimal digits in the GUID, therefore, if there are 128 bits in a GUID the routing table will have 128/4 = 32 rows. Each row


#### Routing Algorithm

#### Host Integration

#### Host failure or departure

#### locality

#### fault tolerance

#### dependability

#### evaluation work

- how to add and delete members and handle lost robots

Their design diminishes their effectiveness for applications that store and update mutable data objects.

Peer to Peer middleware: Pastry, Tapestry, CAN, Chord, and Kademlia

Routing overlay takes responsibility for locating nodes and objects. main goals: routing of requests to objects. Other tasks: insertion of objects, deletion of objects, node addition and removal.

Pastry and Tapestry use prefix routing approach

DOLR: Distributed object location and routing

multicast/reliable multicast (Chapter 6/18) - Matthew

- distributing commands to all the robots or casting states from/to leader.

We will need to use FIFO ordering with multicast, in order to ensure that all messages and commands from the leader to the rest of the robots are received in the proper execution order. Within this system, order of message receipt by the robots is crucial, as executing commands out of order could have disastorous consequences, including the loss of a robot. Our multicast system will maintain reliability by ensuring integrity, validity, and agreement. To ensure the system's integrity, we will ensure that a process only delivers each message at most one time. For validity, we must ensure that if process multicasts a message, then that message will eventually be delivered. Finally, to guarantee agreement, we will ensure that if one process delivers a message, then all other members of the group will also eventually deliver that message.

This reliable multicast can be accomplished by building on top of basic multicast. When a process sends a message, it basic-multicasts it to the group, including itself. Whenever a message is delivered to a process using basic-deliver, the process checks to make sure the message is not a duplicate, and if not, it basic-multicasts the message again to the group, including itself. Once this has been done, it finally delivers the message. This methodology ensures that every process in the group will receive a message, and by checking if it has already been received, we can ensure there are no duplicate messages.

maybe delete
Logical Clocks (Chapter 14) -
how to store states shared among robots

# expected results

In conclusion, we hope to develop and demonstrate a successful simulation of a fleet of 
robots exploring the surface of an unknown planet. 
With this simulation we will show how our four chosen algorithms 
(leader election, concensus, peer-to-peer, and reliable multicast) are able to maintain 
the state of a distributed system, agree upon data, and distribute tasks over a constantly
 changing network. Our results will be shown via the terminal where we can run multiple
  simulated robots, pass commands, and view the states of said robots.
