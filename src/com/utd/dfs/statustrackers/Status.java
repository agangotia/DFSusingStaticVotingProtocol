package com.utd.dfs.statustrackers;

import java.util.ArrayList;
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
	public String getFileName() {
		return fileName;
	}

	private HashMap<Integer,Message> repliesBucket;// replies received
	public HashMap<Integer, Message> getRepliesBucket() {
		return repliesBucket;
	}

	public int expectedReplies;//Total nodes, usually u will exPECT  replies from all
	/**
	 * waitStart used for time out.
	 * Receiver Thread , checks for status objects 
	 * that have timeout, and calls notify explicitly upon them.
	 */
	private long waitStart;
	public long getWaitStart() {
		return waitStart;
	}

	/**
	 * THis is the synchronized wait notify object.
	 * ReadWrite thread, calls wait on this object,
	 * My Status object.addReply() calls notify again on o object,
	 * To indicate read write thread to wait no more,
	 */
	private Object o;
	
	
	public Object getO() {
		return o;
	}

	public Status(String fileName, int expectedReplies, Object o) {
		super();
		this.fileName = fileName;
		this.expectedReplies = expectedReplies;
		this.o = o;
		this.repliesBucket= new HashMap<Integer,Message> ();
		this.waitStart=System.currentTimeMillis();
	}
	
	public int getExpectedReplies() {
		return expectedReplies;
	}

	public void setExpectedReplies(int expectedReplies) {
		this.expectedReplies = expectedReplies;
	}

	public void addReply(Message m){
		repliesBucket.put(m.getSenderNodeID(),m);
		System.out.println("Replies received From"+m.getSenderNodeID()+"Date received is "+m.getData());
		System.out.println("Replies received "+repliesBucket.size());
		System.out.println("Replies expecting "+expectedReplies);
		synchronized(o){
			//if(repliesBucket.size()==expectedReplies || this.returnDecision()==true){
			if(repliesBucket.size()==expectedReplies ){
				System.out.println("Notified RW Thread.This will remove the status object from map");
				o.notify();
			}
		}	
	}
	
	public abstract boolean returnDecision();
	
	public abstract int getMaxVersionNodeId();
	public abstract ArrayList<Integer> nodeIdsRepliedyes();
	public abstract String getContentOfFile(int NodeID);
	public abstract int getVersionOfFile(int NodeID);
}
