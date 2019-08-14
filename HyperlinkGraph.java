// Aaron Barlev 2019

import java.util.*;
import java.text.*;

public class HyperlinkGraph {
	private ArrayList<String> nodes; // List of nodes in the graph
	private HashMap<String, ArrayList<String>> adjacents; // HashMap of nodes (keys) and lists of outgoing edges (values)
	private int numNodes; // Total number of nodes
	private int numEdges; // Total number of edges

	// Constructor for empty graph
	public HyperlinkGraph() {
		nodes = new ArrayList<String>();
		adjacents = new HashMap<String, ArrayList<String>>();
		numNodes = numEdges = 0;

	}

	// Adds a node to the graph with no edges
	public void addNode(String name) {
		if (!(nodes.contains(name))) {
			nodes.add(name);
			adjacents.put(name, new ArrayList<String>());
			numNodes++;
		}
	}

	// Checks if a node exists in the graph
	public boolean hasNode(String name) {
		return nodes.contains(name);
	}

	// Checks if an edge exists in the graph
	public boolean hasEdge(String from, String to) {
		if (!hasNode(from) || !hasNode(to)) {
			return false;
		}
		return adjacentTo(from).contains(to);
	}
	
	// Adds an edge to the graph
	// Creates a node if nonexistent in the graph
	public void addEdge(String from, String to) {
		if (hasEdge(from, to)){
			return;
		}
		numEdges += 1;
		if (!(hasNode(from))) {
			addNode(from);
		}
		if (!(hasNode(to))) {
			addNode(to);
		}
		adjacentTo(from).add(to);
	}

	// Returns list of all nodes
	public ArrayList<String> getNodes() {
		return nodes;
	}
	
	// Returns list of adjacent nodes
	public ArrayList<String> adjacentTo(String n) {
		return adjacents.get(n);
	}

	// Returns total number of nodes
	public int numNodes() {
		return numNodes;
	}
	
	// Returns total number of edges
	public int numEdges() {
		return numEdges;
	}

	// Computes the strength between two web pages in a graph
	public static double hyperlinkPredictionStrength(HyperlinkGraph G, String a, String b, int N, double alpha, double beta) {
		double adjacencyStrength = alpha * G.adjacentTo(a).size();
		double pathStrength = 0;
		for (int i = 1; i <= N; i++) {
			pathStrength += Math.pow(beta, i) * numPaths(G, a, b, i);
		}
		return (adjacencyStrength + pathStrength);
	}

	// Computes the number of paths from a to b of length l in G using DFS
	public static int numPaths(HyperlinkGraph G, String a, String b, int length) {
		return numPathsUtil(G, G.adjacentTo(a), b, 1, length);
	}

	// Utility function for numPaths
	public static int numPathsUtil(HyperlinkGraph G, ArrayList<String> curr, String b, int depth, int length) {
		int total = 0;
		for (int i = 0; i < curr.size(); i++) { 
            String node = curr.get(i);

            // If the node equals the final node and the depth equals the target length, increment the total count
            if (node.equals(b) && depth == length) {
				total += 1;
			}
			// Continue searching as long as the depth has not exceeded the target length
			else if (depth <= length) {
				total += numPathsUtil(G, G.adjacentTo(node), b, depth + 1, length);
			}
        }
		return total;
	}
	
	public static void main(String[] args) {

		// Creates the graph G and adds all edges
		HyperlinkGraph G = new HyperlinkGraph();

		G.addEdge("usnews.com", "umd.edu");

		G.addEdge("umd.edu", "cs.umd.edu");

		G.addEdge("thediamondback.com", "umd.edu");
		G.addEdge("thediamondback.com", "visitmaryland.org");
		
		G.addEdge("cs.umd.edu", "umd.edu");

		G.addEdge("en.wikipedia.org", "visitmaryland.org");
		G.addEdge("en.wikipedia.org", "baltimoresun.com");
		G.addEdge("en.wikipedia.org", "umd.edu");
		G.addEdge("en.wikipedia.org", "cs.umd.edu");

		G.addEdge("visitmaryland.org", "marylandpublicschools.org");

		G.addEdge("twitter.com", "usnews.com");
		G.addEdge("twitter.com", "thediamondback.com");
		G.addEdge("twitter.com", "baltimoresun.com");

		G.addEdge("bloomberg.com", "usnews.com");
		G.addEdge("bloomberg.com", "umd.edu");

		G.addEdge("marylandpublicschools.org", "visitmaryland.org");

		G.addEdge("news.maryland.gov", "visitmaryland.org");
		G.addEdge("news.maryland.gov", "marylandpublicschools.org");

		G.addEdge("baltimoresun.com", "usnews.com");
		G.addEdge("baltimoresun.com", "bloomberg.com");
		G.addEdge("baltimoresun.com", "thediamondback.com");
		
		// Prints each node with its edges
		System.out.println();
		for (String n1 : G.getNodes()) {
			System.out.print("From: " + n1 + ", to: ");
			for (String n2 : G.adjacentTo(n1)) {
				System.out.print(n2 + "  ");
			}
			System.out.println();
		}
		System.out.println();

		DecimalFormat df = new DecimalFormat("#.###");
		double d;
		
		// Test 1 (alpha = 0.1, beta = 0.5)
		TreeMap<Double, String> links = new TreeMap<>(Collections.reverseOrder()); 
		double alpha = 0.1;
		double beta = 0.5;

		System.out.println("Hyperlink-Prediction Strength (alpha = "+alpha+", beta = "+beta+"): ");
		for (int i = 0; i < G.getNodes().size(); i++) { 
			for (int j = 0; j < G.getNodes().size(); j++) { 
				String node1 = G.getNodes().get(i);
				String node2 = G.getNodes().get(j);

				if (!node1.equals(node2) && !(G.hasEdge(node1, node2))) {
					d = hyperlinkPredictionStrength(G, node1, node2, G.numNodes(), alpha, beta);
         			links.put(d, node1+" -> "+node2);
         		}
			}
		}        
  		
        for (Map.Entry<Double, String> entry : links.entrySet()) {
            d = entry.getKey();
        	if (d > (alpha + beta)) {
        		System.out.print(entry.getValue() + ": ");
        		System.out.println(df.format(d));
			}
		}

		// Test 2 (alpha = 0.4, beta = 0.6)
		links.clear(); 
		alpha = 0.4;
		beta = 0.6;

		System.out.println();
		System.out.println("Hyperlink-Prediction Strength (alpha = " + alpha + ", beta = " + beta + "): ");
		for (int i = 0; i < G.getNodes().size(); i++) { 
			for (int j = 0; j < G.getNodes().size(); j++) { 
				String node1 = G.getNodes().get(i);
				String node2 = G.getNodes().get(j);

				if (!node1.equals(node2) && !(G.hasEdge(node1, node2))) {
					d = hyperlinkPredictionStrength(G, node1, node2, G.numNodes(), alpha, beta);
         			links.put(d, node1+" -> "+node2);
         		}	
			}
		}        
  		
        for (Map.Entry<Double, String> entry : links.entrySet()) {
            d = entry.getKey();
        	if (d > (alpha + beta)) {
        		System.out.print(entry.getValue() + ": ");
        		System.out.println(df.format(d));
			}
		}

		// Test 3 (alpha = 0.05, beta = 0.95)
		links.clear(); 
		alpha = 0.05;
		beta = 0.95;

		System.out.println();
		System.out.println("Hyperlink-Prediction Strength (alpha = "+alpha+", beta = "+beta+"): ");
		for (int i = 0; i < G.getNodes().size(); i++) { 
			for (int j = 0; j < G.getNodes().size(); j++) { 
				String node1 = G.getNodes().get(i);
				String node2 = G.getNodes().get(j);

				if (!node1.equals(node2) && !(G.hasEdge(node1, node2))) {
					d = hyperlinkPredictionStrength(G, node1, node2, G.numNodes(), alpha, beta);
         			links.put(d, node1+" -> "+node2);
         		}
			}
		}        
  		
        for (Map.Entry<Double, String> entry : links.entrySet()) {
            d = entry.getKey();
        	if (d > (alpha + beta)) {
        		System.out.print(entry.getValue() + ": ");
        		System.out.println(df.format(d));
			}
		}
	}
}