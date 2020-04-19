//package dfsz;

import java.io.*;
import java.rmi.*;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.zookeeper.KeeperException;

public interface Storage extends Remote{
	public void makeConnected()throws RemoteException, IOException;
	public void updateFiles(Hashtable<String, List<String>> fileTable) throws RemoteException, IOException;
	public void initial(int argsSize, String[] ip, String[] port, String clientID) throws RemoteException, IOException, NotBoundException;
	public String commands(String command) throws RemoteException, IOException, NotBoundException, KeeperException;
	public byte[] read() throws RemoteException;
	public String read(String file ) throws IOException , RemoteException, KeeperException;
	public boolean create (String file) throws RemoteException, IOException, KeeperException;
	public String write(String file, int size) throws java.rmi.UnknownHostException, IOException, KeeperException;
	public String delete(String IP) throws UnknownHostException, IOException, KeeperException;
	public void exit() throws RemoteException;
	public void setLock(String behavior, String file) throws RemoteException, IOException;
	public void removeLock(String behavior, String file) throws RemoteException, IOException;
	public void releaseLock(String behavior, String file) throws RemoteException, IOException;
	public void acquireLock(String behavior, String file) throws RemoteException, IOException;
	public void updateLock(String behavior, Hashtable<String, List<String>> lockedFile) throws RemoteException, IOException;
	public boolean exists(String file) throws InterruptedException,KeeperException, RemoteException;
}
