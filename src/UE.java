import java.util.LinkedList;
import java.util.List;

public class UE {
	private LinkSet ls; // data of links in the map
	private LinkedList<ODPair> ods; // OD-Pairs in the map

	public void init() {
		MyFileReader mfr = new MyFileReader();
		this.ls = mfr.getLinks();
		this.ods = mfr.getDemand();
		System.out.println("DEMAND:");
		for (ODPair odp : ods) {
			System.out.println(odp);
		}
		System.out
				.println("---------------------------------------------------");
		System.out.println("LINKS:");
		System.out.println(ls);
	}

	public void compute(float diff) {
		// diff = totalFlow - lastTotalFlow
		// step 0
		AuxFunctions af = new AuxFunctions();
		af.allOrNothing(ods, ls);
		ls.y2x();
		System.out.println("INIT: ");
		System.out.print(ls);
		System.out
				.println("---------------------------------------------------");
		float alpha = 1;
		int n = 1;
		float step = Float.POSITIVE_INFINITY;
		while (Math.abs(step) > diff) {
			ls.updateTMatrix(); // step 1
			af.allOrNothing(ods, ls);// step 2
			alpha = af.lineSearch(ls);// step 3
			float flowsum = ls.getTotalFlow();
			af.move(ls, alpha);
			step = ls.getTotalFlow() - flowsum;
			System.out.println("decrease of total flow:" + step);
			n++;
		}
		ls.updateTMatrix();
		System.out.println("RESULT: ");
		System.out.print(ls);
	}

	public void updateDemand() {
		// create new LinkSet
		// LinkSet _ls = new LinkSet(ls);
		// LinkedList<ODPair> _odset = new LinkedList<ODPair>(ods);
		// create and initiate an ODPairCostSet
		init();
		List<ODPairCost> _odcost = new LinkedList<ODPairCost>();
		for (ODPair odp : ods) {
			_odcost.add(new ODPairCost(odp));
		}
		Floyd f = new Floyd();
		int count = 0;//
		while (updateDemandCriterion(_odcost)) {
			count++;//
			compute(50); // 1.UE assignment
			float[][] t = ls.getTSurchargeMatrix(); // 2.compute margin cost and
													// 3.calculate link
													// surcharge
			f.setMatrix(t);
			f.compute();
			for (ODPairCost odc : _odcost) {
				float totalCost = f.getTotalCost(odc.getOdPair().getOrigin(),
						odc.getOdPair().getDestination());
				odc.addCost(totalCost);
				float originCost = odc.getOriginCost();
				if (totalCost < originCost) {
					ODPair odp = odc.getOdPair();
					odp.setDemand((float) (odp.getDemand() * 1.05));
				}
			}
		}
		System.out.println("COUNT:"+count);
		System.out.println("CHANGED DEMAND:");
		for (ODPair odp : ods) {
			System.out.println(odp);
		}
	}

	public boolean updateDemandCriterion(List<ODPairCost> costs) {
		if (costs.get(0).getCost().size() < 2) {
			return true;
		} else {
			for (ODPairCost cost : costs) {
				if (cost.getLastCost() < cost.getOriginCost()) {
					return true;
				}
			}
		}

		return false;
	}

	public static void main(String[] args) {
		//UE ue = new UE();
		//ue.init();
		//ue.compute(50);
		ue.updateDemand();
	}

}
