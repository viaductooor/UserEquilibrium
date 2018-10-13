package functions;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.opencsv.CSVReader;

import file.ExcelUtils;
import functions.ShortestPath.Node;
import jnetwork.AbstractEdge;
import jnetwork.Graph;

public class TaxiSurcharge {
	public final static String CAPACITY_URL = "files/newyork/Capacity.csv";
	public final static String Other_VOLUME_URL = "files/newyork/Link Volume (other).csv";
	public final static String FFTT_URL = "files/newyork/FFTT.csv";
	public final static String TRIP_URL = "files/newyork/trips.csv";
	public final static String CURRENT_COST_URL = "files/newyork/Link Travel time.csv";

	/**
	 * 
	 * @param capacityUrl
	 * @param otherVolumeUrl
	 * @param ffttUrl
	 * @return
	 * @throws IOException
	 */
	public Graph<Long, MyLink> init(String capacityUrl, String otherVolumeUrl, String ffttUrl, String currentCostUrl)
			throws IOException {
		Graph<Long, MyLink> graph = initGraph(capacityUrl);
		readOtherVolume(graph, otherVolumeUrl);
		readFftt(graph, ffttUrl);
		readCurrentCost(graph, currentCostUrl);
		checkGraph(graph);
		System.out.println("init: " + graph.describe());
		return graph;
	}

	public Set<Trip> readTrips(String url) {
		Set<Trip> set = new HashSet<Trip>();
		CSVReader reader = null;
		try {
			reader = new CSVReader(new FileReader(new File(url)));
			String[] strs = null;
			reader.readNext();
			while ((strs = reader.readNext()) != null) {
				Long begin = Long.parseLong(strs[1]);
				Long end = Long.parseLong(strs[2]);
				set.add(new Trip(begin, end));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return set;

	}

	private Graph<Long, MyLink> initGraph(String capacityUrl) throws IOException {
		File capacityFile = new File(capacityUrl);
		Reader freader = new FileReader(capacityFile);
		CSVReader reader = new CSVReader(freader);
		Graph<Long, MyLink> graph = new Graph<Long, MyLink>();
		String[] strs = null;
		reader.readNext();
		while ((strs = reader.readNext()) != null) {
			String begin = strs[0];
			String end = strs[1];
			float capacity = Float.parseFloat(strs[2]);
			MyLink mLink = new MyLink(-1, capacity, -1);
			graph.addDiEdge(Long.parseLong(begin), Long.parseLong(end), mLink);
		}
		reader.close();
		return graph;
	}

	private void readOtherVolume(Graph<Long, MyLink> graph, String url) throws IOException {
		File csvFile = new File(url);
		FileReader freader = new FileReader(csvFile);
		CSVReader reader = new CSVReader(freader);
		String[] strs = null;
		reader.readNext();
		while ((strs = reader.readNext()) != null) {
			String begin = strs[0];
			String end = strs[1];
			float otherVolume = Float.parseFloat(strs[2]);
			MyLink link = graph.getEdge(Long.parseLong(begin), Long.parseLong(end));
			if (link != null) {
				link.otherVolume = otherVolume;
			}
		}
		reader.close();
	}

	private void readFftt(Graph<Long, MyLink> graph, String url) throws IOException {
		File csvFile = new File(url);
		FileReader freader = new FileReader(csvFile);
		CSVReader reader = new CSVReader(freader);
		String[] strs = null;
		reader.readNext();
		while ((strs = reader.readNext()) != null) {
			String begin = strs[0];
			String end = strs[1];
			float fftt = Float.parseFloat(strs[2]);
			MyLink link = graph.getEdge(Long.parseLong(begin), Long.parseLong(end));
			if (link != null) {
				link.fftt = fftt;
			}
		}
		reader.close();
	}

	private void readCurrentCost(Graph<Long, MyLink> graph, String url) throws IOException {
		File csvFile = new File(url);
		FileReader freader = new FileReader(csvFile);
		CSVReader reader = new CSVReader(freader);
		String[] strs = null;
		reader.readNext();
		while ((strs = reader.readNext()) != null) {
			String begin = strs[0];
			String end = strs[1];
			float currentCost = Float.parseFloat(strs[2]);
			MyLink link = graph.getEdge(Long.parseLong(begin), Long.parseLong(end));
			if (link != null) {
				link.currentCost = currentCost;
			}
		}
		reader.close();
	}

	private void checkGraph(Graph<Long, MyLink> graph) {
		for (Graph.Entry<Long, MyLink> e : graph.entrySet()) {
			Long begin = e.getBegin();
			Long end = e.getEnd();
			MyLink link = e.getLink();
			if (link.otherVolume < 0 || link.fftt < 0) {
				graph.removeDiEdge(begin, end);
			}
		}
	}

	private float marginalCost(float fftt, float volume, float capacity) {
		return (float) ((3f / 5) * fftt * Math.pow(volume / capacity, 4));
	}

	private float surcharge(int n, float marginalCost, float lastSurcharge) {
		return (1f / n) * marginalCost + (1 - 1f / n) * lastSurcharge;
	}

	private float surchargeDiff(Graph<Long, MyLink> graph) {
		float total = 0;
		for (Graph.Entry<Long, MyLink> e : graph.entrySet()) {
			MyLink link = e.getLink();
			total += Math.abs(link.lastSurcharge - link.surcharge);
		}
		return total;
	}

	private void clearTaxiVolume(Graph<Long, MyLink> graph) {
		for (Graph.Entry<Long, MyLink> e : graph.entrySet()) {
			e.getLink().taxiVolume = 0;
		}
	}

	public void run(String capacityUrl, String ffttUrl, String otherUrl, String tripUrl, String currentUrl,
			float surchargeDiff) throws IOException {
		float totalDiff = Float.MAX_VALUE;
		Set<Trip> trips = readTrips(tripUrl);
		Graph<Long, MyLink> graph = init(capacityUrl, otherUrl, ffttUrl, currentUrl);
		int n = 1;
		do {
			clearTaxiVolume(graph);
			// Assignment based on total volume(taxi volume and other volume)
			int tripcount = 0;
			int tripsum = trips.size();
			ShortestPath<Long, MyLink> sp = new ShortestPath<Long, MyLink>();
			for (Trip trip : trips) {
				tripcount++;
				System.out.println("assignment: " + tripcount + "/" + tripsum);
				List<Long> path = sp.shortestPath(graph, trip.begin, trip.end);
				if (path != null) {
					if (path.size() > 1) {
						for (int i = 0; i < path.size() - 1; i++) {
							Long init = path.get(i);
							Long term = path.get(i + 1);
							graph.getEdge(init, term).taxiVolume += 1;
						}
					}
				}

			}
			/**
			 * update surcharge and lastSurcharge
			 */
			for (Graph.Entry<Long, MyLink> e : graph.entrySet()) {
				MyLink link = e.getLink();
				link.lastSurcharge = link.surcharge;
				float marginal = marginalCost(link.fftt, link.otherVolume + link.taxiVolume, link.capacity);
				link.surcharge = surcharge(n, marginal, link.lastSurcharge);
			}

			totalDiff = surchargeDiff(graph);
			System.out.println("n:" + n + ", totalDiff:" + totalDiff);
			ExcelUtils.writeGraph(graph, "files/newyork/output" + n + ".xls");
			n++;
		} while (totalDiff > surchargeDiff);

	}

	public static class MyLink extends AbstractEdge {
		public float otherVolume;
		public float taxiVolume;
		public float capacity;
		public float fftt;
		public float surcharge;
		public float lastSurcharge;
		public float currentCost;

		public MyLink(float othervolume, float capacity, float fftt) {
			this.otherVolume = othervolume;
			this.capacity = capacity;
			this.fftt = fftt;
			this.taxiVolume = 0;
			this.surcharge = 0;
			this.lastSurcharge = 0;
			this.currentCost = 0;
		}

		@Override
		public float getWeight() {
			return currentCost + surcharge;
		}

		@Override
		public String[] header() {
			return new String[] { "taxiVolume", "othervolume", "capacity", "fftt", "currentcost", "surcharge" };
		}

		@Override
		public float[] items() {
			return new float[] { taxiVolume, otherVolume, capacity, fftt, currentCost, surcharge };
		}

		@Override
		public void setWeight(float arg0) {

		}
	}

	public static class Trip {
		public Long begin;
		public Long end;

		public Trip(Long begin, Long end) {
			this.begin = begin;
			this.end = end;
		}

		@Override
		public String toString() {
			return begin + "->" + end;
		}
	}

}
