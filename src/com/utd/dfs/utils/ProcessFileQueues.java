package com.utd.dfs.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import com.utd.dfs.DFSMain;
import com.utd.dfs.fs.*;

public class ProcessFileQueues {
	Map<String,Integer> map_responsecount= new HashMap<String,Integer>();
	static FileOperationsCount[] foc;

//	ReadWrite rw= new ReadWrite();
	/**
	 * This method gets one element from each of the file queues and sends the control to read-write thread
	 * @param q
	 */

	public static void process_queue(Queue<FileMessage> q[]){
		
		//to be completed........................ based on readwrite class..........
		while(true ){
			int exit_flag=1;
			for(int i=0; i<q.length;i++){
				if(!q[i].isEmpty()){
					//System.out.println("Index--"+i+"::Queue size---"+q[i].size());
					exit_flag=0;
					FileMessage message=q[i].peek();
					if(message.node_id==DFSMain.currentNode.getNodeID()){
						//System.out.println("MY Node"+DFSMain.currentNode.getNodeID()+":: Message Node"+message.node_id);
						if(!FileSystem.map_filestatus.containsKey(message.file)) {
							//					rw.proceess_input(message);
												FileSystem.map_filestatus.put(message.file, "Pending");
												Thread readWrite=new Thread(new ReadWrite(message),"RWThread"+i);
												readWrite.start();
											}
					
											if(FileSystem.map_filestatus.get(message.file).equals("complete")){
												q[i].poll();
												//message=q[i].peek();
												//FileSystem.map_filestatus.remove(message.file);
											//	rw.proceess_input(message);
											}
					}else{
						q[i].poll();
					}
					
				}
				
			}
			if(exit_flag==1){
				System.out.println("-----------------------------------------------");
				System.out.println("--------D   O     N     E    ------------------------");
				break;
			}
		}
	}

}
