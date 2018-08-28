package main;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

import jnetwork.Graph;
import jnetwork.WeightedLink;
import functions.ShortestPath;
import functions.ShortestPath.Node;

public class UserEquilibrium {

	private List<UeLink> mLinks;
	private List<Odpair> mOdpairs;
	private int nodeNumber;

	public UserEquilibrium(UeDataSet dataSet) {
		this.mLinks = dataSet.getLinks();
		this.mOdpairs = dataSet.getTrips();
		this.nodeNumber = dataSet.getNodeNubmer();
	}

	public UserEquilibrium(List<UeLink> links, List<Odpair> odpairs, int nodeNumber) {
		this.mLinks = links;
		this.mOdpairs = odpairs;
		this.nodeNumber = nodeNumber;
	}

	/**
	 * the step ALL-OR-NOTHING ASSIGNMENT
	 * 
	 * @param odset
	 * @param links
	 */
	public void allOrNothing(List<Odpair> odset, List<UeLink> links) {
		float[][] tmat = getTMatrix(links, nodeNumber);
		clearAuxFlow(links);
		Floyd f = new Floyd();
		f.setMatrix(tmat);
		f.compute();
		for (Odpair odpair : odset) {
			int x = odpair.getFrom();
			int y = odpair.getTo();
			List<Integer> path = f.getShortestPath(x, y);
			for (int i = 0; i < path.size() - 1; i++) {
				int init = path.get(i);
				int term = path.get(i + 1);
				UeLink l = getLink(init, term, links);
				l.setAuxFlow(l.getAuxFlow() + odpair.getDemand());
			}
		}
	}

	public static <T> void allOrNothing(Graph<T, UeLink> links, Graph<T, UeLink> trips) {
		HashMap<T, HashMap<T, Node<T>>> allPaths = new ShortestPath<T, UeLink>()
				.allPaths(links);
		BiFunction<T, T, List<T>> getPath = (T begin,
				T end) -> new ShortestPath<T, UeLink>().path(allPaths, begin, end);
		Graph<T,UeLink> newGraph = new Graph<T, UeLink>();
		
		for (Graph.Entry<T, UeLink> e : trips.entrySet()) {
			T begin = e.getBegin();
			T end = e.getEnd();
			float weight = e.getLink().getWeight();
			List<T> route = getPath.apply(begin, end);
			for (int i = 0; i < route.size() - 1; i++) {
				T init = route.get(i);
				T term = route.get(i + 1);
				if(!links.containsEdge(init, term)) {
					newGraph.addEdge(init, term, links.getLink(init, term));
				}else {
					
				}
			}
		}
	}

	public static float[][] getTMatrix(List<UeLink> set, int maxsize) {
		for (UeLink l : set) {
			l.updateTravelTime();
		}
		float[][] mat = new float[maxsize][maxsize];
		for (int i = 0; i < maxsize; i++) {
			for (int j = 0; j < maxsize; j++) {
				mat[i][j] = Float.POSITIVE_INFINITY;
			}
		}
		for (UeLink l : set) {
			mat[l.getFrom() - 1][l.getTo() - 1] = l.getTravelTime();
		}
		return mat;
	}

	public static void clearAuxFlow(List<UeLink> set) {
		for (UeLink l : set) {
			l.setAuxFlow(0);
		}
	}

	public static UeLink getLink(int start, int end, List<UeLink> set) {
		for (UeLink l : set) {
			if (l.getFrom() == start) {
				if (l.getTo() == end) {
					return l;
				}
			}
		}
		return null;
	}

	/**
	 * assign flow (of every link) with the value of auxiliary flow
	 * 
	 * @param set
	 */
	public static void y2x(List<UeLink> set) {
		for (UeLink l : set) {
			l.setFlow(l.getAuxFlow());
		}
	}

	public static void updateTMatrix(List<UeLink> set) {
		for (UeLink l : set) {
			l.updateTravelTime();
		}
	}

	public static float lineSearch(List<UeLink> links) {
		float alpha = 1;
		float minSum = Float.POSITIVE_INFINITY;
		for (float al = 0; al < 1.001; al += 0.001) {
			float sum = 0;
			for (UeLink l : links) {
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

	public static float getTotalFlow(List<UeLink> set) {
		float sum = 0;
		for (UeLink l : set) {
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
	public static void move(List<UeLink> links, float alpha) {
		for (UeLink l : links) {
			l.setFlow(l.getFlow() + alpha * (l.getAuxFlow() - l.getFlow()));
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
		allOrNothing(mOdpairs, mLinks);
		y2x(mLinks);
		float alpha = 1;
		// int n = 1;
		float step = Float.POSITIVE_INFINITY;
		while (Math.abs(step) > diff) {
			updateTMatrix(mLinks); // step 1
			allOrNothing(mOdpairs, mLinks);// step 2
			alpha = lineSearch(mLinks);// step 3
			float flowsum = getTotalFlow(mLinks);
			move(mLinks, alpha);
			float totalFlow = getTotalFlow(mLinks);
			System.out.println(Math.abs(step));
			step = totalFlow - flowsum;
			// n++;
		}
		updateTMatrix(mLinks);
		// LogWriter lw = new LogWriter(mLinks, mOdpairs);
		// lw.init();
		// lw.logWriteLink("UE ASSIGNMENT:");
		// lw.close();
	}
}
