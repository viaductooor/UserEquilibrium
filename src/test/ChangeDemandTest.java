package test;

import functions.ChangeDemand;
import jnetwork.ExcelUtils;
import jnetwork.Graph;
import main.DemandLink;
import main.Link;
import main.TNTPReader;
import main.UeLink;

public class ChangeDemandTest {
	public static void main(String[] a) {
		long startTime = System.currentTimeMillis();
		Graph<Integer,Link> graph = TNTPReader.readGraph(TNTPReader.SIOUXFALLS_NET);
		Graph<Integer,DemandLink> trips = TNTPReader.readTrips(TNTPReader.SIOUXFALLS_TRIP);
		
		ChangeDemand cd = new ChangeDemand(graph,trips);
		Graph<Integer,UeLink> opt = cd.opt();
		ExcelUtils.writeGraph(opt, "log/opt.xls");
		long finishTime = System.currentTimeMillis();
		System.out.println("Time: " + (finishTime - startTime) / 1000 +"s");
	}
}
