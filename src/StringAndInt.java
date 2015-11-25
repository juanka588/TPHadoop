import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class StringAndInt extends WritableComparator implements WritableComparable<StringAndInt> {
	private Text tag;
	private IntWritable nOccurrences;
	public static String split = "----";

	public StringAndInt() {
		this.tag=new Text();
		this.nOccurrences=new IntWritable();
	}

	public StringAndInt(String stringContent, int intContent) {
		this.tag=new Text(stringContent);
		this.nOccurrences=new IntWritable(intContent);
	}


	public String getStringContent() {
		return tag.toString();
	}

	public void setStringContent(Text stringContent) {
		this.tag = stringContent;
	}

	public int getIntContent() {
		return nOccurrences.get();
	}

	public void setIntContent(IntWritable intContent) {
		this.nOccurrences = intContent;
	}

	@Override
	public int compareTo(StringAndInt o) {
		return o.nOccurrences.get() - nOccurrences.get();
	}

	/**
	 * warning with use this method
	 */
	@Override
	public String toString() {
		return tag + " " + nOccurrences;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		nOccurrences.readFields(in);
		tag.readFields(in);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		nOccurrences.write(out);
		tag.write(out);
	}

}
