package com.utd.dfs.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;
import com.sun.nio.sctp.SctpServerChannel;
import com.utd.dfs.DFSMain;
import com.utd.dfs.msg.Message;

/**
 * Connection Manager
 * This class initates the SCTP connections.
 * @author Anupam Gangotia
 * Profile::http://en.gravatar.com/gangotia
 * github::https://github.com/agangotia
*/
public class ConnectionManager {
	
	/**
	 * Function connectHigherNodes
	 * This function connects the following process 
	 * to all the processes with a lower PID
	 */
	public static boolean connectHigherNodes(SctpServerChannel serverChannel,int nodeID,int totalNodes,ConcurrentHashMap<Integer, SctpChannel> connectionSocket){
		
		int i=nodeID + 1;
		while (i <= totalNodes) {
	         SctpChannel sctpChannel;
	         try {
	        	 sctpChannel = serverChannel.accept();
	        	 connectionSocket.put(i, sctpChannel);
	             System.out.println(i + "Connected");
	         } catch (IOException e) {
	        	 System.out.println("Error in Waiting for Higher Nodes to join");
	             e.printStackTrace();
	             return false;
	         }
	         i++;
	     }return true;

	}
	
	/**
	 * Function connectLowerNodes
	 * This function connects the following process 
	 * to all the processes with a lower PID
	 */
	public static boolean connectLowerNodes(int nodeID,ConcurrentHashMap<Integer, SctpChannel> connectionSocket,HashMap<Integer, NodeDetails> mapNodes){
		   
	    for (int i = 1; i < nodeID; i++) {

	        try {
	            SctpChannel sctpChannel;
	            InetSocketAddress Sa= new InetSocketAddress(
	                   mapNodes.get(i).getAddress(),
	                   mapNodes.get(i).getPortNumber());
	            sctpChannel = SctpChannel.open();
	            sctpChannel.connect(Sa);
	            connectionSocket.put(i, sctpChannel);
	            
	            System.out.println(i + "is up and connected");
	            
	            
	        } catch (IOException e) {
	        	System.out.println("This Node is not able to connect to lower Nodes");
	        	System.out.println("Please bring them into existence first");
	        	System.out.println("Exiting");
	        
	            e.printStackTrace();
	            return false;
	        }
	        
	    }
	    return true;
	}
	
	
	/**
	 * This is the main function that needs to be called for creating the connections.
	 * @param currentNode
	 * @param connectionSocket
	 * @param mapNodes
	 * @return
	 */
	public static boolean createConnections(NodeDetails currentNode,ConcurrentHashMap<Integer, SctpChannel> connectionSocket,HashMap<Integer, NodeDetails> mapNodes) {

		int nodeID=currentNode.getNodeID();
		int totalNodes=mapNodes.size();
        SctpServerChannel serverChannel = null;

        try {
        	serverChannel = com.sun.nio.sctp.SctpServerChannel.open();
            InetSocketAddress sA = new InetSocketAddress(
                    currentNode.getAddress(),
                    currentNode.getPortNumber());
            //Binding the server Address
            serverChannel.bind(sA);
        } catch (IOException e) {
        	System.out.println("Process Identification PID-"+nodeID+" ip:port"+currentNode.getAddress()+":"+currentNode.getPortNumber());
            e.printStackTrace();
            return false;
        }
        if(!connectLowerNodes(nodeID,connectionSocket,mapNodes))
        	return false;
        System.out.println(nodeID + "IS UP AND CONNECTED");
        System.out.println(nodeID+"is Waiting for higher nodes to join");
        if(!connectHigherNodes(serverChannel,nodeID,totalNodes,connectionSocket))
        	return false;
        System.out.println(nodeID+"is connected to all");
        return true;
    }
	
	
	public static void  sendMessage(Message msgToSend) {
		 //get the connection object from already stored connections in the map
	        SctpChannel clientSocket = DFSMain.connectionSocket.get(msgToSend.getRecipientNodeID());
	        if(clientSocket==null){
	        	System.out.println("Connection Manager can't get the SCTP Channel object for Recipeint Node Id "+msgToSend.getRecipientNodeID()+" in theconnection Map.");
	        	return;
	        }
	        
	        try {
	        	
	        	sendMessageSCTP(clientSocket, msgToSend);

	        } catch (CharacterCodingException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }

	    }

	    private static void sendMessageSCTP(SctpChannel clientSock, Message message)
	            throws CharacterCodingException,IOException {

	        ByteBuffer Buffer = ByteBuffer.allocate(10000);
	        Buffer.clear();
	        byte[] serialized = null;
	        serialized = serialize(message);
	    

	        // Reset a pointer to point to the start of buffer
	        Buffer.put(serialized);
	        Buffer.flip();

	        try {
	            // Send a message in the channel
	            MessageInfo messageInfo = MessageInfo.createOutgoing(null, 0);
	            clientSock.send(Buffer, messageInfo);
	            String msgPrint="*********************************************";
	            msgPrint+="\nSending Time-"+System.currentTimeMillis()+"\n"
		                +message.printMessage();
	            msgPrint+="\n*********************************************";
	            System.out.println(msgPrint);
	       
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (NullPointerException e) {
	            e.printStackTrace();
	        }
	    }

	    public static byte[] serialize(Object obj) throws IOException {
	        ObjectOutputStream out;
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        out = new ObjectOutputStream(bos);
	        out.writeObject(obj);
	        return bos.toByteArray();
	    }

	
}