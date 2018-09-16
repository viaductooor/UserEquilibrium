package test;

import functions.Game;
import jnetwork.Graph;
import main.DemandLink;
import main.Link;
import main.TNTPReader;

public class GameTest {

	public static void main(String args[]) {
		Graph<Integer,Link> graph = TNTPReader.readGraph(TNTPReader.SIOUXFALLS_NET);
		Graph<Integer,DemandLink> trips = TNTPReader.readTrips(TNTPReader.SIOUXFALLS_TRIP);
		Game game = new Game(graph,trips);
		game.run(1, 0, 1, 0.0001f);
	}
}
