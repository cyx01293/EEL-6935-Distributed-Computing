//package hw4q2;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class TIMINGCLIENT {
	public static void main(String[] args) throws RemoteException, NotBoundException, InterruptedException {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		String addr = args[0];
		String[] info = addr.split(":");
		int N = Integer.valueOf(args[1]);
		int R = Integer.valueOf(args[2]);
		List<Integer> pre = new ArrayList<>();
		for (int i = 0; i < R; ++i) {
			pre.add(i + 1);
		}
		Collections.shuffle(pre);
		int[] unsorted = new int[N];
		for (int i = 0; i < N; ++i) {
			unsorted[i] = pre.get(i);
		}
		System.out.println("unsorted:" + Arrays.toString(unsorted));
		System.out.println("Program is running...");
		//Connect to the server with IP address and port
		Registry registry = LocateRegistry.getRegistry(info[0], Integer.valueOf(info[1]));
		//Look up a service named "MultiThreadSort" 
		SORT sort = (SORT) registry.lookup("MultiThreadSort");
		int[] sorted = sort.sortQuery(unsorted, N, R);
		
		System.out.println("sorted:" + Arrays.toString(sorted));
		long endtTime = System.currentTimeMillis();
		System.out.println("Program running time:" + Long.toString(endtTime - startTime));
	}
}
