import java.util.LinkedList;
import java.util.List;

public class ODPairCost {
	//adding cost-infomation to an ODPair object
	private ODPair odPair;
	private List<Float> cost;
	public ODPair getOdPair() {
		return odPair;
	}
	public void addCost(float c){
		this.cost.add(c);
	}
	public float getOriginCost(){
		return cost.get(0);
	}
	public float getLastCost(){
		return cost.get(cost.size()-1);
	}
	public void setOdPair(ODPair odPair) {
		this.odPair = odPair;
	}
	public List<Float> getCost() {
		return cost;
	}
	public void setCost(List<Float> cost) {
		this.cost = cost;
	}
	public ODPairCost(ODPair odp){
		this.odPair = odp;
		this.cost = new LinkedList<Float>();
	}
}
