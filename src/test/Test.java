package test;

import functions.UserEquilibrium;
import jnetwork.ExcelUtils;
import jnetwork.Graph;
import main.DemandLink;
import main.Link;
import main.TNTPReader;
import main.UeLink;

public class Test {
	public static void main(String args[]) {
		new Test().ten();

	}

	public void ten() {
		Graph<Integer, Link> graph = TNTPReader.readGraph(TNTPReader.SIOUXFALLS_NET);
		Graph<Integer, DemandLink> trips = TNTPReader.readTrips(TNTPReader.SIOUXFALLS_TRIP);

		int[] begin = { 16, 10, 13, 24, 6, 8, 14, 23, 11, 15 };
		int[] end = { 10, 16, 24, 13, 8, 6, 11, 22, 14, 14 };
		for (int i = 0; i < begin.length; i++) {
			Graph<Integer, Link> newGraph = Test.updateGraph(graph, begin[i], end[i], 0.1f);
			Graph<Integer, UeLink> ueGraph = UserEquilibrium.initGraph(newGraph);
			UserEquilibrium.ue(ueGraph, trips, 50);
			ExcelUtils.writeGraph(ueGraph, "log/" + begin[i] + "_" + end[i] + ".xls");
			System.out.println(begin[i] + "_" + end[i] + " total flow:" + UserEquilibrium.getTotalFlow(ueGraph)
					+ " total travel time:" + UserEquilibrium.getTotalTravelTime(ueGraph));
		}
	}

	public void ue() {
		Graph<Integer, Link> graph = TNTPReader.readGraph(TNTPReader.SIOUXFALLS_NET);
		Graph<Integer, DemandLink> trips = TNTPReader.readTrips(TNTPReader.SIOUXFALLS_TRIP);
		Graph<Integer, UeLink> ueGraph = UserEquilibrium.initGraph(graph);
		UserEquilibrium.ue(ueGraph, trips, 50);
		ExcelUtils.writeGraph(ueGraph, "log/original_ue.xls");
		System.out.println(" total flow:" + UserEquilibrium.getTotalFlow(ueGraph) + " total travel time:"
				+ UserEquilibrium.getTotalTravelTime(ueGraph));
	}

	public static Graph<Integer, Link> updateGraph(Graph<Integer, Link> graph, int begin, int end, float capacity) {
		Graph<Integer, Link> newGraph = new Graph<Integer, Link>();
		for (Graph.Entry<Integer, Link> e : graph.entrySet()) {
			newGraph.addDiEdge(e.getBegin(), e.getEnd(), new Link(e.getLink()));
		}
		Link l = graph.getEdge(begin, end);
		Link l2 = new Link(l);
		l2.setCapacity(capacity);
		newGraph.addDiEdge(begin, end, l2);
		return newGraph;
	}
}
