public final class ODPair {
	private int origin;
	private int destination;
	private float demand;

	public ODPair(int o, int d, int demand) {
		this.origin = o;
		this.destination = d;
		this.demand = demand;
	}
	public ODPair(ODPair odp){
		this.origin = odp.origin;
		this.destination = odp.destination;
		this.demand = odp.demand;
	}

	public float getDemand() {
		return this.demand;
	}

	public int getOrigin() {
		return this.origin;
	}
	
	public void setDemand(float demand){
		this.demand = demand;
	}

	public int getDestination() {
		return this.destination;
	}

	@Override
	public String toString() {
		return "[origin=" + origin + ", destination=" + destination
				+ ", demand=" + demand + "]";
	}
}
