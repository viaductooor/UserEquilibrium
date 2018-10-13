package functions;

import java.util.HashMap;
import java.util.List;

import file.ExcelUtils;
import functions.ShortestPath.Node;
import jnetwork.Graph;
import jnetwork.Graph.Entry;
import jnetwork.WeightedEdge;
import main.Link;
import main.UeLink;

public class ChangeDemand {
	private float uediff;
	private float surchargeDiff;
	private float demandStep;
	private Graph<Integer, ? extends Link> graph;
	private Graph<Integer, ? extends WeightedEdge> trips;

	public ChangeDemand(Graph<Integer, ? extends Link> graph, Graph<Integer, ? extends WeightedEdge> trips) {
		this.graph = graph;
		this.trips = trips;
		this.uediff = 50f;
		this.surchargeDiff = 50f;
		this.demandStep = 0.1f;
	}

	public ChangeDemand(float ueDiff, float surchargeDiff, float demandStep, Graph<Integer, ? extends Link> graph,
			Graph<Integer, ? extends WeightedEdge> trips) {
		this.graph = graph;
		this.trips = trips;
		this.uediff = ueDiff;
		this.surchargeDiff = surchargeDiff;
		this.demandStep = demandStep;
	}

	/**
	 * compute marginal cost based volume, freeFlowTime and Capacity
	 * 
	 * @param volume
	 * @param fftt
	 * @param cap
	 * @return
	 */
	public float computeMarginalCost(float volume, float fftt, float cap) {
		float result = (float) (volume * fftt * (4 * Math.pow(volume, 3) * 0.15 / Math.pow(cap, 4)));
		return result;
	}

	/**
	 * Compute surcharge based on marginalCost and last surcharge.
	 * 
	 * @param n
	 * @param marginalCost
	 * @param lastSurcharge
	 * @return
	 */
	public float computeSurcharge(int n, float marginalCost, float lastSurcharge) {
		float result = (1f / n) * marginalCost + (1 - (1f / n)) * lastSurcharge;
		return result;
	}

	/**
	 * Get travel time.
	 * 
	 * @param flow
	 * @param capacity
	 * @param freeFlowTime
	 * @return
	 */
	public float computeTravelTime(float flow, float capacity, float freeFlowTime) {

		return (float) (((Math.pow(flow / capacity, 4)) * 0.15 + 1) * freeFlowTime);
	}

	/**
	 * Load n([0,1]) of the original demand,change flow and travel time of related
	 * links.
	 * 
	 * @param n
	 * @param odp
	 */
	public void load(float n, ChangeDemandLink demandlink, int origin, int dest, Graph<Integer, UeLink> graph,
			HashMap<Integer, HashMap<Integer, ShortestPath.Node<Integer>>> paths) {
		float demand = demandlink.demand;
		float per = demandlink.increPercentage;
		per = per + n;
		demandlink.increPercentage = per;

		List<Integer> route = new ShortestPath<Integer, ChangeDemandLink>().shortestPath(paths, origin, dest);

		for (int i = 0; i < route.size() - 1; i++) {
			UeLink l = graph.getEdge(route.get(i), route.get(i + 1));
			l.setFlow(l.getFlow() + n * demand);
			l.updateTravelTime();
		}
	}

	public void setUediff(float uediff) {
		this.uediff = uediff;
	}

	public void setSurchargeDiff(float surchargeDiff) {
		this.surchargeDiff = surchargeDiff;
	}

	public void setDemandStep(float demandStep) {
		this.demandStep = demandStep;
	}

	/**
	 * Set each trip's (ChangeDemandLink) incremental percentage to 0.
	 * 
	 * @param g
	 */
	public void clearPercentage(Graph<Integer, ChangeDemandLink> g) {
		for (ChangeDemandLink l : g.edges()) {
			l.increPercentage = 0;
		}
	}

	/**
	 * Set each trip's (ChangeDemandLink) lock to false. Lock is a attribute set to
	 * constrain the process of loading. When a trip's demand is loaded 100% or
	 * more, its lock will be set true to avoid increasing it in the next loop.
	 * 
	 * @param g
	 */
	public void clearLock(Graph<Integer, ChangeDemandLink> g) {
		for (ChangeDemandLink l : g.edges()) {
			l.lock = false;
		}
	}

	/**
	 * Update the demandlink's total travel time based on recent graph. Actually we
	 * have to calculate the shortest path every time we update a demandlink's total
	 * cost, which will dramatically lower the performance of the algorithm. So we
	 * have a parameter paths which was worked out once, and it contains all the
	 * shortest paths of the present graph.
	 * 
	 * @param paths
	 * @param graph
	 * @param demandlink
	 */
	public void updateCost(HashMap<Integer, HashMap<Integer, ShortestPath.Node<Integer>>> paths,
			Graph<Integer, UeLink> graph, ChangeDemandLink demandlink, int origin, int dest) {
		float sum = 0;
		List<Integer> route = new ShortestPath<Integer, UeLink>().shortestPath(paths, origin, dest);
		for (int i = 0; i < route.size() - 1; i++) {
			UeLink link = graph.getEdge(route.get(i), route.get(i + 1));
			sum = sum + link.getTravelTime();
		}
		demandlink.cost = sum;
	}

	/**
	 * Convert any graph composed of weighted edges to graph of ChangeDemandLinks.
	 * Return a new graph.
	 * 
	 * @param trips
	 * @return
	 */
	public Graph<Integer, ChangeDemandLink> changeDemandTrips(Graph<Integer, ? extends WeightedEdge> trips) {
		Graph<Integer, ChangeDemandLink> tripGraph = new Graph<Integer, ChangeDemandLink>();
		for (Graph.Entry<Integer, ? extends WeightedEdge> e : trips.entrySet()) {
			Integer begin = e.getBegin();
			Integer end = e.getEnd();
			float weight = e.getLink().getWeight();
			tripGraph.addEdge(begin, end, new ChangeDemandLink(weight));
		}
		return tripGraph;
	}

	/**
	 * When given a new graph of ChangeDemandLinks, we have to initiate each link's
	 * originCost to evaluate the algorithm's performance later.
	 * 
	 * @param trips
	 * @param links
	 */
	public void initTrips(Graph<Integer, ChangeDemandLink> trips, Graph<Integer, UeLink> links) {
		ShortestPath<Integer, UeLink> shortestPath = new ShortestPath<Integer, UeLink>();
		HashMap<Integer, HashMap<Integer, Node<Integer>>> allPaths = shortestPath.allPaths(links);
		for (Graph.Entry<Integer, ChangeDemandLink> e : trips.entrySet()) {
			Integer begin = e.getBegin();
			Integer end = e.getEnd();
			e.getLink().originCost = shortestPath.shortestPathLength(allPaths, begin, end);
		}
	}

	/**
	 * Sum up the differences between the recent surcharge and last surcharge of
	 * every link.
	 * 
	 * @param graph
	 * @return
	 */
	public float allSurchargeDiff(Graph<Integer, UeLink> graph) {
		float diff = 0;
		for (UeLink l : graph.edges()) {
			diff += Math.abs(l.getSurcharge() - l.getLastSurcharge());
		}
		return diff;
	}

	/**
	 * Optimal situation. Count marginal cost and surcharge in, without changing the
	 * demands.
	 * 
	 * @param graph
	 * @param trips
	 * @param ueDiff
	 * @param surchargeDiff
	 * @return
	 */
	public Graph<Integer, UeLink> opt() {
		Graph<Integer, UeLink> linkGraph = UserEquilibrium.initGraph(graph);
		float originalFlow = 0;
		float optFlow = 0;
		float originalCost = 0;
		float optCost = 0;

		UserEquilibrium.ue(linkGraph, trips, uediff);

		for (UeLink l : linkGraph.edges()) {
			originalFlow += l.getFlow();
			originalCost = originalCost + l.getTravelTime() * l.getFlow();
		}

		int n = 0;
		float diff;
		do {
			n++;
			UserEquilibrium.ue(linkGraph, trips, uediff);
			for (UeLink l : linkGraph.edges()) {
				float marginalCost = computeMarginalCost(l.getFlow(), l.getFtime(), l.getCapacity());
				float nowSurcharge = l.getSurcharge();
				l.setLastSurcharge(nowSurcharge);
				float sur = computeSurcharge(n, marginalCost, nowSurcharge);
				l.setSurcharge(sur);
			}
			diff = allSurchargeDiff(linkGraph);
		} while (diff > surchargeDiff);

		for (UeLink l : linkGraph.edges()) {
			l.setTravelTime(l.getTravelTime() - l.getLastSurcharge());
			optFlow += l.getFlow();
			optCost += l.getTravelTime() * l.getFlow();
		}

		System.out.println("origin total flow: " + originalFlow + ", opt total flow: " + optFlow);
		System.out.println("origin total cost: " + originalCost + ", opt total cost: " + optCost);

		return linkGraph;
	}
	
	
	public Graph<Integer, UeLink> optWithoutMsa() {
		Graph<Integer, UeLink> linkGraph = UserEquilibrium.initGraph(graph);

		UserEquilibrium.ue(linkGraph, trips, uediff);

		int n = 0;
		float diff;
		do {
			n++;
			UserEquilibrium.ue(linkGraph, trips, uediff);
			for (UeLink l : linkGraph.edges()) {
				float marginalCost = computeMarginalCost(l.getFlow(), l.getFtime(), l.getCapacity());
				float nowSurcharge = l.getSurcharge();
				l.setLastSurcharge(nowSurcharge);
				l.setSurcharge(marginalCost);
			}
			System.out.println("total volume: "+allVolume(linkGraph)+",total cost: "+allCost(linkGraph));
			ExcelUtils.writeGraph(linkGraph, "files/sf_opt_without_msa/res_"+n+".xls");
			
		} while (n<20);
		
		return linkGraph;
	}

	public float allVolume(Graph<Integer,UeLink> mgraph) {
		float sum = 0;
		for(Entry<Integer, UeLink> e:mgraph.entrySet()) {
			sum+=e.getLink().getFlow();
		}
		return sum;
	}
	
	public float allCost(Graph<Integer,UeLink> mgraph) {
		float sum = 0;
		for(Entry<Integer, UeLink> e:mgraph.entrySet()) {
			sum+=e.getLink().getFlow()*e.getLink().getTravelTime();
		}
		return sum;
	}
	
	/**
	 * main method of this class
	 */
	public Graph<Integer, ChangeDemandLink> changeDemand() {
		Graph<Integer, ChangeDemandLink> newTrips = changeDemandTrips(trips);
		Graph<Integer, UeLink> newGraph = UserEquilibrium.initGraph(graph);
		initTrips(newTrips, newGraph);
		
		int count1 = 0;
		int count2;
		int lockcount;
		float diff;

		do {
			count1++;
			UserEquilibrium.ue(newGraph, newTrips, uediff);

			/**
			 * update marginal cost and link surcharge
			 */
			for (UeLink l : newGraph.edges()) {
				float marginalCost = computeMarginalCost(l.getFlow(), l.getFtime(), l.getCapacity());
				float nowSurcharge = l.getSurcharge();
				l.setLastSurcharge(nowSurcharge);
				float sur = computeSurcharge(count1, marginalCost, nowSurcharge);
				l.setSurcharge(sur);
			}

			count2 = 0;
			lockcount = 0;
			clearPercentage(newTrips);
			clearLock(newTrips);

			// clear the volume of every link which will be set later in the step "load"
			for (UeLink l : newGraph.edges()) {
				l.setFlow(0);
			}

			do {

				// compute shortest path of all OD pairs
				count2++;
				System.out.println("outter loop:" + count1 + "; inner loop: " + count2);
				ShortestPath<Integer, UeLink> shortestPath = new ShortestPath<Integer, UeLink>();
				HashMap<Integer, HashMap<Integer, Node<Integer>>> allPaths = shortestPath.allPaths(newGraph);

				/**
				 * for each OD Pair, find shortest path if it's total cost < original cost then
				 * load 5% original demand(or something else)
				 */
				for (Graph.Entry<Integer, ChangeDemandLink> e : newTrips.entrySet()) {
					/**
					 * get the shortest path of the OD pair, then update its total cost total_cost =
					 * sum_travel_time + sum_surcharge
					 */
					int o = e.getBegin();
					int d = e.getEnd();
					ChangeDemandLink link = e.getLink();
					float totalCost = shortestPath.shortestPathLength(allPaths, o, d);
					link.cost = totalCost;

					if (totalCost > link.originCost | link.increPercentage >= 1) {
						link.lock = true;
						lockcount++;
					}
					if (link.lock == false) {
						/**
						 * load some, like 5% or 1% of original demand and update travel_time of every
						 * link. then update the travel time of every link that composes the shrotest
						 * path.
						 */
						load(demandStep, link, o, d, newGraph, allPaths);
					}
				}

				/**
				 * update the cost of all ODPairs based on the composing links
				 */
				for (Graph.Entry<Integer, ChangeDemandLink> e : newTrips.entrySet()) {
					updateCost(allPaths, newGraph, e.getLink(), e.getBegin(), e.getEnd());
				}

			} while (lockcount < newTrips.edges().size());

			/**
			 * update demand
			 */
			for (ChangeDemandLink l : newTrips.edges()) {
				l.demand = l.originDemand * l.increPercentage;
			}
			diff = allSurchargeDiff(newGraph);
		} while (diff > surchargeDiff);

		for (UeLink l : newGraph.edges()) {
			l.setTravelTime(l.getTravelTime() - l.getSurcharge());
		}
		return newTrips;
	}

	class ChangeDemandLink implements WeightedEdge {
		public float originCost;
		public float cost;
		public float increPercentage;
		public float originDemand;
		public boolean lock;
		public float demand;

		public ChangeDemandLink(float demand) {
			this.originDemand = demand;
			this.lock = false;
			this.increPercentage = 0;
			this.demand = demand;
			this.originCost = 0;
			this.cost = 0;
		}

		public ChangeDemandLink(ChangeDemandLink l) {
			this.originCost = l.originCost;
			this.cost = l.cost;
			this.increPercentage = l.increPercentage;
			this.originDemand = l.originDemand;
			this.lock = l.lock;
			this.demand = l.demand;
		}

		@Override
		public float getWeight() {
			return demand;
		}

		@Override
		public void setWeight(float w) {
			demand = w;
		}
	}

}
