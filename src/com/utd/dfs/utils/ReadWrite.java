package com.utd.dfs.utils;

import java.util.ArrayList;

import com.utd.dfs.DFSMain;
import com.utd.dfs.fs.DFSCommunicator;
import com.utd.dfs.fs.FileSystem;
import com.utd.dfs.statustrackers.Status;
import com.utd.dfs.statustrackers.StatusFileWriteReplies;
import com.utd.dfs.statustrackers.StatusReadWriteQuorumRequest;

public class ReadWrite extends Thread{
	FileMessage mess;
	FileOperationsCount foc;
	public ReadWrite(FileMessage mess, FileOperationsCount foc) {
		super();
		this.mess = mess;
		this.foc=foc;
	}
	
	public void run(){
		
		if(mess.operation.equals("R")){//read operation
			if(FileSystem.getStatus(mess.file)){// check for lock
				FileSystem.lock(mess.file, "R");// if not lock acquire lock
			
				Object o=new Object();
				Status objStatus=new StatusReadWriteQuorumRequest(DFSMain.currentNode.getNodeID(),mess.file,FileSystem.fsobject.get(mess.file).getFile_version(), DFSMain.totalNodes, 1,o);
				DFSCommunicator.broadcastReadRequestForVotes(mess.file,objStatus);
				
				synchronized(o){
					try {
						o.wait();
						//once i have received the replies of votes
						ArrayList<Integer> NodesYes=objStatus.nodeIdsRepliedyes();
						DFSCommunicator.mapFileStatus.remove(mess.file);
						if(objStatus.returnDecision()){//once has the majority
							
							FileSystem.checkout(objStatus);
							//Do the Broadcast for Read Lock Release to quorum
							//Type 5 read broadcast lock release
							
							DFSCommunicator.MulticastRequestForReadLockRelease(mess.file,NodesYes);
							
							String data=FileSystem.read(foc.getFile_name());
							FileSystem.releaseReadLock(foc.getFile_name());
							FileSystem.map_filestatus.put(mess.file, "Complete");
								
							}else{
							//Call the nodes for release locks.
							DFSCommunicator.MulticastRequestForReadLockRelease(mess.file,NodesYes);
							FileSystem.releaseReadLock(foc.getFile_name());
							FileSystem.map_filestatus.remove(mess.file);
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
				FileSystem.map_filestatus.remove(mess.file);
				return;
			}
		
		}
		else{//Write operation
			if(FileSystem.getStatus(mess.file)){// check for lock
				FileSystem.lock(mess.file, "W");// if not lock acquire lock
				//call the broadcast class
				//Type 10 write broadcast request

				Object o=new Object();
				Status objStatus=new StatusReadWriteQuorumRequest(DFSMain.currentNode.getNodeID(),mess.file,FileSystem.fsobject.get(mess.file).getFile_version(), DFSMain.totalNodes, 2,o);
				DFSCommunicator.broadcastWriteRequestForVotes(mess.file,objStatus);
				
				synchronized(o){
					try {
						o.wait();
						//once i have received the replies of votes
						ArrayList<Integer> NodesYes=objStatus.nodeIdsRepliedyes();
						DFSCommunicator.mapFileStatus.remove(mess.file);
						if(objStatus.returnDecision()){//once has the majority
							FileSystem.bup(mess.file);
							FileSystem.checkout(objStatus);
							FileSystem.write(foc.getFile_name(), mess.content);
							//Pass on to Consistency Manager to publish the changes to Quorum.
							//Synchronized on map object inside consistency manager and wait
							//till u notify
							
							boolean result=DFSCommunicator.MulticastRequestForWriteUpdate(mess.file, NodesYes, FileSystem.read(mess.file), FileSystem.fsobject.get(mess.file).getFile_version());
							
							if(result){// all nodes were able to update the changes
								//Again a Broadcast to release the locks.
								FileSystem.releaseWriteLock(mess.file);
								FileSystem.map_filestatus.put(mess.file, "Complete");	
								
							}else{//if one of them fails.
								FileSystem.releaseWriteLock(mess.file);
								FileSystem.restorePreviousVersion(mess.file);
								FileSystem.map_filestatus.remove(mess.file);
								//FileFeatures.bup()//
							}
							
						}else{
							DFSCommunicator.MulticastRequestForWriteLockRelease(mess.file,NodesYes,"Release");
							FileSystem.releaseWriteLock(mess.file);
							FileSystem.map_filestatus.remove(mess.file);
							return;
						}
					} catch (InterruptedException e) {
						FileSystem.map_filestatus.remove(mess.file);
						e.printStackTrace();
						return;
					}
				}
			}else{
				FileSystem.map_filestatus.remove(mess.file);
				return;
			}
			
		}
	}
	
}
