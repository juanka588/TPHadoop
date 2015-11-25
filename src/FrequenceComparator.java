import org.apache.hadoop.io.WritableComparator;

public class FrequenceComparator extends WritableComparator {

	public FrequenceComparator() {
		super(StringAndInt2.class, true);
	}
}
