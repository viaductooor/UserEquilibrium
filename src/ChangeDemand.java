import java.util.LinkedList;
import java.util.List;

public final class ChangeDemand {
	private UE ue;
	private List<MyLink> linkList;
	private List<MyODPair> odList;
	private int maxSize;

	public ChangeDemand() {
		ue = new UE();
		ue.init();

		/**
		 * TEST
		 */
		// Link l1 = new Link(1, 2, 200, 0, 10);
		// Link l2 = new Link(1, 4, 150, 0, 11);
		// Link l3 = new Link(2, 3, 200, 0, 10);
		// Link l4 = new Link(4, 3, 150, 0, 11);
		// LinkedList<Link> mlist = new LinkedList<Link>();
		// mlist.add(l1);
		// mlist.add(l2);
		// mlist.add(l3);
		// mlist.add(l4);
		// LinkedList<ODPair> odl = new LinkedList<ODPair>();
		// odl.add(new ODPair(1, 3, 475));
		// ue.setLinkSet(mlist);
		// ue.setDemandSet(odl);

		List<Link> ll = ue.getLs().getSet();
		List<ODPair> ol = ue.getOds();
		maxSize = ue.getLs().getMaxSize();
		linkList = new LinkedList<MyLink>();
		odList = new LinkedList<MyODPair>();
		for (Link l : ll) {
			linkList.add(new MyLink(l));
		}
		for (ODPair o : ol) {
			odList.add(new MyODPair(o));
		}

		/**
		 * set original total cost
		 */
		ue.compute(5000);
		System.out.println(ue.getLs());
		Floyd f = new Floyd();
		f.setMatrix(ue.getLs().getTMatrix());
		f.compute();
		for (MyODPair modp : odList) {
			int origin = modp.getOdpair().getOrigin();
			int destination = modp.getOdpair().getDestination();
			float totalCost = f.getTotalCost(origin, destination);
			modp.setOriginCost(totalCost);
		}
		System.out.println("original cost:");
		for (MyODPair modp : odList) {
			System.out.println(modp);
		}
	}

	public static float computeMarginalCost(float volume, float fftt, float cap) {
		float result = (float) (volume * fftt * (4 * Math.pow(volume, 3) * 0.15 / Math
				.pow(cap, 4)));
		return result;
	}

	public static float computeSurcharge(int n, float marginalCost,
			float lastSurcharge) {
		float result = (1f / n) * marginalCost + (1 - (1f / n)) * lastSurcharge;
		return result;
	}

	public static float computeTravelTime(float flow, float capacity,
			float freeFlowTime) {
		// 根据当前flow和其他固定属性计算路阻
		return (float) (((Math.pow(flow / capacity, 4)) * 0.15 + 1) * freeFlowTime);
	}

	public UE getUE() {
		return ue;
	}

	public List<MyODPair> getODPairList() {
		return this.odList;
	}

	/**
	 * outter loop criterion
	 * 
	 * @param f
	 * @param list
	 * @return
	 */
	public static boolean isSumSmallerThan(float f, List<MyLink> list) {
		float sum = 0;
		for (MyLink l : list) {
			sum += Math.abs(l.getLastSurcharge() - l.getSurcharge());
		}
		if (sum < f) {
			return true;
		}
		return false;
	}

	/**
	 * inner loop criterion
	 * 
	 * @param list
	 * @return
	 */
	public static boolean isTotalSmallerThanOriginal(List<MyODPair> list) {
		for (MyODPair p : list) {
			if (p.getCost() < p.getOriginCost()) {
				// System.out.println("cost:"+p.getCost()+";origin:"+p.getOriginCost());
				return true;
			}
		}
		return false;
	}

	/**
	 * load n(percentage) of the demand
	 * 
	 * @param n
	 * @param odp
	 */
	public static void load(float n, MyODPair modp, List<MyLink> linklist,
			Floyd f) {
		float demand = modp.getOriginDemand();
		float per = modp.getIncrePercent();
		// if (per < 1) {
		per = per + n;
		modp.setIncrePercent(per);
		int origin = modp.getOdpair().getOrigin();
		int des = modp.getOdpair().getDestination();
		List<Integer> l = f.getShortestPath(origin, des);
		for (int i = 0; i < l.size() - 1; i++) {
			MyLink _l = ChangeDemand.getLink(l.get(i), l.get(i + 1), linklist);
			_l.getLink().setFlow(_l.getLink().getFlow() + n * demand);
			_l.getLink().updateTravelTime();
		}
		// }
	}

	public static MyLink getLink(int start, int end, List<MyLink> list) {
		MyLink ml = null;
		for (MyLink l : list) {
			int s = l.getLink().getInitNode();
			int e = l.getLink().getTermNode();
			if (start == s & end == e) {
				ml = l;
				break;
			}
		}
		return ml;
	}

	/**
	 * prepare for the shortest path algorithm
	 * 
	 * @param list
	 * @param maxSize
	 * @return :the adjacent matrix
	 */
	public static float[][] getTSurchargeMatrix(List<MyLink> list, int maxSize) {
		float[][] mat = new float[maxSize][maxSize];
		for (int i = 0; i < maxSize; i++) {
			for (int j = 0; j < maxSize; j++) {
				mat[i][j] = Float.POSITIVE_INFINITY;
			}
		}
		for (MyLink mlink : list) {
			mlink.getLink().updateTravelTime();
			int origin = mlink.getLink().getInitNode();
			int destination = mlink.getLink().getTermNode();
			float traveltime = mlink.getTravelTime();
			float surcharge = mlink.getSurcharge();
			mat[origin - 1][destination - 1] = traveltime + surcharge;
		}
		return mat;
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
		int num100percent = 0;
		float surchargeFlag;

		do {
			count1++;
			ue.compute(5000);

			/**
			 * update marginal cost and link surcharge
			 */
			for (MyLink l : linkList) {
				float marginalCost = computeMarginalCost(l.getFlow(),
						l.getFreeFlowTime(), l.getCapacity());
				l.setMarginalCost(marginalCost);
				float nowSurcharge = l.getSurcharge();
				l.setLastSurcharge(nowSurcharge);
				float sur = computeSurcharge(count1, marginalCost, nowSurcharge);
				l.setSurcharge(sur);
			}

			count2 = 0;
			clearPercentage(odList);

			/**
			 * clear the volume of every link it'll be set later in the step
			 * "load"
			 */
			for (MyLink ml : linkList) {
				ml.getLink().setFlow(0);
			}

			do {

				/**
				 * compute shortest path of all OD pairs
				 */
				count2++;
				System.out.println("outter loop:" + count1 + "; inner loop: "
						+ count2);
				float[][] mat = getTSurchargeMatrix(linkList, maxSize);
				f.setMatrix(mat);
				f.compute();

				/**
				 * for each OD Pair, find shortest path if it's total cost <
				 * original cost then load 5% original demand(or something else)
				 */
				for (MyODPair modp : odList) {
					/**
					 * get the shortest path of the OD pair, then update its
					 * total cost total_cost = sum_travel_time + sum_surcharge
					 */
					ODPair odp = modp.getOdpair();
					int o = odp.getOrigin();
					int d = odp.getDestination();
					float totalCost = f.getTotalCost(o, d);
					modp.setCost(totalCost);

					if (totalCost < 999999) {
						lw.logWriteOther("normal cost:" + modp);
					}

					if (totalCost < modp.getOriginCost()) {
						/**
						 * load some, like 5% or 1% of original demand and
						 * update travel_time of every link. then update the
						 * travel time of every link that composes the shrotest
						 * path.
						 */
						load(demandStep, modp, linkList, f);
					}
				}

				for (MyODPair modp : odList) {
					updateCost(f, linkList, modp);
				}

				costflag = 0;
				num100percent = 0;
				for (MyODPair modp : odList) {
					if (modp.getCost() < modp.getOriginCost()) {
						costflag++;
					}
					if (modp.getIncrePercent() >= 1) {
						num100percent++;
					}
				}

			} while (costflag > 0 & count2 < 1);

			/**
			 * update demand
			 */
			for (MyODPair modp : odList) {
				float demand = modp.getOriginDemand();
				float percentage = modp.getIncrePercent();
				modp.setDemand(demand * percentage);
			}

			surchargeFlag = 0;
			for (MyLink l : linkList) {
				surchargeFlag += Math.abs(l.getLastSurcharge()
						- l.getSurcharge());
			}
		} while (surchargeFlag > surchargeDiff & count1 <1);

		lw.logWriteLink("Result links:");
		lw.logWriteOd("Result OD pairs:");
		lw.close();
	}

	public float getSurchargeChange() {
		float sum = 0;
		for (MyLink l : linkList) {
			sum += Math.abs(l.getLastSurcharge() - l.getSurcharge());
		}
		return sum;
	}

	public static void clearPercentage(List<MyODPair> odl) {
		for (MyODPair modp : odl) {
			modp.setIncrePercent(0);
		}
	}

	/**
	 * 
	 * @param f
	 * @param ll
	 * @param ol
	 */
	public void updateCost(Floyd f, List<MyLink> ll, MyODPair modp) {
		float sum = 0;
		int origin = modp.getOdpair().getOrigin();
		int des = modp.getOdpair().getDestination();
		List<Integer> l = f.getShortestPath(origin, des);
		for (int i = 0; i < l.size() - 1; i++) {
			MyLink mlink = getLink(l.get(i), l.get(i + 1), ll);
			Link link = mlink.getLink();
			sum = sum + link.getTravelTime() + mlink.getSurcharge();
		}
		modp.setCost(sum);
	}

	public static void main(String[] args) {
		ChangeDemand cd = new ChangeDemand();
		cd.changeDemand(0.2f, 10);
		System.out.println("CHANGED DEMAND:");
		for (MyODPair modp : cd.getODPairList()) {
			System.out.println(modp.getOdpair());
		}
		System.out.println(cd.getUE().getLs());
	}

}
