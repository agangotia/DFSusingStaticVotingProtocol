package com.utd.dfs.utils;

import java.util.ArrayList;

import com.utd.dfs.Constants;
import com.utd.dfs.DFSMain;
import com.utd.dfs.fs.DFSCommunicator;
import com.utd.dfs.fs.FileSystem;
import com.utd.dfs.statustrackers.Status;
import com.utd.dfs.statustrackers.StatusFileWriteReplies;
import com.utd.dfs.statustrackers.StatusReadWriteQuorumRequest;

public class ReadWrite extends Thread{
	FileMessage mess;
	String logFile;
	String logFileM;
	public ReadWrite(FileMessage mess) {
		super();
		this.mess = mess;
		logFile=Constants.LOGFILERWTHREAD+DFSMain.currentNode.getNodeID()+Constants.LOGFILEEND;
		logFileM=Constants.LOGFILEMAIN+DFSMain.currentNode.getNodeID()+Constants.LOGFILEEND;
	}
	
	public void run(){
		FileFeatures.appendText(logFile, "RW Thread For O:"+mess.operation+",F: "+mess.file);
		if(mess.operation.equals("R")){//read operation
			FileFeatures.appendText(logFileM, "Trying Read Operation"+mess.line_index+mess.file);
			if(FileSystem.getStatus(mess.file)){// check for lock
				FileFeatures.appendText(logFile, "RW Thread For O:"+mess.operation+",F: "+mess.file+"Inside ReadLock");
				FileSystem.lock(mess.file, "R");// if not lock acquire lock
			
				Object o=new Object();
				Status objStatus=new StatusReadWriteQuorumRequest(DFSMain.currentNode.getNodeID(),mess.file,FileSystem.fsobject.get(mess.file).getFile_version(), DFSMain.totalNodes, 1,o);
				
					//mapKeyIdentifier::NodeID-OperationNumber-FileName-Operation
					String mapKeyIdentifier=DFSMain.currentNode.getNodeID()+"-"+mess.line_index+"-"+mess.file+"-"+mess.operation;
					DFSCommunicator.broadcastReadRequestForVotes(mess.file,objStatus,mapKeyIdentifier);
				
				
				FileFeatures.appendText(logFile, "RW Thread For O:"+mess.operation+",F: "+mess.file+"Broadcast Request Sent");
				synchronized(o){
					try {
						o.wait();
						FileFeatures.appendText(logFile, "RW Thread For O:"+mess.operation+",F: "+mess.file+"Broadcast Request Received");
						//once i have received the replies of votes
						ArrayList<Integer> NodesYes=objStatus.nodeIdsRepliedyes();
						DFSCommunicator.mapFileStatus.remove(mapKeyIdentifier);
						FileFeatures.appendText(logFile, "Return Decision returns"+objStatus.returnDecision());
						if(objStatus.returnDecision()){//once has the majority
							
							FileFeatures.appendText(logFile, "RW Thread For O:"+mess.operation+",F: "+mess.file+"MAJORITY FOR READ");
							
								FileSystem.checkout(objStatus,mapKeyIdentifier);
							
							
							//Do the Broadcast for Read Lock Release to quorum
							//Type 5 read broadcast lock release
							FileFeatures.appendText(logFile, "RW Thread For O:"+mess.operation+",F: "+mess.file+"MULTICAST LOCK RELEASE");
							DFSCommunicator.MulticastRequestForReadLockRelease(mess.file,NodesYes,mapKeyIdentifier);
							
							String data=FileSystem.read(mess.file);
							System.out.println("File Read"+data);
							FileSystem.releaseReadLock(mess.file);
							FileSystem.map_filestatus.put(mess.file, "Complete");
							FileFeatures.appendText(logFileM, "Read Operation COMPLETE"+mess.line_index+mess.file);
							FileFeatures.appendText(logFile, "RW Thread For O:"+mess.operation+",F: "+mess.file+"FILE READ OPERATION COMPLETE");	
							}else{
							//Call the nodes for release locks.
							DFSCommunicator.MulticastRequestForReadLockRelease(mess.file,NodesYes,mapKeyIdentifier);
							FileSystem.releaseReadLock(mess.file);
							FileSystem.map_filestatus.remove(mess.file);
							FileFeatures.appendText(logFileM, "Read Operation FAILED"+mess.line_index+mess.file);
							FileFeatures.appendText(logFile, "RW Thread For O:"+mess.operation+",F: "+mess.file+"FILE READ OPERATION FAILED");
							return;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
						FileSystem.map_filestatus.remove(mess.file);
						return;
					}
				}
			}
			else{
				System.out.println("file is locked. Unable to do the read operation "+mess.file);
				FileFeatures.appendText(logFileM, "Read Operation FAILED"+mess.line_index+mess.file);
				FileSystem.map_filestatus.remove(mess.file);
				return;
			}
		
		}
		else{//Write operation
			FileFeatures.appendText(logFileM, "Trying WRITE Operation"+mess.line_index+mess.file);
			
			if(FileSystem.getStatus(mess.file)){// check for lock
				FileSystem.lock(mess.file, "W");// if not lock acquire lock
				//call the broadcast class
				//Type 10 write broadcast request

				Object o=new Object();
				Status objStatus=new StatusReadWriteQuorumRequest(DFSMain.currentNode.getNodeID(),mess.file,FileSystem.fsobject.get(mess.file).getFile_version(), DFSMain.totalNodes, 2,o);
				
				
					//mapKeyIdentifier::NodeID-OperationNumber-FileName-Operation
					String mapKeyIdentifier=DFSMain.currentNode.getNodeID()+"-"+mess.line_index+"-"+mess.file+"-"+mess.operation;
					DFSCommunicator.broadcastWriteRequestForVotes(mess.file,objStatus,mapKeyIdentifier);
				
				synchronized(o){
					try {
						o.wait();
						//once i have received the replies of votes
						ArrayList<Integer> NodesYes=objStatus.nodeIdsRepliedyes();
						DFSCommunicator.mapFileStatus.remove(mapKeyIdentifier);
						if(objStatus.returnDecision()){//once has the majority
							FileSystem.bup(mess.file);
							
							FileSystem.checkout(objStatus,mapKeyIdentifier);
							
							
							FileSystem.write(mess.file, mess.content);
							//Pass on to Consistency Manager to publish the changes to Quorum.
							//Synchronized on map object inside consistency manager and wait
							//till u notify
							
							
							boolean result=DFSCommunicator.MulticastRequestForWriteUpdate(mess.file, NodesYes, FileSystem.read(mess.file), FileSystem.fsobject.get(mess.file).getFile_version(),mapKeyIdentifier);
							
							if(result){// all nodes were able to update the changes
								//Again a Broadcast to release the locks.
								FileSystem.releaseWriteLock(mess.file);
								FileSystem.setVersionForFile(mess.file, FileSystem.getVersionForFile(mess.file));
								FileSystem.map_filestatus.put(mess.file, "Complete");
								System.out.println("File Write Complete");
								FileFeatures.appendText(logFileM, "WRITE Operation COMPLETE"+mess.line_index+mess.file);
								
							}else{//if one of them fails.
								FileSystem.releaseWriteLock(mess.file);
								FileSystem.restorePreviousVersion(mess.file);
								FileSystem.map_filestatus.remove(mess.file);
								FileFeatures.appendText(logFileM, " WRITE FAILED"+mess.line_index+mess.file);
								//FileFeatures.bup()//
							}
							
						}else{
							DFSCommunicator.MulticastRequestForWriteLockRelease(mess.file,NodesYes,"Release",mapKeyIdentifier);
							FileSystem.releaseWriteLock(mess.file);
							FileSystem.map_filestatus.remove(mess.file);
							FileFeatures.appendText(logFileM, " WRITE Operation Failed"+mess.line_index+mess.file);
							return;
						}
					} catch (InterruptedException e) {
						FileSystem.map_filestatus.remove(mess.file);
						FileFeatures.appendText(logFileM, " WRITE Operation Failed"+mess.line_index+mess.file);
						e.printStackTrace();
						return;
					}
				}
			}else{
				FileSystem.map_filestatus.remove(mess.file);
				FileFeatures.appendText(logFileM, " WRITE Operation Failed"+mess.line_index+mess.file);
				return;
			}
			
		}
	}
	
}
