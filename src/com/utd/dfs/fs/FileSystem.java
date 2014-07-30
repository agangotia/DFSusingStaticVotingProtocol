package com.utd.dfs.fs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.utd.dfs.DFSMain;
import com.utd.dfs.statustrackers.Status;
import com.utd.dfs.statustrackers.StatusGetFile;
import com.utd.dfs.statustrackers.StatusReadWriteQuorumRequest;
import com.utd.dfs.utils.FileOperationsCount;
import com.utd.dfs.utils.NodeDetails;


public class FileSystem {
	
	/**
	 * Map of File name and file object
	 */
	 public static Map<String,DFSFile> fsobject=new HashMap<String,DFSFile>();
	
	 /**
		 * Map of File name and its versions.
		 * Initially viersion =0,
		 * check and remove 
		 */
	 public static HashMap<String,Integer> myFileVersions=new HashMap<String,Integer>();
	


	 
	 /**
	 * Set determines the status of the files that are currently locked 
	 * @param fname
	 * @return
	 */
	public static Map<String,String> map_filestatus= new HashMap<String,String>();//
	
	 /**
		 * Fills the File system with data
		 */
	public static boolean buildFileSystem(Set<String> fileNames){
		if(fileNames==null || fileNames.size()==0)
			return false;
		else{
			for(String t:fileNames){
				DFSFile file=new DFSFile(t, 0, "");
				fsobject.put(t, file);
			}
			return true;
		}
		
	}
	
	
	public static boolean getStatus(String fname){
		if(map_filestatus.get(fname).equals("pending")){
			return false;
		}
		else{
		return true;
		}
	}
	public static void lock(String file_name, String lock_type){
		DFSFile file_obj=null;
		file_obj= fsobject.get(file_name);
		synchronized(file_obj){
		if(lock_type.equals("R")){
			if(file_obj.readLockCount==0)
				file_obj.rwl.readLock().lock();
			file_obj.readLockCount++;
		}
		else{
			file_obj.rwl.writeLock().lock();
		}
		}
	}
	public static void checkout(Status foc){
		if(DFSMain.currentNode.getNodeID()!=foc.getMaxVersionNodeId()){
			//get the latest version from node.. call function in consistency manager class
			int version=foc.getMaxVersionNodeId();
			
			Object o=new Object();
			Status objStatus=new StatusGetFile(foc.getFileName(),o);
			DFSCommunicator.unicastGetlatestForRead(foc.getMaxVersionNodeId(), foc.getFileName(),objStatus);
			synchronized(o){
				try {
					o.wait();
					
					String data=objStatus.getContentOfFile(foc.getMaxVersionNodeId());
					fsobject.get(foc.getFileName()).setFile_version(version);
					fsobject.get(foc.getFileName()).setData(data);
					DFSCommunicator.mapFileStatus.remove(foc.getFileName());
					
					}catch(InterruptedException ex){
						ex.printStackTrace();
						
					}
			}
		}
	
	}
	
	public static String read(String file_name){
		DFSFile file_obj= fsobject.get(file_name);
		return file_obj.read();
	}
	
	public static void write(String file_name,String data){
		DFSFile file_obj= fsobject.get(file_name);
		 file_obj.append( data);
	}
	
	public static  void checkin(){
	 
	}
	
	public static void bup(String file_name){
		DFSFile file_obj= fsobject.get(file_name);
		 file_obj.backup_original();
	}
	
	public static void restorePreviousVersion(String file_name){
		DFSFile file_obj= fsobject.get(file_name);
		file_obj.restorePreviousVersion();
	}
	
	public static void releaseWriteLock(String fileName){
		DFSFile file_obj= fsobject.get(fileName);
		 file_obj.releaseWrite();
	}
	public static void releaseReadLock(String fileName){
		DFSFile file_obj= fsobject.get(fileName);
		 file_obj.relaseRead();
	}
	

}
