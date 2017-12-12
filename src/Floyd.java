import java.util.ArrayList;
import java.util.List;

public class Floyd {
	private float[][] m;
	private int[][] r;
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

	/*
	 * public static void main(String args[]){ float inf =
	 * Float.POSITIVE_INFINITY; float[][]
	 * d={{0,2,4,inf,inf,inf},{inf,0,1,4,3,inf
	 * },{4,1,0,3,2,inf},{inf,4,3,0,2,5},{inf,3,2,inf,0,2},{inf,inf,inf,5,2,0}};
	 * Floyd f = new Floyd(); f.setMatrix(d); f.compute();
	 * System.out.println(f); System.out.println("6-->1:"); List<Integer> link =
	 * f.getShortestPath(6, 1); for(int i:link){ System.out.print(i+" "); }
	 * for(int i = 0;i<link.size()-1;i++){
	 * System.out.println("["+link.get(i)+","+link.get(i+1)+"]"); } }
	 */
}
