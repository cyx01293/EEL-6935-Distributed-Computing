                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                /************************************************************************************
This precisely specifies the running environment including the operating system, language written, compiler version and any software needed to run your program. It also describes the program structure such as files, classes and significant methods. 
*************************************************************************************/



Environment -
1) OS - Mac OS, Linux

2) compiler version - 
java version "13.0.1" 2019-10-15
Java(TM) SE Runtime Environment (build 13.0.1+9)
Java HotSpot(TM) 64-Bit Server VM (build 13.0.1+9, mixed mode, sharing)

3) Software needed-
  No extra jar needed.

4) Program running instruction:
  A) Task 1
   i. Find the root directory where SORTPARALLEL.java and makefile exist, type "make".
   ii. Implement SORTPARALLEL by typing "java SORTPARALLEL N R" with input arguments, N is the size of the list to be sorted, R is the range of the random number generation. 
  
  B) Task 2
   i. Open the file directory "Task 2 & 3", type "make" in terminal.
   ii. Type "java SORTSERVER" to start the server.
   iii. Open another terminal, and implement SORTCLIENT by typing "java SORTCLIENT localhost:port N R" with input arguments, N is the size of the list to be sorted, R is the range of the random number generation.

  C) Task 3
   i. Go to CloudLab and choose the experiment "cyx01293-QV64725", and start node-0.
   ii. Clone the git repository by typing "http://github.com/cyx01293/EEL-6935-Distributed-Computing" to download my source code.
   iii. Enter the downloaded file, and type "make".
   iv. Type "java SORTSERVER" to start the server on CloudLab, and remember the IP address and the port printed on the console.
   v. Open the terminal on your local laptop, and implement SORTCLIENT by typing "java SORTCLIENT IP:port N R" with input arguments, where IP is the address shown in step iv,  port is 1099, N is the size of the list to be sorted, R is the range of the random number generation.

  D) Bonus
   i. Go to CloudLab and choose the experiment "cyx01293-QV64725", and start node-0.
   ii. Clone the git repository by typing "http://github.com/cyx01293/EEL-6935-Distributed-Computing" to download my source code.
   iii. Enter the downloaded file, and type "make".
   iv. Type "java SORTSERVER" to start the server on CloudLab, and remember the IP address  and the port printed on the console.
   v. Go to CloudLab and choose the experiment "cyx01293-QV64803", and start node-0.
   vi. Clone the git repository by typing "http://github.com/cyx01293/EEL-6935-Distributed-Computing" to download my source code.
   vii. Enter the downloaded file, and type "make".
   viii. Implement SORTCLIENT by typing "java SORTCLIENT IP:port N R" with input arguments, where IP is the address shown in step iv, port is 1099, N is the size of the list to be sorted, R is the range of the random number generation.

5) Program Structure-
  Task 1
  SORTPARALLEL.java
  A) class sortThread implements Runnable
  
  A class for each worker thread spawn by main master thread. This class defines how to run the sort algorithm indicated by the pseudocode in hw3, and return the value to the main thread as soon as this worker thread ends.

  B) public static void main(String[] args) 

  The main master thread. Spawn worker thread for every number in the unsorted list. Start the worker threads at the same time and sort the number in the order worker threads return numbers.

  Taks 2 & 3
  C) SORT.java
  Define the interface.

  D) SORTSERVICE.java
  public class SORTSERVICE implements SORT
  A class whose main function is similar to SORTPARALLEL.java

  E) SORTSERVER.java
  public class SORTSERVER
  A class to respond request for clients. This class prints the IP address of the server. This class transmits the service to remote interface with RPC mechanism.

  F) SORTCLIENT.java
  public class SORTCLIENT 
  A class to produce an unsorted list. This class produces a list of unsorted random numbers and connect to the server with IP address and port. This class sends the request to the server to sort the list.
