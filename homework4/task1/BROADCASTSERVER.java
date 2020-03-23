//package homework4;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class BROADCASTSERVER {
	public static void main(String[] args) throws RemoteException, UnknownHostException {
		// TODO Auto-generated method stub
		System.out.println("create sort remote service...");
		InetAddress inetAddress = InetAddress. getLocalHost();
		System.out.println("IP Address:- " + inetAddress. getHostAddress());
		int port = 1099;
		System.out.println("Port:- " + Integer.toString(port));
		SORT sort = new SORTSERVICE();
		//Transmit this service to remote service interface
        SORT skeleton = (SORT) UnicastRemoteObject.exportObject(sort, 0);
        //Register the service to port 1099
        Registry registry = LocateRegistry.createRegistry(port);
        // Register this service, and assign "MultiThreadSort" to it
        registry.rebind("MultiThreadSort", skeleton);
	}
}
