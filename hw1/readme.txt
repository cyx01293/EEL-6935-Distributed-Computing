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
   jfreechart-1.0.19.jar
   jcommon-1.0.23.jar

4) Program running instruction:
   i. Find the root directory where TMAN.java and makefile exist, type "make".
   ii. Implement TMAN by java -cp /../jcommon-1.0.23.jar:/../jfreechart-1.0.19.jar TMAN.java 1000 30 P;   

5) Program Structure-
  
  A) public static void main(String[] args)
  
  Select the program to run according to the arguments imported.

  B) networkInitialization() - 

  Produce N nodes and assign k neighbors randomly to each node.

  C) networkEvolution() - 

  Set the cycle. For each cycle, update the neighbor lists for each node. Plot the node graphs for each node and export the neighbor lists for each node when cycle is 1, 5, 10, 15, and compute the distance sum according to the distance of each pair of the nodes.

  D) outputNeighborList() -

  Function to write neighbor list of each node.

  E) plotSumDistance() and plotGraph() - 

  Function to plot sum distances after each cycle and function to plot node graphs.

  F) updateList() - 

  Function to update neighbor list of each node in each cycle according to Jelasity and Babaoglu’s algorithm.

  G) sort() - 

  Combine neighbor list and received list, and select top k - closest nodes, then make it as new neighbor list.


  H) Class Node

  Definition of nodes - d, x, y, neighborList[], receivedList[].

