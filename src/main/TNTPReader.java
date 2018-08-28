package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TNTPReader {

	public static String ANAHEIM_TRIP = "files/Anaheim_trips.tntp";
	public static String ANAHEIM_NET = "files/Anaheim_net.tntp";
	public static String CHICAGO_TRIP = "files/ChicagoRegional_trips.tntp";
	public static String CHICAGO_NET = "files/ChicagoRegional_net.tntp";
	public static String SIOUXFALLS_TRIP = "files/SiouxFalls_trips.tntp";
	public static String SIOUXFALLS_NET = "files/SiouxFalls_net.tntp";
	public static String WINNIPEG_ASYM_TRIP = "files/Winnipeg-Asym_trips.tntp";
	public static String WINNIPEG_ASYM_NET = "files/Winnipeg-Asym_net.tntp";
	
	public static DataSet read(String tripUrl, String netUrl) {
		//six essential variables of DataSet
		int zoneNumber = -1;
		int nodeNumber = -1;
		int firstThruNode = -1;
		int linkNumber = -1;
		List<Link> links = new LinkedList<Link>();
		List<Odpair> trips = new LinkedList<Odpair>();
		DataSet dataSet = new DataSet();
		
		//file 
		File tripFile = new File(tripUrl);
		File netFile = new File(netUrl);
		FileInputStream fis = null;
		BufferedReader reader = null;
		String line = "";

		Pattern pZoneNumber = Pattern.compile("<NUMBER OF ZONES>\\s+(\\d+)");
		Pattern pNodeNumber = Pattern.compile("<NUMBER OF NODES>\\s+(\\d+)");
		Pattern pFirstThruNode = Pattern.compile("<FIRST THRU NODE>\\s+(\\d+)");
		Pattern pLinkNumber = Pattern.compile("<NUMBER OF LINKS>\\s+(\\d+)");
		Matcher m = null;
		int linenum = 0;//
		
		//read from net file
		try {
			fis = new FileInputStream(netFile);
			reader = new BufferedReader(new InputStreamReader(fis));
			boolean isLink = false;
			
			while((line = reader.readLine())!=null) {
				if(isLink == false) {
					if((m = pZoneNumber.matcher(line)).find()) {
						zoneNumber = Integer.parseInt(m.group(1));
					}else if((m = pNodeNumber.matcher(line)).find()) {
						nodeNumber = Integer.parseInt(m.group(1));
					}else if((m = pFirstThruNode.matcher(line)).find()) {
						firstThruNode = Integer.parseInt(m.group(1));
					}else if((m = pLinkNumber.matcher(line)).find()) {
						linkNumber = Integer.parseInt(m.group(1));
					}
				}else {
					linenum ++;
					line = " "+line;
					String[] items = line.split("\\s+");
					int from = Integer.parseInt(items[1]);
					int to = Integer.parseInt(items[2]);
					float capacity = Float.parseFloat(items[3]);
					float length = Float.parseFloat(items[4]);
					float ftime = Float.parseFloat(items[5]);
					float B = Float.parseFloat(items[6]);
					float power = Float.parseFloat(items[7]);
					float speed = Float.parseFloat(items[8]);
					int toll = Integer.parseInt(items[9]);
					int type = Integer.parseInt(items[10]);
					Link link = new Link(from, to, capacity, length, ftime, B, power, speed, toll, type);
					links.add(link);
				}
				if(line.contains("~")) {
					isLink  = true;
				}
			}
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch(ArrayIndexOutOfBoundsException e) {
			//e.printStackTrace();
			//System.out.println("linenum:" + linenum);
		}
		
		//read from trip file
		try {
			fis = new FileInputStream(tripFile);
			reader = new BufferedReader(new InputStreamReader(fis));
			boolean isTrip = false;
			int origin = -1;
			int destination;
			float demand;
			while((line = reader.readLine())!=null) {
				if(line.contains("Origin")) {
					isTrip = true;
					Pattern pOrigin = Pattern.compile("Origin\\s+(\\d+)");
					if((m = pOrigin.matcher(line)).find()) {
						origin = Integer.parseInt(m.group(1));
					}
				}else if(isTrip) {
					Pattern pItem = Pattern.compile("\\s*(\\d+)\\s*:\\s+(\\S+);");
					m = pItem.matcher(line);
					while(m.find()) {
						destination = Integer.parseInt(m.group(1));
						demand = Float.parseFloat(m.group(2));
						trips.add(new Odpair(origin, destination, demand));
					}
				}
			}
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		dataSet.setFirstThruNode(firstThruNode);
		dataSet.setLinkNumbers(linkNumber);
		dataSet.setNodeNubmers(nodeNumber);
		dataSet.setZoneNumbers(zoneNumber);
		dataSet.setLinks(links);
		dataSet.setTrips(trips);
		return dataSet;
	}
	
	
	/*public static void main(String args[]) {
		DataSet ds = TNTPReader.read(SIOUXFALLS_TRIP,SIOUXFALLS_NET );
		System.out.println(ds);
	}*/
}
