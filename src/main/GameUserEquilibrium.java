package main;

import java.util.LinkedList;
import java.util.List;

public class GameUserEquilibrium {

	private List<GameLink> mLinks;
	private List<Odpair> mOdpairs;
	private int nodeNumber;

	public GameUserEquilibrium(UeDataSet dataSet) {
		this.mLinks = uelinks2glinks(dataSet.getLinks());
		this.mOdpairs = dataSet.getTrips();
		this.nodeNumber = dataSet.getNodeNubmer();
	}

	public GameUserEquilibrium(List<GameLink> links, List<Odpair> odpairs, int nodeNumber) {
		this.mLinks = links;
		this.mOdpairs = odpairs;
		this.nodeNumber = nodeNumber;
	}
	
	public List<GameLink> uelinks2glinks(List<UeLink> links){
		List<GameLink> glinks = new LinkedList<GameLink>();
		for(UeLink l:links) {
			glinks.add(new GameLink(l.getFrom(), l.getTo(), l.getCapacity(), l.getLength(), l.getFtime(), l.getB(), l.getPower(), l.getSpeed(), l.getToll(), l.getType()));
		}
		return glinks;
	}

	/**
	 * the step ALL-OR-NOTHING ASSIGNMENT
	 * 
	 * @param odset
	 * @param links
	 */
	public void allOrNothing(List<Odpair> odset, List<GameLink> links) {
		float[][] tmat = getTMatrix(links, nodeNumber);
		clearAuxFlow(links);
		Floyd f = new Floyd();
		f.setMatrix(tmat);
		f.compute();
		for (Odpair odpair : odset) {
			int x = odpair.getFrom();
			int y = odpair.getTo();
			List<Integer> path = f.getShortestPath(x, y);
			for (int i = 0; i < path.size() - 1; i++) {
				int init = path.get(i);
				int term = path.get(i + 1);
				UeLink l = getLink(init, term, links);
				l.setAuxFlow(l.getAuxFlow() + odpair.getDemand());
			}
		}
	}

	public static float[][] getTMatrix(List<GameLink> set, int maxsize) {
		for (UeLink l : set) {
			l.updateTravelTime();
		}
		float[][] mat = new float[maxsize][maxsize];
		for (int i = 0; i < maxsize; i++) {
			for (int j = 0; j < maxsize; j++) {
				mat[i][j] = Float.POSITIVE_INFINITY;
			}
		}
		for (UeLink l : set) {
			mat[l.getFrom() - 1][l.getTo() - 1] = l.getTravelTime();
		}
		return mat;
	}

	public static void clearAuxFlow(List<GameLink> set) {
		for (UeLink l : set) {
			l.setAuxFlow(0);
		}
	}

	public static UeLink getLink(int start, int end, List<GameLink> set) {
		for (UeLink l : set) {
			if (l.getFrom() == start) {
				if (l.getTo() == end) {
					return l;
				}
			}
		}
		return null;
	}

	/**
	 * assign flow (of every link) with the value of auxiliary flow
	 * 
	 * @param set
	 */
	public static void y2x(List<GameLink> set) {
		for (UeLink l : set) {
			l.setFlow(l.getAuxFlow());
		}
	}

	public static void updateTMatrix(List<GameLink> set) {
		for (UeLink l : set) {
			l.updateTravelTime();
		}
	}

	public static float lineSearch(List<GameLink> links) {
		float alpha = 1;
		float minSum = Float.POSITIVE_INFINITY;
		for (float al = 0; al < 1.001; al += 0.001) {
			float sum = 0;
			for (UeLink l : links) {
				float flow = l.getFlow();
				float auxFlow = l.getAuxFlow();
				float freeFlowTime = l.getFtime();
				float capacity = l.getCapacity();
				float upper = flow + al * (auxFlow - flow);
				float surcharge = l.getSurcharge();
				float C = (float) ((0.03 * freeFlowTime) / Math.pow(capacity, 4));
				sum += (float) (C * Math.pow(upper, 5) + (freeFlowTime + surcharge) * upper);
			}
			if (sum < minSum) {
				alpha = al;
				minSum = sum;
			}
		}
		return alpha;
	}

	public static float getTotalFlow(List<GameLink> set) {
		float sum = 0;
		for (UeLink l : set) {
			sum += l.getFlow();
		}
		return sum;
	}

	/**
	 * the step MOVE
	 * 
	 * @param links
	 * @param alpha
	 */
	public static void move(List<GameLink> links, float alpha) {
		for (UeLink l : links) {
			l.setFlow(l.getFlow() + alpha * (l.getAuxFlow() - l.getFlow()));
		}
	}

	/**
	 * diff = this_total_flow - last_total_flow
	 * 
	 * @param diff
	 */
	public void compute(float diff) {
		/**
		 * step 0
		 */
		allOrNothing(mOdpairs, mLinks);
		y2x(mLinks);
		float alpha = 1;
		// int n = 1;
		float step = Float.POSITIVE_INFINITY;
		while (Math.abs(step) > diff) {
			updateTMatrix(mLinks); // step 1
			allOrNothing(mOdpairs, mLinks);// step 2
			alpha = lineSearch(mLinks);// step 3
			float flowsum = getTotalFlow(mLinks);
			move(mLinks, alpha);
			float totalFlow = getTotalFlow(mLinks);
			System.out.println(Math.abs(step));
			step = totalFlow - flowsum;
			// n++;
		}
		updateTMatrix(mLinks);
		// LogWriter lw = new LogWriter(mLinks, mOdpairs);
		// lw.init();
		// lw.logWriteLink("UE ASSIGNMENT:");
		// lw.close();
	}
}
