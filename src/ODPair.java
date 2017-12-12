public class ODPair {
	private int origin;
	private int destination;
	private int demand;

	public ODPair(int o, int d, int demand) {
		this.origin = o;
		this.destination = d;
		this.demand = demand;
	}

	public int[] getContent() {
		int[] res = new int[3];
		res[0] = origin;
		res[1] = destination;
		res[2] = demand;
		return res;
	}

	public int getDemand() {
		return this.demand;
	}

	public int getOrigin() {
		return this.origin;
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
