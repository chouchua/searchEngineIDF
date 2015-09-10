

import java.util.ArrayList;
import java.util.HashMap;


public class termFrequency {
	//map<term,termFrequency> Inverted Index data structure
	//HashMap used for quick look-ups in order to update positional arguments by O(1)
	//given the term(key), know which documents contains the term.
    HashMap<String,ArrayList<Integer>> docToList;  //document id:list
	//this HashMap will be accessed many times during index building.
	
	public termFrequency(String docId,ArrayList<Integer> position){
		//constructor
		this.docToList=new HashMap<String,ArrayList<Integer>>();
		docToList.put(docId,position);
	}
	
	public int getTF(String documentId){
		//needs document id and term to get the length of list is TF.
		return this.docToList.get(documentId).size();
	}
	
	public void addOccurrence(String documentId,int index){;
		ArrayList<Integer> old = this.docToList.get(documentId);
		if(old == null){ //create positional index
			ArrayList<Integer> position = new ArrayList<Integer>();
			position.add(index);
			this.docToList.put(documentId,position);
		}
		else{ //add to current positional index
			//copy list, because cannot modify list value in HashMap
			ArrayList<Integer> newlist = new ArrayList<Integer>();
			for(int i=0;i<old.size();i++){
				newlist.add(old.get(i));
			}
			newlist.add(index);
			this.docToList.put(documentId,newlist);
		}
	}
	
	public static void main (String args[]){
		//testing copy list, because cannot modified list value in HashMap.
		ArrayList<Integer> old = new ArrayList<Integer>();
		old.add(1);
		old.add(2);
		old.add(24);
		ArrayList<Integer> newlist=new ArrayList<Integer>();
		for(int i=0;i<old.size();i++){
			newlist.add(old.get(i));
		}
		System.out.println(newlist);
	}
}
