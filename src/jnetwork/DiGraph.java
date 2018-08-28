package jnetwork;

public class DiGraph<K, L> extends Graph<K, L> {

	@Override
	public void addEdge(K initNode, K endNode, L linkInfo) {
		addDiEdge(initNode, endNode, linkInfo);
	}

}
