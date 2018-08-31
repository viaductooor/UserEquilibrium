package functions;

import java.util.LinkedList;
import java.util.List;

import jnetwork.Graph;
import jnetwork.WeightedLink;
import main.ChangeDemandOdpair;
import main.DemandLink;
import main.Floyd;
import main.LogWriter;
import main.Odpair;
import main.TNTPReader;
import main.UeDataSet;
import main.UeLink;

public final class ChangeDemand {
	private List<UeLink> mLinks;
	private List<ChangeDemandOdpair> mOdpairs;
	private UeDataSet mDataSet;
	private float uediff;
	private int maxSize;

	public ChangeDemand(UeDataSet ds) {
		this.mOdpairs = odp2cdodp(ds.getTrips());
		this.mLinks = ds.getLinks();
		this.mDataSet = ds;
		this.maxSize = ds.getNodeNubmer();
	}

	public void init() {
		UserEquilibrium ue = new UserEquilibrium(mDataSet);

		/**
		 * set original total cost
		 */
		ue.compute(uediff);
		Floyd f = new Floyd();
		f.setMatrix(UserEquilibrium.getTMatrix(mLinks, maxSize));
		f.compute();
		for (ChangeDemandOdpair odp : mOdpairs) {
			int origin = odp.getFrom();
			int destination = odp.getTo();
			float totalCost = f.getTotalCost(origin, destination);
			odp.setOriginCost(totalCost);
		}
	}
	
	public List<ChangeDemandOdpair> odp2cdodp(List<Odpair> odps){
		List<ChangeDemandOdpair> list = new LinkedList<ChangeDemandOdpair>();
		for(Odpair odp:odps) {
			list.add(new ChangeDemandOdpair(odp));
		}
		return list;
	}

	/**
	 * compute marginal cost based volume, freeFlowTime and Capacity
	 * 
	 * @param volume
	 * @param fftt
	 * @param cap
	 * @return
	 */
	public static float computeMarginalCost(float volume, float fftt, float cap) {
		float result = (float) (volume * fftt * (4 * Math.pow(volume, 3) * 0.15 / Math.pow(cap, 4)));
		return result;
	}

	/**
	 * compute surcharge based on marginalCost and last surcharge
	 * 
	 * @param n
	 * @param marginalCost
	 * @param lastSurcharge
	 * @return
	 */
	public static float computeSurcharge(int n, float marginalCost, float lastSurcharge) {
		float result = (1f / n) * marginalCost + (1 - (1f / n)) * lastSurcharge;
		return result;
	}

	public static float computeTravelTime(float flow, float capacity, float freeFlowTime) {

		return (float) (((Math.pow(flow / capacity, 4)) * 0.15 + 1) * freeFlowTime);
	}

	public List<ChangeDemandOdpair> getODPairList() {
		return this.mOdpairs;
	}

	/**
	 * 
	 * @param list
	 * @return
	 */
	public static float getSuchargeDiff(List<UeLink> list) {
		float sum = 0;
		for (UeLink l : list) {
			sum += Math.abs(l.getLastSurcharge() - l.getSurcharge());
		}
		return sum;
	}

	public static float getTotalLinkTravelTime(List<UeLink> list) {
		float sum = 0;
		for (UeLink l : list) {
			sum += l.getTravelTime();
		}
		return sum;
	}

	/**
	 * load n(percentage) of the demand,change flow and traveltime of related links
	 * 
	 * @param n
	 * @param odp
	 */
	public static void load(float n, ChangeDemandOdpair odp, List<UeLink> linklist, Floyd f) {
		float demand = odp.getOriginDemand();
		float per = odp.getIncrePercentage();
		per = per + n;
		odp.setIncrePercentage(per);
		int origin = odp.getFrom();
		int des = odp.getTo();
		List<Integer> l = f.getShortestPath(origin, des);
		for (int i = 0; i < l.size() - 1; i++) {
			UeLink _l = ChangeDemand.getLink(l.get(i), l.get(i + 1), linklist);
			_l.setFlow(_l.getFlow() + n * demand);
			_l.updateTravelTime();
		}
	}

	public static UeLink getLink(int start, int end, List<UeLink> list) {
		UeLink link = null;
		for (UeLink l : list) {
			int s = l.getFrom();
			int e = l.getTo();
			if (start == s & end == e) {
				link = l;
				break;
			}
		}
		return link;
	}

	/**
	 * prepare for the shortest path algorithm
	 * 
	 * @param list
	 * @param maxSize
	 * @return :the adjacent matrix
	 */
	public static float[][] getTravelTimeMatrix(List<UeLink> list, int maxSize) {
		float[][] mat = new float[maxSize][maxSize];
		for (int i = 0; i < maxSize; i++) {
			for (int j = 0; j < maxSize; j++) {
				mat[i][j] = Float.POSITIVE_INFINITY;
			}
		}
		for (UeLink link : list) {
			link.updateTravelTime();
			int origin = link.getFrom();
			int destination = link.getTo();
			float traveltime = link.getTravelTime();
			mat[origin - 1][destination - 1] = traveltime;
		}
		return mat;
	}

	public float getUediff() {
		return uediff;
	}

	public void setUediff(float uediff) {
		this.uediff = uediff;
	}

	/**
	 * main method of this class
	 */
	public void changeDemand(float demandStep, float surchargeDiff) {
		/**
		 * write log to txt file
		 */
		LogWriter lw = new LogWriter(mLinks, mOdpairs);
		lw.init();

		lw.logWriteLink("Original Link:");
		lw.logWriteOd("Original Trip");

		init();
		UserEquilibrium ue = new UserEquilibrium(mDataSet);
		int count1 = 0;
		int count2;
		int lockcount;
		Floyd f = new Floyd();
		int costflag = 0;
		float diff;

		do {
			count1++;
			ue.compute(uediff);

			/**
			 * update marginal cost and link surcharge
			 */
			for (UeLink l : mLinks) {
				float marginalCost = computeMarginalCost(l.getFlow(), l.getFtime(), l.getCapacity());
				float nowSurcharge = l.getSurcharge();
				l.setLastSurcharge(nowSurcharge);
				float sur = computeSurcharge(count1, marginalCost, nowSurcharge);
				l.setSurcharge(sur);
			}

			count2 = 0;
			lockcount = 0;
			clearPercentage(mOdpairs);
			clearLock(mOdpairs);

			/**
			 * clear the volume of every link which will be set later in the step "load"
			 */
			for (UeLink l : mLinks) {
				l.setFlow(0);
			}

			do {

				/**
				 * compute shortest path of all OD pairs
				 */
				count2++;
				System.out.println("outter loop:" + count1 + "; inner loop: " + count2);
				float[][] mat = getTravelTimeMatrix(mLinks, maxSize);
				f.setMatrix(mat);
				f.compute();

				/**
				 * for each OD Pair, find shortest path if it's total cost < original cost then
				 * load 5% original demand(or something else)
				 */
				for (ChangeDemandOdpair odp : mOdpairs) {
					/**
					 * get the shortest path of the OD pair, then update its total cost total_cost =
					 * sum_travel_time + sum_surcharge
					 */
					int o = odp.getFrom();
					int d = odp.getTo();
					float totalCost = f.getTotalCost(o, d);
					odp.setCost(totalCost);

					if (totalCost > odp.getOriginCost() | odp.getIncrePercentage() >= 1) {
						odp.setLock(true);
						lockcount++;
					}
					if (odp.isLock() == false) {
						/**
						 * load some, like 5% or 1% of original demand and update travel_time of every
						 * link. then update the travel time of every link that composes the shrotest
						 * path.
						 */
						load(demandStep, odp, mLinks, f);
					}
				}

				/**
				 * update the cost of all ODPairs based on the composing links
				 */
				for (ChangeDemandOdpair odp : mOdpairs) {
					updateCost(f, mLinks, odp);
				}

			} while (lockcount < mOdpairs.size());

			/**
			 * update demand
			 */
			for (ChangeDemandOdpair odp : mOdpairs) {
				float demand = odp.getOriginDemand();
				float percentage = odp.getIncrePercentage();
				odp.setDemand(demand * percentage);
			}

			// lw.logWriteLink("After surcharge computation");
			diff = ChangeDemand.getSuchargeDiff(mLinks);
			System.out.println("surcharge diff:" + diff);
			lw.logWriteOther("surcharge diff:" + diff);
			// lw.logWriteLink("Links:");
			// lw.logWriteOd("ODPairs:");
		} while (diff > surchargeDiff);

		for (UeLink l : mLinks) {
			l.setTravelTime(l.getTravelTime() - l.getSurcharge());
		}

		lw.logWriteLink("Result links:");
		lw.logWriteOd("Result OD pairs:");
		lw.close();
	}

	public static void clearPercentage(List<ChangeDemandOdpair> odl) {
		for (ChangeDemandOdpair odp : odl) {
			odp.setIncrePercentage(0);
		}
	}

	public static void clearLock(List<ChangeDemandOdpair> odl) {
		for (ChangeDemandOdpair odp : odl) {
			odp.setLock(false);
		}
	}

	/**
	 * 
	 * @param f
	 * @param ll
	 * @param ol
	 */
	public void updateCost(Floyd f, List<UeLink> ll, ChangeDemandOdpair odp) {
		float sum = 0;
		int origin = odp.getFrom();
		int des = odp.getTo();
		List<Integer> l = f.getShortestPath(origin, des);
		for (int i = 0; i < l.size() - 1; i++) {
			UeLink link = getLink(l.get(i), l.get(i + 1), ll);
			sum = sum + link.getTravelTime();
		}
		odp.setCost(sum);
	}

	public static Graph<Integer,DemandLink> opt(Graph<Integer,UeLink> graph,Graph<Integer,DemandLink> trips,float ueDiff,float surchargeDiff){
		
		
	}
	
	public Graph<Integer,ChangeDemandLink> transChangeDemandLink(Graph<Integer,? extends WeightedLink> graph){
		Graph<Integer, ChangeDemandLink> newGraph = new Graph<Integer,ChangeDemandLink>();
		for(Graph.Entry<Integer, ? extends WeightedLink> e:graph.entrySet()) {
			Integer begin = e.getBegin();
			Integer end = e.getEnd();
			float weight = e.getLink().getWeight();
			newGraph.addEdge(begin, end, new ChangeDemandLink(weight));
		}
		return newGraph;
	}
	
	public void opt(float sdiff) {
		float originalFlow = 0;
		float optFlow = 0;
		float originCost = 0;
		float optCost = 0;

		/**
		 * write log to txt file
		 */
		LogWriter lw = new LogWriter(mLinks, mOdpairs);
		lw.init();

		init();
		lw.logWriteLink("Original Link:");
		UserEquilibrium ue = new UserEquilibrium(mDataSet);

		for (UeLink l : mLinks) {
			originalFlow += l.getFlow();
			originCost = originCost + l.getTravelTime() * l.getFlow();
		}

		int count1 = 0;
		Floyd f = new Floyd();
		float diff;

		do {
			count1++;
			ue.compute(uediff);

			/**
			 * update marginal cost and link surcharge
			 */
			for (UeLink l : mLinks) {
				float marginalCost = computeMarginalCost(l.getFlow(), l.getFtime(), l.getCapacity());
				float nowSurcharge = l.getSurcharge();
				l.setLastSurcharge(nowSurcharge);
				float sur = computeSurcharge(count1, marginalCost, nowSurcharge);
				l.setSurcharge(sur);
			}

			diff = ChangeDemand.getSuchargeDiff(mLinks);
			System.out.println("surcharge diff:" + diff);
			lw.logWriteOther("surcharge diff:" + diff);
		} while (diff > sdiff);

		for (UeLink l : mLinks) {
			l.setTravelTime(l.getTravelTime() - l.getSurcharge());
			optFlow += l.getFlow();
			optCost += l.getTravelTime() * l.getFlow();
		}

		lw.logWriteLink("Result links:");
		lw.logWriteOther("origin total cost: " + originCost + ", opt total cost: " + optCost);
		lw.close();
	}
	
	class ChangeDemandLink implements WeightedLink{
		float originCost;
		float cost;
		float increPercentage;
		float originDemand;
		boolean lock;
		float demand;
		public ChangeDemandLink(float demand) {
			this.originDemand = demand;
			this.lock = false;
			this.increPercentage = 0;
			this.demand = demand;
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

	public static void main(String[] a) {
		long startTime = System.currentTimeMillis();
		UeDataSet dataSet = new UeDataSet(TNTPReader.read(TNTPReader.SIOUXFALLS_TRIP, TNTPReader.SIOUXFALLS_NET));
		ChangeDemand cd = new ChangeDemand(dataSet);
		cd.setUediff(50);
		cd.changeDemand(0.01f, 5);
		long finishTime = System.currentTimeMillis();
		System.out.println("Time: " + (finishTime - startTime) / 1000);
	}

}
