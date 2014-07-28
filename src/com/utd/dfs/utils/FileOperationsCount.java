package com.utd.dfs.utils;

import java.util.TreeMap;
import com.utd.dfs.*;

public class FileOperationsCount {
	private String file_name;
	private int version;
	private int votes_acquired;
	private int response_count;
	private int votes_needed;
	private int local_nodeid;
	public int getLocal_nodeid() {
		return local_nodeid;
	}
	public void setLocal_nodeid(int local_nodeid) {
		this.local_nodeid = local_nodeid;
	}
	Long timeStarted;
	public Long getTimeStarted() {
		return timeStarted;
	}
	public void setTimeStarted(Long timeStarted) {
		this.timeStarted = timeStarted;
	}
	private  TreeMap<Integer,Integer> version_map=new TreeMap<Integer,Integer>();
	
	
	public String getFile_name() {
		return file_name;
	}
	/**
	 * increments the file version and returns the new version
	 * @return
	 */
	public int incrementVersion(){
		this.version=this.version+1;
		return this.version;
	}
	public int getVersion(){
		return this.version;
	}
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	/**
	 * returns filename without version number
	 * @return
	 */
	public String getFileWithoutVersion(){
		String[] split_version=file_name.split("-");
		return split_version[0];
	}
	/**
	 * returns version number for the file in current file system
	 * @return
	 */
	public int myFileVersion() {
		String[] split_version= file_name.split("_");
		this.version= Integer.parseInt(split_version[1].substring(1));
		return this.version;
	}
	/**
	 * add the given votes to the votes_acquired so far
	 * @param votes
	 * @return
	 */
	public int addVodes(int votes){
		this.votes_acquired+=votes;
		return this.votes_acquired;
	}
	/**
	 * Increment number of responses obtained so far
	 * @return
	 */
	public int incrementResponseCount(){
		this.response_count+=1;
		return this.response_count;
	}
	/**
	 * Returns the latest version of the file
	 * @return
	 */
	public int getLatestVersion(){
		return version_map.lastKey();
	}
	/**
	 * This function returns NodeDetails of the Node having latest Version.
	 * @return
	 */
	public Integer getlatest_versionNodeid(){
		if(this.version==getLatestVersion()){
			return local_nodeid;
		}
		return version_map.get(version_map.lastKey());
	}
	/**
	 * checks if the file can be locked or not
	 * @return
	 */
	public synchronized void canLockAll(){
		if(this.votes_acquired>= this.votes_needed){
			notifyAll();
		}
		if(System.currentTimeMillis()-getTimeStarted()>Constants.timeOut){
			System.out.println("Operation timeout");
			notifyAll();
		}
	}
	public boolean checkMajority(){
		if(this.votes_acquired>this.votes_needed){
			return true;
		}
		return false;
	}
	
}
