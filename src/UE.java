import java.util.LinkedList;
import java.util.List;

public class UE {
	private LinkSet ls; // data of links in the map
	private LinkedList<ODPair> ods; // OD-Pairs in the map
	

	public void init() {
		MyFileReader mfr = new MyFileReader();
		this.ls = mfr.getLinks();
		this.ods = mfr.getDemand();
		/*System.out.println("DEMAND:");
		for (ODPair odp : ods) {
			System.out.println(odp);
		}*/
		/*
		 * System.out
		 * .println("---------------------------------------------------");
		 * System.out.println("LINKS:"); System.out.println(ls);
		 */
	}

	public void compute(float diff) {
		// diff = totalFlow - lastTotalFlow
		// step 0
		AuxFunctions af = new AuxFunctions();
		af.allOrNothing(ods, ls);
		ls.y2x();
		/*
		 * System.out.println("INIT: "); System.out.print(ls); System.out
		 * .println("---------------------------------------------------");
		 */
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
			//System.out.println("decrease of total flow:" + step);
			n++;
		}
		ls.updateTMatrix();
		System.out.println("RESULT: ");
		System.out.print(ls);
	}

	/*public void updateDemand() {
		init();
		compute(50);
		List<ODPairCost> _odcost = new LinkedList<ODPairCost>();
		for (ODPair odp : ods) {
			_odcost.add(new ODPairCost(odp));
		}

		Floyd f = new Floyd();
		float[][] t = ls.getTMatrix();
		f.setMatrix(t);
		f.compute();
		for (ODPairCost odc : _odcost) {
			int origin = odc.getOdPair().getOrigin();
			int destination = odc.getOdPair().getDestination();
			float totalCost = f.getTotalCost(origin, destination);
			odc.addCost(totalCost);
		}

		for (ODPair odp : ods) {
			odp.setDemand(0);
		}

		int count = 0;
		do {
			count++;//
			compute(50); // 1.UE assignment
			t = ls.getTSurchargeMatrix(count); // 2.compute margin cost and
											// 3.calculate link
			f.compute();								// surcharge
			do {
				for (ODPairCost odc : _odcost) {
					ODPair odp = odc.getOdPair();
					int origin = odp.getOrigin();
					int des = odp.getDestination();
					float totalCost = f.getTotalCost(origin, des);
					odc.addCost(totalCost);
					float originCost = odc.getOriginCost();
					if (totalCost <= originCost) {
						AuxFunctions.increaseBy5(odp);
					}
				}
				f.compute();
			} while (updateDemandCriterion(_odcost));

		} while (!outCriterion(5, ls));
		System.out.println("COUNT:" + count);
		System.out.println("CHANGED DEMAND:");
		for (ODPair odp : ods) {
			System.out.println(odp);
		}
	}*/


	public LinkSet getLs() {
		return ls;
	}


	public LinkedList<ODPair> getOds() {
		return ods;
	}


	/*public static void main(String[] args) {
		UE ue = new UE();
		ue.init();
		ue.compute(50);
		//ue.updateDemand();
	}*/

}
