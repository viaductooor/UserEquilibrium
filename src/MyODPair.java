/**
 * a class based on ODPair, but contains cost infomation.
 * 
 * @author John Smith
 * 
 */
public class MyODPair {
	private ODPair odpair;
	private float originCost;
	private float cost;
	private float originDemand;

	public MyODPair(ODPair odp) {
		this.odpair = odp;
		this.originDemand = odp.getDemand();
	}
	
	public void setDemand(float demand){
		odpair.setDemand(demand);
	}
	
	public float getDemand(){
		return odpair.getDemand();
	}
	
	public float getOriginDemand() {
		return originDemand;
	}

	public void setOriginDemand(float originDemand) {
		this.originDemand = originDemand;
	}

	

	public void setOriginCost(float c) {
		this.originCost = c;
	}

	public ODPair getOdpair() {
		return odpair;
	}

	public void setOdpair(ODPair odpair) {
		this.odpair = odpair;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	public float getOriginCost() {
		return originCost;
	}

	@Override
	public String toString() {
		return "MyODPair [origin=" + odpair.getOrigin() + ", destination=" + odpair.getDestination()
				+ ", demand=" + getDemand() + ", originDemand=" + originDemand + "]";
	}
	
}