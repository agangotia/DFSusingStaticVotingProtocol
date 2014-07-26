package com.utd.dfs;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.nio.sctp.SctpChannel;
import com.utd.dfs.logicalclock.LogicalClock;
import com.utd.dfs.utils.NodeDetails;


public class DFSMain {
	
	/**
	 * applicationRunning=true, Server will listen for requests
	 * applicationRunning=false, normally happens when u quit the application
	 */
	private boolean applicationRunning;
	
	/**
	 * This is the total number of nodes in topology read from config.
	 */
	private int totalNodes;
	/**
	 * contains information about current Node read from config.
	 */
	private  NodeDetails currentNode;
	/**
	 * This is the map of all Nodes Present in the Topology
	 */
	private  HashMap<Integer, NodeDetails> mapNodes;
	/**
	 * This is the map of all Nodes Present in the Topology
	 */
	 private HashMap<String, NodeDetails> mapNodesByAddress;
	
	/**
	 * This is the map of SCTP Connections.Each Process will contain the connection objects
	 */
	private ConcurrentHashMap<Integer, SctpChannel> connectionSocket;
	
	/**
	 * Lamport's logical clock
	 * initial value=0
	 * On send event : +1
	 * On recieve event : Max(currentval,valFromMessage)+1
	 */
	private LogicalClock LC;//Lamport's Logical Clock

	
	public DFSMain(){
		applicationRunning=true;
		totalNodes=0;//read from topology.txt later
		mapNodes=new HashMap<Integer, NodeDetails>();
		mapNodesByAddress=new HashMap<String, NodeDetails>();
		connectionSocket=new ConcurrentHashMap<Integer, SctpChannel>();
	}
	

	/**
	 * FileAppend This class writes to files
	 * 
	 * @author Anupam Gangotia Profile::http://en.gravatar.com/gangotia
	 *         github::https://github.com/agangotia
	 * 
	 * @author Dilip Profile:: github::
	 * 
	 * @author Rashmi Profile:: github::
	 */
	public static void main(String[] args) {
		if (args.length != 1) {

			System.out
					.println("Inappropriate arguement passed, please pass only 1 arguement");
			return;
		}
		
		DFSMain objMain=new DFSMain();

		if (!objMain.readConfig(Constants.TOPOLOGYFILE, Integer.parseInt(args[0]))) {
			return;
		}

	}
	
	
	public boolean readConfig(String fileName,int nodeID){
		return false;
	}

}
