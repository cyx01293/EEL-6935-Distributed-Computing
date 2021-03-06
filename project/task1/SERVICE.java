//package dfs;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

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
	public String commands(String command) throws RemoteException, IOException, NotBoundException {
		//input commands from the client
		String[] info = command.split(" ");
		if (info.length < 2) return "Input parameter error";
		if (!info[0].equals("exit") && !info[0].equals("read") && lockedFile.containsKey(info[1])) {
			return "The file is being read. Operation fails.";
		}
		if (info[0].equals("create")) {
			if(create(info[1])) {
				printFiles();
				return "File created";
			}else {
				printFiles();
				return "File already exists or file name error";
			}
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
	public void printFiles() {
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
	public String read(String file) throws IOException, RemoteException {
		//If the file does not exists, return;
		if (!fileTable.containsKey(file)) {
			String s = "File does not exists or file name error";
			return s;
		}

		if (fileTable.get(file).contains(this.localIP)) {
			//If the file already exists in this server, read the content of the file
			File f = new File(file); 
			FileReader fr = new FileReader(f); 
			StringBuilder sb = new StringBuilder();
			int i; 
			while ((i=fr.read()) != -1) {
				// System.out.print((char) i); 
				sb.append((char) i);
			} 
			// System.out.println(); 
			fr.close();
			return sb.toString();
		}else {
			//If the file does not exist in this server, connect the available server and get the buffer.
			File f = new File(file); 
			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();
			String addr = fileTable.get(file).get(0);
			Storage sr = storageTable.get(addr);
			String s = sr.read(file);
			FileWriter fw = new FileWriter(f);
			fw.write(s);
			fw.flush();
			fileTable.get(file).add(this.localIP);
			for (String key : storageTable.keySet()) {
				storageTable.get(key).updateFiles(this.fileTable);
			}
			return s;
		}
	}
	@Override
	public boolean create(String file) throws RemoteException, IOException {
		//Create a file
		File f = new File(file);
		if (fileTable.containsKey(file)) {
			return false;	
		}
		else {
			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();
			List<String> temp = new LinkedList<>();
			temp.add(this.localIP);
			fileTable.put(file, temp);
			for (String key : storageTable.keySet()) {
				storageTable.get(key).updateFiles(this.fileTable);
			}
			return true;
		}
	}
	@Override
	public String write(String file, int size) throws java.rmi.UnknownHostException, IOException {
		//Write a file
		if (!fileTable.containsKey(file)) {
			String s = "File does not exists or file name error";
			return s;
		}
		//If old buffer exists in this server, delete it first.
		File f = new File(file);
		delete(file);
		f.createNewFile();
		Random random = new Random();
		FileWriter fw = new FileWriter(f);
		//Generate size random characters, and write it to the file
		for (int i = 0; i < size; i++) {
			char c = (char) random.nextInt(128);
			fw.write(c);
			fw.flush();
		}
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
		fw.close();
		return s;
		
	}


	@Override
	public String delete(String file) throws java.rmi.UnknownHostException, IOException {
		// Delete the file
		File f = new File(file);
		if (!fileTable.containsKey(file)) {
			String s = "File does not exists or file name error";
			return s;
		}else {
			//delete all the buffer in every replica
			f.delete();
			fileTable.remove(file);
			String s = "File deleted";
			for (String key : storageTable.keySet()) {
				storageTable.get(key).delete(file);
			}
			return s;
		}
	}
	@Override
	public void exit() throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("Client disconnected");
	}
	public static void main(String[] args) {
		
	}
}
