import java.util.LinkedList;
import java.util.List;

public class UE {

	private LinkSet ls;
	private LinkedList<ODPair> ods;

	public void init() {
		MyFileReader mfr = new MyFileReader(DataCase.chicago);
		this.ls = mfr.getLinks();
		this.ods = mfr.getDemand();
	}

	public void setLinkSet(LinkedList<Link> list) {
		LinkSet ls = new LinkSet(list);
		this.ls = ls;
	}

	public void setDemandSet(LinkedList<ODPair> odp) {
		this.ods = odp;
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
		AuxFunctions af = new AuxFunctions();
		af.allOrNothing(ods, ls);
		ls.y2x();
		float alpha = 1;
		int n = 1;
		float step = Float.POSITIVE_INFINITY;
		while (Math.abs(step) > diff) {
			System.out.println("count " + n + ": diff=" + Math.abs(step));
			ls.updateTMatrix(); // step 1
			af.allOrNothing(ods, ls);// step 2
			alpha = af.lineSearch(ls);// step 3
			float flowsum = ls.getTotalFlow();
			af.move(ls, alpha);
			step = ls.getTotalFlow() - flowsum;
			n++;
		}
		ls.updateTMatrix();
	}

	public LinkSet getLs() {
		return ls;
	}

	public LinkedList<ODPair> getOds() {
		return ods;
	}

}
