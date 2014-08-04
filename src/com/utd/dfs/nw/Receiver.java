package com.utd.dfs.nw;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.sun.nio.sctp.SctpChannel;
import com.utd.dfs.Constants;
import com.utd.dfs.DFSMain;
import com.utd.dfs.fs.DFSCommunicator;
import com.utd.dfs.fs.DFSFile;
import com.utd.dfs.fs.FileSystem;
import com.utd.dfs.msg.Message;
import com.utd.dfs.statustrackers.Status;
import com.utd.dfs.utils.ConnectionManager;
import com.utd.dfs.utils.FileFeatures;

public class Receiver implements Runnable {
	String logFile;
	
	public Receiver(){
		logFile=Constants.LOGFILERCVR+DFSMain.currentNode.getNodeID()+Constants.LOGFILEEND;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		 ByteBuffer byteBuffer= ByteBuffer.allocate(10000);
		while(DFSMain.applicationRunning){
			
			try {

                for (int id : DFSMain.connectionSocket.keySet()) {
                    SctpChannel schnl = DFSMain.connectionSocket.get(id);

                    byteBuffer.clear();
                    schnl.configureBlocking(false);
                    schnl.receive(byteBuffer, null,null);
                    byteBuffer.flip();

                    if (byteBuffer.remaining() > 0) {
                        Message receivedMsg = (Message) deserialize(byteBuffer
                                .array());
               
                        String msgPrint="";
        	            msgPrint+="\nRecieved  Time-"+System.currentTimeMillis()+"\n"
        		                +receivedMsg.printMessage();
        	           
        	  
                        System.out.println(msgPrint);
                        
                        if(Constants.TESTSENDERRECEIVER==true && receivedMsg.getMsgType()==30)//for check
                        	DFSMain.applicationRunning=false;

                    byteBuffer.clear();
                    FileFeatures.appendText(logFile, "Receiver Thread Message Received"+receivedMsg.printMessage());
                    if(receivedMsg.getMsgType()==0 ||receivedMsg.getMsgType()==10){
                    //case 1: When Message Type is 0 & 10
                    //i.e Read or Write Quorum Request
                    ReadWriteQuorumRequest(receivedMsg);
                    }else if(receivedMsg.getMsgType()==1 || receivedMsg.getMsgType()==11 || receivedMsg.getMsgType()==2 || receivedMsg.getMsgType()==4|| receivedMsg.getMsgType()==12 || receivedMsg.getMsgType()==141){
                    	//case 2: When Message Type is 1
                        //i.e Now I have received the Read Yes,
                    	//NodeID-OperationNumber-FileName-Operation
                    	Status obj=DFSCommunicator.mapFileStatus.get(receivedMsg.getMapKeyIdentifier());
                    	if(obj!=null)
                    		obj.addReply(receivedMsg);
                    	else{
                    		System.out.println("Unexpected Can't get the Status Object in Map for file name"+receivedMsg.getMapKeyIdentifier());
                    		System.out.println("Printing Map \n"+printMap());
                    		
                    		FileFeatures.appendText(logFile, "Unexpected Can't get the Status Object in Map for file name"+receivedMsg.getMapKeyIdentifier());
                    		FileFeatures.appendText(logFile, "Printing Map \n"+printMap());
                    		
                    	}
                    	
                    }else if(receivedMsg.getMsgType()==3){
                    	//case 4: When Message Type is 3
                        //i.e Send the latest from ur copy of file
                      
                    	sendLatestFromLocal(receivedMsg);
                    	
                    }else if(receivedMsg.getMsgType()==5){
                    	//case 3: Unlock ur Read Copy Message TYpe 5  
                    	FileSystem.releaseReadLock(receivedMsg.getFileName());
                    	
                    }else if(receivedMsg.getMsgType()==14){
                    	//case 4:Type 14, write the copy received into ur file system.
                    	writeLatestIntoLocal(receivedMsg);
                    	Message tosend=receivedMsg.WriteLatestIntoLocalReply();
        				DFSMain.sendQueue.add(tosend);
                    }else if(receivedMsg.getMsgType()==15){
                    	 //case 5:Type 15, Write Release Lock
                    	writeReleaseLock(receivedMsg);
                    }
            }
                }// end for
                } catch (IOException e) {
	                e.printStackTrace();
	            } catch (ArrayIndexOutOfBoundsException e) {
	            } catch (NullPointerException e) {
	                e.printStackTrace();
	            }finally {
	                
	            }


		}
	}	
	
	public void ReadWriteQuorumRequest(Message receivedMsg){
		//Check in my file System Do i have the lock ,
		// if no one locked send a reply with ur weight.
		if(receivedMsg.getMsgType()==0){//Reading
			if(FileSystem.getWriteLockStatus(receivedMsg.getFileName())){
				Message tosend=receivedMsg.getReadMessageQuorumReplyFalse();
				DFSMain.sendQueue.add(tosend);
			}else{
				FileSystem.lock(receivedMsg.getFileName(),"R");
				Message tosend=receivedMsg.getReadMessageQuorumReplyTrue(DFSMain.currentNode.getMy_votes(),FileSystem.getVersionForFile(receivedMsg.getFileName()));
				DFSMain.sendQueue.add(tosend);
			}
		}else if(receivedMsg.getMsgType()==10){
			if(FileSystem.getWriteLockStatus(receivedMsg.getFileName()) ||FileSystem.getReadLockStatus(receivedMsg.getFileName())){
				Message tosend=receivedMsg.getWriteMessageQuorumReplyFalse();
				DFSMain.sendQueue.add(tosend);
			}else{
				//OOps this was so imp, to lock the file which i missed.
				FileSystem.lock(receivedMsg.getFileName(), "W");
				Message tosend=receivedMsg.getWriteMessageQuorumReplyTrue(DFSMain.currentNode.getMy_votes(),FileSystem.getVersionForFile(receivedMsg.getFileName()));
				DFSMain.sendQueue.add(tosend);
			}
				
		}
	}
	
	public void sendLatestFromLocal(Message receivedMsg){
		//String data=FileSystem.getCachedData(receivedMsg.getFileName());
		String data=FileSystem.getFileObject(receivedMsg.getFileName()).getData();
		int version=FileSystem.getVersionForFile(receivedMsg.getFileName());
		FileFeatures.appendText(logFile, "Sending File Version :"+ version);
		FileFeatures.appendText(logFile, "Sending File Data :"+ data);
		Message localCopy=receivedMsg.sendLatestLocalCopy(data,version);
		DFSMain.sendQueue.add(localCopy);
	}

	public void writeLatestIntoLocal(Message receivedMsg){
		FileSystem.write(receivedMsg.getFileName(), receivedMsg.getData(),receivedMsg.getFileVersion());
	}
	
	public void writeReleaseLock(Message receivedMsg){
		if(receivedMsg.getData().equals("Release")){//Plain release
			FileSystem.releaseWriteLock(receivedMsg.getFileName());
		}else if(receivedMsg.getData().equals("RollBack")){
			FileSystem.releaseWriteLock(receivedMsg.getFileName());
			FileSystem.restorePreviousVersion(receivedMsg.getFileName());
		}
	}
	
			public static Object deserialize(byte[] obj) {
				
				ByteArrayInputStream bos = new ByteArrayInputStream(obj);
				
				try {
					ObjectInputStream in = new ObjectInputStream(bos);
					return in.readObject();
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.print("Error in deserialization");
				}
				return null;
			}
			
			public String printMap(){
				StringBuffer data=new StringBuffer();
				if(DFSCommunicator.mapFileStatus.size()==0){
					return "Unfortunately!! Map is empty.... ";
				}
				for(String key:DFSCommunicator.mapFileStatus.keySet()){
					data.append("Key :"+key+"=>"+"Value :"+DFSCommunicator.mapFileStatus.get(key).getClass().getName());
				}
				return data.toString();
			}
}