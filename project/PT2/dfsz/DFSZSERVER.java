//package dfsz;

import java.util.*;


import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;

public class DFSZSERVER {
	public static void main(String[] args) throws UnknownHostException, RemoteException, KeeperException {
		// TODO Auto-generated method stub
		InetAddress inetAddress = InetAddress. getLocalHost();
		System.out.println("IP Address:- " + inetAddress. getHostAddress());
		int port = 1099;
		System.out.println("Port:- " + Integer.toString(port));
		SERVICE serv = new SERVICE(inetAddress. getHostAddress() + ":" + port);
		//Transmit this service to remote service interface
    Storage skeleton = (Storage) UnicastRemoteObject.exportObject(serv, 0);
      //Register the service to port 1099
    Registry registry = LocateRegistry.createRegistry(port);
      // Register this service, and assign "DFS" to it
    registry.rebind("DFS" + inetAddress. getHostAddress() + ":" + port, skeleton);
		
    System.out.println("This server is connected:" + serv.isConnected);

	}

}
