package com.utd.dfs.msg;

import java.io.Serializable;

/**
 * Class Message
 *  Messsage Types:
 * 	Type 0 : Read Broadcast Request
 *  Type 1 : Read Reply Yes quorum wieght
 *  Type 2 : Read Reply No 0
 *  Type 3 : Read "Send Latest" Request
 *  Type 4 : Read "Latest" Reply
 *  Type 5 : Read Release Read Lock
 *  
 *  Type 10 :Write Broadcast Request
 *  Type 11 :Write Reply Yes
 *  Type 12 :Write Reply No
 *  Type 14 : write "updated copy to quorum" Request
 *  Type 141 : write "updated copy to quorum" Reply Yes
 *  Type 15 : write Release  Lock
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

	/**
	 * NodeID-OperationNumber-FileName-Operation
	 */
	private String mapKeyIdentifier;


	public String getMapKeyIdentifier() {
		return mapKeyIdentifier;
	}
	public void setMapKeyIdentifier(String mapKeyIdentifier) {
		this.mapKeyIdentifier = mapKeyIdentifier;
	}
	public Message(String messageId, int senderNodeID, int recipientNodeID,
			int msgType, String data, String fileName, int fileVersion) {
		super();
		this.messageId = messageId;
		this.senderNodeID = senderNodeID;
		this.recipientNodeID = recipientNodeID;
		this.msgType = msgType;
		this.data = data;
		this.fileName = fileName;
		this.fileVersion = fileVersion;
	}
	public Message(String messageId, int senderNodeID, int recipientNodeID,
			int msgType, String data, String fileName, int fileVersion,String mapKeyIdentifier) {
		super();
		this.messageId = messageId;
		this.senderNodeID = senderNodeID;
		this.recipientNodeID = recipientNodeID;
		this.msgType = msgType;
		this.data = data;
		this.fileName = fileName;
		this.fileVersion = fileVersion;
		this.mapKeyIdentifier=mapKeyIdentifier;
	}
	private int senderNodeID;
	private int recipientNodeID;

	private int msgType;

	private String data;

	private String fileName;


	private int fileVersion;

	public Message(){

	}
	public Message(String messageId, int senderNodeID, int recipientNodeID,
			int msgType, String data, String fileName,String mapKeyIdentifier) {
		super();
		this.messageId = messageId;
		this.senderNodeID = senderNodeID;
		this.recipientNodeID = recipientNodeID;
		this.msgType = msgType;
		this.data = data;
		this.fileName = fileName;
		this.mapKeyIdentifier=mapKeyIdentifier;
	}
	public Message(String messageId, int senderNodeID, int recipientNodeID,
			int msgType, String data, String fileName) {
		super();
		this.messageId = messageId;
		this.senderNodeID = senderNodeID;
		this.recipientNodeID = recipientNodeID;
		this.msgType = msgType;
		this.data = data;
		this.fileName = fileName;
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
		
		 String details="";
		 switch(msgType){
		 
		 	case 0:details="Read Broadcast Request";
		 			break;
	
			 case 1:details="Read Reply Yes quorum wieght";
				 break;
			 case 2:details="Read Reply No";
				 break;
			 case 3:details="Send Latest Request";
				 break;
			 case 4:details="Latest File replied";
				 break;
			 case 5:details="Read Release Lock";
				 break;
			 case 10:details="Write Broadcast Request";
				 break;
			 case 11:details="Write Reply Yes";
				 break;
			 case 12:details="Write Reply No";
				 break;
			 case 14:details="write updated copy to quorum";
				 break;
			 case 141:details="write updated Reply";
			 break;
			 case 15:details="write Release  Lock";
				 break;
			 default:details="Invalid Code";
				 break;
		 }		 
		 String msgPrint="\n*********************************************\n";
		return msgPrint+"MID-"+this.messageId +
				"\nType-"+this.msgType+" ;"+details+
				"\nS:-"+this.senderNodeID+
				"R:-"+this.recipientNodeID+
				"\nContent-"+this.data+
				"\nFName-"+this.fileName+" FVersion-"+this.fileVersion+msgPrint;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getFileVersion() {
		return fileVersion;
	}

	public void setFileVersion(int fileVersion) {
		this.fileVersion = fileVersion;
	}

	public Message getReadMessageQuorumReplyTrue(int weight,int version){
		Message msg=new Message(messageId, recipientNodeID, senderNodeID, 1, weight+"", fileName,version,mapKeyIdentifier);
		return msg;
	}
	public Message getReadMessageQuorumReplyFalse(){
		Message msg=new Message(messageId, recipientNodeID, senderNodeID, 2, 0+"", fileName,mapKeyIdentifier);
		return msg;
	}

	public Message getWriteMessageQuorumReplyTrue(int weight,int version){
		Message msg=new Message(messageId, recipientNodeID, senderNodeID, 11, weight+"", fileName,version,mapKeyIdentifier);
		return msg;
	}
	public Message getWriteMessageQuorumReplyFalse(){
		Message msg=new Message(messageId, recipientNodeID, senderNodeID, 12, 0+"", fileName,mapKeyIdentifier);
		return msg;
	}

	public Message sendLatestLocalCopy(String data,int version){
		Message msg=new Message(messageId, recipientNodeID, senderNodeID, 4, data, fileName,version,mapKeyIdentifier);
		return msg;
	}
	
	public Message WriteLatestIntoLocalReply(){
		Message msg=new Message(messageId,recipientNodeID,senderNodeID,141,"Updated",fileName,fileVersion,mapKeyIdentifier);
		return msg;
	}
}
