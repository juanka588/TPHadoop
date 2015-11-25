import org.apache.hadoop.io.WritableComparator;

public class SortComparator extends WritableComparator {

	public SortComparator() {
		super(StringAndInt.class, true);
	}	
}
