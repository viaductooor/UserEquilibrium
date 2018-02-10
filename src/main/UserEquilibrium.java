package main;

import java.util.List;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import dataset.SiouxFalls;

public class UserEquilibrium {

	private List<Link> mLinks;
	private List<ODPair> mOdpairs;
	private DataSet mDataSet;

	public UserEquilibrium(DataSet dataSet) {
		this.mDataSet = dataSet;
		this.mLinks = dataSet.getLinks();
		this.mOdpairs = dataSet.getOdpairs();
	}

	/**
	 * the step ALL-OR-NOTHING ASSIGNMENT
	 * 
	 * @param odset
	 * @param links
	 */
	public void allOrNothing(List<ODPair> odset, List<Link> links) {
		float[][] tmat = getTMatrix(links, mDataSet.getMaxsize());
		clearAuxFlow(links);
		Floyd f = new Floyd();
		f.setMatrix(tmat);
		f.compute();
		for (ODPair odpair : odset) {
			int x = odpair.getOrigin();
			int y = odpair.getDestination();
			List<Integer> path = f.getShortestPath(x, y);
			for (int i = 0; i < path.size() - 1; i++) {
				int init = path.get(i);
				int term = path.get(i + 1);
				Link l = getLink(init, term, links);
				l.setAuxFlow(l.getAuxFlow() + odpair.getDemand());
			}
		}
	}

	public static float[][] getTMatrix(List<Link> set, int maxsize) {
		for (Link l : set) {
			l.updateTravelTime();
		}
		float[][] mat = new float[maxsize][maxsize];
		for (int i = 0; i < maxsize; i++) {
			for (int j = 0; j < maxsize; j++) {
				mat[i][j] = Float.POSITIVE_INFINITY;
			}
		}
		for (Link l : set) {
			mat[l.getInitNode() - 1][l.getTermNode() - 1] = l.getTravelTime();
		}
		return mat;
	}

	public static void clearAuxFlow(List<Link> set) {
		for (Link l : set) {
			l.setAuxFlow(0);
		}
	}

	public static Link getLink(int start, int end, List<Link> set) {
		for (Link l : set) {
			if (l.getInitNode() == start) {
				if (l.getTermNode() == end) {
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
	public static void y2x(List<Link> set) {
		for (Link l : set) {
			l.setFlow(l.getAuxFlow());
		}
	}

	public static void updateTMatrix(List<Link> set) {
		for (Link l : set) {
			l.updateTravelTime();
		}
	}

	public static float lineSearch(List<Link> links) {
		float alpha = 1;
		float minSum = Float.POSITIVE_INFINITY;
		for (float al = 0; al < 1.001; al += 0.001) {
			float sum = 0;
			for (Link l : links) {
				float flow = l.getFlow();
				float auxFlow = l.getAuxFlow();
				float freeFlowTime = l.getFreeFlowTime();
				float capacity = l.getCapacity();
				float upper = flow + al * (auxFlow - flow);
				float surcharge = l.getSurcharge();
				float C = (float) ((0.03 * freeFlowTime) / Math
						.pow(capacity, 4));
				sum += (float) (C * Math.pow(upper, 5) + (freeFlowTime + surcharge)
						* upper);
			}
			if (sum < minSum) {
				alpha = al;
				minSum = sum;
			}
		}
		return alpha;
	}

	public static float getTotalFlow(List<Link> set) {
		float sum = 0;
		for (Link l : set) {
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
	public static void move(List<Link> links, float alpha) {
		for (Link l : links) {
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
		int n = 1;
		float step = Float.POSITIVE_INFINITY;
		while (Math.abs(step) > diff) {
			System.out.println("count " + n + ": diff=" + Math.abs(step));
			updateTMatrix(mLinks); // step 1
			allOrNothing(mOdpairs, mLinks);// step 2
			alpha = lineSearch(mLinks);// step 3
			float flowsum = getTotalFlow(mLinks);
			move(mLinks, alpha);
			step = getTotalFlow(mLinks) - flowsum;
			n++;
		}
		updateTMatrix(mLinks);
	}
}
