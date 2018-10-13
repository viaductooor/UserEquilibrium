package test;

import java.io.IOException;
import java.util.Set;

import functions.TaxiSurcharge;
import jnetwork.Graph;

public class TaxiSurchargeReadTest {
	public static void main(String[] args) throws IOException {
		String curl = "files/newyork/Capacity.csv";
		String ourl = "files/newyork/Link Volume (other).csv";
		String furl = "files/newyork/FFTT.csv";
		String tripUrl = "files/newyork/trips.csv";
//		Graph<String, MyLink> graph = new TaxiSurcharge().init(curl, ourl, furl);
//		for (Graph.Entry<String, MyLink> e : graph.entrySet()) {
//			System.out.println(e.getBegin() + " " + e.getEnd() + " cap:" + e.getLink().capacity + ", fftt:"
//					+ e.getLink().fftt + ", other:" + e.getLink().otherVolume);
//		}
//		TaxiSurcharge taxiSurcharge = new TaxiSurcharge();
//		Set<Trip> trips = taxiSurcharge.readTrips(tripUrl);
//		for(Trip t:trips) {
//			System.out.println(t);
//		}
//		System.out.println(trips.size());
	}
}
