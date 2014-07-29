package com.utd.dfs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;


import com.sun.nio.sctp.SctpChannel;
import com.utd.dfs.logicalclock.LogicalClock;
import com.utd.dfs.msg.Message;
import com.utd.dfs.nw.Receiver;
import com.utd.dfs.nw.Sender;
import com.utd.dfs.utils.ConnectionManager;
import com.utd.dfs.utils.NodeDetails;


public class DFSMain {
	
	/**
	 * applicationRunning=true, Server will listen for requests
	 * applicationRunning=false, normally happens when u quit the application
	 */
	public static boolean applicationRunning=true;
	
	/**
	 * This is the total number of nodes in topology read from config.
	 */
	public static int totalNodes=0;
	/**
	 * contains information about current Node read from config.
	 */
	public static  NodeDetails currentNode;
	
	/**
	 * contains information about coordinator Node read from config.
	 */
	public static  NodeDetails coordinatorNode;
	/**
	 * map to maintain file versions of current node
	 */
	public static HashMap<String,Integer> myFileVersions=new HashMap<String,Integer>();
	
	/**
	 * This is the map of all Nodes Present in the Topology
	 */
	public static  HashMap<Integer, NodeDetails> mapNodes=new HashMap<Integer, NodeDetails>();
	/**
	 * This is the map of all Nodes Present in the Topology
	 */
	public static HashMap<String, NodeDetails> mapNodesByAddress=new HashMap<String, NodeDetails>();
	
	/**
	 * This is the map of SCTP Connections.Each Process will contain the connection objects
	 */
	public static ConcurrentHashMap<Integer, SctpChannel> connectionSocket=new ConcurrentHashMap<Integer, SctpChannel>();
	

	/**
	 * Queue containing messages to be send
	 * Used By Send Thread, and Broad Cast Service
	 */
	public static BlockingQueue<Message> sendQueue=new ArrayBlockingQueue<Message>(Constants.SIZESENDQ, true);
	
	/**
	 * Queue containing messages received
	 * Used By Receive Thread, and Broad Cast Service
	 */
	public static BlockingQueue<Message> recvQueue=new ArrayBlockingQueue<Message>(Constants.SIZESRECVQ, true);;
	

	/**
	 * This is the main.
	 * Starting point of execution for our app.
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
		
		

		if (!readTopology(Constants.TOPOLOGYFILE, Integer.parseInt(args[0]))) {
			System.out
			.println("Error in reading file "+Constants.TOPOLOGYFILE);
			System.out
			.println("Exit");
			return;
		}
		
		if(!ConnectionManager.createConnections(currentNode, connectionSocket,mapNodes)){
			System.out
			.println("Error in creating connections");
			System.out
			.println("Exit");
			return;
		}
		
		//Start the Threads objects
		Thread recvThread;//T2 RECEIVE THREAD
		Thread sendThread;//T1 SEND THREAD
		try {
			sendThread = new Thread(new Sender(),"T1");
			sendThread.start();
			recvThread = new Thread(new Receiver(),"T2");
			recvThread.start();
		} catch (IllegalThreadStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		
		
		//Just a Test
		if(Constants.TESTSENDERRECEIVER && currentNode.getNodeID()==2){
			testSenderReceiver();
		}
		
	
		//wait for all threads to finish
		try {
			if(sendThread!=null)
				sendThread.join();
			if(sendThread!=null)
				sendThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

	}
	
	
	/**
	 * readTopology()
	 * reads the topology files.
	 * and fills the required datastructures.
	 */
	public static boolean readTopology(String fileName,int nodeID){
		System.out.println("Reading config for"+nodeID);
		BufferedReader bReader = null;
		int nodesCount=0;
		try {
			bReader = new BufferedReader(new FileReader(fileName));
			String line = bReader.readLine();
			boolean firstLine=true;
			while(line!=null){
				if(firstLine){
					firstLine=false;
				}else{
					StringTokenizer st = new StringTokenizer(line, ",");
					//1.NODE ID
					int nodeIDLoop=Integer.parseInt((String) st.nextElement());
					//2.IP ADDRESS
					String address=(String)st.nextElement();
					//3.PORT NO
					int portNumber=Integer.parseInt((String) st.nextElement());	
					//4.DELAYFAIL 
					int delayFail=Integer.parseInt((String) st.nextElement());	
					//5.MYVOTES 
					int myVotes=Integer.parseInt((String) st.nextElement());
					//6.TOTALVOTES
					int totalVotes=Integer.parseInt((String) st.nextElement());
					//8.IS_COORDINATOR
					char isCoordinator=((String) st.nextElement()).charAt(0);
					
					NodeDetails nodeObj=new NodeDetails(nodeIDLoop, portNumber, address,delayFail,myVotes,totalVotes,isCoordinator);
					mapNodes.put(nodeIDLoop,nodeObj);
					mapNodesByAddress.put(address+String.valueOf(portNumber),nodeObj);
					nodesCount++;
					
					if(isCoordinator=='Y')
						coordinatorNode=nodeObj;
				}
				line = bReader.readLine();
				if(line!=null && line.length()==0)
					break;
				}
			
			totalNodes=nodesCount;
			
			//All the Node info has been filled
			if(mapNodes.containsKey(nodeID))
				currentNode=mapNodes.get(nodeID);
			else{
				
				System.out.println("*********************************************************");
				System.out.println("Please Supply the correct Process ID"+nodeID);
				System.out.println("*********************************************************");
				System.out.println("Exiting");
				return false;
			}
			}catch (IOException e) {
			e.printStackTrace();
			System.out.println("*********************************************************");
			System.out.println("Exception in reading config"+e.toString());
			return false;
		} finally {
			try {
				if (bReader != null)
					bReader.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}
		return true;
	}
	
	/**
	 * This method checks the Sender & receiver thread by sending a dummy message
	 */
	public static void testSenderReceiver(){
		Message TestMessage=new Message("1", 2, 1, 30, "Hello");
		sendQueue.add(TestMessage);
		DFSMain.applicationRunning=false;
	}

}
