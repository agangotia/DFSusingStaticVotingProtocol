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
	String logFileRWThread;
	public ReadWrite(FileMessage mess) {
		super();
		this.mess = mess;
		logFile=Constants.LOGFILERWTHREAD+DFSMain.currentNode.getNodeID()+Constants.LOGFILEEND;
		logFileM=Constants.LOGFILEMAIN+DFSMain.currentNode.getNodeID()+Constants.LOGFILEEND;
		logFileRWThread=Constants.logFileRWThread+DFSMain.currentNode.getNodeID()+Constants.LOGFILEEND;
		FileFeatures.appendText(logFileRWThread, "THread Started"+this.getName());
	}
	
	public void run(){
		
		String mapKeyIdentifier=DFSMain.currentNode.getNodeID()+"-"+mess.line_index+"-"+mess.file+"-"+mess.operation+"A";
		String mapKeyIdentifierCheckOut=DFSMain.currentNode.getNodeID()+"-"+mess.line_index+"-"+mess.file+"-"+mess.operation+"B";
		
		String mapKeyIdentifierReadLockRelease=DFSMain.currentNode.getNodeID()+"-"+mess.line_index+"-"+mess.file+"-"+mess.operation+"C";
		//MulticastRequestForWriteUpdate
		String mapKeyIdentifierWRITEUPDATE=DFSMain.currentNode.getNodeID()+"-"+mess.line_index+"-"+mess.file+"-"+mess.operation+"D";
		String mapKeyIdentifierWriteLockRelease=DFSMain.currentNode.getNodeID()+"-"+mess.line_index+"-"+mess.file+"-"+mess.operation+"E";

		
		
		System.out.println("OPERATION STARTED --- ");
		FileFeatures.appendText(logFile, "RW Thread For O:"+mess.operation+",F: "+mess.file);
		if(mess.operation.equals("R")){//read operation
			System.out.println("read operation --- ");
			FileFeatures.appendText(logFileM, "Trying Read Operation"+mess.line_index+mess.file);
			if(FileSystem.getStatus(mess.file)){// check for lock
				FileFeatures.appendText(logFile, "RW Thread For O:"+mess.operation+",F: "+mess.file+"Inside ReadLock");
				FileSystem.lock(mess.file, "R");// if not lock acquire lock
			
				Object o=new Object();
				Status objStatus=new StatusReadWriteQuorumRequest(DFSMain.currentNode.getNodeID(),mess.file,FileSystem.fsobject.get(mess.file).getFile_version(), DFSMain.totalNodes-1, 1,o,DFSMain.currentNode.getMy_votes());
				
					//mapKeyIdentifier::NodeID-OperationNumber-FileName-Operation
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
							
								FileSystem.checkout(objStatus,mapKeyIdentifierCheckOut);
							
							
							//Do the Broadcast for Read Lock Release to quorum
							//Type 5 read broadcast lock release
							FileFeatures.appendText(logFile, "RW Thread For O:"+mess.operation+",F: "+mess.file+"MULTICAST LOCK RELEASE");
							DFSCommunicator.MulticastRequestForReadLockRelease(mess.file,NodesYes,mapKeyIdentifierReadLockRelease);
							
							String data=FileSystem.read(mess.file);
							System.out.println("File Read"+data);
							FileSystem.releaseReadLock(mess.file);
							FileSystem.map_filestatus.put(mess.file, "complete");
							FileFeatures.appendText(logFileM, "Read Operation COMPLETE"+mess.line_index+mess.file);
							FileFeatures.appendText(logFile, "RW Thread For O:"+mess.operation+",F: "+mess.file+"FILE READ OPERATION COMPLETE");	
							}else{
							//Call the nodes for release locks.
							DFSCommunicator.MulticastRequestForReadLockRelease(mess.file,NodesYes,mapKeyIdentifier);
							FileSystem.releaseReadLock(mess.file);
							System.out.println("GOing to sleep");
							backOff(DFSMain.currentNode.getDelay_fail());
							System.out.println("Upfrom  sleep");
							synchronized(FileSystem.map_filestatus){
								FileSystem.map_filestatus.remove(mess.file);
								}
							FileFeatures.appendText(logFileM, "Read Operation FAILED"+mess.line_index+mess.file);
							FileFeatures.appendText(logFile, "RW Thread For O:"+mess.operation+",F: "+mess.file+"FILE READ OPERATION FAILED");
							
							return;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
						System.out.println("GOing to sleep");
						backOff(DFSMain.currentNode.getDelay_fail());
						System.out.println("Upfrom  sleep");
						synchronized(FileSystem.map_filestatus){
							FileSystem.map_filestatus.remove(mess.file);
							}
						return;
					}
				}
			}
			else{
				System.out.println("file is locked. Unable to do the read operation "+mess.file);
				FileFeatures.appendText(logFileM, "Read Operation FAILED"+mess.line_index+mess.file);
				System.out.println("GOing to sleep");
				backOff(DFSMain.currentNode.getDelay_fail());
				System.out.println("Upfrom  sleep");
				synchronized(FileSystem.map_filestatus){
					FileSystem.map_filestatus.remove(mess.file);
					}
				
				return;
			}
		
		}
		else{//Write operation
			System.out.println("WRITE operation --- lock status"+FileSystem.getStatus(mess.file));
			FileFeatures.appendText(logFileM, "Trying WRITE Operation"+mess.line_index+mess.file);
			
			if(FileSystem.getStatus(mess.file)){// check for lock
				System.out.println("can write");
				//FileFeatures.appendText(logFileM, "can write");
				
				if(!FileSystem.lock(mess.file, "W")){
					FileSystem.printLockStatus();
					System.out.println("File is locked");
					System.out.println("GOing to sleep");
					backOff(DFSMain.currentNode.getDelay_fail());
					System.out.println("Upfrom  sleep");
					synchronized(FileSystem.map_filestatus){
						FileSystem.map_filestatus.remove(mess.file);
						}
					FileFeatures.appendText(logFileM, " WRITE Operation Failed"+mess.line_index+mess.file);
					
					return;
				}
				
				System.out.println("Locked the file");
				//FileFeatures.appendText(logFileM, "Locked the file");
				//call the broadcast class
				//Type 10 write broadcast request

				Object o=new Object();
				Status objStatus=new StatusReadWriteQuorumRequest(DFSMain.currentNode.getNodeID(),mess.file,FileSystem.fsobject.get(mess.file).getFile_version(), DFSMain.totalNodes-1, 2,o,DFSMain.currentNode.getMy_votes());
				
				
					//mapKeyIdentifier::NodeID-OperationNumber-FileName-Operation
					DFSCommunicator.broadcastWriteRequestForVotes(mess.file,objStatus,mapKeyIdentifier);
					System.out.println("Broadcasted everyone");
				synchronized(o){
					try {
						o.wait();
						System.out.println("Notify CAlled on rw thread");
						//once i have received the replies of votes
						ArrayList<Integer> NodesYes=objStatus.nodeIdsRepliedyes();
						DFSCommunicator.mapFileStatus.remove(mapKeyIdentifier);
						if(objStatus.returnDecision()){//once has the majority
							System.out.println("***GOt the Majority");
							FileSystem.bup(mess.file);
							
							System.out.println("-----Data before check out"+FileSystem.read(mess.file));
							FileSystem.checkout(objStatus,mapKeyIdentifierCheckOut);
							System.out.println("-----Data after check out"+FileSystem.read(mess.file));
							
							FileSystem.append(mess.file, mess.content);
							System.out.println("-----Data Written is "+FileSystem.read(mess.file));
							//Pass on to Consistency Manager to publish the changes to Quorum.
							//Synchronized on map object inside consistency manager and wait
							//till u notify
							
							
							boolean result=DFSCommunicator.MulticastRequestForWriteUpdate(mess.file, NodesYes, FileSystem.read(mess.file), FileSystem.fsobject.get(mess.file).getFile_version(),mapKeyIdentifierWRITEUPDATE);
							
							if(result){// all nodes were able to update the changes
								//Again a Broadcast to release the locks.
								FileSystem.releaseWriteLock(mess.file);
								//FileSystem.setVersionForFile(mess.file, FileSystem.getVersionForFile(mess.file));
								FileSystem.map_filestatus.put(mess.file, "complete");
								System.out.println("File Write Complete, Version Written"+FileSystem.getVersionForFile(mess.file));
								FileFeatures.appendText(logFileM, "WRITE Operation COMPLETE"+mess.line_index+mess.file+"Version Written"+FileSystem.getVersionForFile(mess.file));
								
							}else{//if one of them fails.
								
								FileSystem.releaseWriteLock(mess.file);
								FileSystem.restorePreviousVersion(mess.file);
								System.out.println("GOing to sleep");
								backOff(DFSMain.currentNode.getDelay_fail());
								System.out.println("Upfrom  sleep");
								synchronized(FileSystem.map_filestatus){
									FileSystem.map_filestatus.remove(mess.file);
									}
								FileFeatures.appendText(logFileM, " WRITE FAILED"+mess.line_index+mess.file);
							
								//FileFeatures.bup()//
							}
							
						}else{
							System.out.println("*****Unable in gewtting  the Majority");
							DFSCommunicator.MulticastRequestForWriteLockRelease(mess.file,NodesYes,"Release",mapKeyIdentifierWriteLockRelease);
							FileSystem.releaseWriteLock(mess.file);
							System.out.println("GOing to sleep");
							backOff(DFSMain.currentNode.getDelay_fail());
							System.out.println("Upfrom  sleep");
							synchronized(FileSystem.map_filestatus){
								FileSystem.map_filestatus.remove(mess.file);
								}
							FileFeatures.appendText(logFileM, " WRITE Operation Failed"+mess.line_index+mess.file);
						
							return;
						}
					} catch (InterruptedException e) {
						FileSystem.releaseWriteLock(mess.file);
						System.out.println("GOing to sleep");
						FileSystem.releaseWriteLock(mess.file);
						backOff(DFSMain.currentNode.getDelay_fail());
						System.out.println("Upfrom  sleep");
						synchronized(FileSystem.map_filestatus){
							FileSystem.map_filestatus.remove(mess.file);
							}
						FileFeatures.appendText(logFileM, " WRITE Operation Failed"+mess.line_index+mess.file);
					
						e.printStackTrace();
						return;
					}
				}
			}else{
				System.out.println("File is locked");
				System.out.println("GOing to sleep");
				backOff(DFSMain.currentNode.getDelay_fail());
				System.out.println("Upfrom  sleep");
				synchronized(FileSystem.map_filestatus){
					FileSystem.map_filestatus.remove(mess.file);
					}
				FileFeatures.appendText(logFileM, " WRITE Operation Failed"+mess.line_index+mess.file);
				
				return;
			}
			
		}
		FileFeatures.appendText(logFileRWThread, "THread Terminates"+this.getName());
	}
	
	public void backOff(long delay){
		try {
			System.out.println("Backing OFF AS FAILED");
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
