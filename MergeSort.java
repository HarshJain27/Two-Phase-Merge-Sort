
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;

import java.util.ArrayList;
import java.util.List;

import java.io.PrintWriter;
import java.util.Collections;

public class MergeSort {

	static long totalRecords;
	static int totalColumns;
	static LinkedHashMap<String,String> columnList;
	static long allowedTuples;
	static String metaFile;
	static String inputFile;
	static String outputFile;
	static List<Integer> outputColumnList;
	static List<String> outputColumnNames;
	static List<Integer> sortColumnList;
	static List<String> sortColumnNames;
	static int mainMemorySize;
	static String order;
	static int recordSize;
	static List<String> intermediateFiles;
	static long startTime;
	static long endTime;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		startTime = System.currentTimeMillis();
		int i=0;
		while(i<args.length)
		{
			switch(args[i])
			{
			case "--meta_file": metaFile=args[i+1];
				break;
			case "--input_file": inputFile=args[i+1];
				break;
			case "--output_file": outputFile=args[i+1];
				break;
			case "--output_column":
				StringTokenizer st1 = new StringTokenizer(args[i+1],",");
				outputColumnNames=new ArrayList<String>();
				outputColumnList=new ArrayList<Integer>();
				while(st1.hasMoreElements())
				{
					String columnName = st1.nextToken();
					outputColumnNames.add(columnName);
					outputColumnList.add(Integer.parseInt(columnName.substring(columnName.length()-1)));
				}
				break;
			case "--sort_column":
				StringTokenizer st2 = new StringTokenizer(args[i+1],",");
				sortColumnList=new ArrayList<Integer>();
				sortColumnNames=new ArrayList<String>();
				int j=0;
				while(st2.hasMoreElements())
				{
					String columnName = st2.nextToken();
					sortColumnList.add(j,Integer.parseInt(columnName.substring(columnName.length()-1)));
					sortColumnNames.add(j,columnName);
					j++;
				}
				break;
			case "--mm": //mainMemorySize=Integer.parseInt(args[i+1]);
			mainMemorySize=100;
				break;
			case "--order": order=args[i+1];
				break;
			default:System.out.println("Invalid Argument");System.exit(0);
			}
			i+=2;
		}
		
		ParseMeta.parse();
		endTime = System.currentTimeMillis();
		System.out.println("Total  time taken: " + (endTime-startTime) + "ms");
		}

}

 class ParseMeta {

	public static void parse() {
		// TODO Auto-generated method stub
		MergeSort.columnList=new LinkedHashMap<String,String> ();
		MergeSort.recordSize=0;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(MergeSort.metaFile)));
			StringTokenizer token;
			String line;
			while((line=br.readLine()) != null){
				token = new StringTokenizer(line,",");
				String s1=token.nextToken();
				String s2=token.nextToken();
				MergeSort.recordSize+=findSize(s2);
				if(s2.contains("char")) s2=s2.substring(0,s2.indexOf("("));
				MergeSort.columnList.put(s1, s2);
				MergeSort.totalColumns++;
				
			}
			
			MergeSort.allowedTuples=(long) ((MergeSort.mainMemorySize*(0.8)*1024*1024)/MergeSort.recordSize);
			
			BufferedReader reader = new BufferedReader(new FileReader(MergeSort.inputFile));
			MergeSort.totalRecords=0;
			while (reader.readLine() != null) MergeSort.totalRecords++;
			reader.close();
			
			if(MergeSort.allowedTuples!=0 && Math.ceil(MergeSort.totalRecords/MergeSort.allowedTuples)*MergeSort.recordSize > MergeSort.mainMemorySize*1024*1024)
			{
				System.out.println("Two phase merge sort is NOT feasible"); System.exit(0);
			}
			
			ParseRecordsAndSort.parse();
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static int findSize(String s2) {
		// TODO Auto-generated method stub
		int size=0;
		if(s2.contains("char")) 
		{ 
			s2=s2.substring(s2.indexOf("(")+1);
			s2=s2.substring(0,s2.indexOf(")"));
			size=Integer.parseInt(s2);}
		else if(s2.contains("int")) size=6;
		else if(s2.contains("date")) size=10;
		return size;
	}
	

}

 class ParseRecordsAndSort {

	public static void parse() {
		// TODO Auto-generated method stub
		System.out.println("Dividing Input File Started...");
		MergeSort.intermediateFiles=new ArrayList<String>(); 
		List<Tuple> currFileBuffer = new ArrayList<Tuple>();
		String currLine;
		int totalLines=0;
		int totalFiles=0;
		try {
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(MergeSort.inputFile)));
			while(true)
			{
				currLine=br.readLine();
				if(currLine==null || totalLines==MergeSort.allowedTuples){
					totalLines=0;
					Collections.sort(currFileBuffer);
					MergeSort.intermediateFiles.add(totalFiles,"file"+totalFiles);
					PrintWriter pr=new PrintWriter("file"+totalFiles);
					for(Tuple t : currFileBuffer){
						pr.println(t.toString());
					}
					 pr.close();
					 currFileBuffer.clear();
					 totalFiles++;
					 if(currLine==null)
					 {
						br.close();break;
					 }
				}
				
				currFileBuffer.add(totalLines,new Tuple(currLine));
				totalLines++;
			}
			System.out.println("Merging files started...");
			MergePhase.mergeFiles();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	}

}

 class Tuple implements Comparable<Tuple> {

	List<String> values;
	int fileNumber;
	public Tuple(String currLine)
	{
	values=new ArrayList<String>();
	StringTokenizer tc = new StringTokenizer(currLine,",");
	int i=0;
	while(tc.hasMoreTokens()){
		values.add(i, tc.nextToken());
		i++;
	}
	}
	
	public Tuple(String currLine,int fn)
	{
	values=new ArrayList<String>();
	fileNumber=fn;
	StringTokenizer tc = new StringTokenizer(currLine,",");
	int i=0;
	while(tc.hasMoreTokens()){
		values.add(i, tc.nextToken());
		i++;
	}
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder s = new StringBuilder();
		for(int i=0;i<values.size();i++){
			s.append(values.get(i));s.append(",");
		}
		return s.toString();
		
	}
	
	public String toString1() {
		// TODO Auto-generated method stub
		int size=MergeSort.outputColumnList.size();
		int k=0;
		StringBuilder s = new StringBuilder();
		for(int i=0;i<values.size();i++){
			if(MergeSort.outputColumnList.contains(i)) 
			{
				s.append(values.get(i));
				if(k< size-1)
				s.append(",");
				k++;
			}
		}
		return s.toString();
		
	}
	
	@Override
	public int compareTo(Tuple arg) {
		// TODO Auto-generated method stub
		int temp=1;
		if(MergeSort.order.equals("asc")) temp=1;
		else if(MergeSort.order.equals("desc")) temp=-1;
		
		int k,r=0;
		for(k=0;k<MergeSort.sortColumnList.size();k++)
		{
			String dt=MergeSort.columnList.get(MergeSort.sortColumnNames.get(k));
			String temp1=values.get(MergeSort.sortColumnList.get(k));
			String temp2=arg.values.get(MergeSort.sortColumnList.get(k));
			
			if(dt.equals("char"))
			{
				r=temp1.compareTo(temp2);
				if(r==0) continue;
				else if(r>0) return temp;
				else return -temp;
			}
			else if(dt.equals("int"))
			{
				int t1=Integer.parseInt(temp1);
				int t2=Integer.parseInt(temp2);
				if(t1>t2) return temp;
				else if(t2>t1) return -temp;
			}
			else if (dt.equals("date"))
			{
				StringTokenizer st=new StringTokenizer(temp1,"/");
				int day1=Integer.parseInt(st.nextToken());
				int month1=Integer.parseInt(st.nextToken());
				int year1=Integer.parseInt(st.nextToken());
				st=new StringTokenizer(temp2,"/");
				int day2=Integer.parseInt(st.nextToken());
				int month2=Integer.parseInt(st.nextToken());
				int year2=Integer.parseInt(st.nextToken());
				
				if(year1>year2)
				{
					return temp;
				}
				else if(year1<year2)
				{
					return -temp;
				}
				else if(month1>month2)
				{
					return temp;
				}
				else if(month1<month2)
				{
					return -temp;
				}
				else if(day1>day2)
				{
					return temp;
				}
				else if(day1<day2)
				{
					return -temp;
				}

			}
			else {System.out.println("Invalid Datatype..."); System.exit(0);}
		}
		return 0;
	}

}

 class MergePhase {

	public static void mergeFiles() {
		// TODO Auto-generated method stub
		List<Tuple> currFileBuffer = new ArrayList<Tuple>();
		
		try {
		BufferedReader br[]=new BufferedReader[MergeSort.intermediateFiles.size()];
		
		for(int i=0;i<MergeSort.intermediateFiles.size();i++)
		{
				br[i]=new BufferedReader(new FileReader("file"+i));	
		}
		
		PrintWriter pr=new PrintWriter(MergeSort.outputFile);
		
		String line=null;
		int fileCount=0;
		for(int i=0;i<MergeSort.intermediateFiles.size();i++)
		{
			line=br[i].readLine();
			if(line==null)
			{
				fileCount++;
				br[i].close();
			}
			else
			{
				currFileBuffer.add(new Tuple(line,i));
			}
		}
		
		while(fileCount<MergeSort.intermediateFiles.size())
		{
			Collections.sort(currFileBuffer);
			
			if(currFileBuffer!=null) pr.println(currFileBuffer.get(0).toString1());
			
			int fileNum=currFileBuffer.get(0).fileNumber;
			currFileBuffer.remove(0);
			
			line=br[fileNum].readLine();
			if(line==null)
			{
				fileCount++;
				br[fileNum].close();
			}
			else
			{
				currFileBuffer.add(new Tuple(line,fileNum));
			}
		}
		pr.close();
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
