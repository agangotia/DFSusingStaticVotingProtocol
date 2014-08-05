import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.TreeMap;


public class Testing {
	TreeMap<Long,String> checkOverlap= new TreeMap<Long,String>();
	public boolean testLogfile(String path){
		File f = new File(path);
		String line=null;
		try {
			Scanner readIn= new Scanner(f);
			while(readIn.hasNextLine()){
				line = readIn.nextLine();
				String lineSplit[]= line.split("::");
				Long sTime= Long.parseLong(lineSplit[0],10);
				checkOverlap.put(sTime, lineSplit[1]);
				
			}
			boolean result= checkMap();
			return result;
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			return false;
		}
		
	}
	public boolean checkMap(){
		String prevOpr="";
		Long minStime=Long.MAX_VALUE;
		Long maxEtime=Long.MIN_VALUE;
		for(Long sTime : checkOverlap.keySet()){
			String etimeOper= checkOverlap.get(sTime);
			String[] eTimeOpersplit= etimeOper.split(";;");
			Long eTime= Long.parseLong(eTimeOpersplit[0],10);
			String currOpr= eTimeOpersplit[1];
			if(prevOpr.equals("")){
				prevOpr=currOpr;
				minStime=sTime;
				maxEtime=eTime;
			}
			else if(prevOpr.equals("R")){
				if(currOpr.equals("R")){
					if(eTime>maxEtime){
						maxEtime=eTime;
					}
				}
				else if(currOpr.equals("W")){
					if(maxEtime>sTime){
						System.out.println("********Mutual Exclusion violated***********");
						return false;
					}
					else{
						prevOpr="W";
						minStime=sTime;
						maxEtime=eTime;
					}
				}
			}
			else if(prevOpr.equals("W")){
				if(currOpr.equals("R")){
					if(maxEtime>sTime){
						System.out.println("********Mutual Exclusion violated***********");
						return false;
					}
					else{
						prevOpr="R";
						minStime=sTime;
						maxEtime=eTime;
					}
				}
				else{
					if(maxEtime>sTime){
						if(maxEtime>sTime){
							System.out.println("********Mutual Exclusion violated***********");
							return false;
						}
						else{
							prevOpr="W";
							minStime=sTime;
							maxEtime=eTime;
						}
					}
				}
			}
		}
		System.out.println("********Mutual Exclusion Followed***********");
		return true;
	}
	public static void main(String args[]){
		Testing t = new Testing();
		boolean result=t.testLogfile("test//logRWNode1.log");
		System.out.println("1 "+result);
		
		Testing t2 = new Testing();
		boolean result2=t2.testLogfile("test//logRWNode2.log");
		System.out.println("2 "+result2);
		
		Testing t3 = new Testing();
		boolean result3=t3.testLogfile("test//logRWNode2.log");
		System.out.println("3 "+result3);
		
		System.out.println("Final Result "+(result&result2&result3));
	}
}