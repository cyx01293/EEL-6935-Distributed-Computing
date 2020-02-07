//package RPC;

import java.rmi.*; 
import java.rmi.server.*; 

public interface SORT extends Remote {
	public int[] sortQuery(int[] unsorted, int N, int R) throws RemoteException, InterruptedException;
}