package functions;

import jnetwork.Graph;
import main.DemandLink;
import main.Link;
import main.UeLink;

public class Game {
	public float beta;
	public float omega;
	public float d;

	private Graph<Integer,Link> links;
	private Graph<Integer,DemandLink> trips;
	
	private int linkNumber;
	private int n_iteration;
	private float v;

	public Game(Graph<Integer,Link> graph,Graph<Integer,DemandLink> trips) {
		this.links = graph;
		this.trips = trips;
		this.linkNumber = graph.edges().size();
		this.omega = 0;
		this.d = 1;
		this.v = 0;
	}
	
	/**
	 * Convert Graph with links that extend {@link Link} to Graph with {@link GameLink}
	 * @param graph
	 * @return
	 */
	public Graph<Integer,GameLink> transGraph(Graph<Integer,? extends Link> graph){
		Graph<Integer, GameLink> newGraph = new Graph<Integer,GameLink>();
		for(Graph.Entry<Integer, ? extends Link> e:graph.entrySet()) {
			Integer begin = e.getBegin();
			Integer end = e.getEnd();
			Link link = e.getLink();
			newGraph.addDiEdge(begin, end, new GameLink(link));
		}
		return newGraph;
	}
	

	public float getBeta() {
		return beta;
	}

	public void setBeta(float beta) {
		this.beta = beta;
	}

	public float getOmega() {
		return omega;
	}

	public void setOmega(float omega) {
		this.omega = omega;
	}

	public float getD() {
		return d;
	}

	public void setD(float d) {
		this.d = d;
	}

	public Graph<Integer,GameLink> run(float beta, float omega, float d, float delta) {
		float _prev = 0;
		float _v = 0;
		n_iteration = 1;

		Graph<Integer, GameLink> gameGraph = transGraph(links);
		
		for (GameLink l : gameGraph.edges()) {
			l.setRho(1f / linkNumber);
			l.setGamma(0);
			l.setC_normal(l.getFtime());
			l.setC_fail(beta * linkNumber);
		}

		n_iteration = 2;

		do {
			_prev = v; // save the previous V
			_v = 0; // initiate the present V
			for (GameLink l : gameGraph.edges()) {
				float _rho = l.getRho();
				float _t = l.getT();
				float _s = (1 - _rho) * l.getC_normal() + _rho * l.getC_fail();
				_t = (1f / n_iteration) * _s + (1 - 1f / n_iteration) * _t;
				l.setS(_s);
				l.setT(_t);
			}

			UserEquilibrium.ue(gameGraph, trips, 50);

			float _totalflow = UserEquilibrium.getTotalFlow(gameGraph);
			float _sum = 0;
			for (GameLink l : gameGraph.edges()) {
				_sum += l.getRho() * (l.getT() + omega) / d;
			}
			for (GameLink l : gameGraph.edges()) {
				float _gamma = l.getFlow() / _totalflow;
				float _rho = ((_gamma * (l.getT() + omega)) / d) / _sum;
				l.setGamma(_gamma);
				l.setRho(_rho);
			}
			for (GameLink l : gameGraph.edges()) {
				_v += l.getRho() * l.getGamma() * l.getT();
			}
			v = _v;
			n_iteration++;

		} while (Math.abs(_prev - _v) > delta);
		return gameGraph;
	}
	
	class GameLink extends UeLink {
		private float rho;
		private float gamma;
		private float c_normal;
		private float c_fail;
		private float s;
		private float t;
		private float delta;

		public GameLink(int from, int to, float capacity, float length, float ftime, float b, float power, float speed,
				float toll, int type) {
			super(from, to, capacity, length, ftime, b, power, speed, toll, type);
			this.rho = 0;
			this.gamma = 0;
			this.c_normal = 0;
			this.c_fail = 0;
			this.s = 0;
			this.t = 0;
			this.delta = 0;
		}

		public GameLink(Link l) {
			super(l.getFrom(), l.getTo(), l.getCapacity(), l.getLength(), l.getFtime(), l.getB(), l.getPower(), l.getSpeed(), l.getToll(), l.getType());
			this.rho = 0;
			this.gamma = 0;
			this.c_normal = 0;
			this.c_fail = 0;
			this.s = 0;
			this.t = 0;
			this.delta = 0;
		}

		public float getRho() {
			return rho;
		}

		public void setRho(float rho) {
			this.rho = rho;
		}

		public float getGamma() {
			return gamma;
		}

		public void setGamma(float gamma) {
			this.gamma = gamma;
		}

		public float getC_normal() {
			return c_normal;
		}

		public void setC_normal(float c_normal) {
			this.c_normal = c_normal;
		}

		public float getC_fail() {
			return c_fail;
		}

		public void setC_fail(float c_fail) {
			this.c_fail = c_fail;
		}

		public float getS() {
			return s;
		}

		public void setS(float s) {
			this.s = s;
		}

		public float getT() {
			return t;
		}

		public void setT(float t) {
			this.t = t;
		}

		public float getDelta() {
			return delta;
		}

		public void setDelta(float delta) {
			this.delta = delta;
		}
		
		@Override
		public void updateTravelTime() {
			this.travelTime = (float) (((Math.pow(this.flow / this.capacity, 4)) * 0.15 + 1) * this.ftime);
			this.travelTime += this.surcharge;
			this.travelTime += this.t;
		}

		@Override
		public String[] header() {
			String[] strs = {"from","to","flow","rho","gamma"};
			return strs;
		}

		@Override
		public float[] items() {
			float[] a = {from,to,flow,rho,gamma};
			return a;
		}
		
		

	}


}
