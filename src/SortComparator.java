import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class SortComparator extends WritableComparator {

	public SortComparator() {
		super(StringAndInt.class, true);
	}

	@Override
	public int compare(WritableComparable a, WritableComparable b) {
		StringAndInt s1 = (StringAndInt) a;
		StringAndInt s2 = (StringAndInt) b;
		if (s1.getStringContent().compareTo(s2.getStringContent()) == 0) {
			return s2.getIntContent() - s1.getIntContent();
		}
		return s1.getStringContent().compareTo(s2.getStringContent());
	}

}
