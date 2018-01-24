import java.util.ArrayList;
import java.util.List;

public class Floyd {
	private float[][] m;
	private int[][] r;
	private float[][] d;
	private int size;

	public void setMatrix(float a[][]) {
		// 设置邻接矩阵
		this.m = a;
		this.size = this.m[0].length;
	}

	public void compute() {
		// Floyd算法的实现，获取R矩阵
		int[][] _r = new int[size][size];
		float[][] _m = new float[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				_m[i][j] = m[i][j];
			}
		}
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				_r[i][j] = j + 1;
			}
		}
		for (int i = 0; i < size; i++) {
			// i表示依次添加的点的序号
			for (int j = 0; j < size; j++) {
				for (int k = 0; k < size; k++) {
					if ((_m[j][i] + _m[i][k]) < _m[j][k]) {
						_m[j][k] = _m[j][i] + _m[i][k];
						_r[j][k] = _r[j][i];
					}
				}
			}
		}
		this.r = _r;
		this.d = _m;
	}
	
	public float[][] getD(){
		return this.d;
	}

	public List<Integer> getShortestPath(int origin, int destination) {
		// 根据起始点和终点获取最短路径
		List<Integer> l = new ArrayList<Integer>();
		int t = origin;
		while (t != destination) {
			l.add(t);
			t = r[t - 1][destination - 1];
		}
		l.add(destination);
		return l;
	}
	
	public float getTotalCost(int origin, int destination){
		return d[origin-1][destination-1];
	}

	@Override
	public String toString() {
		String str = "";
		str += "Origin\n";
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				str = str + this.m[i][j] + "\t";
			}
			str += "\n";
		}
		str += "R:\n";
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				str = str + this.r[i][j] + "\t";
			}
			str += "\n";
		}
		return str;
	}
}
