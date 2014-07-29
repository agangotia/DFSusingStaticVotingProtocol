package com.utd.dfs.fs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.utd.dfs.DFSMain;
import com.utd.dfs.utils.FileOperationsCount;
import com.utd.dfs.utils.NodeDetails;

public class FileSystem {
	 Map<String,File> fsobject=new HashMap<String,File>();
	 public static HashMap<String,Integer> myFileVersions=new HashMap<String,Integer>();
	/**
	 * Set determines the status of the files that are currently locked 
	 * @param fname
	 * @return
	 */
	public static Map<String,String> map_filestatus= new HashMap<String,String>();//
	public boolean getStatus(String fname){
		if(map_filestatus.get(fname).equals("pending")){
			return false;
		}
		else{
		return true;
		}
	}
	public void lock(String file_name, String lock_type){
		File file_obj=null;
		file_obj= fsobject.get(file_name);
		if(lock_type.equals("R")){
			file_obj.rwl.readLock().lock();
		}
		else{
			file_obj.rwl.writeLock().lock();
		}
	}
	public void checkout(FileOperationsCount foc){
		int latest_nodeid= foc.getlatest_versionNodeid();
		if(foc.getLocal_nodeid()!=foc.getlatest_versionNodeid()){
			//get the latest version from node.. call function in consistency manager class
		}
	
	}
	public void checkin(){
	 
	}
	

}
