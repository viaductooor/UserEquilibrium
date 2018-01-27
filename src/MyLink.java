public class MyLink {
	/**
	 * a class based on Link contains variables and methods related to marginal
	 * cost information
	 * 
	 * @author John Smith
	 * 
	 */
	private Link link;
	private float marginalCost;
	private float surcharge;
	private float lastSurcharge;

	public MyLink(Link l) {
		this.link = l;
		marginalCost = 0;
		surcharge = 0;
		lastSurcharge = 0;
	}

	public float getTravelTime() {
		return link.getTravelTime();
	}

	public float getFreeFlowTime() {
		return link.getFreeFlowTime();
	}

	public float getCapacity() {
		return link.getCapacity();
	}

	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}

	public float getMarginalCost() {
		return marginalCost;
	}

	public void setMarginalCost(float marginalCost) {
		this.marginalCost = marginalCost;
	}

	public float getSurcharge() {
		return surcharge;
	}

	public void setSurcharge(float surcharge) {
		this.surcharge = surcharge;
	}

	public float getLastSurcharge() {
		return lastSurcharge;
	}

	public void setLastSurcharge(float lastSurcharge) {
		this.lastSurcharge = lastSurcharge;
	}
	
	public float getFlow(){
		return link.getFlow();
	}

	@Override
	public String toString() {
		return "MyLink ["+"init= " + link.getInitNode() + ", term="
				+ link.getTermNode() + ", flow=" + link.getFlow()
				+ ", lastSurcharge="+lastSurcharge+", surcharge=" + surcharge + ", marginalcost=" + marginalCost
				+ "]";
	}

}
