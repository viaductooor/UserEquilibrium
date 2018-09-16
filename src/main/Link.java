package main;

import jnetwork.AbstractEdge;

public class Link extends AbstractEdge{
	protected int from;
	protected int to;
	protected float capacity;
	protected float length;
	protected float ftime;
	protected float B;
	protected float power;
	protected float speed;
	protected float toll;
	protected int type;

	public Link(int from, int to, float capacity, float length, float ftime, float b, float power, float speed, float toll,
			int type) {
		super();
		this.from = from;
		this.to = to;
		this.capacity = capacity;
		this.length = length;
		this.ftime = ftime;
		B = b;
		this.power = power;
		this.speed = speed;
		this.toll = toll;
		this.type = type;
	}
	
	public Link(Link l) {
		this(l.from,l.to,l.capacity,l.length,l.ftime,l.B,l.power,l.speed,l.toll,l.type);
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public float getCapacity() {
		return capacity;
	}

	public void setCapacity(float capacity) {
		this.capacity = capacity;
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public float getFtime() {
		return ftime;
	}

	public void setFtime(float ftime) {
		this.ftime = ftime;
	}

	public float getB() {
		return B;
	}

	public void setB(float b) {
		B = b;
	}

	public float getPower() {
		return power;
	}

	public void setPower(float power) {
		this.power = power;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public float getToll() {
		return toll;
	}

	public void setToll(float toll) {
		this.toll = toll;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "from=" + from + ", to=" + to + ", capacity=" + capacity + ", length=" + length + ", ftime="
				+ ftime + ", B=" + B + ", power=" + power + ", speed=" + speed + ", toll=" + toll + ", type=" + type+"\n";
	}

	@Override
	public String[] header() {
		return new String[] {"from","to","capacity","length","ftime","B","power","speed","toll","type"};
	}

	@Override
	public float[] items() {
		return new float[] {from,to,capacity,length,ftime,B,power,speed,toll,type};
	}

	@Override
	public float getWeight() {
		return ftime;
	}

	@Override
	public void setWeight(float w) {}


}
