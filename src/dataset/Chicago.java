package dataset;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import main.DataSet;
import main.Link;
import main.ODPair;

public class Chicago extends DataSet {

	public Chicago(String netUrl, String tripUrl) {
		super(netUrl, tripUrl);
	}

	public Chicago() {
		super("files/ChicagoRegional_net.txt",
				"files/ChicagoRegional_trips.txt");
	}

	@Override
	public List<Link> readLinks() {
		LinkedList<Link> linkset = new LinkedList<Link>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(this.getNetUrl()));
			String str = null;
			boolean iscontent = false;
			while ((str = br.readLine()) != null) {
				if (iscontent) {
					String[] strings = str.split("\\t+");
					int initNode = Integer.parseInt(strings[0]);
					int termNode = Integer.parseInt(strings[1]);
					float capacity = Float.parseFloat(strings[2]);
					float length = Float.parseFloat(strings[3]);
					float freeFlowTime = Float.parseFloat(strings[4]);
					Link link = new Link(initNode, termNode, capacity, length,
							freeFlowTime);
					linkset.add(link);
				}
				if (str.contains("~")) {
					iscontent = true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return linkset;
	}

	@Override
	public List<ODPair> readTrips() {
		LinkedList<ODPair> odps = new LinkedList<ODPair>();
		BufferedReader br = null;
		Pattern phead = Pattern.compile("\\D+(\\d+)\\D*");
		Pattern pitem = Pattern.compile("\\D+(\\d+)\\D+:\\D+(\\d+\\.?\\d*);");
		try {
			br = new BufferedReader(new FileReader(this.getTripUrl()));
			String str = null;
			int origin = 0;
			while ((str = br.readLine()) != null) {
				if (str.contains("Origin")) {
					Matcher m1 = phead.matcher(str);
					if (m1.find()) {
						origin = Integer.parseInt(m1.group(1));
					}
				} else if (origin > 0) {
					Matcher m2 = pitem.matcher(str);
					while (m2.find()) {
						float demand = Float.parseFloat(m2.group(2));
						if (demand != 0) {
							int destination = Integer.parseInt(m2.group(1));
							ODPair odp = new ODPair(origin, destination, demand);
							odps.add(odp);
						}

					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return odps;
	}

}
