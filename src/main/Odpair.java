package main;

public class Odpair {
	protected int from;
	protected int to;
	protected float demand;

	public Odpair(int from, int to, float demand) {
		super();
		this.from = from;
		this.to = to;
		this.demand = demand;
	}
	
	public Odpair() {}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public float getDemand() {
		return demand;
	}

	public void setDemand(float demand) {
		this.demand = demand;
	}

	@Override
	public String toString() {
		return "from=" + from + ", to=" + to + ", demand=" + demand+"\n";
	}

}
