//package TMAN;

import java.util.*;
import java.io.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
//import org.jfree.chart.ChartFactory;
public class TMAN {
	static int N;
	static int k;
	static char topology;
	static int[][] distance;
	static Node[] nodes;
	static int cycleNum = 40;
	static int[] distanceSum = new int[cycleNum];
	public static void main(String[] args) {
		//read the arguments of input 
		N = Integer.valueOf(args[0]);
		k = Integer.valueOf(args[1]);
		topology  = args[2].charAt(0);
		distance = new int[N][N];
		if (topology == 'R') {
			ringTopology();
		}else if (topology == 'P') {
			PTopology();
		}else if (topology == 'S') {
			smileyBallTopology();
		}else throw new IllegalArgumentException("Topology argument error");
		plotSumDistance();
	}
	public static void ringTopology() {
		nodes = new Node[N];
		networkConstruction();
		//set the angle difference and initial angle, then plot a ring with radius = 1, center = (0, 0)
		double angleDifference = (double) 360 / N;
		double angle = 90;
		int radius = 1;
		for (int i = 0; i < N; ++i) {
			double xValue = Math.cos(Math.toRadians(angle)) * radius;
			double yValue = Math.sin(Math.toRadians(angle)) * radius;
			nodes[i].setXCordinate(xValue);
			nodes[i].setYCordinate(yValue);
			angle -= angleDifference;
			for (int j = 0; j < N; ++j) {
				//compute the distances between nodes
				distance[i][j] = Math.min(N - Math.abs(nodes[i].getID() - nodes[j].getID()), Math.abs(nodes[i].id - nodes[j].id));
				//System.out.println(distance[i][j]);
			}
		}
		networkEvolution();
		
	}
	public static void PTopology() {
		nodes = new Node[N];
		networkConstruction();
		//set the angle difference and initial angle, then plot a half circle with radius = 1, center = (0, 0), and a line from (0, 1) to (0, -2)
		double angleDifference = (double) 180 / (N - 2);
		double angle = 90;
		int radius = 1;
		for (int i = 0; i < N - 1; ++i) {
			double xValue = Math.cos(Math.toRadians(angle)) * radius;
			double yValue = Math.sin(Math.toRadians(angle)) * radius;
			nodes[i].setXCordinate(xValue);
			nodes[i].setYCordinate(yValue);
			angle -= angleDifference;
		}
		nodes[N - 1].setXCordinate(0);
		nodes[N - 1].setYCordinate(-2 * radius);
		for (int i = 0; i < N; ++i) {
			for (int j = 0; j < N; ++j) {
				//compute the distances
				if ((i == 0 || i == N - 2) && j == N - 1) {
					distance[i][j] = 1;
				}else {
					distance[i][j] = Math.abs(i - j);
				}
			}
		}
		networkEvolution();
	}
	public static void smileyBallTopology() {
		nodes = new Node[N];
		networkConstruction();
		//set the angle difference and initial angle, then plot the face with radius = 1, center = (0, 0),
		//plot the two eyes with radius = 0.2, and the centers are (0.4, 0.4) and (-0.4, 0.4), respectively.
		//plot the mouth with a half circle whose radius is 0.6, and the center is (0, -0.2).
		double angleDifference = (double) 360 / (N / 4);
		double angle = 90;
		int radius = 1;
		//use first N / 4 nodes to plot face
		for (int i = 0; i < N / 4; ++i) {
			double xValue = Math.cos(Math.toRadians(angle)) * radius;
			double yValue = Math.sin(Math.toRadians(angle)) * radius;
			nodes[i].setXCordinate(xValue);
			nodes[i].setYCordinate(yValue);
			angle -= angleDifference;
		}
		//use second N / 4 nodes to plot right eye
		angleDifference = (double) 360 / (N / 4);
		angle = 90;
		double eyeRadius = 0.2;
		for (int i = N / 4; i < N / 2; ++i) {
			double xValue = Math.cos(Math.toRadians(angle)) * eyeRadius + 0.4;
			double yValue = Math.sin(Math.toRadians(angle)) * eyeRadius + 0.4;
			nodes[i].setXCordinate(xValue);
			nodes[i].setYCordinate(yValue);
			angle -= angleDifference;
		}
		//use third N / 4 nodes to plot left eye
		angleDifference = (double) 360 / (N / 4);
		angle = 90;
		eyeRadius = 0.2;
		for (int i = N / 2; i < 3 * N / 4; ++i) {
			double xValue = Math.cos(Math.toRadians(angle)) * eyeRadius - 0.4;
			double yValue = Math.sin(Math.toRadians(angle)) * eyeRadius + 0.4;
			nodes[i].setXCordinate(xValue);
			nodes[i].setYCordinate(yValue);
			angle -= angleDifference;
		}
		//use last N / 4 nodes to plot the mouth
		angleDifference = (double) 180 / (N / 4);
		angle = 0;
		double mouthRadius = 0.6;
		for (int i = 3 * N / 4; i < N; ++i) {
			double xValue = Math.cos(Math.toRadians(angle)) * mouthRadius;
			double yValue = Math.sin(Math.toRadians(angle)) * mouthRadius - 0.2;
			nodes[i].setXCordinate(xValue);
			nodes[i].setYCordinate(yValue);
			angle -= angleDifference;
		}
		for (int i = 0; i < N; ++i) {
			for (int j = 0; j < N; ++j) {
				if ((i >= 0 && i < N / 4) && j >= 0 && j < N / 4) {
					distance[i][j] = Math.min(N / 4 - Math.abs(nodes[i].getID() - nodes[j].getID()), Math.abs(nodes[i].id - nodes[j].id));
				}else if ((i >= N / 4 && i < N / 2) && j >= N / 4 && j < N / 2){
					distance[i][j] = Math.min(N / 4 - Math.abs(nodes[i].getID() - nodes[j].getID()), Math.abs(nodes[i].id - nodes[j].id));
				}else if ((i >= N / 2 && i < 3 * N / 4) && j >= N / 2 && j < 3 * N / 4){
					distance[i][j] = Math.min(N / 4 - Math.abs(nodes[i].getID() - nodes[j].getID()), Math.abs(nodes[i].id - nodes[j].id));
				}else if ((i >= 3 * N / 4 && i < N) && j >= 3 * N / 4 && j < N){
					distance[i][j] = Math.abs(nodes[i].getID() - nodes[j].getID());
				}else  distance[i][j] = 10000;
			}
		}
		networkEvolution();
	}
	public static void networkConstruction() {
		Set<Integer> set = new HashSet<>();
		if (topology == 'R' || topology == 'P') {
			//assign k neighbors randomly
			for (int i = 0; i < N; ++i) {
				nodes[i] = new Node(i + 1);
				nodes[i].neighborList = new int[k];
				//System.out.println(nodes[i].id);
				Random random = new Random();
				for (int j = 0; j < k; ++j) {
					int temp = random.nextInt(N) + 1;
					while (set.contains(temp)) {
						temp = random.nextInt(N) + 1;
					}
					nodes[i].neighborList[j] = temp;
					set.add(temp);
					//System.out.println(nodes[i].neighborList[j]);
				}
				set.clear();
			}
		}else {
			//divide N nodes into 4 parts, and for each part, assign k neighbors randomly.
			for (int parts = 0; parts < 4; ++parts) {
				int start = parts * N / 4, end = parts * N / 4 + N / 4;
				for (int i = start; i < end; ++i) {
					nodes[i] = new Node(i + 1);
					nodes[i].neighborList = new int[k];
					//System.out.println(nodes[i].id);
					Random random = new Random();
					for (int j = 0; j < k; ++j) {
						int temp = random.nextInt(N / 4) + start + 1;
						while (set.contains(temp)) {
							temp = random.nextInt(N / 4) + start + 1;
						}
						nodes[i].neighborList[j] = temp;
						set.add(temp);
						//System.out.println(nodes[i].neighborList[j]);
					}
					set.clear();
				}
			}
		}
	}
	public static void networkEvolution() {
		Random random = new Random();
		//to iterate and update node neighbor lists cycleNum times
		for (int cycle = 0; cycle < cycleNum; ++cycle) {
			System.out.println("cycle:" + cycle);
			for (int i = 0; i < N; ++i) {
				int dest = updateList(i);
				sort(i);
				sort(dest);
				
			}
			if (cycle == 0 || cycle == 4 || cycle == 9 || cycle == 14) {
				//for cycle 1, 5, 10, 15, plot and output neighbor lists.
				plotGraph(cycle);
				try {
					FileWriter writer = new FileWriter(topology + "_N" + Integer.toString(N) + "_k" + Integer.toString(k) +"_" + Integer.toString(cycle + 1) + ".txt");
					for (int i = 0; i < nodes.length; ++i) {
						outputNeighborList(writer, nodes[i].neighborList, cycle);
					}
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			int sum = 0;
			//compute distance sum of between each pair of nodes
			for (int i = 0; i < nodes.length; ++i) {
				/*
				 * if (cycle == 0 || cycle == 4 || cycle == 9 || cycle == 14) {
				 * //outputNeighborList(nodes[i].neighborList, cycle); }
				 */
				for (int j = 0; j < k; ++j) {
					int xIndex = nodes[i].getID() - 1;
					int yIndex = nodes[i].neighborList[j] - 1;
					sum += distance[xIndex][yIndex];
				}
			}
			distanceSum[cycle] = sum;
		}
	}
	public static void outputNeighborList(FileWriter writer, int[] neighborList, int cycle) {
		try {
			//FileWriter writer = new FileWriter(topology + "_N" + Integer.toString(N) + "_k" + Integer.toString(k) +"_" + Integer.toString(cycle + 1) + ".txt");
			for (int i = 0; i < neighborList.length; ++i) {
				writer.write(neighborList[i] + " ");
			}
			writer.write("\n");
			//writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public static void plotSumDistance() {
		/* plot the distance sum with respect to number of cycle */
		XYSeries series = new XYSeries("distanceSum",false);
		/* input data */
		for (int cycle = 0; cycle < cycleNum; ++cycle) {
			XYDataItem XY = new XYDataItem(cycle + 1, distanceSum[cycle]);
			//int[] neighbor = nodes[i].getNeighborList();
			//System.out.println("neighborLength:" + neighbor.length);
			
			//XYSeries series = new XYSeries(Integer.toString(i),false);
			series.add(XY);
		}
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series);
		JFreeChart chart = ChartFactory.createXYLineChart(
				"distanceSum", 
				"nubmer of cycles",
				"sum", 
				dataset, 
				PlotOrientation.VERTICAL,
				false, true, false);
        
		/* Width of the image */
		int width = 640; 
		/* Height of the image */
	    int height = 480;  
	    String filename = topology + "_N" + Integer.toString(N) + "_k" + Integer.toString(k);
	    File XYChart = new File( filename + ".jpeg" ); 
	    try {
			ChartUtilities.saveChartAsJPEG( XYChart, chart, width, height);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void plotGraph(int cycle) {
		/* plot the two dimensional node graph of different cycles */
		XYSeriesCollection dataset = new XYSeriesCollection();
		ArrayList<XYSeries> seriesList = new ArrayList<XYSeries>();
		
		//XYSeries series = new XYSeries("", false);
		System.out.println("nodeLength:" + nodes.length);
		for (int i = 0; i < nodes.length; ++i) {
			XYDataItem XY = new XYDataItem(nodes[i].getX(),nodes[i].getY());
			int[] neighbor = nodes[i].getNeighborList();
			//System.out.println("neighborLength:" + neighbor.length);
			for (int j = 0; j < neighbor.length; ++j) {
				XYSeries series = new XYSeries(Integer.toString(i) + "*"+Integer.toString(j),false);
				series.add(XY);
				series.add(new XYDataItem(nodes[neighbor[j] - 1].getX(), nodes[neighbor[j] - 1].getY()));
				seriesList.add(series);
			}
		}
		System.out.println("seriesList length:" +seriesList.size());
		
		Iterator<XYSeries> irt = seriesList.iterator(); 
		while(irt.hasNext()){
		  dataset.addSeries((XYSeries) irt.next()); 
		}
		 
		//dataset = new XYSeriesCollection(series);
		JFreeChart chart = ChartFactory.createXYLineChart(
				""+topology, 
				"X",
				"Y", 
				dataset, 
				PlotOrientation.VERTICAL,
				false, true, false);
        
		/* Width of the image */
		int width = 640; 
		/* Height of the image */
		int height = 480; 
		String filename = topology + "_N" + Integer.toString(N) + "_k" + Integer.toString(k) +"_" + Integer.toString(cycle + 1);
		File XYChart = new File( filename + ".jpeg" ); 
		try {
			ChartUtilities.saveChartAsJPEG( XYChart, chart, width, height);
			seriesList.clear();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static int updateList(int src) {
		/* update the neighbors of each node */
		Random random = new Random();
		int dest = random.nextInt(k);
		dest = nodes[src].neighborList[dest] - 1;
		/* if the source node and one of its neighbor node are the same node, then select a new neighbor node */
		while (src == dest) {
			dest = random.nextInt(k);
			dest = nodes[src].neighborList[dest] - 1;
		}
		
		System.out.println("src:" + src);
		System.out.println("dest:" + dest);
		//sends a list consisting of the id of its neighbors and of itself to the selected neighbor
		int[] srcNeighbor = nodes[src].neighborList;
		int[] destReceived = new int[srcNeighbor.length + 1];
		System.arraycopy(srcNeighbor, 0, destReceived, 0, srcNeighbor.length);
		destReceived[destReceived.length - 1] = nodes[src].getID();
		//set the received list for both source node and destination node
		nodes[dest].receivedList = destReceived;
		nodes[src].receivedList = nodes[dest].neighborList;
		System.out.println("srcReceive:" + Arrays.toString(nodes[src].receivedList));
		System.out.println("srcNeighb:" + Arrays.toString(nodes[src].neighborList));
		System.out.println("destReceive:" + Arrays.toString(nodes[dest].receivedList));
		System.out.println("destNeighbor:" + Arrays.toString(nodes[dest].neighborList));
		return dest;
	}
	public static void sort(int index) { 
		HashMap<Integer, ArrayList<Integer>> map = new HashMap<>(); 
		Set<Integer> set = new HashSet<>();
		int m = nodes[index].neighborList.length;
		int n = nodes[index].receivedList.length;
		int[] preNewAndOld = new int[m + n];
		//Integer[] newAndOld = new Integer[m + n];
		int[] updatedNeighbor = new int[k];
		System.arraycopy(nodes[index].neighborList, 0, preNewAndOld, 0, m);
		System.arraycopy(nodes[index].receivedList, 0, preNewAndOld, m, n); 
		for (int i = 0; i < preNewAndOld.length; ++i) {
			/* if the destination node has already been selected, or the destination node equals source node, then select a new one */
			if (set.contains(preNewAndOld[i]) || preNewAndOld[i] == index + 1) {
				continue;
			}else {
				set.add(preNewAndOld[i]);
			}
			//newAndOld[i] = Integer.valueOf(preNewAndOld[i]);
		}
		Integer[] newAndOld = new Integer[set.size()];
		Iterator value = set.iterator();
		int m1 = 0;
		while (value.hasNext()) {
			newAndOld[m1] = (Integer) value.next();
			//System.out.println(newAndOld[m1]);
			++m1;
		}
		/* update the neighbor list by combining neighbor list and received list, pick k shortest distance nodes */
		Integer[] newAndOldDistance = new Integer[newAndOld.length];
		for (int i = 0 ; i < newAndOldDistance.length; ++i) {
			newAndOldDistance[i] = distance[index][newAndOld[i] - 1];
		}
		for (int i = 0; i < newAndOldDistance.length; ++i) {
			if (map.containsKey(newAndOldDistance[i])) {
				/*
				 * if (map.get(newAndOldDistance[i]).contains(newAndOld[i])) { continue; }else {
				 * 
				 * }
				 */
				map.get(newAndOldDistance[i]).add(newAndOld[i]);
			}else {
				map.put(newAndOldDistance[i], new ArrayList<Integer>());
				map.get(newAndOldDistance[i]).add(newAndOld[i]);
			}
		}
		Arrays.sort(newAndOldDistance);
		for (int i = 0; i < k; ++i) {
			if (!map.containsKey(newAndOldDistance[i])) {
				throw new IllegalArgumentException("HashMap argument error");
			}else {
				int temp = map.get(newAndOldDistance[i]).get(0).intValue();
				updatedNeighbor[i] = temp;
				if (map.get(newAndOldDistance[i]).size() == 0) {
					map.remove(newAndOldDistance[i]);
				}else {
					map.get(newAndOldDistance[i]).remove(0);
				}
			}
		}
		nodes[index].neighborList = updatedNeighbor;
		System.out.println("newNeighbor:" + Arrays.toString(nodes[index].neighborList));
		//System.out.println("newReceivenewandold:" + Arrays.toString(indexes));
	}
	
}
class Node{
	int id;
	double x;
	double y;
	int[] neighborList;
	int[] receivedList;
	public Node(int id) {
		this.id = id;
	};
	public void setXCordinate(double x) {
		this.x = x;
	};
	public void setYCordinate(double y) {
		this.y = y;
	};
	public int getID() {
		return id;
	};
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public int[] getNeighborList() {
		return neighborList;
	}
}