package com.utd.dfs.msg;

import java.io.Serializable;

/**
 * Class Message
 *  Messsage Types:
 * 	Type 0 : Read Broadcast Request
 *  Type 1 : Read Reply Yes
 *  Type 2 : Read Reply No
 *  Type 3 : Read "Send Latest" Request
 *  Type 4 : Read "Latest" Reply
 *  
 *  Type 10 :Write Broadcast Request
 *  Type 11 :Write Reply Yes
 *  Type 12 :Write Reply No
 *  
 *  Type 30 :Test Message
 *  
 * @author Anupam Gangotia
 * Profile::http://en.gravatar.com/gangotia
 * github::https://github.com/agangotia
*/
public class Message implements Serializable {



	private static final long serialVersionUID = 1L;
	private String messageId;
	


	private int senderNodeID;
	private int recipientNodeID;
	
	private int msgType;
	
	private String data;
	
	
	
	public Message(){
		
	}
	
	public Message(String messageId, int senderNodeID, int recipientNodeID,
			int msgType, String data) {
		super();
		this.messageId = messageId;
		this.senderNodeID = senderNodeID;
		this.recipientNodeID = recipientNodeID;
		this.msgType = msgType;
		this.data = data;
		//this.logicalClockValue = logicalClockValue;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public int getSenderNodeID() {
		return senderNodeID;
	}

	public void setSenderNodeID(int senderNodeID) {
		this.senderNodeID = senderNodeID;
	}

	public int getRecipientNodeID() {
		return recipientNodeID;
	}

	public void setRecipientNodeID(int recipientNodeID) {
		this.recipientNodeID = recipientNodeID;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public String printMessage(){
		return "MID-"+this.messageId +
				"\nType-"+this.msgType+
				"\nSender-"+this.senderNodeID+
				"\nReciever-"+this.recipientNodeID+
				"\nContent-"+this.data;
	}
}
