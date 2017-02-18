/* Time spent on a7:  hh hours and mm minutes.

 * Name:
 * Netid: 
 * What I thought about this assignment:
 *
 *
 */

package student;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import graph.Edge;
import graph.Node;
import heap.Heap;

/** This class contains Dijkstra's shortest-path algorithm and some other methods. */
public class Paths {

    /** Return a list of the nodes on the shortest path from start to 
     * end, or the empty list if a path does not exist.
     * Note: The empty list is NOT "null"; it is a list with 0 elements. */
    public static List<Node> shortestPath(Node start, Node end) {
        /* TODO Implement Dijkstras's shortest-path algorithm presented
         in the slides titled "Final Algorithm" (slide 37 or so) in the slides
         for lecture 20.
         In particular, a min-heap (as implemented in assignment A6) should be
         used for the frontier set. We provide a declaration of the frontier.
         We will make our solution to min-heap available as soon as the deadline
         for submission has passed.

         Maintaining information about shortest paths will require maintaining
         for each node in the settled and frontier sets the backpointer for
         that node, as described in the A7 handout, along with the length of the
         shortest path (thus far, for nodes in the frontier set). For this
         purpose, we provide static class SFdata. You have to declare
         the HashMap field for it and describe carefully what it
         means. WRITE A PRECISE DEFINITION OF IT.

         Note 1: Read the notes on pages 2..3 of the handout carefully.
         Note 2: The method MUST return as soon as the shortest path to node
                 end has been calculated. In that case, do NOT continue to find
                 shortest paths. You will lost 15 points if you do not do this.
         Note 3: the only graph methods you may call are these:

            1. n.getExits():  Return a List<Edge> of edges that leave Node n.
            2. e.getOther(n): n must be one of the Nodes of Edge e.
                              Return the other Node.
            3. e.length():    Return the length of Edge e.

         Method pathDistance uses one more: n1.getEdge(n2)
         */

     // The frontier set, as discussed in lecture 20
        Heap<Node> F= new Heap<Node>();

        // Each node in the Settled and Frontier sets has an entry in map,
        // which gives its shortest distance from node start and the backpointer
        // of the node on a shortest path from node start.
        HashMap<Node, SFdata> map= new HashMap<Node, SFdata>();

        F.add(start, 0);
        map.put(start, new SFdata(0, null));

        // invariant: As presented in notes for Lecture 20
        while (F.size() > 0) {
            Node f= F.poll();
            if (f == end) return constructPath(f, map);

            SFdata fData= map.get(f);

            for (Edge edge : f.getExits()) {
                Node w= edge.getOther(f);
                int distToW= fData.distance + edge.length;
                SFdata wData= map.get(w);
                if (wData == null) {  // if w is in the far-out set
                    F.add(w, distToW);
                    map.put(w, new SFdata(distToW, f));
                }
                else // w in settled or frontier set 
                    if (distToW < wData.distance) {
                        F.changePriority(w, distToW);
                        wData.distance= distToW;
                        wData.backPointer= f;
                    }
            }
        }

        return new LinkedList<Node>(); // no path found
    }

    /** Return the path from the start node to node end.
     *  Precondition: nData contains all the necessary information about
     *  the path. */
    public static List<Node> constructPath(Node end, HashMap<Node, SFdata> nData) {
        LinkedList<Node> path= new LinkedList<Node>();
        Node p= end;
        // invariant: All the nodes from p's successor to the end are in
        //            path, in reverse order.
        while (p != null) {
            path.addFirst(p);
            p= nData.get(p).backPointer;
        }
        return path;
    }

    /** Return the sum of the weights of the edges on path path. */
    public static int pathDistance(List<Node> path) {
        if (path.size() == 0) return 0;
        synchronized(path) {
            Iterator<Node> iter= path.iterator();
            Node p= iter.next();  // First node on path
            int s= 0;
            // invariant: s = sum of weights of edges from start to p
            while (iter.hasNext()) {
                Node q= iter.next();
                s= s + p.getEdge(q).length;
                p= q;
            }
            return s;
        }
    }
    
    /**
     * Return a HashMap whose key is all the Nodes whose distance is in the acceptable 
     * interval the user assign and the value is the time required for this customer to 
     * go from this Node to the final destination.
     * @param timeThreshold: The longest time that the customer accept for walking
     * @param end: the final destination
     */
    public HashMap<Node,Double> generateHashMapForTime(int timeThreshold, Node end){
    	HashMap<Node, Double> mapForTime = new HashMap<Node, Double>();
    	int distance;
    	int v = 1;
    	double time;
    	for (Node n : getAllNearNodes(timeThreshold, end)){
    		distance = pathDistance(shortestPath(end, n));
    		time = (double) distance / (double) v;
    		mapForTime.put(n, time);
    	}
    	return mapForTime;
    }
    
    /**
     * Return a List of Nodes which contains all the Nodes whose distance is in the acceptable 
     * interval the user assign.
     * @param timeThreshold: the longest time that the customer accept for walking
     * @param end: the final destination
     */
    //TODO
    public List<Node> getAllNearNodes(int timeThreshold, Node end){
    	return null;
    }
    
    /**
     * Return a HashMap whose key is all the Nodes whose distance is in the acceptable 
     * interval the user assign and the value is the price it takes from the starting Node
     * to that Node
     * @param timeThreshold: the longest time that the customer accept for walking
     * @param start: the place where the customer departs
     * @param end: the final destination
     */
    public HashMap<Node, Double> generateHashMapForPrice(int timeThreshold, Node start, Node end){
    	HashMap<Node, Double> mapForPrice = new HashMap<Node, Double>();
    	for (Node n : getAllNearNodes(timeThreshold, end)){
    		mapForPrice.put(n, getPrice(start, n));
    	}
    	return mapForPrice;
    }
    
    //TODO
    public double getPrice(Node start, Node end){
    	return 0.0;
    }
    
       
    /**
     * Return the Node which maps the smallest value in mapForPrice.
     * @param mapForPrice: a HashMap whose key is all the Nodes whose distance is in the 
     * acceptable interval the user assign and the value is the price it takes from the 
     * starting Node
     * @param timeThreshold: the longest time that the customer accept for walking
     * @param end: the final destination
     * @return
     */
    public Node minNode(HashMap<Node, Double> mapForPrice, int timeThreshold, Node end){
    	Heap<Node> minHeap = new Heap<Node>();
    	for (Node n : getAllNearNodes(timeThreshold, end)){
    		minHeap.add(n, mapForPrice.get(n));
    	}
    	return minHeap.poll();
    }
    
       
    public Node bestChoice (Node start, Node end, int timeThreshold){
    	HashMap <Node, Double> mapForTime = generateHashMapForTime(timeThreshold, end);
    	HashMap <Node, Double> mapForPrice = generateHashMapForPrice(timeThreshold, start,end);
    	double priceThreshold = - mapForPrice.get(minNode(mapForPrice, timeThreshold, end)) + getPrice(start, end);
    	double rate = timeThreshold / priceThreshold;
    	Node min = start;
    	for(Node n : getAllNearNodes(timeThreshold, end)){
    		if (mapForTime.get(n) <= mapForTime.get(min) && mapForPrice.get(n) <= mapForPrice.get(min))
    			min = n;
    		else if (mapForTime.get(n) <= mapForTime.get(min) && mapForPrice.get(n) > mapForPrice.get(min)){
    			if (mapForTime.get(min) - mapForTime.get(n) <= rate * (mapForPrice.get(n) - mapForPrice.get(min)))
    				min = n;
    		}
    		else if(mapForTime.get(n) > mapForTime.get(min) && mapForPrice.get(n) <= mapForPrice.get(min)){
    			if (mapForTime.get(n) - mapForTime.get(min) <= rate * (mapForPrice.get(min) - mapForPrice.get(n)))
    				min = n;
    		}
    	}
    	return min;
    }

    /** An instance contains information about a node: the previous node
     *  on a shortest path from the start node to this node and the distance
     *  of this node from the start node. */
    private static class SFdata {
        private Node backPointer; // backpointer on path from start node to this one
        private int distance; // distance from start node to this one

        /** Constructor: an instance with distance d from the start node and
         *  backpointer p.*/
        private SFdata(int d, Node p) {
            distance= d;     // Distance from start node to this one.
            backPointer= p;  // Backpointer on the path (null if start node)
        }

        /** return a representation of this instance. */
        public String toString() {
            return "dist " + distance + ", bckptr " + backPointer;
        }
    }
}