public class Link {
	private int initNode;
	private int termNode;
	private float capacity;
	private float length;
	private float freeFlowTime;
	private float flow;
	private float auxFlow;
	private float travelTime;

	public Link(int initNode, int termNode, float capacity, float length,
			float freeFlowTime) {
		super();
		this.initNode = initNode;
		this.termNode = termNode;
		this.capacity = capacity;
		this.length = length;
		this.freeFlowTime = freeFlowTime;
		this.flow = 0;
		this.auxFlow = 0;
		updateRes();
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

	public void updateRes() {
		// 根据当前flow和其他固定属性计算路阻
		this.travelTime = (float) (((Math.pow(this.flow / this.capacity, 4)) * 0.15 + 1) * this.freeFlowTime);
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


	@Override
	public String toString() {
		return "Link [initNode=" + initNode + ", termNode=" + termNode + "]";
	}

}
