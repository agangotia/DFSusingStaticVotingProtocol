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

	private String fileName;//This is the FileName for which we are maintaining the datastructure
	private HashMap<Integer,Message> repliesBucket;// replies received
	private int expectedReplies;//Total nodes, usually u will exPECT  replies from all
	/**
	 * waitStart used for time out.
	 * Receiver Thread , checks for status objects 
	 * that have timeout, and calls notify explicitly upon them.
	 */
	private long waitStart;
	/**
	 * THis is the synchronized wait notify object.
	 * ReadWrite thread, calls wait on this object,
	 * My Status object.addReply() calls notify again on o object,
	 * To indicate read write thread to wait no more,
	 */
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
