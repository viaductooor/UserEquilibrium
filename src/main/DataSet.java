package main;

import java.util.List;

public abstract class DataSet {
	private String netUrl;
	private String tripUrl;
	private List<Link> links;
	private List<ODPair> odpairs;
	private int maxSize;

	public DataSet(String netUrl, String tripUrl) {
		this.netUrl = netUrl;
		this.tripUrl = tripUrl;
		setLinks(readLinks());
		setOdpairs(readTrips());
		this.maxSize = computeMaxsize();
	}

	public DataSet() {

	}

	public String getNetUrl() {
		return netUrl;
	}

	public void setNetUrl(String netUrl) {
		this.netUrl = netUrl;
	}

	public String getTripUrl() {
		return tripUrl;
	}

	public void setTripUrl(String tripUrl) {
		this.tripUrl = tripUrl;
	}

	public List<ODPair> getOdpairs() {
		return odpairs;
	}

	public void setOdpairs(List<ODPair> odpairs) {
		this.odpairs = odpairs;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public List<Link> getLinks() {
		return links;
	}

	public abstract List<Link> readLinks();

	public abstract List<ODPair> readTrips();

	@Override
	public String toString() {
		return "Case [netUrl=" + netUrl + ", tripUrl=" + tripUrl + "]";
	}

	public int computeMaxsize() {
		int max = 0;
		for (Link l : links) {
			int origin = l.getInitNode();
			int des = l.getTermNode();
			int t = origin > des ? origin : des;
			if (t > max) {
				max = t;
			}
		}
		return max;
	}

	public int getMaxsize() {
		return this.maxSize;
	}
}
