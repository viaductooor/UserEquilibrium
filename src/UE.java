import java.util.LinkedList;
import java.util.List;

public class UE {
	private LinkSet ls; // data of links in the map
	private LinkedList<ODPair> ods; // OD-Pairs in the map
	

	public void init() {
		MyFileReader mfr = new MyFileReader();
		this.ls = mfr.getLinks();
		this.ods = mfr.getDemand();
	}
	
	public void setLinkSet(LinkedList<Link> list){
		LinkSet ls = new LinkSet(list);
		this.ls = ls;
	}
	
	public void setDemandSet(LinkedList<ODPair> odp){
		this.ods = odp;
	}

	public void compute(float diff) {
		// diff = totalFlow - lastTotalFlow
		// step 0
		AuxFunctions af = new AuxFunctions();
		af.allOrNothing(ods, ls);
		ls.y2x();
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
//		System.out.println("RESULT: ");
//		System.out.print(ls);
	}

	public LinkSet getLs() {
		return ls;
	}


	public LinkedList<ODPair> getOds() {
		return ods;
	}


//	public static void main(String[] args) {
//		UE ue = new UE();
//		ue.init();
//		ue.compute(50);
//	}

}
