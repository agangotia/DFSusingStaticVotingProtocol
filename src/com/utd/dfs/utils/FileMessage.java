package com.utd.dfs.utils;

public class FileMessage {
	String operation;//read or write
	String content=null;
	String file=null;
	int line_index=0;
	int node_id=0;
	public FileMessage(int line_index,int nodeId,String file, String operation, String content) {
		this.operation=operation;
		this.content=content;
		this.file=file;
		this.line_index=line_index;
		this.node_id=nodeId;
	}
		public FileMessage(int line_index,String operation, String content) {
		this.operation=operation;
		this.content=content;
		this.line_index=line_index;
	}

	public String toString(){
		String output;
		if(operation.equals("R")){
			output="R";
		}
		else{
			output="W "+content;
		}
		return output;
	}
}
