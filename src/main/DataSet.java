package main;

import java.util.List;

public class DataSet {
	private int zoneNumber;
	private int nodeNubmer;
	private int firstThruNode;
	private int linkNumber;
	private List<Link> links;
	private List<Odpair> trips;

	public int getZoneNumbers() {
		return zoneNumber;
	}

	public int getNodeNubmer() {
		return nodeNubmer;
	}

	public int getFirstThruNode() {
		return firstThruNode;
	}

	public int getLinkNumber() {
		return linkNumber;
	}

	public List<Link> getLinks() {
		return links;
	}

	public List<Odpair> getTrips() {
		return trips;
	}

	public void setZoneNumbers(int mZoneNumbers) {
		this.zoneNumber = mZoneNumbers;
	}

	public void setNodeNubmers(int mNodeNubmers) {
		this.nodeNubmer = mNodeNubmers;
	}

	public void setFirstThruNode(int mFirstThruNode) {
		this.firstThruNode = mFirstThruNode;
	}

	public void setLinkNumbers(int mLinkNumbers) {
		this.linkNumber = mLinkNumbers;
	}

	public void setLinks(List<Link> mLinks) {
		this.links = mLinks;
	}

	public void setTrips(List<Odpair> mTrips) {
		this.trips = mTrips;
	}

	@Override
	public String toString() {
		return "DataSet [mZoneNumbers=" + zoneNumber + ", mNodeNubmers=" + nodeNubmer + ", mFirstThruNode="
				+ firstThruNode + ", mLinkNumbers=" + linkNumber + ", mLinks=" + links + ", mTrips=" + trips + "]";
	}
	
	

}
