package com.utd.dfs.statustrackers;

import java.util.ArrayList;

import com.utd.dfs.DFSMain;
import com.utd.dfs.fs.DFSFile;
import com.utd.dfs.msg.Message;

public class StatusReadWriteQuorumRequest extends Status {
	
	private int local_nodeid;
	private int local_version;
	private int maxVersionNodeId;
	

	private int votes_acquired;
	

	/**
	 * 1 for read quorum request
	 * 2 for write quorum request
	 */
	private int typeOfRequest;
	
	public StatusReadWriteQuorumRequest(int localNodeId,String fileName,int local_version, int expectedReplies, int typeOfRequest,Object o){
		super(fileName,expectedReplies,o);
		this.local_version=local_version;
		this.local_nodeid=localNodeId;
		this.maxVersionNodeId=localNodeId;
		this.typeOfRequest=typeOfRequest;
		this.votes_acquired=0;
		
	}

	@Override
	public boolean returnDecision() {
		if(typeOfRequest==1){//read Quorum
			if(votes_acquired>=DFSMain.readQuorumSize)
				return true;
			else
				return false;
		}else if(typeOfRequest==2){//write Quorum
			if(votes_acquired>=DFSMain.writeQuorumSize)
				return true;
			else
				return false;
		} 
		return false;
	}
	
	/**
	 * Change this acc to votes weights
	 */
	public void addReply(Message m){
		
		if(Integer.parseInt(m.getData())>0){
			votes_acquired+=Integer.parseInt(m.getData());
			if(m.getFileVersion()>this.local_version){
				this.maxVersionNodeId=m.getSenderNodeID();
				this.local_version=m.getFileVersion();
			}
		}
		super.addReply(m);
	}

	public int getMaxVersionNodeId() {
		return maxVersionNodeId;
	}
	
	public ArrayList<Integer> nodeIdsRepliedyes(){
		return new ArrayList<Integer>();
	}
}
