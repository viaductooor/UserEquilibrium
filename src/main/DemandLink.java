package main;
import jnetwork.WeightedEdge;

public class DemandLink implements WeightedEdge{
	private float demand;
	public DemandLink(float d) {
		this.demand = d;
	}
	@Override
	public float getWeight() {
		return demand;
	}

	@Override
	public void setWeight(float w) {
		this.demand = w;
	}

}
