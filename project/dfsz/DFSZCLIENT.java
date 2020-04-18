//package dfsz;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.*;




public class DFSZCLIENT {

	public static void main(String[] args) throws NumberFormatException, NotBoundException, IOException {
		// TODO Auto-generated method stub
		int argsSize = args.length;
		long startTime = System.currentTimeMillis();
		InetAddress clientIP = InetAddress. getLocalHost();
		System.out.println("Client started");
		String[] ip = new String[argsSize];
		String[] port = new String[argsSize];
		for (int i = 0; i < argsSize; i++) {
			String temp = args[i];
			String[] info = temp.split(":");
			ip[i] = info[0];
			port[i] = info[1];
		}
		Random random = new Random();
		int server = random.nextInt(argsSize);
		Registry registry = LocateRegistry.getRegistry(ip[server], Integer.valueOf(port[server]));
		System.out.println("...");
		//Look up a service named "DFS" 
		Storage service = (Storage) registry.lookup("DFS" + ip[server] + ":" + port[server]);
		service.makeConnected();
		//Initialize the server
		String clientID = "";
		clientID += random.nextInt(100000);
		service.initial(argsSize, ip, port, clientID);
		Scanner in = new Scanner(System.in);
		String r = "";
		String lastCommand = "";
		//Keep fetching the command from the console
		while (!r.equals("exit")) {
			try {
				System.out.println("Enter command:"); 
				r = in.nextLine();
				String[] lastInfo = lastCommand.split(" ");
				if (lastInfo[0].equals("read") || lastInfo[0].equals("write")) {
					service.releaseLock(lastInfo[0], lastInfo[1]);
				}
				String s = service.commands(r);
				String[] currentInfo = r.split(" ");
				if (currentInfo[0].equals("read") || currentInfo[0].equals("write")) {
					service.acquireLock(currentInfo[0], currentInfo[1]);
				}
				lastCommand = r;
				System.out.println(s);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		System.out.println("unsorted:" + Arrays.toString(unsorted));
//		System.out.println("sorted:" + Arrays.toString(sorted));
		long endtTime = System.currentTimeMillis();
		System.out.println("Program running time:" + Long.toString(endtTime - startTime));
	}

}
