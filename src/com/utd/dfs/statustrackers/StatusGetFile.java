package com.utd.dfs.statustrackers;

import java.util.ArrayList;
import java.util.HashMap;

import com.utd.dfs.DFSMain;
import com.utd.dfs.msg.Message;

public class StatusGetFile extends Status{

	private int votes_acquired;
	private HashMap<Integer,Integer> repliesFileVersion;// replies received

	
	
	public StatusGetFile(String fileName,Object o){
		super(fileName,1,o);
		this.votes_acquired=0;
		repliesFileVersion=new HashMap<Integer,Integer>();
	}

	/**
	 * Change this acc to votes weights
	 */
	public void addReply(Message m){
		/*if(m.getData().equals("Agree")){
			votes_acquired++;
		}*/
		votes_acquired++;
		this.repliesFileVersion.put(m.getSenderNodeID(),m.getFileVersion());
		super.addReply(m);
	}
	
	@Override
	public boolean returnDecision() {
			if(votes_acquired>=this.getExpectedReplies())
				return true;
			else
				return false;	
	}

	public String getContentOfFile(int NodeID){
		if(this.getRepliesBucket().size()==0){
			System.out.println("Unable to store the reply of Node , which returned the latest Content");
			return null;
		}else
		return this.getRepliesBucket().get(NodeID).getData();
	}
	public int getVersionOfFile(int NodeID){
		return this.repliesFileVersion.get(NodeID);
	}
	
	@Override
	public int getMaxVersionNodeId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ArrayList<Integer> nodeIdsRepliedyes() {
		// TODO Auto-generated method stub
		return null;
	}
}
