                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                /************************************************************************************
This precisely specifies the running environment including the operating system, language written, compiler version and any software needed to run your program. It also describes the program structure such as files, classes and significant methods. 
*************************************************************************************/



Environment -
1) OS - Mac OS, Linux,
   Language Java

2) compiler version - 
java version "13.0.1" 2019-10-15
Java(TM) SE Runtime Environment (build 13.0.1+9)
Java HotSpot(TM) 64-Bit Server VM (build 13.0.1+9, mixed mode, sharing)

3) Software needed-
  No extra jar needed.

4) Program running instruction:
  A) Task 1
   i. Go to CloudLab and choose the experiment "hw4server" created by cyx01293, and start node-0.
   ii. Clone the git repository by typing "git clone https://github.com/cyx01293/EEL-6935-Distributed-Computing" to clone my repository.
   iii. Enter the "task1" folder, and type "make".
   iv. Type "java BROADCASTSERVER" to start the server on CloudLab, and remember the IP address  and the port printed on the console.
   v. Go to CloudLab and choose the experiment "hw4client" created by cyx01293, and start node-0.
   vi. Clone the git repository by typing "git clone https://github.com/cyx01293/EEL-6935-Distributed-Computing" to clone my repository.
   vii. Enter the "task1" folder, and type "make".
   viii. Implement BROADCASTCLIENT by typing "java BROADCASTCLIENT IP:port N R" with input arguments, where IP is the address shown in step iv, port is 1099, N is the size of the list to be sorted, R is the range of the random number generation.
  
  B) Task 2
   i. Go to CloudLab and choose the experiment "hw4server" created by cyx01293, and start node-0.
   ii. Clone the git repository by typing "git clone https://github.com/cyx01293/EEL-6935-Distributed-Computing" to clone my repository.
   iii. Enter the "task2" folder, and type "make".
   iv. Type "java TIMINGSERVER" to start the server on CloudLab, and remember the IP address  and the port printed on the console.
   v. Go to CloudLab and choose the experiment "hw4client" created by cyx01293, and start node-0.
   vi. Clone the git repository by typing "git clone https://github.com/cyx01293/EEL-6935-Distributed-Computing" to clone my repository.
   vii. Enter the "task2" folder, and type "make".
   viii. Implement TIMINGCLIENT by typing "java TIMINGCLIENT IP:port N R" with input arguments, where IP is the address shown in step iv, port is 1099, N is the size of the list to be sorted, R is the range of the random number generation.

  

5) Program Structure-
  Task 1
  A) SORT.java
  Define the interface.

  B) SORTSERVICE.java
  public class SORTSERVICE implements SORT
  A class whose main function is to spawn threads and print unsorted and sorted lists on the console.

  class sortThread
  A class to define the worker thread, and it defines how to send messages to other threads and how to receive messages from other threads for a thread. 

  C) BROADCASTSERVER.java
  public class BROADCASTSERVER
  A class to respond request for clients. This class prints the IP address of the server. This class transmits the service to remote interface with RPC mechanism.

  D) BROADCASTCLIENT.java
  public class BROADCASTCLIENT 
  A class to produce an unsorted list. This class produces a list of unsorted random numbers and connect to the server with IP address and port. This class sends the request to the server to sort the list.

  Taks 2
  A) SORT.java
  Define the interface.

  B) SORTSERVICE.java
  public class SORTSERVICE implements SORT
  A class whose main function is to spawn threads and print unsorted and sorted lists on the console.

  class central
  A class to define time server, and it defines how to receive messages from worker threads and how to send messages back to them.

  class sortThread
  A class to define the worker thread, and it defines how to send messages to other threads and how to receive messages from other threads for a thread. 

  C) BROADCASTSERVER.java
  public class TIMINGSERVER
  A class to respond request for clients. This class prints the IP address of the server. This class transmits the service to remote interface with RPC mechanism.

  D) BROADCASTCLIENT.java
  public class TIMINGCLIENT 
  A class to produce an unsorted list. This class produces a list of unsorted random numbers and connect to the server with IP address and port. This class sends the request to the server to sort the list.