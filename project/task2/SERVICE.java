//package dfsz;


import java.util.*;
import java.util.concurrent.Semaphore;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;


public class SERVICE implements Storage {
	String[] ip;
	String[] port;
	List<File> localFiles = new LinkedList<>();
	int argsSize;
	String clientID;
	String localIP;
	String realIP;
	Semaphore sem;
	boolean isConnected;
	Map<String, Registry> registryTable = new HashMap<>();
	Map<String, Storage> storageTable = new HashMap<>();
	Hashtable<String, List<String>> fileTable = new Hashtable<>();
	Hashtable<String, List<String>> lockedFile = new Hashtable<>();
	Hashtable<String, List<String>> writeLock = new Hashtable<>();

	// create static instance for zookeeper class.
   private static ZooKeeper zk;

   // create static instance for ZooKeeperConnection class.
   private static ZooKeeperConnection conn;

	public SERVICE(String localIP) {
		this.localIP = localIP;
		String[] ad = localIP.split(":");
		this.realIP = ad[0];
	}
	public void makeConnected() throws RemoteException, IOException {
		//change this server's connection status to true
		this.isConnected = true;
	}
	public void updateFiles(Hashtable<String, List<String>> fileTable) throws RemoteException, IOException{
		this.fileTable = fileTable;
	}
	public void setLock(String behavior, String file) throws RemoteException, IOException{
		// set a lock for the file 
		File f = new File(file);
		if (behavior.equals("read")) {
			if (fileTable.containsKey(file)) {
				if (lockedFile.containsKey(file)) {
					this.lockedFile.get(file).add(this.localIP);
				}else {
					List<String> list = new LinkedList<>();
					list.add(this.localIP);
					this.lockedFile.put(file, list);
				}
			}
		}else {
			if (fileTable.containsKey(file)) {
				if (writeLock.containsKey(file)) {
					this.writeLock.get(file).add(this.localIP);
				}else {
					List<String> list = new LinkedList<>();
					list.add(this.localIP);
					this.writeLock.put(file, list);
				}
			}
		}
		
	}
	public void removeLock(String behavior, String file) throws RemoteException, IOException{
		//Remove a lock for the file
		File f = new File(file);
		if (behavior.equals("read")) {
			if (lockedFile.containsKey(file)) {
				List<String> list = this.lockedFile.get(file);
				list.remove(this.localIP);
				if (list.size() == 0) {
					lockedFile.remove(file);
				}else {
					lockedFile.put(file, list);
				}
			}
		}else {
			if (writeLock.containsKey(file)) {
				List<String> list = this.writeLock.get(file);
				list.remove(this.localIP);
				if (list.size() == 0) {
					writeLock.remove(file);
				}else {
					writeLock.put(file, list);
				}
			}
		}
		
	}
	public void updateLock(String behavior, Hashtable<String, List<String>> lockedFile) throws RemoteException, IOException{
		//update every server's locked file hashtable
		if (behavior.equals("read")) {
			this.lockedFile = lockedFile;
		}else {
			this.writeLock = lockedFile;
		}
	}
	public void acquireLock(String behavior, String file) throws RemoteException, IOException{
		setLock(behavior, file);
		for (String key: storageTable.keySet()) {
			if (behavior.equals("read")) {
				storageTable.get(key).updateLock(behavior, this.lockedFile);
			}else {
				storageTable.get(key).updateLock(behavior, this.writeLock);
			}
			
		}
	}
	public void releaseLock(String behavior, String file) throws RemoteException, IOException{
		removeLock(behavior,file);
		for (String key: storageTable.keySet()) {
			if (behavior.equals("read")) {
				storageTable.get(key).updateLock(behavior,this.lockedFile);
			}else {
				storageTable.get(key).updateLock(behavior,this.writeLock);
			}
			
		}
	}
	public void initial(int argsSize, String[] ip, String[] port, String clientID) throws RemoteException, IOException, NotBoundException {
		//initialize the server, store all the connections to other servers
		this.argsSize = argsSize;
		this.ip = ip;
		this.port = port;
		this.clientID = clientID;
		System.out.println("connection with clientID:" + this.clientID);
		System.out.println("This server is connected:" + this.isConnected);
		String localAddr = this.localIP.split(":")[0];
		for (int i = 0; i < this.ip.length; i++) {
      	if (this.localIP.equals(this.ip[i] + ":" + this.port[i])) {
      		continue;
      	}else {
      		String otherIP = this.ip[i];
      		int otherPort = Integer.valueOf(this.port[i]);
      		String IPPort = otherIP + ":" + otherPort;
      		Registry temp = LocateRegistry.getRegistry(otherIP, otherPort);
      		registryTable.put(IPPort, temp);
      		Storage sr;
				sr = (Storage) temp.lookup("DFS" + ip[i] + ":" + port[i]);
      		storageTable.put(IPPort, sr);
      	}
      }
	}
	public String commands(String command) throws RemoteException, IOException, NotBoundException, KeeperException {
		//input commands from the client
		String[] info = command.split(" ");
		if (!info[0].equals("exit") && info.length < 2) return "Input parameter error";
		if (!info[0].equals("exit") && !info[0].equals("read") && lockedFile.containsKey(info[1])) {
			return "The file is being read. Operation fails.";
		}
		if (info[0].equals("create")) {
			String s = create(info[1]);
			printFiles();
			return s;
			// if(create(info[1])) {
			// 	printFiles();
			// 	return "File created";
			// }else {
			// 	printFiles();
			// 	return "File already exists or file name error";
			// }
		}else if (info[0].equals("read")) {
			String s = read(info[1]);
			printFiles();
			return s;
		}else if (info[0].equals("write")) {
			if (info.length < 3) return "File size missing";
			String s = write(info[1], Integer.valueOf(info[2]));
			printFiles();
			return s;
		}else if (info[0].equals("delete")) {
			if (writeLock.containsKey(info[1])) {
				return "The file is on write operation. Delete operation fails.";
			}
			String s = delete(info[1]);
			printFiles();
			return s;
		}else if (info[0].equals("exit")){
			exit();
			printFiles();
			return "Program stopped";
		}else {
			return "command error";
		}
	}
	public void printFiles() throws RemoteException, IOException, NotBoundException, KeeperException{
		
		System.out.println("File list:");
		try {
			String path = "/";
        	conn = new ZooKeeperConnection();
         	zk = conn.connect(this.realIP + ":" + "2181");
         	Stat stat = znode_exists(path); // Stat checks the path

         	if(stat!= null) {

            	List <String> children = zk.getChildren(path, false);
            	for(int i = 0; i < children.size(); i++) {
            		System.out.println(children.get(i)); //Print children's
            	}
            	System.out.println();
         	} else {
            	System.out.println("Node does not exists");
         	}

      	} catch(Exception e) {
         	System.out.println(e.getMessage());
      	}

		//Print out the latest file list stored in hashtable
		// System.out.println("Hashtable content:");
		// for (String key : fileTable.keySet()) {
		// 	System.out.println("File name:" + key);
		// 	System.out.println("Available server:" + fileTable.get(key));
		// }
		// System.out.println();
	}
	@Override
	public byte[] read() throws RemoteException {
		return null;
	}
	@Override
	public String read(String file) throws IOException, RemoteException, KeeperException {
		String path = file;
      	final CountDownLatch connectedSignal = new CountDownLatch(1);
		
      	try {
         	conn = new ZooKeeperConnection();
         	zk = conn.connect(this.realIP + ":" + "2181");
         	Stat stat = znode_exists(path);
			
         	if(stat != null) {
            	byte[] b = zk.getData(path, new Watcher() {
				
               	public void process(WatchedEvent we) {
					
                  	if (we.getType() == Event.EventType.None) {
                     	switch(we.getState()) {
                        case Expired:
                        	connectedSignal.countDown();
                        break;
                    	}
							
                  	} else {
                     	String path = file;
							
                     	try {
                        	byte[] bn = zk.getData(path, false, null);
                        	String data = new String(bn, "UTF-8");
                        	// System.out.println(data);
                        	// connectedSignal.countDown();
							
                     	} catch(Exception ex) {
                        	System.out.println(ex.getMessage());
                     	}
                  	}
               }
            }, null);
				
            String data = new String(b, "UTF-8");
            System.out.println(data);
            return data;
            // connectedSignal.await();
				
         	} else {
            	// System.out.println("Node does not exist");
            	return "File does not exists or file name error";
         	}
      	} catch(Exception e) {
        	// System.out.println(e.getMessage());
        	return e.getMessage();
      	}
	}
	@Override
	public String create(String file) throws RemoteException, IOException, KeeperException{
      	// znode path
      	String path = file; // Assign path to znode

      	// data in byte array
      	byte[] data = "".getBytes(); // Declare data
    
      	try {
         	conn = new ZooKeeperConnection();
         	zk = conn.connect(this.realIP + ":" + "2181");
         	createZnode(path, data); // Create the data to the specified path
         	conn.close();
         	return "File created";
      	} catch (Exception e) {
         	// System.out.println(e.getMessage()); //Catch error message
         	return e.getMessage(); //Catch error message
      	}
	}
	public void createZnode(String path, byte[] data) throws KeeperException,InterruptedException, KeeperException {
      	zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
  	}
  	@Override
  	public boolean exists(String file) throws InterruptedException,KeeperException, RemoteException {
      	String path = file; // Assign znode to the specified path
         
      	try {
         	conn = new ZooKeeperConnection();
         	zk = conn.connect(this.realIP + ":" + "2181");
         	Stat stat = znode_exists(path); // Stat checks the path of the znode
            
         	if(stat != null) {
            	// System.out.println("Node exists and the node version is " + stat.getVersion());
            	return true;
         	} else {
            	// System.out.println("Node does not exists");
            	return false;
         	}
            
      	} catch(Exception e) {
         	// System.out.println(e.getMessage()); // Catches error messages
         	return false;
      	}
  	}
  	public static Stat znode_exists(String path) throws KeeperException,InterruptedException {
      	return zk.exists(path, true);
   	}
	@Override
	public String write(String file, int size) throws java.rmi.UnknownHostException, IOException, KeeperException {
      	String path= file;
      	Random random = new Random();
		String content = "";
		//Generate size random characters, and write it to the file
		for (int i = 0; i < size; i++) {
			char c = (char) random.nextInt(128);
			content += c;
		}
		byte[] data = content.getBytes(); //Assign data which is to be updated.
		
      	try {
         	conn = new ZooKeeperConnection();
         	zk = conn.connect(this.realIP + ":" + "2181");
         	update(path, data); // Update znode data to the specified path
         	String s = "Writing successful";
         	System.out.println("the total number of bytes written to the file:" + size);
         	return s;
      	} catch(Exception e) {
         	// System.out.println(e.getMessage());
         	String s = "File does not exists";
         	return s;
      	}
	}
	public static void update(String path, byte[] data) throws KeeperException,InterruptedException {
      	zk.setData(path, data, zk.exists(path,true).getVersion());
   	}

	@Override
	public String delete(String file) throws java.rmi.UnknownHostException, IOException, KeeperException {
      	String path = file; //Assign path to the znode
		
      	try {
         	conn = new ZooKeeperConnection();
         	zk = conn.connect(this.realIP + ":" + "2181");
         	deleteZnode(path); //delete the node with the specified path
         	return "File deleted";
      	} catch(Exception e) {
         	// System.out.println(e.getMessage()); // catches error messages
         	String s = "File does not exists or file name error";
         	return s;
      	}
	}
	public static void deleteZnode(String path) throws KeeperException,InterruptedException {
     	zk.delete(path,zk.exists(path,true).getVersion());
   	}
	@Override
	public void exit() throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("Client disconnected");
	}
	public static void main(String[] args) {
		
	}
}

