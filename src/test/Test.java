package test;

import functions.UserEquilibrium;
import jnetwork.Graph;
import main.DemandLink;
import main.Link;
import main.TNTPReader;
import main.UeLink;

public class Test {
	public static void main(String args[]) {
		Graph<Integer, Link> graph = TNTPReader.readGraph(TNTPReader.SIOUXFALLS_NET);
		Graph<Integer, DemandLink> trips = TNTPReader.readTrips(TNTPReader.SIOUXFALLS_TRIP);
		Graph<Integer, UeLink> result = UserEquilibrium.ue(graph, trips, 50f);
		for (Graph.Entry<Integer, UeLink> e : result.entrySet()) {
			System.out.println(e.getBegin() + " " + e.getEnd() + " " + e.getLink().getFlow());
		}
	}
}
