package com.ltx.path;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;


/**
 * This class provide functionality to add route connectivity between two end-point (cities) and retrieving 
 * connection information. This is a bidirectional connectivity between two end point.
 * Method route(...) return the first shortest(minimum or equal no of stops to any other alternative route) route found, though there is possible of multiple routes between two end-point. 
 * In case of multiple route exist between two end-point, method route(...) will return same first route every time.
 * This class is thread safe.
 * 
 * @author PradeepKrChahal
 */
public class RouteManagerImpl implements RouteManager {

	//By using concurrentHashMap with value as CopyOnWriteArraySet, will not require 
	//to take lock for adding connection and retrieving route from different thread.
	//CopyOnWriteArraySet provide changes in connection between two threads and  iterator of end-point in
	// same order as cities has been added. Same order will help to return always same first route between two
	//city1 to city2 and city2 to city1 without any sorting.
	//If we had not expose addConnection() method and had initialize all route in constructor, we could use regular HashMap.
	private final Map<String, CopyOnWriteArraySet<String>> allNodes = new ConcurrentHashMap<>();

	/**
	 * 
	 * Constructor.
	 */
	public RouteManagerImpl() {

	}

	/**
	 * Constructor.
	 * This constructor accept the Edge iterator and initialize initial state with it.
	 * 
	 * @param edgeIterator
	 *            By accepting a iterator, we can get data from various source.
	 */
	public RouteManagerImpl(final Iterator<Edge> edgeIterator) {
		while (edgeIterator.hasNext()) {
			final Edge edge = edgeIterator.next();
			// add connection in both direction.
			addConnection(edge.getStartNode(), edge.getEndNode());
		}
	}

	/**
	 * This function return true if tow city is connected.
	 * @param city1  source city
	 * @param city2 destination city
	 */
	@Override
	public boolean connected(String city1, String city2) {
		// we just need to know if a route exist.
		if(verifyEndPointNameValidity(city1, city2)){
		     return findRoute(city1.trim(), city2.trim()).size() > 0;
		}
		return false;
	}

	/**
	 * This function return first route route found, this route consist lesser or equal number of stops if alternative routes exist.
	 * @param city1  source city
	 * @param city2 destination city
	 */
	@Override
	public List<String> getRoute(String city1, String city2) {

		if(verifyEndPointNameValidity(city1, city2)){
		// find first shortest(minimum number of stops) route city1 to city2
		return  findRoute(city1.trim(), city2.trim());
		}
		return Collections.emptyList();
	

	}

	/**
	 * Add connectivity in both direction. will ignore if city1 and city2 is
	 * same.
	 * 
	 * @param city1
	 * @param city2
	 */
	@Override
	public final void addConnection(String city1, String city2) {
		//this is not a atomic operation, there is chance that at a particular time we might find connectivity between city1
		// to city2 but might not find connectivity between city2 to city1. 
		//
		if (verifyEndPointNameValidity(city1, city2)) {
			city1 = city1.trim();
			city2 = city2.trim();
			addEdge(city1, city2);
			addEdge(city2, city1);
			
		}else{
			//log message, dont want include sl4j dependency, logging to console
			System.out.println("invalid source or desitinatio: source: "+ city1 + " , destination: " + city2);
		}
	}

	/**
	 * This function search city2 route by iterating over all neighbors of
	 * city1. Its utilizing level traversal. There is a possibility of multiple
	 * routes between city1 and city2, this function return first shortest(minimum number of stops) route found.  If source or destination
	 * does not exist in routes, empty list will be returned. If no route is found, empty list will be returned.
	 * 
	 * @param city1
	 *            source
	 * @param city2
	 *            destination
	 * 
	 */
	private List<String> findRoute(String city1, String city2) {
		if(isEndPointExist(city1, city2)){
			final Queue<LinkedList<String>> nodesToSearched = new LinkedList<>();
			final Set<String> nodesVisitied = new HashSet<String>();
			// current path, using LinkedList as it provide getLast() and use less memory
			LinkedList<String> currentPath = new LinkedList<>();
			currentPath.add(city1);
			nodesToSearched.add(currentPath);
			nodesVisitied.add(city1);
			
			while (!nodesToSearched.isEmpty()) {
				currentPath = nodesToSearched.poll();
				final String currentNode = currentPath.getLast();

				if (currentNode.equalsIgnoreCase(city2)) {
					return currentPath; 
					// if we are interested in finding first route, this path will consist lesser or equal number of stops if there
					// exist any other alternative route.
				}

				final Set<String> allConnectedNodes = allNodes.get(currentNode);
				for (String connectedNode : allConnectedNodes) {
					if (!nodesVisitied.contains(connectedNode)) {
						// we need to copy previous path and append new city as we don't know on in which one we find destination.w 
						LinkedList<String> newPossiblePath = new LinkedList<>(currentPath);
						newPossiblePath.add(connectedNode);
						nodesToSearched.add(newPossiblePath);
						nodesVisitied.add(connectedNode);
					}
				}

			}
		}

		return Collections.emptyList();
	}

	/**
	 * This function add connectivity between nodeOne to nodeTow in one direction (nodeOne->nodeTwo)
	 * Two make bidirectional connectivity need call this function twice by toggling param twice 
	 * @param nodeOne   first end point of edge
	 * @param nodeTwo second end point of edge
	 */
	private void addEdge(String nodeOne, String nodeTwo) {
		CopyOnWriteArraySet<String> connectedNodes = allNodes.get(nodeOne);
		if (connectedNodes == null) {
			connectedNodes = new CopyOnWriteArraySet<>();
			allNodes.put(nodeOne, connectedNodes);
		}
		connectedNodes.add(nodeTwo);
	}

	/**
	 * This function verify validity for input params, check for null and if both source and destination is same.
	 * @param source   city1 (source city)
	 * @param destination city2 (destination city)
	 */
	private boolean verifyEndPointNameValidity(final String source, final String destination) {

		if(source == null || destination == null) {return false;}
		
		if (!source.trim().equalsIgnoreCase(destination.trim())) {return true;}
		
		return false;
	}
	
	/**
	 * This function assume null checked and prefix or suffix space already been verified.
	 * this function return true if both source node and destination are different and exist.
	 * @param source   city1 (source city)
	 * @param destination city2 (destination city)
	 */
	private boolean isEndPointExist(final String source, final String destination) {
		
		if (!source.equalsIgnoreCase(destination) && allNodes.containsKey(source) && allNodes.containsKey(destination)) {return true;}
		
		return false;
	}

	/**
	 * this class warp two node (city) connectivity.
	 * 
	 * @author PradeepKrChahal
	 *
	 */
	public static class Edge {
		private final String startNode;
		private final String endEnd;

		public Edge(final String nodeOne, String nodeTwo) {
			// trim city name
			this.startNode = nodeOne.trim();
			this.endEnd = nodeTwo.trim();
		}

		public String getStartNode() {
			return startNode;
		}

		public String getEndNode() {
			return endEnd;
		}

	}
	
	/**
	 * 
	 * Abstract class two load initial routed data
	 */
	public static abstract class RoutesLoader implements Iterator<Edge>{
		private final String delimiter;
		private Edge nextEdge;
		
		public Edge getNextEdge() {
			return nextEdge;
		}

		public void setNextEdge(Edge nextEdge) {
			this.nextEdge = nextEdge;
		}

		public String getDelimiter() {
			return delimiter;
		}

		public RoutesLoader(String delimiter){
			this.delimiter = delimiter;
		}
      
		@Override
		public Edge next() {
			return getNextEdge();
		}
		@Override
		public void remove() {
			throw new RuntimeException("Not supported.");
		}
		
	}

	/**
	 * File iterator, not thread safe, for single use only.
	 * 
	 * @author PradeepKrChahal
	 *
	 */
	public static class FileRoutesLoader extends RoutesLoader {

		private final BufferedReader bufferedReader;
		
		/**
		 * Constructor.
		 * 
		 * @param file
		 * @param delimiter
		 * @throws java.io.FileNotFoundException
		 */
		public FileRoutesLoader(final File file, String delimiter) throws FileNotFoundException {
			super(delimiter);
			final Reader fileReader = new FileReader(file);
			this.bufferedReader = new BufferedReader(fileReader);

		}

		@Override
		public boolean hasNext() {
			try {
				String line = bufferedReader.readLine();
				while (line != null && line.isEmpty()) {
					// read next line until file is over or a valid line.
					line = bufferedReader.readLine();
				}
				if (line != null && !line.isEmpty()) {
					String[] nodes = line.split(getDelimiter());
					if (nodes.length == 2) {
						setNextEdge(new Edge(nodes[0], nodes[1]));
						return true;
					}
					bufferedReader.close();
					return false;
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return false;

		}		

	}

}
