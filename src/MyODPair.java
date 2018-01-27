/**
 * a class based on ODPair, but contains cost information.
 * 
 * @author John Smith
 * 
 */
public class MyODPair {
	private ODPair odpair;
	private float originCost;
	private float cost;
	private float increPercent;

	public MyODPair(ODPair odp) {
		this.odpair = odp;
		this.increPercent = 0;
	}
	
	public void setDemand(float demand){
		odpair.setDemand(demand);
	}
	
	public float getDemand(){
		return odpair.getDemand();
	}

	public float getIncrePercent() {
		return increPercent;
	}

	public void setIncrePercent(float increPercent) {
		this.increPercent = increPercent;
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
				+ ", demand=" + getDemand()+", cost="+cost+", originCost="+originCost+", percentage="+increPercent+"]";
	}
	
}