package s3f.pyrite.util.intervaltree;

/**
 * The Interval class maintains an interval with some associated data
 * @author Kevin Dolan
 * 
 * @param <Type> The type of data being stored
 */
public class Interval<Type> implements Comparable<Interval<Type>> {

	private double start;
	private double end;
	private Type data;
	
	public Interval(double start, double end, Type data) {
		this.start = start;
		this.end = end;
		this.data = data;
	}

	public double getStart() {
		return start;
	}

	public void setStart(double start) {
		this.start = start;
	}

	public double getEnd() {
		return end;
	}

	public void setEnd(double end) {
		this.end = end;
	}

	public Type getData() {
		return data;
	}

	public void setData(Type data) {
		this.data = data;
	}
	
	/**
	 * @param time
	 * @return	true if this interval contains time (inclusive)
	 */
	public boolean contains(double time) {
		return time <= end && time >= start;
	}
	
	/**
	 * @param other
	 * @return	return true if this interval intersects other
	 */
	public boolean intersects(Interval<?> other) {
		return other.getEnd() >= start && other.getStart() <= end;
	}
	
	/**
	 * Return -1 if this interval's start time is less than the other, 1 if greater
	 * In the event of a tie, -1 if this interval's end time is less than the other, 1 if greater, 0 if same
	 * @param other
	 * @return 1 or -1
	 */
	public int compareTo(Interval<Type> other) {		
		if(start < other.getStart())
			return -1;
		else if(start > other.getStart())
			return 1;
		else if(end < other.getEnd())
			return -1;
		else if(end > other.getEnd())
			return 1;
		else
			return 0;
	}
	
}
