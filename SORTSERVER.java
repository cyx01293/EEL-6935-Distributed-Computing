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
		// 将此服务转换为远程服务接口://Transmit this serve to remote serve interface
        SORT skeleton = (SORT) UnicastRemoteObject.exportObject(sort, 0);
        // 将RMI服务注册到1099端口://Register the serve to port 1099
        Registry registry = LocateRegistry.createRegistry(1099);
        // 注册此服务，服务名为"WorldClock":// Register this serve, and assign "MultiThreadSort" to it
        registry.rebind("MultiThreadSort", skeleton);
	}

}
