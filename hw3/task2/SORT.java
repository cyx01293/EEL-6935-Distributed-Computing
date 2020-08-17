//package hw4q2;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SORT extends Remote {
	public int[] sortQuery(int[] unsorted, int N, int R) throws RemoteException, InterruptedException;
}
