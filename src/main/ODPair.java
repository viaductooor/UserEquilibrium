package main;

public final class ODPair {
	private int origin;
	private int destination;
	private float demand;
	private float originCost;
	private float cost;
	private float increPercentage;
	private float originDemand;

	public ODPair(int o, int d, float demand) {
		this.origin = o;
		this.destination = d;
		this.demand = demand;
		this.originDemand = demand;
		this.increPercentage = 0;
	}

	public ODPair(ODPair odp) {
		this.origin = odp.origin;
		this.destination = odp.destination;
		this.demand = odp.demand;
		this.originDemand = odp.originDemand;
	}

	public float getDemand() {
		return this.demand;
	}

	public int getOrigin() {
		return this.origin;
	}

	public void setDemand(float demand) {
		this.demand = demand;
	}

	public int getDestination() {
		return this.destination;
	}

	public float getOriginCost() {
		return originCost;
	}

	public void setOriginCost(float originCost) {
		this.originCost = originCost;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	public float getIncrePercentage() {
		return increPercentage;
	}

	public void setIncrePercentage(float increPercentage) {
		this.increPercentage = increPercentage;
	}

	public float getOriginDemand() {
		return originDemand;
	}

	public void setOriginDemand(float originDemand) {
		this.originDemand = originDemand;
	}

	@Override
	public String toString() {
		return "ODPair [origin=" + origin + ", destination=" + destination
				+ ", demand=" + demand + ", originCost=" + originCost
				+ ", cost=" + cost + ", increPercentage=" + increPercentage
				+ "]";
	}

}
