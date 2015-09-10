import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
	private int numDocuments=0;
	
	public void readFile(String file,IndexBuilder Builder){
		System.out.println("Reading File: " + file + " to construct the index.");
		try{
			//open input stream for text file.
			InputStream in=new FileInputStream(file);
			
			//create input stream.
			InputStreamReader inRead= new InputStreamReader(in);

			BufferedReader br= new BufferedReader(inRead);
			
			System.out.println("Building index while reading " + file);
	
			//bypass the file setting, save unnecessary comparisons otherwise if included in the below while loop.
			//String passId=byPassId(br);
			//byPass(br,passId);
			readBooks(br,Builder);
			
			//when file is read, the inverted index data structure is READY to be queried.
			 System.out.println("Finished building index, READY for QUERY.");
			 System.out.println("");
			 
			 Builder.numDocuments=numDocuments;
		}
		
		catch(Exception e){
			e.printStackTrace();
		}
		return ;
	}
	
	private void readBooks(BufferedReader br,IndexBuilder Builder) throws IOException{
		//helper
		String line;
		while((line = br.readLine())!=null){
			//Parse line by line, adding to data structure in memory.
			List<String> parsedList = null;
			
			if(line.length()>0 && !line.equals("\\")){
				System.out.print(".");
				//spaces,punctuation split.
				parsedList = new ArrayList<String>(Arrays.asList(line.split("\\W+")));
				String documentId=null;
				if(parsedList.size()>0){
					//if ArrayList exists...otherwise would get index out of bounds exception.
					//first element is the document id.
					numDocuments++;
					documentId=parsedList.get(0);
					List<String> parseSecond = new ArrayList<String>(Arrays.asList(line.split("\\t")));
					
					Pair booknAuthor=new Pair(parseSecond.get(1),parseSecond.get(2));
					Builder.idToRecord.put(documentId, booknAuthor);
					for (int i=1;i<parsedList.size();i++){
						String term=parsedList.get(i).toLowerCase();
						
						if(Builder.wordToTF.get(term)==null){ //get termFrequency.
							ArrayList<Integer> position=new ArrayList<Integer>();
							position.add(i);
							termFrequency newTF=new termFrequency(documentId,position);
							Builder.wordToTF.put(term,newTF);
						}
						else{ //then update the termFrequency.
							termFrequency currentTF = Builder.wordToTF.get(term);
							currentTF.addOccurrence(documentId,i);
						}
					}
				}
			}
		}
	}
	
	private String byPassId(BufferedReader br) throws IOException{
		//helper,by pass the file settings.
		String settingLine;
		settingLine = br.readLine();
		List<String> parsedList = null;
		String passId = null;
		if(settingLine.length()>0 && !settingLine.equals("\\")){
			parsedList = new ArrayList<String>(Arrays.asList(settingLine.split("\\W+")));
			passId = parsedList.get(0);
		}
		return passId;		
	}
	
	private static void byPass(BufferedReader br,String byPassId) throws IOException{
		//bypass the file setting
		String settingLine;
		String documentId=byPassId;
		while(documentId.equals(byPassId)){
			if((settingLine = br.readLine())!=null){
			List<String> parsedList = null;
				if(settingLine.length()>0 && !settingLine.equals("\\")){
					parsedList = new ArrayList<String>(Arrays.asList(settingLine.split("\\W+")));
					if(parsedList.size()>0){
						documentId=parsedList.get(0);
					}
				}
			}				
		}
	}
	
	public static void main(String args[]) throws IOException{
		//test for reading a file.
		//String file="ten_documents.txt";
		String file="title_author_data.tab.txt";
		InputStream in=new FileInputStream(file);
		
		//create input stream.
		InputStreamReader inRead= new InputStreamReader(in);
		
		BufferedReader br = new BufferedReader(inRead);
		String line;
		while((line = br.readLine())!=null){
			//Parse line by line, adding to data structure in memory.
			List<String> parsedList = null;
			if(line.length()>0 && !line.equals("\\")){
				parsedList = new ArrayList<String>(Arrays.asList(line.split("\\t")));
			}
			System.out.println(parsedList);
		}
	}
}
