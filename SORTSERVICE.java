//package RPC;

import java.rmi.*; 
import java.rmi.server.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java. net. InetAddress;

public class SORTSERVICE implements SORT {
	static List<Integer> sortedList = new LinkedList<>();
	public static final Object lock = new Object();
	@Override
	public int[] sortQuery(int[] unsorted, int N, int R) throws RemoteException, InterruptedException {
		//Implement the sort algorithm with multithreads
		if (!sortedList.isEmpty()) {
			sortedList.clear();
		}
		Thread[] spawnWorker = new Thread[N];
		sortThread[] spawnWorkerRunnable = new sortThread[N];
		int[] sorted = new int[N];
		CountDownLatch gate = new CountDownLatch(1);
		for (int i = 0; i < N; ++i) { 
			spawnWorkerRunnable[i] = new sortThread(unsorted[i], gate); 
			spawnWorker[i] = new Thread(spawnWorkerRunnable[i]);
			spawnWorker[i].start(); 
			//spawnWorker[i].join(); 
		} 
		gate.countDown();
		
	//	Thread.sleep(R);
		for (Thread thread : spawnWorker) {
		    thread.join();
		}
		System.out.println("sortList.size:" + Integer.toString(sortedList.size()));
		for (int i = 0; i < sortedList.size(); ++i) {
			//System.out.println("sorted[" + i +"]:" + Integer.toString(sorted[i]));
			sorted[i] = sortedList.get(i);
			//System.out.println("sorted[" + i +"]:" + Integer.toString(sorted[i]));
		}
		System.out.println("unsorted:" + Arrays.toString(unsorted));
		System.out.println("sorted:" + Arrays.toString(sorted));
		return sorted;
	}
	
}
class sortThread implements Runnable {
	// class of a thread to sort
	int value;
	int number;
	CountDownLatch gate;
	public sortThread(int number, CountDownLatch gate) {
		this.number = number;
		this.gate = gate;
		
	}
	@Override
	public void run() {
		System.out.println("number" + Integer.toString(number) + "thread"+ Thread.currentThread().getName());
		try {
			gate.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//System.out.println("threadFinished" + Integer.toString(number) + "thread"+ Thread.currentThread().getName());
		value = sort(number);
		synchronized(SORTSERVICE.lock) {
			SORTSERVICE.sortedList.add(value);
        }
		
		//System.out.println("value" + Integer.toString(value));
	}
	public int sort(int number) {
		try {
			Thread.sleep(number);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return number;
	}
	public int getValue() {
		return this.value;
	}
}