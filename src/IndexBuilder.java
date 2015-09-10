import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class IndexBuilder {
	//Given a built index, the run-time of search depends on the calculation of tf-idf value.
	//the variables to calculate tf & idf value are retrieved by O(1) run-time,
	//since the variables are stored in HashMap.
	//Requires intensive retrieval, HashMaps are used for this reason  .
	final HashMap<String,termFrequency> wordToTF;
	final HashMap<String,Pair> idToRecord;
	HashMap<String,Double> tfidfMap;
	int numDocuments;
	public IndexBuilder(){
		this.tfidfMap=new HashMap<String,Double>();
		this.wordToTF=new HashMap<String,termFrequency>();
		this.idToRecord=new HashMap<String,Pair>();
		System.out.println("Builder is ready!");
	}
	
	void refresh(){ //refresh for a new query.
		this.tfidfMap=new HashMap<String,Double>();
	}
	
	private HashMap<String,Double> exhaust(String term){ //The key function to search run-time.
		//given the search term, calculate score for each document. 
		//run-time: q * (m)
		//score is stored in tfidfMap<documentId,score>.
		if(this.wordToTF.containsKey(term)){
			HashMap<String,ArrayList<Integer>> map = this.wordToTF.get(term).docToList;
			int numDoc=this.numDocuments;
			System.out.println("idf = natural log ( number of documents / number of documents that contain the term");
			System.out.println("Number of documents: " + numDoc);
			
			BigDecimal numerator = new BigDecimal(numDoc);
			int docsContain = map.size();
			System.out.println("documents containing term: " + docsContain);	
			BigDecimal denominator = new BigDecimal(docsContain);
			
			BigDecimal tempidf = numerator.divide(denominator,2, RoundingMode.HALF_UP);
			double divide = tempidf.doubleValue();
		
			double idf = Math.log10(divide);
			System.out.println("idf is: " + idf);
			
			double tfidf;
			for(Map.Entry<String,ArrayList<Integer>> entry : map.entrySet()){
				int tf=entry.getValue().size();
				tfidf = tf * idf;
				String docId = entry.getKey();
				if(tfidfMap.containsKey(docId)){ 
					//score is the sum of score. Adds to previous score.
					double currentScore = tfidfMap.get(docId);
					tfidfMap.put(docId, currentScore + tfidf);
				}
				else{ //otherwise create new key-value pair.
					tfidfMap.put(docId,tfidf);
				}
			}
		}
		System.out.println("*******Done Calculating tf-idf score********");
		return null;
	}
	
	private HashMap<String,Double> show_calc(String term){
		//used for testing. The key to search run-time.
		System.out.println("Term searched: " + term);
		if(this.wordToTF.containsKey(term)){
			HashMap<String,ArrayList<Integer>> map = this.wordToTF.get(term).docToList;
			int numDoc=this.numDocuments;
			BigDecimal numerator = new BigDecimal(numDoc);
			System.out.println("idf = natural log ( number of documents / number of documents that contain the term");
			System.out.println("Number of docs: " + numDoc);
			int docsContain = map.size();
			
			BigDecimal denominator = new BigDecimal(docsContain);
			System.out.println("Map Size " + docsContain);	
			
			BigDecimal tempidf = numerator.divide(denominator, 2, RoundingMode.HALF_UP);
			double divide = tempidf.doubleValue();
		
			double idf = Math.log10(divide);
			System.out.println("idf is: " + idf);
			
			double tfidf;
			for(Map.Entry<String,ArrayList<Integer>> entry:map.entrySet()){
				int tf = entry.getValue().size();
				tfidf = tf * idf ;
				System.out.println("tfidf is: " + tfidf);
				String docId=entry.getKey();
				System.out.println("key (doc ID)= " + docId + ",value(index)= " + entry.getValue() + ",tf is: " + tf);
				if(tfidfMap.containsKey(docId)){
					double currentScore = tfidfMap.get(docId);
					tfidfMap.put(docId, currentScore+tfidf);
				}
				else{
					tfidfMap.put(docId,tfidf);
				}
			}
			System.out.println("UPDATED SCORE:");
			for(Map.Entry<String,Double> entry:tfidfMap.entrySet()){
				System.out.println("key (doc ID)= " + entry.getKey() + ",value(score)= " + entry.getValue());
			}
		}
		System.out.println("Done calculating td-idf score. Run-time is O(q (m)), q for number of terms per query, m for average number of documents that contain a particular word.");
		return null;
	}
	
	public void find(List<String> searchTermArgs){
		//needs 2 STEPS: calculate tf-idf score and list results.
		long startTime = System.nanoTime();
		//get the query results
		for(String term : searchTermArgs){
			//for each search term, calculate the scores for relevant document id.
			exhaust(term.toLowerCase());
		}
		Time(startTime); //the time to calculate tf-idf and to retrieve relevant document/term is printed.
		
		//Having the calculated td*idf scores, list the in descending order.
		ListResults(searchTermArgs); 
		//O(m log(m)) to sort in descending order.
		System.out.println("Enter your search terms or type EXIT: ");
	}
	
	private void ListResults(List<String> searchTermArgs){
		//displayed top ten scores and their information(id,title,author).
		if(this.tfidfMap.size()!=0){
		HashMap<String,Double> finalResult = topTen();
			System.out.println("Results as follow:");
			System.out.println("Search terms:");
			System.out.println(searchTermArgs);
			int count=0;
			for(Map.Entry<String, Double> entry: finalResult.entrySet()){
				if(count > 9){
					break;
				}
				String id=entry.getKey();
				double score = this.tfidfMap.get(id); //each docId with search terms now has a score.
				String book = this.idToRecord.get(id).book;
				String author = this.idToRecord.get(id).author;
				System.out.println("id: " + id + " " + score + " Title: " + book + ", Author: " + author);
				count++;
			}
		}
	}
	
	private HashMap<String,Double> topTen(){
		//helper, sorts the scores.
		//O(m log(m)) to sort the scores, implements Comparator interface.
		//Map ---> List ---> Sort --> SortedList ---> Map
		HashMap<String,Double> sortedMap = sortByComparator(this.tfidfMap);
		
		return sortedMap; //used to print out book title and author.
	}
	
	private static HashMap<String,Double> sortByComparator(HashMap <String,Double> unsorted){
		List<Map.Entry<String, Double>> list=new LinkedList<Map.Entry<String, Double>>(unsorted.entrySet());
		//uses merge sort: O(m log(m)) for sort.
		//Map ---> List ---> Sort --> SortedList ---> Map
		Collections.sort(list,new Comparator<Map.Entry<String, Double>>(){

			@Override
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {
				// TODO Auto-generated method stub
				return (o2.getValue()).compareTo(o1.getValue());
			}	
		});
		
		// Convert sorted map back to a Map		
		HashMap<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		for (Iterator<Map.Entry<String, Double>> it = list.iterator(); it.hasNext();) {
			Entry<String, Double> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	private void Time(long startTime) {
		// TODO Auto-generated method stub
		long endTime = System.nanoTime();
		System.out.println("Took " + (endTime - startTime) + " ns " +"to query.");
	}
	
	private void insertSort(ArrayList<String> sortedTopTenId, String id) {
		// not used anymore. helper, build sorted TopTenId.
		double target= this.tfidfMap.get(id);
		int i;
		for (i=0;i<sortedTopTenId.size();i++){
			String currentID = sortedTopTenId.get(i);
			double currentSCORE=this.tfidfMap.get(currentID);
			if(currentSCORE < target){
				sortedTopTenId.add(i,id);
				return;
			}
		}
		sortedTopTenId.add(i,id);
		return;
	}
	
	private static void insertionSort(ArrayList<Integer> sorted,Integer target){
		//testing insertion sort.
		int i;
		for (i=0;i<sorted.size();i++){
			if(sorted.get(i)<target){
				sorted.add(i, target);
				return;
			}
		}
		sorted.add(i,target);
	}
	
	private static void printMap(HashMap<String,Double> target){
		for(Map.Entry<String, Double> entry: target.entrySet()){
			System.out.println("key: " + entry.getKey() + " value: " + entry.getValue());
		}
	}
	
	public static void main(String args[]){
		//System.out.println("Testing search:");
		//testing insertion sort.
		ArrayList<Integer> places = new ArrayList<>(Arrays.asList(7,8,1,2,4));
		System.out.println(places);
		ArrayList<Integer> sorted=new ArrayList();
		for(Integer item:places){
			insertionSort(sorted,item);
		}
		System.out.println(sorted);
		String test="hmJJIADF";
		System.out.println(test.toLowerCase());
		
		HashMap<String,Double> idtoscore=new HashMap<String,Double>();
		idtoscore.put("1", 98.9);
		idtoscore.put("2", 11.9);
		idtoscore.put("3", 44.9);
		idtoscore.put("4", 100.9);
		idtoscore.put("5", 44.9);
		System.out.println("unsorted map: ");
		printMap(idtoscore);
		
		System.out.println("sorted map: ");
		HashMap<String,Double> result = sortByComparator(idtoscore);
		printMap(result);
	}
	
	
}
