package com.utd.dfs.statustrackers;

import java.util.HashMap;

import com.utd.dfs.msg.Message;

/**
 * Abstract Status Class
 * Keeps track of operations
 * @author Anupam Gangotia Profile::http://en.gravatar.com/gangotia
 *         github::https://github.com/agangotia
 * 
 * @author Dilip Profile:: github::
 * 
 * @author Rashmi Profile:: github::
 */
public  abstract class Status {

	private String fileName;
	private HashMap<Integer,Message> repliesBucket;
	private int expectedReplies;
	private long waitStart;
	private Object o;
	
	
	public Status(String fileName, int expectedReplies, Object o) {
		super();
		this.fileName = fileName;
		this.expectedReplies = expectedReplies;
		this.o = o;
		this.repliesBucket= new HashMap<Integer,Message> ();
		this.waitStart=System.currentTimeMillis();
	}
	
	public void addReply(Message m){
		repliesBucket.put(m.getSenderNodeID(),m);
		synchronized(o){
			if(repliesBucket.size()==expectedReplies || this.returnDecision()==true){
				o.notify();
			}
		}	
	}
	
	public abstract boolean returnDecision();
	
		
}
