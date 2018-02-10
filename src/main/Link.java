package main;

public class Link {
	private int initNode;
	private int termNode;
	private float capacity;
	private float length;
	private float freeFlowTime;
	private float flow;
	private float auxFlow;
	private float travelTime;
	private float surcharge;
	private float lastSurcharge;

	public Link(int initNode, int termNode, float capacity, float length,
			float freeFlowTime) {
		super();
		this.initNode = initNode;
		this.termNode = termNode;
		this.capacity = capacity;
		this.length = length;
		this.freeFlowTime = freeFlowTime;
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

	public void updateTravelTime() {
		// calculate travelTime based on BPR function
		this.travelTime = (float) (((Math.pow(this.flow / this.capacity, 4)) * 0.15 + 1) * this.freeFlowTime);
		this.travelTime += this.surcharge;
	}

	public float[] getContent() {
		float[] res = new float[3];
		res[0] = this.initNode;
		res[1] = this.termNode;
		res[2] = this.flow;
		return res;
	}

	public int getInitNode() {
		return initNode;
	}

	public int getTermNode() {
		return termNode;
	}

	public float getCapacity() {
		return capacity;
	}

	public void setCapacity(float capacity) {
		this.capacity = capacity;
	}

	public float getFreeFlowTime() {
		return freeFlowTime;
	}

	public void setFreeFlowTime(float freeFlowTime) {
		this.freeFlowTime = freeFlowTime;
	}

	public void setTravelTime(float res) {
		this.travelTime = res;
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

	@Override
	public String toString() {
		return "Link [init=" + initNode + ", term=" + termNode + ", flow="
				+ flow + ", travelTime=" + travelTime + ", surcharge="
				+ surcharge + ", lastSurcharge=" + lastSurcharge + "]";
	}

}
