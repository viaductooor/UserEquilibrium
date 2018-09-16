package jnetwork;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Get shortest path by Dijkstra Algorithm
 * 
 * @author John Smith
 *
 * @param <K>
 *            the type of vertices
 * @param <L>
 *            the type of edges
 */
public class ShortestPath<K, L> {

	/**
	 * Standard Dijkstra Algorithm which to get shortest paths between one source
	 * vertex and all other vertices
	 * 
	 * @param g
	 *            the graph to be performed
	 * @param begin
	 *            source vertex
	 * @return
	 */
	public HashMap<K, ShortestPath.Node<K>> one2all(Graph<K, ? extends WeightedEdge> g, K begin) {
		HashMap<K, Node<K>> Q = new HashMap<K, ShortestPath.Node<K>>();
		HashMap<K, Node<K>> S = new HashMap<K, ShortestPath.Node<K>>();
		for (K k : g.vertices()) {
			if (k == begin) {
				Q.put(begin, new ShortestPath.Node<K>(0));
			} else {
				Q.put(k, new ShortestPath.Node<K>());
			}
		}
		while (!Q.isEmpty()) {
			// get the vertex of minimal weight from set Q, and remove it from Q
			K minK = null;
			Node<K> minNode = null;
			float minValue = Float.POSITIVE_INFINITY;
			for (Map.Entry<K, Node<K>> entry : Q.entrySet()) {
				if (entry.getValue().getWeight() < minValue) {
					minK = entry.getKey();
					minValue = entry.getValue().getWeight();
					minNode = entry.getValue();
				}
			}

			if (minK == null) {
				/**
				 * minK is key of the vertex which takes minimal weight from the begin vertex.
				 * When the value of it is null(and Q is not empty), it means there are at least
				 * one vertex with minimal weight of positive_infinity. Therefore we have to
				 * break the loop and leave the left vertices unchanged
				 */
				break;
			}

			Q.remove(minK);
			S.put(minK, minNode);

			for (Entry<K, ? extends WeightedEdge> entry : g.getAdjNodes(minK).entrySet()) {
				K _key = entry.getKey();
				float _weight = entry.getValue().getWeight();
				float _sum = minNode.getWeight() + _weight;
				if (Q.containsKey(_key)) {
					Node<K> _node = Q.get(_key);
					if (_node.getWeight() > _sum) {
						_node.setWeight(_sum);
						_node.setPreK(minK);
					}
				}

			}

		}
		return S;
	}

	/**
	 * Get the shortest path between two vertices immediately without computing all
	 * the shortest paths
	 * 
	 * @param g
	 *            the graph to be performed
	 * @param begin
	 *            source vertex
	 * @param end
	 *            terminate vertex
	 * @return
	 */
	public List<K> path(Graph<K, ? extends WeightedEdge> g, K begin, K end) {
		HashMap<K, Node<K>> map = one2all(g, begin);
		if (begin == end) {
			return null;
		}
		if (map.get(end) == null) {
			return null;
		} else {
			List<K> route = new LinkedList<K>();
			route.add(end);
			Node<K> node = map.get(end);
			while (node.getPreK() != begin) {
				route.add(0, node.getPreK());
				node = map.get(node.getPreK());
				if (node == null) {
					break;
				}
			}
			route.add(0, begin);
			return route;
		}
	}

	/**
	 * Get the shortest path after computing all the shortest paths, hence need a
	 * map of the shortest paths which is the result of {@link allPaths()}}
	 * 
	 * @param allShortestPaths
	 * @param begin
	 * @param end
	 * @return
	 */
	public List<K> path(HashMap<K, HashMap<K, Node<K>>> allShortestPaths, K begin, K end) {
		if (begin == end) {
			return null;
		}
		HashMap<K, Node<K>> adjs = null;
		Node<K> node = null;
		List<K> route = new LinkedList<K>();
		if ((adjs = allShortestPaths.get(begin)) != null) {
			if ((node = adjs.get(end)) != null) {
				route.add(end);
				while (node.getPreK() != begin) {
					route.add(0, node.getPreK());
					node = adjs.get(node.getPreK());
				}
				route.add(0, begin);
				return route;
			}
		}
		return null;
	}

	public float shortestPathLength(HashMap<K, HashMap<K, Node<K>>> allShortestPaths, K begin, K end) {
		if (begin == end) {
			return 0;
		}
		HashMap<K, Node<K>> adjs = null;
		Node<K> node = null;
		if ((adjs = allShortestPaths.get(begin)) != null) {
			if ((node = adjs.get(end)) != null) {
				return node.weight;
			}
		}
		return Float.POSITIVE_INFINITY;
	}

	/**
	 * Get all shortest paths
	 * 
	 * @param g
	 *            the graph to be performed
	 * @return
	 */
	public HashMap<K, HashMap<K, Node<K>>> allPaths(Graph<K, ? extends WeightedEdge> g) {
		Set<K> nodes = g.vertices();
		HashMap<K, HashMap<K, ShortestPath.Node<K>>> map = new HashMap<K, HashMap<K, ShortestPath.Node<K>>>();
		for (K k : nodes) {
			map.put(k, one2all(g, k));
		}
		return map;
	}

	/**
	 * Node is a structure during the shortest path computing. Every vertex has a
	 * Node object, and its two fields separately stand for the minimal weight from
	 * the original vertex and the precursor vertex of the shortest path.
	 * 
	 * @author John Smith
	 *
	 * @param <K>
	 */
	public static class Node<K> {
		float weight;
		K preK;

		public Node() {
			this.weight = Float.POSITIVE_INFINITY;
			this.preK = null;
		}

		public Node(float weight) {
			this.weight = weight;
		}

		public float getWeight() {
			return weight;
		}

		public void setWeight(float weight) {
			this.weight = weight;
		}

		public K getPreK() {
			return preK;
		}

		public void setPreK(K preK) {
			this.preK = preK;
		}

	}
}
