//package RPC;

import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.util.Arrays;
import java.util.Random;

public class SORTCLIENT {

	public static void main(String[] args) throws RemoteException, NotBoundException, InterruptedException {
		// TODO Auto-generated method stub
		String addr = args[0];
		String[] info = addr.split(":");
		int N = Integer.valueOf(args[1]);
		int R = Integer.valueOf(args[2]);
		int[] unsorted = new int[N];
		Random random = new Random();
		for (int i = 0; i < N; ++i) {
			int number = random.nextInt(R) + 1; 
			//Math.max(max, number);
			unsorted[i] = number;
		}
		Registry registry = LocateRegistry.getRegistry(info[0], Integer.valueOf(info[1]));
		SORT sort = (SORT) registry.lookup("MultiThreadSort");
		int[] sorted = sort.sortQuery(unsorted, N, R);
		System.out.println("unsorted:" + Arrays.toString(unsorted));
		System.out.println("sorted:" + Arrays.toString(sorted));
	}

}
