//package homework4;

import java.rmi.*; 
import java.rmi.server.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;



import java. net. InetAddress;

public class SORTSERVICE implements SORT {
	static List<Integer> sortedList = new LinkedList<>();
	static List<BlockingQueue<String>> BQList = new LinkedList<>();
	static sortThread[] spawnWorkerRunnable;
	static Thread[] spawnWorker;
	public static final Object lock = new Object();
	public static final Object resLock = new Object();
	static int max = -1;
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
		for (int i = 0; i < N; ++i) {
			max = Math.max(max, unsorted[i]);
		}
		//Produce N worker threads
		for (int i = 0; i < N; ++i) { 
			spawnWorkerRunnable[i] = new sortThread(unsorted[i], N, i, gate, max); 
			spawnWorker[i] = new Thread(spawnWorkerRunnable[i]);

			BQList.add(new LinkedBlockingQueue<String>()); 
		} 
		for (int i = 0; i < N; ++i) {
			spawnWorker[i].start(); 
		}
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
		try {
			gate.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		 
		sort(number);
	}
	public synchronized void sort(int number) {
		//Iterate max times for every thread
		for (int i = 0 ; i < this.max; ++i) {
			int liveThreadNumber = 0;
			int receivedNumber = 0;
			//Send a message to other threads, so the sum is N.
			for (int j = 0; j < N; ++j) {
				try {
					synchronized(SORTSERVICE.lock) {
						SORTSERVICE.BQList.get(j).put(id + "to" + j);
					}

					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			//Receive a message from other threads, so the sum is N.
			while (receivedNumber < N) {
				try {
					SORTSERVICE.BQList.get(id).take();
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				receivedNumber++;
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