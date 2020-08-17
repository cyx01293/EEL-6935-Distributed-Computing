//package SORTPARALLEL;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.io.*;
public class SORTPARALLEL {
	static List<Integer> sortedList = new LinkedList<>();
	public static final Object lock = new Object();
	public static void main(String[] args) throws InterruptedException {
		int N = Integer.valueOf(args[0]);
		int R = Integer.valueOf(args[1]);
		Thread[] spawnWorker = new Thread[N];
		sortThread[] spawnWorkerRunnable = new sortThread[N];
		int[] unsorted = new int[N];
		
		int[] sorted = new int[N];
		int max = 0;
		Random random = new Random();
		CountDownLatch gate = new CountDownLatch(1);
		for (int i = 0; i < N; ++i) { 
			//int number = unsorted[i];
			int number = random.nextInt(R) + 1; 
			Math.max(max, number);
			unsorted[i] = number;
  
			spawnWorkerRunnable[i] = new sortThread(number, gate); 
			spawnWorker[i] = new Thread(spawnWorkerRunnable[i]);
			spawnWorker[i].start(); 
			//spawnWorker[i].join(); 
		} 
		gate.countDown();
		
		//Thread.sleep(R + 100);
		for (Thread thread : spawnWorker) {
		    thread.join();
		}
		
		/*
		 * System.out.println("sorted[0]:" + Integer.toString(sorted[0])); sorted[0] =
		 * st.getValue(); System.out.println("sorted[0]:" +
		 * Integer.toString(sorted[0]));
		 */
		//spawnWorker[0].join();
		for (int i = 0; i < sortedList.size(); ++i) {
			//System.out.println("sorted[" + i +"]:" + Integer.toString(sorted[i]));
			sorted[i] = sortedList.get(i);
			//System.out.println("sorted[" + i +"]:" + Integer.toString(sorted[i]));
		}
		System.out.println("unsorted:" + Arrays.toString(unsorted));
		System.out.println("sorted:" + Arrays.toString(sorted));
	}
	
}
class sortThread implements Runnable {
	int value;
	int number;
	CountDownLatch gate;
	public sortThread(int number, CountDownLatch gate) {
		this.number = number;
		this.gate = gate;
		
	}
	@Override
	public void run() {
		//System.out.println("number" + Integer.toString(number) + "thread"+ Thread.currentThread().getName());
		try {
			gate.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//System.out.println("threadFinished" + Integer.toString(number) + "thread"+ Thread.currentThread().getName());
		value = sort(number);
		synchronized(SORTPARALLEL.lock) {
			SORTPARALLEL.sortedList.add(value);
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