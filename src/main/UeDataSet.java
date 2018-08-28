package main;

import java.util.LinkedList;
import java.util.List;

public class UeDataSet {
	private int zoneNumbers;
	private int nodeNubmers;
	private int firstThruNode;
	private int linkNumbers;
	private List<UeLink> links;
	private List<Odpair> trips;
	
	public UeDataSet(DataSet ds) {
		this.zoneNumbers = ds.getZoneNumbers();
		this.firstThruNode = ds.getFirstThruNode();
		this.linkNumbers = ds.getLinkNumber();
		this.nodeNubmers = ds.getNodeNubmer();
		this.links = new LinkedList<UeLink>();
		this.trips = new LinkedList<Odpair>();
		List<Link> links = ds.getLinks();
		List<Odpair> trips = ds.getTrips();
		for(Link l:links) {
			this.links.add(new UeLink(l));
		}
		for(Odpair odp:trips) {
			this.trips.add(new ChangeDemandOdpair(odp));
		}
	}

	public int getmZoneNumbers() {
		return zoneNumbers;
	}

	public int getNodeNubmer() {
		return nodeNubmers;
	}

	public int getFirstThruNode() {
		return firstThruNode;
	}

	public int getLinkNumbers() {
		return linkNumbers;
	}

	public List<UeLink> getLinks() {
		return links;
	}

	public List<Odpair> getTrips() {
		return trips;
	}

	public void setZoneNumbers(int mZoneNumbers) {
		this.zoneNumbers = mZoneNumbers;
	}

	public void setNodeNubmers(int mNodeNubmers) {
		this.nodeNubmers = mNodeNubmers;
	}

	public void setFirstThruNode(int mFirstThruNode) {
		this.firstThruNode = mFirstThruNode;
	}

	public void setLinkNumbers(int mLinkNumbers) {
		this.linkNumbers = mLinkNumbers;
	}

	public void setLinks(List<UeLink> mLinks) {
		this.links = mLinks;
	}

	public void setTrips(List<Odpair> mTrips) {
		this.trips = mTrips;
	}

	@Override
	public String toString() {
		return "DataSet [mZoneNumbers=" + zoneNumbers + ", mNodeNubmers=" + nodeNubmers + ", mFirstThruNode="
				+ firstThruNode + ", mLinkNumbers=" + linkNumbers + ", mLinks=" + links + ", mTrips=" + trips + "]";
	}
	
	

}
