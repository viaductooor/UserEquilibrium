package jnetwork;

public abstract class AbstractEdge implements WeightedEdge, Excelable {

	@Override
	public abstract String[] header();

	@Override
	public abstract float[] items();

	@Override
	public abstract float getWeight();

	@Override
	public abstract void setWeight(float w);

}
