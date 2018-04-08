package main;

import java.util.LinkedList;
import java.util.List;

import dataset.FourLinks;
import dataset.SiouxFalls;

public final class ChangeDemand {
	private List<Link> mLinks;
	private List<ODPair> mOdpairs;
	private DataSet mDataSet;
	private float uediff;
	private int maxSize;

	public ChangeDemand(DataSet ds) {
		this.mOdpairs = ds.getOdpairs();
		this.mLinks = ds.getLinks();
		this.mDataSet = ds;
		this.maxSize = ds.getMaxsize();
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
		for (ODPair odp : mOdpairs) {
			int origin = odp.getOrigin();
			int destination = odp.getDestination();
			float totalCost = f.getTotalCost(origin, destination);
			odp.setOriginCost(totalCost);
		}
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

	public List<ODPair> getODPairList() {
		return this.mOdpairs;
	}

	/**
	 * 
	 * @param list
	 * @return
	 */
	public static float getSuchargeDiff(List<Link> list) {
		float sum = 0;
		for (Link l : list) {
			sum += Math.abs(l.getLastSurcharge() - l.getSurcharge());
		}
		return sum;
	}

	public static float getTotalLinkTravelTime(List<Link> list) {
		float sum = 0;
		for (Link l : list) {
			sum += l.getTravelTime();
		}
		return sum;
	}

	/**
	 * load n(percentage) of the demand
	 * 
	 * @param n
	 * @param odp
	 */
	public static void load(float n, ODPair odp, List<Link> linklist, Floyd f) {
		float demand = odp.getOriginDemand();
		float per = odp.getIncrePercentage();
		// if (per < 1) {
		per = per + n;
		odp.setIncrePercentage(per);
		int origin = odp.getOrigin();
		int des = odp.getDestination();
		List<Integer> l = f.getShortestPath(origin, des);
		for (int i = 0; i < l.size() - 1; i++) {
			Link _l = ChangeDemand.getLink(l.get(i), l.get(i + 1), linklist);
			_l.setFlow(_l.getFlow() + n * demand);
			_l.updateTravelTime();
		}
		// }
	}

	public static Link getLink(int start, int end, List<Link> list) {
		Link link = null;
		for (Link l : list) {
			int s = l.getInitNode();
			int e = l.getTermNode();
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
	public static float[][] getTravelTimeMatrix(List<Link> list, int maxSize) {
		float[][] mat = new float[maxSize][maxSize];
		for (int i = 0; i < maxSize; i++) {
			for (int j = 0; j < maxSize; j++) {
				mat[i][j] = Float.POSITIVE_INFINITY;
			}
		}
		for (Link link : list) {
			link.updateTravelTime();
			int origin = link.getInitNode();
			int destination = link.getTermNode();
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
		Floyd f = new Floyd();
		int costflag = 0;
		float diff;

		do {
			count1++;
			ue.compute(uediff);

			/**
			 * update marginal cost and link surcharge
			 */
			for (Link l : mLinks) {
				float marginalCost = computeMarginalCost(l.getFlow(), l.getFreeFlowTime(), l.getCapacity());
				float nowSurcharge = l.getSurcharge();
				l.setLastSurcharge(nowSurcharge);
				float sur = computeSurcharge(count1, marginalCost, nowSurcharge);
				l.setSurcharge(sur);
			}

			count2 = 0;
			clearPercentage(mOdpairs);

			/**
			 * clear the volume of every link which will be set later in the step "load"
			 */
			for (Link l : mLinks) {
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
				for (ODPair odp : mOdpairs) {
					/**
					 * get the shortest path of the OD pair, then update its total cost total_cost =
					 * sum_travel_time + sum_surcharge
					 */
					int o = odp.getOrigin();
					int d = odp.getDestination();
					float totalCost = f.getTotalCost(o, d);
					odp.setCost(totalCost);

					if (totalCost < odp.getOriginCost()) {
						/**
						 * load some, like 5% or 1% of original demand and update travel_time of every
						 * link. then update the travel time of every link that composes the shrotest
						 * path.
						 */
						load(demandStep, odp, mLinks, f);
					}
				}

				for (ODPair odp : mOdpairs) {
					updateCost(f, mLinks, odp);
				}

				costflag = 0;
				for (ODPair odp : mOdpairs) {
					if (odp.getCost() < odp.getOriginCost()) {
						costflag++;
					}
				}

			} while (costflag > 0);

			/**
			 * update demand
			 */
			for (ODPair odp : mOdpairs) {
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

		lw.logWriteLink("Result links:");
		lw.logWriteOd("Result OD pairs:");
		lw.close();
	}

	public static void clearPercentage(List<ODPair> odl) {
		for (ODPair odp : odl) {
			odp.setIncrePercentage(0);
		}
	}

	/**
	 * 
	 * @param f
	 * @param ll
	 * @param ol
	 */
	public void updateCost(Floyd f, List<Link> ll, ODPair odp) {
		float sum = 0;
		int origin = odp.getOrigin();
		int des = odp.getDestination();
		List<Integer> l = f.getShortestPath(origin, des);
		for (int i = 0; i < l.size() - 1; i++) {
			Link link = getLink(l.get(i), l.get(i + 1), ll);
			sum = sum + link.getTravelTime();
		}
		odp.setCost(sum);
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

		for (Link l : mLinks) {
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
			for (Link l : mLinks) {
				float marginalCost = computeMarginalCost(l.getFlow(), l.getFreeFlowTime(), l.getCapacity());
				float nowSurcharge = l.getSurcharge();
				l.setLastSurcharge(nowSurcharge);
				float sur = computeSurcharge(count1, marginalCost, nowSurcharge);
				l.setSurcharge(sur);
			}

			diff = ChangeDemand.getSuchargeDiff(mLinks);
			System.out.println("surcharge diff:" + diff);
			lw.logWriteOther("surcharge diff:" + diff);
		} while (diff > sdiff);

		for (Link l : mLinks) {
			l.setTravelTime(l.getTravelTime() - l.getSurcharge());
			optFlow += l.getFlow();
			optCost += l.getTravelTime() * l.getFlow();
		}

		lw.logWriteLink("Result links:");
		lw.logWriteOther("origin total cost: " + originCost + ", opt total cost: " + optCost);
		lw.close();

		System.out.println("origin total cost: " + originCost + ", opt total cost: " + optCost);

	}

	public static void main(String[] a) {
		DataSet dataSet = new SiouxFalls();
		ChangeDemand cd = new ChangeDemand(dataSet);
		cd.setUediff(50);
		cd.opt(5);
		;
	}

}
