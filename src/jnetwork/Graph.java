package jnetwork;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**An implementation of graph with structure of adjacency list.
 * 
 * @author John Smith
 *
 * @param <K> Type of the nodes
 * @param <L> Type of the links
 * 
 */
public class Graph<K, L> {
	private HashMap<K, HashMap<K, L>> map;

	public Graph() {
		map = new HashMap<K, HashMap<K, L>>();
	}

	/**
	 * 
	 * @param node
	 * @return the set of the node's adjacent nodes
	 */
	public HashMap<K, L> addVertic(K node) {
		return map.put(node, new HashMap<K, L>());
	}

	/**
	 * 
	 * @param initNode
	 * @param endNode
	 * @param linkInfo
	 * @return the added linkInfo
	 */
	public L addDiEdge(K initNode, K endNode, L linkInfo) {
		if (map.containsKey(initNode)) {
			return map.get(initNode).put(endNode, linkInfo);
		} else {
			addVertic(initNode);
			return addDiEdge(initNode, endNode, linkInfo);
		}
	}
	
	public void addEdge(K initNode,K endNode, L linkInfo) {
		addDiEdge(initNode,endNode,linkInfo);
		addDiEdge(endNode,initNode,linkInfo);
	}


	/**
	 * 
	 * @param initNode
	 * @param endNode
	 * @return linkInfo
	 */
	public L getLink(K initNode, K endNode) {
		if(map.containsKey(initNode)) {
			HashMap<K, L> adjs = map.get(initNode);
			if(adjs.containsKey(endNode)) {
				return adjs.get(endNode);
			}
		}
		return null;
	}


	/**
	 * 
	 * @param node
	 * @return the map of adjacent nodes of this node
	 */
	public HashMap<K, L> getAdjNodes(K node) {
		return map.get(node);
	}
	
	public static class Entry<K,L>{
		K begin;
		K end;
		L link;
		public Entry(K begin,K end,L link){
			this.begin = begin;
			this.end = end;
			this.link = link;
		}
		public K getBegin() {
			return begin;
		}
		public K getEnd() {
			return end;
		}
		public L getLink() {
			return link;
		}
	}
	
	/**
	 * An entrySet is a set of entries of type {@link Entry}.
	 * Use function to visit all the links of the graph.
	 * @return
	 */
	public List<Graph.Entry<K,L>> entrySet() {
		List<Entry<K, L>> l = new LinkedList<Graph.Entry<K,L>>();
		for(Map.Entry<K, HashMap<K, L>> _m:map.entrySet()) {
			K init = _m.getKey();
			for(Map.Entry<K, L> _l:_m.getValue().entrySet()) {
				K end = _l.getKey();
				L link = _l.getValue();
				l.add(new Graph.Entry<K, L>(init, end, link));
			}
		}
		return l;
	}
	
	/**
	 * Return all the distinct nodes of the graph
	 * @return
	 */
	public Set<K> nodes(){
		HashSet<K> set = new HashSet<K>();
		for (Map.Entry<K, HashMap<K,L>> entry:map.entrySet()) {
			set.add(entry.getKey());
		}
		return set;
	}
	
	public Set<L> edges(){
		HashSet<L> set = new HashSet<L>();
		for(Map.Entry<K, HashMap<K, L>> _m:map.entrySet()) {
			for(Map.Entry<K, L> _l:_m.getValue().entrySet()) {
				L link = _l.getValue();
				set.add(link);
			}
		}
		return set;
	}
	
	public boolean containsEdge(K initNode, K endNode) {
		if(getLink(initNode, endNode) != null)
			return true;
		return false;
	}
	
	public boolean containsVertex(K vertex) {
		if(map.containsKey(vertex))
			return true;
		return false;
	}
}
