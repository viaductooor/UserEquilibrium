public class Link {
	private int initNode;
	private int termNode;
	private float capacity;
	private float length;
	private float freeFlowTime;
	private float flow;
	private float auxFlow;
	private float resistence;
	private float marginalCost;
	private float linkSurcharge;

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
		this.linkSurcharge = 0;
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

	public float getRes() {
		return this.resistence;
	}

	public void updateRes() {
		// ���ݵ�ǰflow�������̶����Լ���·��
		this.resistence = (float) (((Math.pow(this.flow / this.capacity, 4)) * 0.15 + 1) * this.freeFlowTime);
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

	public void setRes(float res) {
		this.resistence = res;
	}

	public float getMarginalCost() {
		return marginalCost;
	}

	public void setMarginalCost(float marginalCost) {
		this.marginalCost = marginalCost;
	}

	public float getLinkSurcharge() {
		return linkSurcharge;
	}

	public void setLinkSurcharge(float linkSurcharge) {
		this.linkSurcharge = linkSurcharge;
	}
	
	public void updateMarginalCost(){
		float mc = (float) (this.getFlow()*this.getFreeFlowTime()*(4*Math.pow(this.getFlow(), 3)*0.15)/(Math.pow(this.getCapacity(), 4)));
		this.setMarginalCost(mc);
	}

	@Override
	public String toString() {
		return "Link [initNode=" + initNode + ", termNode=" + termNode + "]";
	}

}