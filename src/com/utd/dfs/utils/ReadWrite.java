package com.utd.dfs.utils;

import java.util.ArrayList;

import com.utd.dfs.DFSMain;
import com.utd.dfs.fs.DFSCommunicator;
import com.utd.dfs.fs.FileSystem;
import com.utd.dfs.statustrackers.Status;
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
				//call the broadcast class
				//Type 0 read broadcast request
				//foc.setTimeStarted(System.currentTimeMillis());// set the tiem out for broadcast
				Object o=new Object();
				Status objStatus=new StatusReadWriteQuorumRequest(DFSMain.currentNode.getNodeID(),mess.file,FileSystem.fsobject.get(mess.file).getFile_version(), DFSMain.totalNodes, 1,o);
				DFSCommunicator.broadcastReadRequestForVotes(mess.file,objStatus);
				synchronized(o){
					try {
						o.wait();
						ArrayList<Integer> NodesYes=objStatus.nodeIdsRepliedyes();
						DFSCommunicator.mapFileStatus.remove(mess.file);
						if(objStatus.returnDecision()){//once has the majority
							FileSystem.checkout(objStatus);
							//Do the Broadcast for Read Lock Release to quorum
							//Type 5 read broadcast lock release
							Object o2=new Object();
							Status objStatus2=new StatusReadWriteQuorumRequest(DFSMain.currentNode.getNodeID(),mess.file,FileSystem.fsobject.get(mess.file).getFile_version(), DFSMain.totalNodes, 1,o2);
							DFSCommunicator.MulticastWriteRequestForVotes(mess.file,NodesYes);
							synchronized(o2){
								try {
									o2.wait();
									DFSCommunicator.mapFileStatus.remove(mess.file);
									if(objStatus2.returnDecision()){
										FileSystem.read(foc.getFile_name());
										FileSystem.map_filestatus.put(mess.file, "Complete");
									}else{
										FileSystem.map_filestatus.remove(mess.file);
										return;
									}
								} catch (InterruptedException e) {
									e.printStackTrace();
									FileSystem.map_filestatus.remove(mess.file);
									return;
								}
								
							}
							
						}else{
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
				foc.setTimeStarted(System.currentTimeMillis());// set the tiem out for broadcast
				synchronized(foc){
					try {
						foc.wait();
						if(foc.checkMajority()){//once has the majority
							FileSystem.bup(mess.file);
							FileSystem.checkout(foc);
							FileSystem.write(foc.getFile_name(), mess.content);
							//Pass on to Consistency Manager to publish the changes to Quorum.
							//Synchronized on map object inside consistency manager and wait
							//till u notify
							
							if(){// all nodes were able to update the changes
								//Again a Broadcast to release the locks.
								FileSystem.releaseLock(foc.getFile_name());
								FileSystem.map_filestatus.put(mess.file, "Complete");	
								
							}else{//if one of them fails.
								FileSystem.map_filestatus.remove(mess.file);
								//FileFeatures.bup()//
								
							}
							
							
							
						}else{
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
