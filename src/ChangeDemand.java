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
		ue.compute(50);
		Floyd f = new Floyd();
		f.setMatrix(ue.getLs().getTMatrix());
		f.compute();
		for (MyODPair modp : odList) {
			int origin = modp.getOdpair().getOrigin();
			int destination = modp.getOdpair().getDestination();
			float totalCost = f.getTotalCost(origin, destination);
			modp.setOriginCost(totalCost);
		}
	}

	public static float computeMarginalCost(float volume, float fftt, float cap) {
		float result = (float) (volume * fftt * (4 * Math.pow(volume, 3) * 0.15 / Math
				.pow(cap, 4)));
		return result;
	}

	public static float computeSurcharge(int n, float marginalCost,
			float lastSurcharge) {
		float result = (1 / n) * marginalCost + (1 - (1 / n)) * lastSurcharge;
		return result;
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
				//System.out.println("cost:"+p.getCost()+";origin:"+p.getOriginCost());
				return true;
			}
		}
		return false;
	}

	/**
	 * load n% of the demand
	 * 
	 * @param n
	 * @param odp
	 */
	public static void increaseDemandBy(int n, MyODPair modp) {
		float step = (float) (n * 0.01);
		float demand = modp.getDemand();
		float originDemand = modp.getOriginDemand();
		//System.out.println(modp);
		if (demand < originDemand) {
			modp.setDemand(demand + step * originDemand);
			System.out.println("increase:"+demand+":"+modp.getDemand()+":"+step*originDemand);//TESTs
		}

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
	public void changeDemand() {
		int count = 0;
		Floyd f = new Floyd();
		do {
			count++;
			ue.compute(50);
			System.out.println("count " + count + " out");
			for (MyLink l : linkList) {
				float marginalCost = computeMarginalCost(l.getTravelTime(),
						l.getFreeFlowTime(), l.getCapacity());
				l.setMarginalCost(marginalCost);
				float surcharge = computeSurcharge(count, marginalCost,
						l.getLastSurcharge());
				l.setLastSurcharge(l.getSurcharge());
				l.setSurcharge(surcharge);
			}
			/**
			 * clear demand
			 */
			for(MyODPair modp:odList){
				modp.setDemand(0.0f);
				System.out.println(modp);
			}
			do {
				/**
				 * compute shortest path
				 */
				//System.out.println("count " + count + " in");
				float[][] mat = getTSurchargeMatrix(linkList, maxSize);
				f.setMatrix(mat);
				f.compute();
				for (MyODPair modp : odList) {
					ODPair odp = modp.getOdpair();
					int o = odp.getOrigin();
					int d = odp.getDestination();
					float totalCost = f.getTotalCost(o, d);
					modp.setCost(totalCost);
					if (totalCost < modp.getOriginCost()) {
						increaseDemandBy(5, modp);
					}
				}
			} while (isTotalSmallerThanOriginal(odList));
		} while (!isSumSmallerThan(5, linkList));
	}

	public static void main(String[] args) {
		ChangeDemand cd = new ChangeDemand();
		cd.changeDemand();
		System.out.println("CHANGED DEMAND:");
		for (MyODPair modp : cd.getODPairList()) {
			System.out.println(modp.getOdpair());
		}
	}
	

}
