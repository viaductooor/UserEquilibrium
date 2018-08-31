package functions;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

import jnetwork.Graph;
import jnetwork.ShortestPath;
import jnetwork.Graph.Entry;
import jnetwork.ShortestPath.Node;
import main.Link;
import main.UeLink;
import jnetwork.WeightedLink;

public class UserEquilibrium {

	/**
	 * All-or-nothing assignment. When given a trip(or an origin-destination pair)
	 * of any demand, we first get the shortest path of the trip. Then we add the
	 * demand as volume onto every single link which composes the shortest path.For
	 * example, if we have a trip (a,d) with demand of n, We first get the shortest
	 * path (a,c,d), then we need to add n to the volume of link(a,c) and link(c,d).
	 * 
	 * @param links
	 * @param trips
	 */
	public static void allOrNothing(Graph<Integer, ? extends UeLink> links,
			Graph<Integer, ? extends WeightedLink> trips) {
		HashMap<Integer, HashMap<Integer, Node<Integer>>> allPaths = new ShortestPath<Integer, UeLink>()
				.allPaths(links);
		BiFunction<Integer, Integer, List<Integer>> getPath = (Integer begin,
				Integer end) -> new ShortestPath<Integer, UeLink>().path(allPaths, begin, end);

		clearAuxFlow(links);

		for (Entry<Integer, ? extends WeightedLink> e : trips.entrySet()) {
			int begin = e.getBegin();
			int end = e.getEnd();
			float weight = e.getLink().getWeight();
			List<Integer> route = getPath.apply(begin, end);
			if (route != null) {
				for (int i = 0; i < route.size() - 1; i++) {
					int init = route.get(i);
					int term = route.get(i + 1);
					UeLink l = links.getLink(init, term);
					l.setAuxFlow(l.getAuxFlow() + weight);
				}
			}

		}
	}

	/**
	 * Line search.Alpha is a parameter between 0 and 1. In this method, we get the
	 * optimal alpha which is going to minimize the total flow(by the step
	 * {@link move}) by trying incrementally)
	 * 
	 * @param graph
	 * @return
	 */
	public static float lineSearch(Graph<Integer, ? extends UeLink> graph) {
		float alpha = 1;
		float minSum = Float.POSITIVE_INFINITY;
		for (float al = 0; al < 1.001; al += 0.001) {
			float sum = 0;
			for (UeLink l : graph.edges()) {
				float flow = l.getFlow();
				float auxFlow = l.getAuxFlow();
				float freeFlowTime = l.getFtime();
				float capacity = l.getCapacity();
				float upper = flow + al * (auxFlow - flow);
				float surcharge = l.getSurcharge();
				float C = (float) ((0.03 * freeFlowTime) / Math.pow(capacity, 4));
				sum += (float) (C * Math.pow(upper, 5) + (freeFlowTime + surcharge) * upper);
			}
			if (sum < minSum) {
				alpha = al;
				minSum = sum;
			}
		}
		return alpha;
	}

	/**
	 * Get the total flow of a graph composed by UeLinks.
	 * 
	 * @param graph
	 * @return
	 */
	public static float getTotalFlow(Graph<Integer, ? extends UeLink> graph) {
		float sum = 0;
		for (UeLink l : graph.edges()) {
			sum += l.getFlow();
		}
		return sum;
	}

	/**
	 * Change volume of every link of the graph to decrease the total flow. The
	 * basis of alpha is in {@link lineSearch}}
	 * 
	 * @param graph
	 * @param alpha
	 */
	public static void move(Graph<Integer, ? extends UeLink> graph, float alpha) {
		for (Graph.Entry<Integer, ? extends UeLink> e : graph.entrySet()) {
			UeLink l = e.getLink();
			l.setFlow(l.getFlow() + alpha * (l.getAuxFlow() - l.getFlow()));
		}
	}

	/**
	 * Set auxFlow(which is also known as y in the algorithm) of every link of the
	 * graph to zero.
	 * 
	 * @param graph
	 */
	public static void clearAuxFlow(Graph<Integer, ? extends UeLink> graph) {
		for (UeLink l : graph.edges()) {
			l.setAuxFlow(0);
		}
	}

	/**
	 * Set flow of every link with the value of auxFlow. In some of the methods (eg.
	 * allOrNothing) we don't directly change the flow of every link, we firstly
	 * change auxFlow of them and then change flow when needed.
	 * 
	 * @param graph
	 */
	public static void y2x(Graph<Integer, ? extends UeLink> graph) {
		for (UeLink l : graph.edges()) {
			l.setFlow(l.getAuxFlow());
		}
	}

	/**
	 * Update travel-time of every link according to volume, free-flow-travel-time,
	 * capacity etc.
	 * 
	 * @param graph
	 */
	public static void updateAllTraveltime(Graph<Integer, ? extends UeLink> graph) {
		for (UeLink l : graph.edges()) {
			l.updateTravelTime();
		}
	}

	/**
	 * Change the graph of {@link Link} to graph of {@link UeLink}. When we perform
	 * a user-equilibrium assignment, we have to make sure the links of the graph
	 * have property volume, which is in {@link UeLink} but not in {@link Link}.
	 * 
	 * @param graph
	 */
	public static Graph<Integer, UeLink> initGraph(Graph<Integer, Link> graph) {
		Graph<Integer, UeLink> newGraph = new Graph<Integer, UeLink>();
		for (Graph.Entry<Integer, Link> e : graph.entrySet()) {
			Integer begin = e.getBegin();
			Integer end = e.getEnd();
			Link link = e.getLink();
			newGraph.addDiEdge(begin, end, new UeLink(link));
		}
		return newGraph;
	}

	/**
	 * 
	 * @param graph
	 * @param trips
	 * @param diff
	 * @return
	 */
	public static Graph<Integer, UeLink> ue(Graph<Integer, Link> graph,
			Graph<Integer, ? extends WeightedLink> trips, float diff) {

		Graph<Integer, UeLink> workgraph = initGraph(graph);
		/**
		 * step 0
		 */
		allOrNothing(workgraph, trips);
		y2x(workgraph);
		float alpha = 1;
		float step = Float.POSITIVE_INFINITY;
		while (Math.abs(step) > diff) {
			updateAllTraveltime(workgraph); // step 1
			allOrNothing(workgraph, trips);// step 2
			alpha = lineSearch(workgraph);// step 3
			float beforemove = getTotalFlow(workgraph);
			move(workgraph, alpha);
			float aftermove = getTotalFlow(workgraph);
			step = aftermove - beforemove;
			System.out.println("UserEquilibrium step: " + step);
		}
		updateAllTraveltime(workgraph);
		return workgraph;
	}
}
