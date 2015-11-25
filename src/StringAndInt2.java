import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class StringAndInt2 extends WritableComparator implements WritableComparable<StringAndInt2> {
	private Text tag;
	private IntWritable nOccurrences;
	public static String split = "----";

	public StringAndInt2() {
		//super(StringAndInt.class,true);
		this.tag=new Text();
		this.nOccurrences=new IntWritable();
	}

	public StringAndInt2(String stringContent, int intContent) {
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
	public int compareTo(StringAndInt2 o) {
		return o.tag.toString().compareTo(tag.toString());
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
