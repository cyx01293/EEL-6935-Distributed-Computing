//package dfsz;

import java.util.*;


import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.data.Stat;


public class DFSZSERVER {
	// create static instance for zookeeper class.
   private static ZooKeeper zk;

   // create static instance for ZooKeeperConnection class.
   private static ZooKeeperConnection conn;

   // Method to create znode in zookeeper ensemble
   public static void create(String path, byte[] data) throws 
      KeeperException,InterruptedException {
      zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE,
      CreateMode.PERSISTENT);
   }
	public static void main(String[] args) throws UnknownHostException, RemoteException {
		// TODO Auto-generated method stub
		InetAddress inetAddress = InetAddress. getLocalHost();
		System.out.println("IP Address:- " + inetAddress. getHostAddress());
		int port = 1099;
		System.out.println("Port:- " + Integer.toString(port));
		// SERVICE serv = new SERVICE(inetAddress. getHostAddress() + ":" + port);
		// //Transmit this service to remote service interface
  //     Storage skeleton = (Storage) UnicastRemoteObject.exportObject(serv, 0);
  //     //Register the service to port 1099
  //     Registry registry = LocateRegistry.createRegistry(port);
  //     // Register this service, and assign "DFS" to it
  //     registry.rebind("DFS" + inetAddress. getHostAddress() + ":" + port, skeleton);
		
    //  System.out.println("This server is connected:" + serv.isConnected);
		// znode path
      String path = "/MyFirstZnode"; // Assign path to znode

      // data in byte array
      byte[] data = "My first zookeeper app".getBytes(); // Declare data
		
      try {
         conn = new ZooKeeperConnection();
         zk = conn.connect("localhost");
         create(path, data); // Create the data to the specified path
         conn.close();
      } catch (Exception e) {
         System.out.println(e.getMessage()); //Catch error message
      }
      
	}

}
