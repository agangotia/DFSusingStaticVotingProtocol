package com.utd.dfs.fs;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.utd.dfs.DFSMain;
import com.utd.dfs.msg.Message;
import com.utd.dfs.statustrackers.Status;
import com.utd.dfs.statustrackers.StatusFileWriteReplies;
import com.utd.dfs.statustrackers.StatusReadWriteQuorumRequest;
/**
 * This is the Main Communication Provider and Status Checker.
 * For Each  Read Or Write Requests,
 * This class maintains the status,
 * and has the functions called by STATIC VOTING PROTOCOL.
 * 
 * @author Anupam Gangotia Profile::http://en.gravatar.com/gangotia
 *         github::https://github.com/agangotia
 * 
 * @author Dilip Profile:: github::
 * 
 * @author Rashmi Profile:: github::
 */
public class DFSCommunicator {

	/**
	 * Map of FIle Operations Status.
	 * Used in :
	 * 1.ReadWrite thread -> Status entry is created
	 * 2.Receiver thread -> Status entry is Updated
	 * Upon Completion of an operation,
	 * Readwrite thread does the task of removing that key,value pair from this map.
	 */
	public static ConcurrentHashMap<String,Status> mapFileStatus=new ConcurrentHashMap<String,Status>();
	
	/**
	 * Function : Broadcast All nodes, asking for votes for a read operation.
	 */
	public static void broadcastReadRequestForVotes(String fileName,Status o,String mapKeyIdentifier){

		mapFileStatus.put(mapKeyIdentifier, o);
		for (Integer key : DFSMain.mapNodes.keySet()) {
			if(key!=DFSMain.currentNode.getNodeID()){
				Message m=new Message("0", DFSMain.currentNode.getNodeID(), DFSMain.mapNodes.get(key).getNodeID(),
						0, "", fileName,mapKeyIdentifier);
				DFSMain.sendQueue.add(m);
			}
		    
		}
	}
	
	/**
	 * Function : Broadcast All nodes, asking for votes for a write operation.
	 */
	public static void broadcastWriteRequestForVotes(String fileName,Status o,String mapKeyIdentifier){
		mapFileStatus.put(mapKeyIdentifier, o);
		for (Integer key : DFSMain.mapNodes.keySet()) {
			if(key!=DFSMain.currentNode.getNodeID()){
				Message m=new Message("0", DFSMain.currentNode.getNodeID(), DFSMain.mapNodes.get(key).getNodeID(),
						10, "", fileName,mapKeyIdentifier);
				DFSMain.sendQueue.add(m);
			}
		    
		}
	}
	
	/**
	 * Function : Unicast to get the reply
	 */
	public static void unicastGetlatestForRead(int nodeId, String fileName,Status o,String mapKeyIdentifier){
		mapFileStatus.put(mapKeyIdentifier, o);
		Message m=new Message("0", DFSMain.currentNode.getNodeID(), nodeId,
				3, "", fileName,mapKeyIdentifier);
		DFSMain.sendQueue.add(m);
	}
	
	/**
	 * Function : Multicast All nodes, asking for read Lock Release.
	 */
	public static void MulticastRequestForReadLockRelease(String fileName,ArrayList<Integer> Nodes,String mapKeyIdentifier){
		for (Integer key : Nodes) {
		
				Message m=new Message("0", DFSMain.currentNode.getNodeID(), key,
						5, "", fileName,mapKeyIdentifier);
				DFSMain.sendQueue.add(m);
		    
		}
	}
	
	
	/**
	 * Function : Multicast All nodes, asking for write Lock Release.
	 */
	public static void MulticastRequestForWriteLockRelease(String fileName,ArrayList<Integer> Nodes,String opcode,String mapKeyIdentifier){
		for (Integer key : Nodes) {
		
				Message m=new Message("0", DFSMain.currentNode.getNodeID(), key,
						15, opcode, fileName,mapKeyIdentifier);
				DFSMain.sendQueue.add(m);
		    
		}
	}
	
	/**
	 * Function : Multicast All nodes, with data and version to uodate their copy
	 */
	public static boolean MulticastRequestForWriteUpdate(String fileName,ArrayList<Integer> Nodes,String data,int version,String mapKeyIdentifier){
		Object o2=new Object();
		Status objStatus2=new StatusFileWriteReplies(fileName, Nodes.size(), o2); 
		mapFileStatus.put(mapKeyIdentifier, objStatus2);
		for (Integer key : Nodes) {
			
				Message m=new Message("0", DFSMain.currentNode.getNodeID(), key,
						14, data, fileName,version,mapKeyIdentifier);
				DFSMain.sendQueue.add(m);

		    
		}
		synchronized (o2) {
			try {
				o2.wait();
				mapFileStatus.remove(fileName);
				// all nodes were able to update the changes
				if(objStatus2.returnDecision()){
					MulticastRequestForWriteLockRelease(fileName,Nodes,"Release",mapKeyIdentifier);
					return true;
				}else{
					MulticastRequestForWriteLockRelease(fileName,Nodes,"RollBack",mapKeyIdentifier);
					return false;
				}
						
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		return false;
	}
}
