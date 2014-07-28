package com.utd.dfs.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import com.utd.dfs.fs.*;

public class ProessFileQueues {
	Map<String,Integer> map_responsecount= new HashMap<String,Integer>();
	FileOperationsCount[] foc;

//	ReadWrite rw= new ReadWrite();
	/**
	 * This method gets one element from each of the file queues and sends the control to read-write thread
	 * @param q
	 */

	public void process_queue(Queue<FileMessage> q[]){
		foc= new FileOperationsCount[q.length];
		for(int i=0; i<foc.length;i++){
			foc[i]=new FileOperationsCount();
		}
		//to be completed........................ based on readwrite class..........
		while(true){
			int exit_flag=1;
			for(int i=0; i<q.length;i++){
				if(!q[i].isEmpty()){
					FileMessage message=q[i].peek();
					if(!FileSystem.map_filestatus.containsKey(message.file)) {
	//					rw.proceess_input(message);
					}
					if(FileSystem.map_filestatus.get(message.file).equals("complete")){
						q[i].poll();
						message=q[i].peek();
					//	rw.proceess_input(message);
					}
				}
				else{
					exit_flag=0;
				}
			}
			if(exit_flag==1){
				break;
			}
		}
	}

}
