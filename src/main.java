import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class main {
	public static void main(String args[]){
		//String file="ten_documents.txt";
		//String file ="100617.txt";
		//String file="title_author_data.tab.txt";
		//String file="test.txt";
		String file=args[0];
		IndexBuilder Builder = new IndexBuilder();
		//HashMap<String,termFrequency> wordToTF=new HashMap<String,termFrequency>();
		
		final Parser parser = new Parser();
		
		long startTime = System.nanoTime();
		
		parser.readFile(file,Builder);
		//modifies the IndexBuilder.
		Time(startTime);
		
		//uncomment these to see implementation details on the TF and IDF.
		//System.out.println("SEARCH TERM: from - ");
		//Builder.show_calc("from");
		//System.out.println("***********above is the sample calculation details for TF*IDF for SEARCH TERM \"from\" ********");
		//System.out.println("SEARCH TERM: and - ");
		//Builder.show_calc("and");
		//System.out.println("***********above is the sample calculation details for TF*IDF for SEARCH TERM \"and\" ********");
		
		System.out.println("Total Documents: "+Builder.numDocuments);
		System.out.println("Start Searching.");
		System.out.println("Enter your search terms or type EXIT: ");
		Scanner scanner = new Scanner(System.in);
		List<String> searchTermArgs;
		String input = scanner.nextLine();
		searchTermArgs = new ArrayList<String>(Arrays.asList(input.split("\\W+")));
		
        while (!searchTermArgs.get(0).equalsIgnoreCase("EXIT")) {
        	
        	Builder.refresh();
            //use the search Term to search.	
            Builder.find(searchTermArgs);
            
            input = scanner.nextLine();
            searchTermArgs = new ArrayList<String>(Arrays.asList(input.split("\\W+")));
        }
	}
	
	private static void Time(long startTime) {
		// TODO Auto-generated method stub
		long endTime = System.nanoTime();
		System.out.println("Took " + (endTime - startTime) + " ns " + "to build index.");
	}
	
}
	
