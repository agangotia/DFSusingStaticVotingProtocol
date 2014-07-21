package com.utd.dfs.utils;

/**
 * Class:NodeDetails
 * This contains the Node Information
* @author Anupam Gangotia
* Profile::http://en.gravatar.com/gangotia
* github::https://github.com/agangotia
*/

public class NodeDetails {
	
	private int nodeID;
	private int portNumber;	
	private String address;
	
	NodeDetails(int nodeId,int portNumber,String address){
		this.nodeID=nodeId;
		this.portNumber=portNumber;
		this.address=address;
	}
	
	
	public int getNodeID() {
		return nodeID;
	}

	public void setNodeID(int nodeID) {
		this.nodeID = nodeID;
	}
	
	public Integer getPortNumber() {
		return portNumber;
	}
	public void setPortNumber(Integer portNumber) {
		this.portNumber = portNumber;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}