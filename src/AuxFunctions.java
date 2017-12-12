import java.util.LinkedList;
import java.util.List;

public class AuxFunctions {
	public void allOrNothing(LinkedList<ODPair> odset, LinkSet ls) {
		// all-or-nothing assignment
		float[][] tmat = ls.getTMatrix();
		ls.clearAuxFlow(); // clear auxiliary flow information, or y in the
							// algorithm before the assignment
		Floyd f = new Floyd(); // use Floyd Method to get the shortest path
		f.setMatrix(tmat);
		f.compute();
		for (ODPair odpair : odset) {
			int x = odpair.getOrigin();
			int y = odpair.getDestination();
			List<Integer> path = f.getShortestPath(x, y);
			for (int i = 0; i < path.size() - 1; i++) {
				int init = path.get(i);
				int term = path.get(i + 1);
				Link l = ls.getLink(init, term);
				l.setAuxFlow(l.getAuxFlow() + odpair.getDemand());
			}
		}
	}

	public void testAllOrNothing(ODPair odp, LinkSet ls) {
		ls.clearAuxFlow();
		float min = Float.POSITIVE_INFINITY;
		Link link = null;
		for (Link l : ls.getSet()) {
			if (l.getRes() < min) {
				min = l.getRes();
				link = l;
			}
		}
		link.setAuxFlow(odp.getDemand());
	}

	public float lineSearch(LinkSet ls) {
		float alpha = 1;
		float minSum = Float.POSITIVE_INFINITY;
		for (float al = 0; al < 1.001; al += 0.001) {
			float sum = 0;
			for (Link l : ls.getSet()) {
				float flow = l.getFlow();
				float auxFlow = l.getAuxFlow();
				float freeFlowTime = l.getFreeFlowTime();
				float capacity = l.getCapacity();
				float upper = flow + al * (auxFlow - flow);
				float C = (float) ((0.03 * freeFlowTime) / Math
						.pow(capacity, 4));
				sum += (float) (C * Math.pow(upper, 5) + freeFlowTime * upper);
			}
			if (sum < minSum) {
				alpha = al;
				minSum = sum;
			}
		}
		return alpha;
	}

	public void move(LinkSet ls, float alpha) {
		for (Link l : ls.getSet()) {
			l.setFlow(l.getFlow() + alpha * (l.getAuxFlow() - l.getFlow()));
		}
	}
}
