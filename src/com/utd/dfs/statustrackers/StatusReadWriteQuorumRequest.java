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
	
	public StatusReadWriteQuorumRequest(int localNodeId,String fileName,int local_version, int expectedReplies, int typeOfRequest,Object o,int myvotes){
		super(fileName,expectedReplies,o);
		this.local_version=local_version;
		this.local_nodeid=localNodeId;
		this.maxVersionNodeId=localNodeId;
		this.typeOfRequest=typeOfRequest;
		this.votes_acquired=myvotes;
		
	}

	@Override
	public boolean returnDecision() {
		System.out.println("........");
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
		try{
				
			if(Integer.parseInt(m.getData())>0){
				votes_acquired+=Integer.parseInt(m.getData());
				if(m.getFileVersion()>this.local_version){
					this.maxVersionNodeId=m.getSenderNodeID();
					this.local_version=m.getFileVersion();
				}
			}
			super.addReply(m);
		}catch(Exception ex){
			System.out.println("Data 1"+m.getData());
			System.out.println("Message 2"+m.printMessage());
			ex.printStackTrace();
			
		}
		
	}

	public int getMaxVersionNodeId() {
		return maxVersionNodeId;
	}
	
	public ArrayList<Integer> nodeIdsRepliedyes(){
		ArrayList<Integer> ob=new ArrayList<Integer>();
		for (Integer id : this.getRepliesBucket().keySet()) {
		if(Integer.parseInt(this.getRepliesBucket().get(id).getData())>0)
			ob.add(id);
	}
		return ob;
}

	@Override
	public String getContentOfFile(int NodeID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getVersionOfFile(int NodeID) {
		// TODO Auto-generated method stub
		return 0;
	}
}