package test;

import file.ExcelUtils;
import functions.ChangeDemand;
import jnetwork.Graph;
import main.DemandLink;
import main.Link;
import main.TNTPReader;
import main.UeLink;

public class optWithoutMsaTest {
	public static void main(String[] args) {
		Graph<Integer,Link> graph = TNTPReader.readGraph(TNTPReader.SIOUXFALLS_NET);
		Graph<Integer,DemandLink> trips = TNTPReader.readTrips(TNTPReader.SIOUXFALLS_TRIP);
		
		ChangeDemand cd = new ChangeDemand(graph,trips);
		Graph<Integer,UeLink> opt = cd.optWithoutMsa();
	}
}
