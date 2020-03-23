//package hw4q2;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;


public class SORTSERVICE implements SORT {
	static List<Integer> sortedList = new LinkedList<>();
	static List<BlockingQueue<Integer>> BQList = new LinkedList<>();
	static sortThread[] spawnWorkerRunnable;
	static BlockingQueue<Integer> centralList = new LinkedBlockingQueue<>();
	static Thread[] spawnWorker;
	public static final Object lock = new Object();
	public static final Object resLock = new Object();
	public static final Object pauseLock = new Object();
	static int preNum = 0;
	@Override
	public int[] sortQuery(int[] unsorted, int N, int R) throws RemoteException, InterruptedException {
		//Implement the sort algorithm with multithreads
		if (!sortedList.isEmpty()) {
			sortedList.clear();
		}
		spawnWorker = new Thread[N];
		spawnWorkerRunnable = new sortThread[N];
		int[] sorted = new int[N];
		CountDownLatch gate = new CountDownLatch(1);
		//Find the maximum element of the unsorted list.
		int max = -1;
		for (int i = 0; i < N; ++i) {
			max = Math.max(max, unsorted[i]);
		}
		//Produce N worker threads
		for (int i = 0; i < N; ++i) { 
			
			spawnWorkerRunnable[i] = new sortThread(unsorted[i], N, i, gate, max); 
			spawnWorker[i] = new Thread(spawnWorkerRunnable[i]);

			BQList.add(new LinkedBlockingQueue<Integer>());
		} 
		//Produce time server
		central centralRunnable = new central(N, gate, max);
		Thread centralServer = new Thread(centralRunnable);
		
		for (int i = 0; i < N; ++i) {
			spawnWorker[i].start(); 
		}
		centralServer.start();
		//Start all the threads at the same time
		gate.countDown();
		
		for (Thread thread : spawnWorker) {
		    thread.join();
		}
		
		System.out.println("sortList.size:" + Integer.toString(sortedList.size()));
		for (int i = 0; i < sortedList.size(); ++i) {
			sorted[i] = sortedList.get(i);
		}
		System.out.println("unsorted:" + Arrays.toString(unsorted));
		System.out.println("sorted:" + Arrays.toString(sorted));
		return sorted;
	}
	
}
class central implements Runnable {
	int N;
	CountDownLatch gate;
	int max;
	public central(int N, CountDownLatch gate, int max) {
		this.N = N;
		this.gate = gate;
		this.max = max;
	}
	@Override 
	public synchronized void run() {
		try {
			gate.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int count = 0;
		//Iterate max times for every thread
		while (count < max) {
			int liveWorker = 0;
			int messageTaken = 0;
			//Receive a message from a thread, so the sum is N.
			while (messageTaken < N) {
				try {
					int num = SORTSERVICE.centralList.take();
					messageTaken++;
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			try {
				Thread.sleep(3);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Notify all worker threads to start the next iteration
			synchronized(SORTSERVICE.pauseLock) {
				SORTSERVICE.pauseLock.notifyAll();
			}
			count++;
		}
	}
}
class sortThread implements Runnable {
	// class of a thread to sort
	int value;
	int number;
	int N;
	int id;
	int max;
	CountDownLatch gate;
	BlockingQueue<String> messages;
	public sortThread(int number, int N, int id, CountDownLatch gate, int max) {
		this.number = number;
		this.N = N;
		this.id = id;
		this.gate = gate;
		this.max = max;
		messages = new LinkedBlockingQueue<String>();
		
	}
	@Override
	public void run() {
		//Wait until the main thread sends the countDown signal
		try {
			gate.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		 
		sort(number, max);
	}
	public synchronized void sort(int number, int max) {
		for (int i = 0 ; i < max; ++i) {
			int liveThreadNumber = 0;
			int receivedNumber = 0;
			//Send a message to time server for each thread
			try {
				SORTSERVICE.centralList.put(this.id);
			} catch (InterruptedException e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Wait until time server sends back a message
			synchronized(SORTSERVICE.pauseLock) {
				try {
					SORTSERVICE.pauseLock.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (i == number - 1) {
				synchronized(SORTSERVICE.resLock) {
					SORTSERVICE.sortedList.add(number);
				}
			}
		}
		return;
	}
	public int getValue() {
		return this.value;
	}
}