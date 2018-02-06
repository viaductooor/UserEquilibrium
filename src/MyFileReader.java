import java.awt.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class MyFileReader {
	private String demand_src;
	private String link_src;
	private String num_pattern = "\\d+\\.?\\d*";
	private DataCase dataCase;
	
	public MyFileReader(DataCase dc){
		this.dataCase = dc;
		this.demand_src = dc.getTripUrl();
		this.link_src = dc.getNetUrl();
	}

	public LinkedList<ODPair> getDemand() {
		LinkedList<ODPair> odps = new LinkedList<ODPair>();
		BufferedReader br = null;
		Pattern phead = Pattern.compile("\\D+(\\d+)\\D*");
		//Pattern pitem = Pattern.compile("\\D+(\\d+)\\D+:\\D+(\\d+);");
		Pattern pitem = Pattern.compile("\\D+(\\d+)\\D+:\\D+(\\d+\\.?\\d*);");
		try {
			br = new BufferedReader(new FileReader(demand_src));
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

	public LinkSet getLinks() {
		LinkedList<Link> linkset = new LinkedList<Link>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(link_src));
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

		LinkSet ls = new LinkSet(linkset);
		return ls;
	}
}
