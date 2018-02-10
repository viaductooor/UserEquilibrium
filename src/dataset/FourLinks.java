package dataset;

import java.util.LinkedList;
import java.util.List;

import main.DataSet;
import main.Link;
import main.ODPair;

public class FourLinks extends DataSet {

	public FourLinks(String netUrl, String tripUrl) {
		super(netUrl, tripUrl);
	}

	public FourLinks() {
		super("", "");
	}

	@Override
	public List<Link> readLinks() {
		Link l1 = new Link(1, 2, 200, 0, 10);
		Link l2 = new Link(1, 4, 150, 0, 11);
		Link l3 = new Link(2, 3, 200, 0, 10);
		Link l4 = new Link(4, 3, 150, 0, 11);
		List<Link> list = new LinkedList<Link>();
		list.add(l1);
		list.add(l2);
		list.add(l3);
		list.add(l4);
		return list;
	}

	@Override
	public List<ODPair> readTrips() {
		LinkedList<ODPair> odl = new LinkedList<ODPair>();
		odl.add(new ODPair(1, 3, 475));
		return odl;
	}

}
