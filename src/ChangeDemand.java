import java.util.LinkedList;
import java.util.List;

public final class ChangeDemand {
	private UE ue;
	private List<Link> linkList;
	private List<ODPair> odList;
	private float uediff;
	private int maxSize;

	public ChangeDemand() {
		uediff = 50;
		ue = new UE();
		ue.init();

		/**
		 * TEST
		 */
//		 Link l1 = new Link(1, 2, 200, 0, 10);
//		 Link l2 = new Link(1, 4, 150, 0, 11);
//		 Link l3 = new Link(2, 3, 200, 0, 10);
//		 Link l4 = new Link(4, 3, 150, 0, 11);
//		 LinkedList<Link> mlist = new LinkedList<Link>();
//		 mlist.add(l1);
//		 mlist.add(l2);
//		 mlist.add(l3);
//		 mlist.add(l4);
//		 LinkedList<ODPair> odl = new LinkedList<ODPair>();
//		 odl.add(new ODPair(1, 3, 475));
//		 ue.setLinkSet(mlist);
//		 ue.setDemandSet(odl);

		odList = ue.getOds();
		linkList = ue.getLs().getSet();
		maxSize = ue.getLs().getMaxSize();

		/**
		 * set original total cost
		 */
		ue.compute(uediff);
		Floyd f = new Floyd();
		f.setMatrix(ue.getLs().getTMatrix());
		f.compute();
		for (ODPair odp : odList) {
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
		float result = (float) (volume * fftt * (4 * Math.pow(volume, 3) * 0.15 / Math
				.pow(cap, 4)));
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
	public static float computeSurcharge(int n, float marginalCost,
			float lastSurcharge) {
		float result = (1f / n) * marginalCost + (1 - (1f / n)) * lastSurcharge;
		return result;
	}

	public static float computeTravelTime(float flow, float capacity,
			float freeFlowTime) {

		return (float) (((Math.pow(flow / capacity, 4)) * 0.15 + 1) * freeFlowTime);
	}

	public UE getUE() {
		return ue;
	}

	public List<ODPair> getODPairList() {
		return this.odList;
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
		//if (per < 1) {
			per = per + n;
			odp.setIncrePercentage(per);
			int origin = odp.getOrigin();
			int des = odp.getDestination();
			List<Integer> l = f.getShortestPath(origin, des);
			for (int i = 0; i < l.size() - 1; i++) {
				Link _l = ChangeDemand
						.getLink(l.get(i), l.get(i + 1), linklist);
				_l.setFlow(_l.getFlow() + n * demand);
				_l.updateTravelTime();
			}
		//}
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
		LogWriter lw = new LogWriter(linkList, odList);
		lw.init();

		 lw.logWriteLink("Original Link:");
		 lw.logWriteOd("Original Trip");

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
			for (Link l : linkList) {
				float marginalCost = computeMarginalCost(l.getFlow(),
						l.getFreeFlowTime(), l.getCapacity());
				float nowSurcharge = l.getSurcharge();
				l.setLastSurcharge(nowSurcharge);
				float sur = computeSurcharge(count1, marginalCost, nowSurcharge);
				l.setSurcharge(sur);
			}

			count2 = 0;
			clearPercentage(odList);

			/**
			 * clear the volume of every link which will be set later in the
			 * step "load"
			 */
			for (Link l : linkList) {
				l.setFlow(0);
			}

			do {

				/**
				 * compute shortest path of all OD pairs
				 */
				count2++;
				System.out.println("outter loop:" + count1 + "; inner loop: "
						+ count2);
				float[][] mat = getTravelTimeMatrix(linkList, maxSize);
				f.setMatrix(mat);
				f.compute();

				/**
				 * for each OD Pair, find shortest path if it's total cost <
				 * original cost then load 5% original demand(or something else)
				 */
				for (ODPair odp : odList) {
					/**
					 * get the shortest path of the OD pair, then update its
					 * total cost total_cost = sum_travel_time + sum_surcharge
					 */
					int o = odp.getOrigin();
					int d = odp.getDestination();
					float totalCost = f.getTotalCost(o, d);
					odp.setCost(totalCost);

					if (totalCost < odp.getOriginCost()) {
						/**
						 * load some, like 5% or 1% of original demand and
						 * update travel_time of every link. then update the
						 * travel time of every link that composes the shrotest
						 * path.
						 */
						load(demandStep, odp, linkList, f);
					}
				}

				for (ODPair odp : odList) {
					updateCost(f, linkList, odp);
				}

				costflag = 0;
				for (ODPair odp : odList) {
					if (odp.getCost() < odp.getOriginCost()) {
						costflag++;
					}
				}

			} while (costflag > 0);

			/**
			 * update demand
			 */
			for (ODPair odp : odList) {
				float demand = odp.getOriginDemand();
				float percentage = odp.getIncrePercentage();
				odp.setDemand(demand * percentage);
			}

			// lw.logWriteLink("After surcharge computation");
			diff = ChangeDemand.getSuchargeDiff(linkList);
			System.out.println("surcharge diff:" + diff);
			lw.logWriteOther("surcharge diff:" + diff);
//			lw.logWriteLink("Links:");
//			lw.logWriteOd("ODPairs:");
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

	public static void main(String[] args) {
		ChangeDemand cd = new ChangeDemand();
		cd.setUediff(5000);
		cd.changeDemand(0.05f, 10);
		cd.setUediff(5000);
		
		// System.out.println("CHANGED DEMAND:");
		// for (MyODPair modp : cd.getODPairList()) {
		// System.out.println(modp.getOdpair());
		// }
		// System.out.println(cd.getUE().getLs());
	}

}
