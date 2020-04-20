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
		
		// System.out.println("File list:");
		// try {
		// 	String path = "/";
  //       	conn = new ZooKeeperConnection();
  //        	zk = conn.connect("localhost");
  //        	Stat stat = znode_exists(path); // Stat checks the path

  //        	if(stat!= null) {

  //           	List <String> children = zk.getChildren(path, false);
  //           	for(int i = 0; i < children.size(); i++) {
  //           		System.out.println(children.get(i)); //Print children's
  //           	}
  //           	System.out.println();
  //        	} else {
  //           	System.out.println("Node does not exists");
  //        	}

  //     	} catch(Exception e) {
  //        	System.out.println(e.getMessage());
  //     	}

		//Print out the latest file list stored in hashtable
		System.out.println("Hashtable content:");
		for (String key : fileTable.keySet()) {
			System.out.println("File name:" + key);
			System.out.println("Available server:" + fileTable.get(key));
		}
		System.out.println();
	}
	@Override
	public byte[] read() throws RemoteException {
		return null;
	}
	@Override
	public String read(String file) throws IOException, RemoteException, KeeperException {
		String path = file;
      	final CountDownLatch connectedSignal = new CountDownLatch(1);
		if (!fileTable.containsKey(file)) {
			String s = "File does not exists or file name error";
			return s;
		}
      	try {
        	conn = new ZooKeeperConnection();
         	zk = conn.connect("localhost");
         	Stat stat = znode_exists(path);
			
         	if(fileTable.get(file).contains(this.localIP) && stat != null) {
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
                        		//System.out.println(data);
                        		connectedSignal.countDown();
							
                     		} catch(Exception ex) {
                        		System.out.println(ex.getMessage());
                     		}
                  		}
               		}
            	}, null);
				
            String data = new String(b, "UTF-8");
            // System.out.println(data);
            // connectedSignal.await();
            return data;
				
         	} else {
         		if (exists(file)) {
         			deleteZnode(file);
         		}
         		String addr = fileTable.get(file).get(0);
				Storage sr = storageTable.get(addr);
				String s = sr.read(file);
         		createZnode(path, s.getBytes());
         		fileTable.get(file).add(this.localIP);
				for (String key : storageTable.keySet()) {
					storageTable.get(key).updateFiles(this.fileTable);
				}
            	//System.out.println("Node does not exists");
            	// return "File does not exists or file name error";
            	return s;
         	}
      	} catch(Exception e) {
        	System.out.println(e.getMessage());
        	return e.getMessage();
      	}
		// //If the file does not exists, return;
		// if (!fileTable.containsKey(file)) {
		// 	String s = "File does not exists or file name error";
		// 	return s;
		// }

		// if (fileTable.get(file).contains(this.localIP)) {
		// 	//If the file already exists in this server, read the content of the file
		// 	File f = new File(file); 
		// 	FileReader fr = new FileReader(f); 
		// 	StringBuilder sb = new StringBuilder();
		// 	int i; 
		// 	while ((i=fr.read()) != -1) {
		// 		// System.out.print((char) i); 
		// 		sb.append((char) i);
		// 	} 
		// 	// System.out.println(); 
		// 	fr.close();
		// 	return sb.toString();
		// }else {
		// 	//If the file does not exist in this server, connect the available server and get the buffer.
		// 	File f = new File(file); 
		// 	if (f.exists()) {
		// 		f.delete();
		// 	}
		// 	f.createNewFile();
		// 	String addr = fileTable.get(file).get(0);
		// 	Storage sr = storageTable.get(addr);
		// 	String s = sr.read(file);
		// 	FileWriter fw = new FileWriter(f);
		// 	fw.write(s);
		// 	fw.flush();
		// 	fileTable.get(file).add(this.localIP);
		// 	for (String key : storageTable.keySet()) {
		// 		storageTable.get(key).updateFiles(this.fileTable);
		// 	}
		// 	return s;
		// }
	}
	@Override
	public String create(String file) throws RemoteException, IOException, KeeperException{
      	// znode path
      	String path = file; // Assign path to znode

      	// data in byte array
      	byte[] data = "".getBytes(); // Declare data
    
      	try {
         	conn = new ZooKeeperConnection();
         	zk = conn.connect("localhost");
         	if (fileTable.containsKey(file)) {
				return "File already exists"; // Create the data to the specified path
			}
			if (exists(file)) {
				deleteZnode(file);
			}
         	createZnode(path, data); // Create the data to the specified path
         	conn.close();
         	String s = "File created";
         	List<String> temp = new LinkedList<>();
			temp.add(this.localIP);
			fileTable.put(file, temp);
			for (String key : storageTable.keySet()) {
				storageTable.get(key).updateFiles(this.fileTable);
			}
         	return s;
      	} catch (Exception e) {
        	// System.out.println(e.getMessage());
        	return e.getMessage(); //Catch error message
      	}
		//Create a file
		// File f = new File(file);
		// if (fileTable.containsKey(file)) {
		// 	return false;	
		// }
		// else {
		// 	if (f.exists()) {
		// 		f.delete();
		// 	}
		// 	f.createNewFile();
		// 	List<String> temp = new LinkedList<>();
		// 	temp.add(this.localIP);
		// 	fileTable.put(file, temp);
		// 	for (String key : storageTable.keySet()) {
		// 		storageTable.get(key).updateFiles(this.fileTable);
		// 	}
		// 	return true;
		// }
	}
	public void createZnode(String path, byte[] data) throws KeeperException,InterruptedException, KeeperException {
      	zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
  	}
  	@Override
  	public boolean exists(String file) throws InterruptedException,KeeperException, RemoteException {
  		String path = file; // Assign znode to the specified path
         
      	try {
         	conn = new ZooKeeperConnection();
         	zk = conn.connect("localhost");
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
      	
        if (!fileTable.containsKey(file)) {
			String s = "File does not exists or file name error";
			return s;
		}
		delete(file);
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
			zk = conn.connect("localhost");
         	//update(path, data); // Update znode data to the specified path
         	createZnode(path, data);
         	List<String> temp = new LinkedList<>();
			temp.add(this.localIP);
			fileTable.put(file, temp);
			//update the file list hashtable
			for (String key : storageTable.keySet()) {
				Hashtable<String, List<String>> ht = this.fileTable;
				storageTable.get(key).updateFiles(ht);
			}
			String s = "Writing successful";
         	System.out.println("the total number of bytes written to the file:" + size);
         	return s;
      	} catch(Exception e) {
         	// System.out.println(e.getMessage());
         	String s = "File does not exists or file name error";
         	return s;
      	}
		// //Write a file
		// if (!fileTable.containsKey(file)) {
		// 	String s = "File does not exists or file name error";
		// 	return s;
		// }
		// //If old buffer exists in this server, delete it first.
		// File f = new File(file);
		// delete(file);
		// f.createNewFile();
		// Random random = new Random();
		// FileWriter fw = new FileWriter(f);
		// //Generate size random characters, and write it to the file
		// for (int i = 0; i < size; i++) {
		// 	char c = (char) random.nextInt(128);
		// 	fw.write(c);
		// 	fw.flush();
		// }
		// List<String> temp = new LinkedList<>();
		// temp.add(this.localIP);
		// fileTable.put(file, temp);
		// //update the file list hashtable
		// for (String key : storageTable.keySet()) {
		// 	Hashtable<String, List<String>> ht = this.fileTable;
		// 	storageTable.get(key).updateFiles(ht);
		// }
		// String s = "Writing successful";
		// System.out.println("the total number of bytes written to the file:" + size);
		// fw.close();
		// return s;
		
	}
	public static void update(String path, byte[] data) throws KeeperException,InterruptedException {
      	zk.setData(path, data, zk.exists(path,true).getVersion());
   	}

	@Override
	public String delete(String file) throws java.rmi.UnknownHostException, IOException, KeeperException {
   		String path = file; //Assign path to the znode
		
      	try {
      		if (!fileTable.containsKey(file)) {
				String s = "File does not exists or file name error";
				return s;
			}
         	conn = new ZooKeeperConnection();
         	zk = conn.connect("localhost");
         	if (exists(file)) {
         		deleteZnode(file); //delete the node with the specified path
         	}
         	
         	String s = "File deleted";
         	// System.out.println(s);
         	fileTable.remove(file);
			for (String key : storageTable.keySet()) {
				storageTable.get(key).delete(file);
			}
         	return s;
      	} catch(Exception e) {
         	System.out.println(e.getMessage()); // catches error messages
         	String s = "File does not exists or file name error";
         	return s;
      	}
		// // Delete the file
		// File f = new File(file);
		// if (!fileTable.containsKey(file)) {
		// 	String s = "File does not exists or file name error";
		// 	return s;
		// }else {
		// 	//delete all the buffer in every replica
		// 	f.delete();
		// 	fileTable.remove(file);
		// 	String s = "File deleted";
		// 	for (String key : storageTable.keySet()) {
		// 		storageTable.get(key).delete(file);
		// 	}
		// 	return s;
		// }
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

