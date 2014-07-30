package com.utd.dfs.statustrackers;

import java.util.ArrayList;

import com.utd.dfs.DFSMain;
import com.utd.dfs.msg.Message;

public class StatusGetFile extends Status{

	private int votes_acquired;

	public StatusGetFile(String fileName,Object o){
		super(fileName,1,o);
		this.votes_acquired=0;
	}

	/**
	 * Change this acc to votes weights
	 */
	public void addReply(Message m){
		if(m.getData().equals("Agree")){
			votes_acquired++;
		}
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
		return this.getRepliesBucket().get(NodeID).getData();
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
