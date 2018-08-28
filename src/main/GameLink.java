package main;

public class GameLink extends UeLink {
	private float rho;
	private float gamma;
	private float c_normal;
	private float c_fail;
	private float s;
	private float t;
	private float delta;

	public GameLink(int from, int to, float capacity, float length, float ftime, float b, float power, float speed,
			int toll, int type) {
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
		super(l.from, l.to, l.capacity, l.length, l.ftime, l.B, l.power, l.speed, l.toll, l.type);
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

}
