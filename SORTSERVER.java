//package RPC;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SORTSERVER {

	public static void main(String[] args) throws RemoteException, UnknownHostException {
		// TODO Auto-generated method stub
		System.out.println("create sort remote service...");
		InetAddress inetAddress = InetAddress. getLocalHost();
		System.out.println("IP Address:- " + inetAddress. getHostAddress());
		SORT sort = new SORTSERVICE();
		//Transmit this serve to remote serve interface
        SORT skeleton = (SORT) UnicastRemoteObject.exportObject(sort, 0);
        //Register the serve to port 1099
        Registry registry = LocateRegistry.createRegistry(1099);
        // Register this serve, and assign "MultiThreadSort" to it
        registry.rebind("MultiThreadSort", skeleton);
	}

}
