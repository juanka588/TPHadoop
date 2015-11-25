import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class CountryComparator extends WritableComparator {

	public CountryComparator() {
		super(StringAndInt.class, true);
	}

	@Override
	public int compare(WritableComparable a, WritableComparable b) {
		StringAndInt s1 = (StringAndInt) a;
		StringAndInt s2 = (StringAndInt) b;
		return s1.getStringContent().compareTo(s2.getStringContent());
	}

}
