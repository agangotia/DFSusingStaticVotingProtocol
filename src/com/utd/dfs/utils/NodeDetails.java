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
	private int delay_fail;
	private int my_votes;
	private int total_votes;
	private char isCoordinator;
	private int opCounts;
	
	


	public int getOpCounts() {
		return opCounts;
	}


	public void setOpCounts(int opCounts) {
		this.opCounts = opCounts;
	}


	public NodeDetails(int nodeId,int portNumber,String address,int delay_fail, int my_votes, int total_votes, char coordinator_address,int opCounts){
		this.nodeID=nodeId;
		this.portNumber=portNumber;
		this.address=address;
		this.delay_fail=delay_fail;
		this.my_votes=my_votes;
		this.total_votes=total_votes;
		this.isCoordinator=coordinator_address;
		this.opCounts=opCounts;
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
	public int getDelay_fail() {
		return delay_fail;
	}
	public void setDelay_fail(int delay_fail) {
		this.delay_fail = delay_fail;
	}
	public int getMy_votes() {
		return my_votes;
	}
	public void setMy_votes(int my_votes) {
		this.my_votes = my_votes;
	}
	public int getTotal_votes() {
		return total_votes;
	}
	public void setTotal_votes(int total_votes) {
		this.total_votes = total_votes;
	}
	
	
	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}
	public char getIsCoordinator() {
		return isCoordinator;
	}


	public void setIsCoordinator(char isCoordinator) {
		this.isCoordinator = isCoordinator;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}