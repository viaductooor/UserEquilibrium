package main;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import jnetwork.Graph;
import jnetwork.Graph.Entry;
import jnetwork.WeightedLink;
import functions.ShortestPath;
import functions.ShortestPath.Node;

public class UserEquilibrium<T> {

	private Graph<T,UeLink> graph;
	private Graph<T,WeightedLink> trips;
	private int nodeNumber;

	public UserEquilibrium(Graph<T,UeLink> graph,Graph<T,WeightedLink> trips) {
		this.graph = graph;
		this.trips = trips;
	}

	/**
	 * the step ALL-OR-NOTHING ASSIGNMENT
	 * 
	 * @param odset
	 * @param links
	 */
	public static <T> Graph<T,UeLink> allOrNothing(Graph<T, UeLink> links, Graph<T, WeightedLink> trips) {
		HashMap<T, HashMap<T, Node<T>>> allPaths = new ShortestPath<T, UeLink>()
				.allPaths(links);
		BiFunction<T, T, List<T>> getPath = (T begin,
				T end) -> new ShortestPath<T, UeLink>().path(allPaths, begin, end);
		Graph<T,UeLink> newGraph = new Graph<T, UeLink>();
		
		for (Entry<T, WeightedLink> e : trips.entrySet()) {
			T begin = e.getBegin();
			T end = e.getEnd();
			float weight = e.getLink().getWeight();
			List<T> route = getPath.apply(begin, end);
			for (int i = 0; i < route.size() - 1; i++) {
				T init = route.get(i);
				T term = route.get(i + 1);
				if(!newGraph.containsEdge(init, term)) {
					newGraph.addEdge(init, term, links.getLink(init, term));
				}else {
					UeLink _oldlink = newGraph.getLink(init, term);
					float _oldflow =_oldlink.getFlow();
					UeLink _newlink = new UeLink(_oldlink);
					_newlink.setFlow(_oldflow + weight);
					newGraph.addEdge(init, term, _newlink);
				}
			}
		}
		return newGraph;
	}


	public static <T> float lineSearch(Graph<T,UeLink> graph) {
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

	public static <T> float getTotalFlow(Graph<T,UeLink> graph) {
		float sum = 0;
		for (UeLink l : graph.edges()) {
			sum += l.getFlow();
		}
		return sum;
	}

	/**
	 * the step MOVE
	 * 
	 * @param links
	 * @param alpha
	 */
	public static <T> Graph<T,UeLink> move(Graph<T,UeLink> graph, float alpha) {
		Graph<T, UeLink> newGraph = new Graph<T,UeLink>();
		for(Graph.Entry<T, UeLink> e:graph.entrySet()) {
			T begin = e.getBegin();
			T end = e.getEnd();
			UeLink l = e.getLink();
			UeLink newlink = new UeLink(l);
			newlink.setFlow(l.getFlow() + alpha * (l.getAuxFlow() - l.getFlow()));
			newGraph.addEdge(begin, end, newlink);
		}
		return newGraph;
	}

	public static <T> void clearAuxFlow(Graph<T,UeLink> graph) {
		for(UeLink l:graph.edges()) {
			l.setAuxFlow(0);
		}
	}
	
	public static <T> void y2x(Graph<T,UeLink> graph) {
		for(UeLink l:graph.edges()) {
			l.setFlow(l.getAuxFlow());
		}
	}
	
	public static <T> void updateAllTraveltime(Graph<T,UeLink> graph) {
		for(UeLink l:graph.edges()) {
			l.updateTravelTime();
		}
	}
	
	/**
	 * diff = this_total_flow - last_total_flow
	 * 
	 * @param diff
	 */
	public void compute(float diff) {
		/**
		 * step 0
		 */
		Graph<T, UeLink> workgraph = allOrNothing(graph, trips);
		y2x(workgraph);
		float alpha = 1;
		float step = Float.POSITIVE_INFINITY;
		while (Math.abs(step) > diff) {
			updateAllTraveltime(workgraph); // step 1
			workgraph = allOrNothing(workgraph, trips);// step 2
			alpha = lineSearch(workgraph);// step 3
			float flowsum = getTotalFlow(workgraph);
			workgraph = move(workgraph, alpha);
			float totalFlow = getTotalFlow(workgraph);
			System.out.println(Math.abs(step));
			step = totalFlow - flowsum;
			// n++;
		}
		updateAllTraveltime(workgraph);
		// LogWriter lw = new LogWriter(mLinks, mOdpairs);
		// lw.init();
		// lw.logWriteLink("UE ASSIGNMENT:");
		// lw.close();
	}
}
