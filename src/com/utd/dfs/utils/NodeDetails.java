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
	private int operations_count;
	private String coordinator_address;
	
	NodeDetails(int nodeId,int portNumber,String address,int delay_fail, int my_votes, int total_votes, int operations_count, String coordinator_address){
		this.nodeID=nodeId;
		this.portNumber=portNumber;
		this.address=address;
		this.delay_fail=delay_fail;
		this.my_votes=my_votes;
		this.total_votes=total_votes;
		this.operations_count=operations_count;
		this.coordinator_address=coordinator_address;
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
	public int getOperations_count() {
		return operations_count;
	}
	public void setOperations_count(int operations_count) {
		this.operations_count = operations_count;
	}
	public String getCoordinator_address() {
		return coordinator_address;
	}
	public void setCoordinator_address(String coordinator_address) {
		this.coordinator_address = coordinator_address;
	}
	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}