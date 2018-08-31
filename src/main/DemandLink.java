package main;
import jnetwork.WeightedLink;

public class DemandLink implements WeightedLink{
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
