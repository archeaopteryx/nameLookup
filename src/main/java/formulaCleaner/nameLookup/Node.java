package formulaCleaner.nameLookup;

import java.util.ArrayList;

public class Node {
	
	private String name;
	private Node link;
	
	public Node (String nameIn) {
		this(nameIn, null);
	}
	
	public Node (String nameIn, Node linkIn) {
		this.name = nameIn;
		this.link = linkIn;
	}
	
	public String get_name() {
		return this.name; 
	}
	
	public Node get_link() {
		return this.link;
	}
	
	static Node generateSubNames(String nameIn) {
		String[] subStrings = nameIn.split(" ");
		int numEntries = subStrings.length;
		Node firstNode = new Node(subStrings[0]);
		Node prevNode = firstNode;
		String nameString = subStrings[0];
		for(int i =1; i<numEntries; i++) {
			String nextPiece = " "+subStrings[i];
			nameString += nextPiece;
			Node n = new Node(nameString, prevNode);
			prevNode = n;
		}
		return prevNode;//will be full nameIn after for loop
	}
	
	static ArrayList<String> iterateNodes(Node startNode, ArrayList<String> lookupNames) {
		lookupNames.add(startNode.get_name());
		if(startNode.get_link() !=null) {
			iterateNodes(startNode.get_link(), lookupNames);
		}
		return lookupNames;
	}
	
	static ArrayList<String> iterateNodes(Node startNode){
		ArrayList<String> lookupNames = new ArrayList<String>();
		if(startNode.get_link() != null) {
			lookupNames.add(startNode.get_name());
			iterateNodes(startNode.get_link(), lookupNames);
		}
		lookupNames.add(startNode.get_name());
		return lookupNames;
	}

}
