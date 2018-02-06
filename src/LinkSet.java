import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Set;

public class LinkSet {
	private LinkedList<Link> set;
	private int maxSize;

	public LinkSet(LinkedList<Link> set) {
		this.set = set;
		this.maxSize = computeMaxSize();
	}
	
	public LinkSet(LinkSet ls){
		LinkedList<Link> set = new LinkedList<Link>(ls.getSet());
		this.setSet(set);
		this.maxSize = ls.getMaxSize();
	}

	public void setSet(LinkedList<Link> set) {
		this.set = set;
		this.maxSize = computeMaxSize();
	}

	public LinkedList<Link> getSet() {
		return this.set;
	}

	public int getMaxSize() {
		return this.maxSize;
	}

	public void addLink(Link link) {
		this.set.add(link);
	}

	public Link getLink(int start, int end) {
		for (Link l : this.set) {
			if (l.getInitNode() == start) {
				if (l.getTermNode() == end) {
					return l;
				}
			}
		}
		return null;
	}

	public void setFlows(int[][] m) {
		for (Link l : this.set) {
			l.setFlow(m[l.getInitNode()][l.getTermNode()]);
		}
	}

	public float[][] getTMatrix() {
		for (Link l : set) {
			l.updateTravelTime();
		}
		float[][] mat = new float[maxSize][maxSize];
		for (int i = 0; i < maxSize; i++) {
			for (int j = 0; j < maxSize; j++) {
				mat[i][j] = Float.POSITIVE_INFINITY;
			}
		}
		for (Link l : set) {
			mat[l.getInitNode() - 1][l.getTermNode() - 1] = l.getTravelTime();
		}
		return mat;
	}

	
	public float[][] getXMatrix() {
		float[][] mat = new float[maxSize][maxSize];
		for (Link l : set) {
			mat[l.getInitNode()][l.getTermNode()] = l.getFlow();
		}
		return mat;
	}

	public int computeMaxSize() {
		int max = 0;
		for (Link l : set) {
			int t = l.getInitNode() > l.getTermNode() ? l.getInitNode() : l
					.getTermNode();
			if (t > max) {
				max = t;
			}
		}
		return max;
	}

	public void clearFlow() {
		for (Link l : set) {
			l.setFlow(0);
		}
	}

	public void clearAuxFlow() {
		for (Link l : set) {
			l.setAuxFlow(0);
		}
	}

	public void updateTMatrix() {
		for (Link l : set) {
			l.updateTravelTime();
		}
	}

	public void y2x() {
		for (Link l : set) {
			l.setFlow(l.getAuxFlow());
		}
	}

	@Override
	public String toString() {
		DecimalFormat df = new DecimalFormat("##0.00");
		String str = "";
		str += "(ini, term, x, y, t)\n";
		for (Link l : set) {
			str += l.getInitNode() + "\t\t\t" + l.getTermNode() + "\t\t\t"
					+ df.format(l.getFlow()) + "\t\t\t" + l.getAuxFlow() + "\t\t\t"
					+ df.format(l.getTravelTime()) + "\n";
		}
		return str;
	}

	public float getTotalFlow() {
		float sum = 0;
		for (Link l : set) {
			sum += l.getFlow();
		}
		return sum;
	}
}
