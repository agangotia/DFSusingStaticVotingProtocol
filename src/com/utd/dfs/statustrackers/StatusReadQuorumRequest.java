package com.utd.dfs.statustrackers;

public class StatusReadQuorumRequest extends Status {
	
	private int local_nodeid;
	private int votes_acquired;
	private int response_count;
	
	public StatusReadQuorumRequest(String fileName, int expectedReplies, Object o){
		super(fileName,expectedReplies,o);
	}

	@Override
	public boolean returnDecision() {
		// TODO Auto-generated method stub
		return false;
	}
	

}
