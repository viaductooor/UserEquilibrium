package jnetwork;

/**
 * Any class that implements this interface can easily have its objects written
 * into an Excel workbook or a database table. It is most important here to log
 * a graph. When we want monitor the status of the edges of a graph, we can have
 * the class of its edges implemented {@link Excelable}, and write the edges
 * into a new Excel table every time the graph changes, as well as the vertices.
 * 
 * @author John Smith
 *
 */
public interface Excelable {
	/**
	 * Return names of the columns
	 * 
	 * @return
	 */
	public String[] header();

	/**
	 * Return the attributes, usually the fields of the object. For example, length,
	 * volume and capacity of a road.
	 * 
	 * @return
	 */
	public float[] items();
}
