package main;

import jnetwork.WeightedLink;

public class UeLink extends Link implements WeightedLink {
	protected float flow;
	protected float auxFlow;
	protected float travelTime;
	protected float surcharge;
	protected float lastSurcharge;

	public UeLink(int from, int to, float capacity, float length, float ftime, float b, float power, float speed,
			float toll, int type) {
		super(from, to, capacity, length, ftime, b, power, speed, toll, type);
		this.travelTime = 0;
		this.flow = 0;
		this.auxFlow = 0;
		this.surcharge = 0;
		this.lastSurcharge = 0;
		updateTravelTime();
	}

	public UeLink(Link l) {
		super(l.getFrom(), l.getTo(), l.getCapacity(), l.getLength(), l.getFtime(), l.getB(), l.getPower(),
				l.getSpeed(), l.getToll(), l.getType());
		this.travelTime = 0;
		this.flow = 0;
		this.auxFlow = 0;
		this.surcharge = 0;
		this.lastSurcharge = 0;
		updateTravelTime();
	}

	public void setFlow(float flow) {
		this.flow = flow;
	}

	public float getFlow() {
		return this.flow;
	}

	public float getAuxFlow() {
		return auxFlow;
	}

	public void setAuxFlow(float f) {
		this.auxFlow = f;
	}

	public float getTravelTime() {
		return this.travelTime;
	}

	public void setTravelTime(float t) {
		this.travelTime = t;
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

	public void updateTravelTime() {
		// calculate travelTime based on BPR function
		this.travelTime = (float) (((Math.pow(this.flow / this.capacity, 4)) * 0.15 + 1) * this.ftime);
		this.travelTime += this.surcharge;
	}

	public float[] getContent() {
		float[] res = new float[3];
		res[0] = this.from;
		res[1] = this.to;
		res[2] = this.flow;
		return res;
	}

	@Override
	public String toString() {
		return "Link [init=" + from + ", term=" + to + ", flow=" + flow + ", travelTime=" + travelTime + ", surcharge="
				+ surcharge + ", lastSurcharge=" + lastSurcharge + "]\n";
	}

	@Override
	public float getWeight() {
		return travelTime;
	}

	@Override
	public void setWeight(float w) {
		this.travelTime = w;
	}

}
