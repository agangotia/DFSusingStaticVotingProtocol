package com.utd.dfs.statustrackers;

import java.util.ArrayList;

import com.utd.dfs.DFSMain;
import com.utd.dfs.msg.Message;

public class StatusFileWriteReplies extends Status{

	private int votes_acquired;
	public StatusFileWriteReplies(String fileName, int expectedReplies,Object o){
		super(fileName,expectedReplies,o);
		this.votes_acquired=0;
		
	}
	
	@Override
	public boolean returnDecision() {
	
			if(votes_acquired==this.expectedReplies)
				return true;
			else
				return false;
		
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
	public int getMaxVersionNodeId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ArrayList<Integer> nodeIdsRepliedyes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContentOfFile(int NodeID) {
		// TODO Auto-generated method stub
		return null;
	}
}
