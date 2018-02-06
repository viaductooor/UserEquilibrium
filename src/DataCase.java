public class DataCase {
	private String netUrl;
	private String tripUrl;
	
	public static DataCase winnipeg = new DataCase("files/Winnipeg_net.txt","files/Winnipeg_trips.txt");
	public static DataCase chicago = new DataCase("files/ChicagoRegional_net.txt","files/ChicagoRegional_trips.txt");
	public static DataCase sioux = new DataCase("files/Sioux-Falls-Network.txt","files/Sioux-Falls-Demand.txt");

	public DataCase(String netUrl, String tripUrl) {
		super();
		this.netUrl = netUrl;
		this.tripUrl = tripUrl;
	}

	public String getNetUrl() {
		return netUrl;
	}

	public void setNetUrl(String netUrl) {
		this.netUrl = netUrl;
	}

	public String getTripUrl() {
		return tripUrl;
	}

	public void setTripUrl(String tripUrl) {
		this.tripUrl = tripUrl;
	}

	@Override
	public String toString() {
		return "Case [netUrl=" + netUrl + ", tripUrl=" + tripUrl + "]";
	}
}
