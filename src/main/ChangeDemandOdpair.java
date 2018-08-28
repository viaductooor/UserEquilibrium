package main;

public final class ChangeDemandOdpair extends Odpair {
	private float originCost;
	private float cost;
	private float increPercentage;
	private float originDemand;
	private boolean lock;

	public ChangeDemandOdpair(int o, int d, float demand) {
		super(0, d, demand);
		this.originDemand = demand;
		this.increPercentage = 0;
		this.lock = false;
	}

	public ChangeDemandOdpair(ChangeDemandOdpair odp) {
		this.from = odp.from;
		this.to = odp.to;
		this.demand = odp.demand;
		this.originDemand = odp.originDemand;
		this.lock = false;
	}
	
	public ChangeDemandOdpair(Odpair odp) {
		super(odp.getFrom(),odp.getTo(),odp.getDemand());
		this.originDemand = demand;
		this.increPercentage = 0;
		this.lock = false;
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

	public boolean isLock() {
		return lock;
	}

	public void setLock(boolean lock) {
		this.lock = lock;
	}

	@Override
	public String toString() {
		return "ODPair [origin=" + from + ", destination=" + to + ", demand=" + demand + ", originCost=" + originCost
				+ ", cost=" + cost + ", increPercentage=" + increPercentage + "]";
	}

}
