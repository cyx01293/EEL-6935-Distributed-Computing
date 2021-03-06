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
  extra jar needed:
  zookeeper-3.6.0.jar
  zookeeper-jute-3.6.0.jar
  log4j-1.2.17.7.jar
  slf4j-api-1.7.25.jar
  slf4j-log4j12-1.7.25.jar 

4) Program running instruction:
  A) Task 1
   i. Go to CloudLab and choose the experiment "cyxDFS1" created by cyx01293, and start node-0.
   ii. Clone the git repository by typing "git clone https://github.com/cyx01293/EEL-6935-Distributed-Computing" to clone my repository.(If repository already exists, jump to iii)
   iii. Enter the "task1" folder with the directory(../EEL-6935-Distributed-Computing/project/task1), and type "make" in the terminal.
   iv. Type "java DFSSERVER" to start the server on CloudLab, and remember the IP address  and the port printed on the console.
   v. Repeat ii. to iv. for node-1 and node-2.     

   vi. For node-3 and node-4, repeat ii. to iii. 
   vii. Implement DFSCLIENT by typing "java DFSCLIENT IP1:port1 IP2:port2 IP3:port3" with input arguments, where the three IPs are from the consoles of node-0 to node-2, which are 128.110.153.75:1099 128.110.153.74:1099 128.110.153.66:1099  
   viii. On the client console, you can implement the operation like "create filename.txt", "write filename.txt integer", "read filename.txt", "delete filename.txt", exit".
  
  B) Task 2
   i. Go to CloudLab and choose the experiment "CYXDFSproject" created by cyx01293, and start node-0.
   ii. Use the user "zookeeper" by "su -l zookeeper", the password is CYXzookeeper!!. Start the server by "bin/zkServer.sh start";
   iii. Exit "zookeeper", go back to the "/Users/cyx01293" and clone the git repository by typing "git clone https://github.com/cyx01293/EEL-6935-Distributed-Computing" to clone my repository.(If repository already exists, jump to iii)
   iv. Enter the "task2" folder with the directory(../EEL-6935-Distributed-Computing/project/task2), and type "make" in the terminal.
   v. Type "java -cp .:zookeeper-3.6.0.jar:zookeeper-jute-3.6.0.jar:log4j-1.2.17.7.jar:slf4j-api-1.7.25.jar:slf4j-log4j12-1.7.25.jar DFSZSERVER" to start the server on CloudLab, and remember the IP address  and the port printed on the console.
   v. Repeat ii. to iv. for node-1 and node-2.     

   vi. For node-3 and node-4, repeat iii. to iv. 
   vii. Implement DFSCLIENT by typing "java -cp .:zookeeper-3.6.0.jar:zookeeper-jute-3.6.0.jar:log4j-1.2.17.jar:slf4j-api-1.7.25.jar:slf4j-log4j12-1.7.25.jar DFSZCLIENT IP1:port1 IP2:port IP3:port3" with input arguments, where the three IPs are from the consoles of node-0 to node-2, which are 128.110.154.33:1099 128.110.154.23:1099 128.110.153.247:1099.
   viii. On the client console, you can implement the operation like "create filename.txt", "write filename.txt integer", "read filename.txt", "delete filename.txt", exit". 
   Caution: Filename here must start by "/". For example, if you want to create a file named "test.txt", please type "/test.txt" instead.
  

5) Program Structure-
  Task 1
  A) Storage.java
  Define the interface.

  B) SERVICE.java
  public class SERVICE implements SORT
  A class whose main function is to define the create, write, read, delete operations, and to realize the hashtable update for each server.

  C) DFSSERVER.java
  public class DFSSERVER
  A class to respond request for clients. This class prints the IP address of the server. This class transmits the service to remote interface with RPC mechanism.

  D) DFSCLIENT.java
  public class DFSCLIENT 
  A class to produce operations. This class produces a list of operations and connect to the server with IP address and port, then realize the function of file store, retrieval, etc. This class sends the request to the server to do the operation for the server.

  Taks 2
  A) ZooKeeperConnection.java
  Define a class to instantiate zookeeper.

  B) Storage.java
  Define the interface.

  C) SERVICE.java
  public class SERVICE implements SORT
  A class whose main function is to define the create, write, read, delete operations with the ZooKeeper API. All the files are stored in zookeeper server.

  D) DFSZSERVER.java
  public class DFSZSERVER
  A class to respond request for clients. This class prints the IP address of the server. This class transmits the service to remote interface with RPC mechanism, and each server connects to one server of a zookeeper cluster by the port 2181.

  E) DFSZCLIENT.java
  public class DFSZCLIENT 
  A class to produce operations. This class produces a list of operations and connect to the server with IP address and port, then realize the function of file store, retrieval, etc. This class sends the request to the server to do the operation for the server.