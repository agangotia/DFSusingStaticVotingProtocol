package com.utd.dfs.utils;

public class FileMessage {
	String operation;//read or write
	String content=null;
	public FileMessage(String operation, String content) {
		this.operation=operation;
		this.content=content;
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
