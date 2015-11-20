import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

public class StringAndInt implements WritableComparable<StringAndInt> {
	private String tag;
	private int nOccurrences;
	public static String split = "----";

	public StringAndInt() {
	}

	public StringAndInt(String stringContent, int intContent) {
		super();
		this.tag = stringContent;
		this.nOccurrences = intContent;
	}

	public StringAndInt(Text txt) {
		super();
		String cad[] = txt.toString().split(split);
		this.tag = cad[0];
		this.nOccurrences = Integer.parseInt(cad[1]);
	}

	public String getStringContent() {
		return tag;
	}

	public void setStringContent(String stringContent) {
		this.tag = stringContent;
	}

	public int getIntContent() {
		return nOccurrences;
	}

	public void setIntContent(int intContent) {
		this.nOccurrences = intContent;
	}

	@Override
	public int compareTo(StringAndInt o) {
		return o.nOccurrences - nOccurrences;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return tag + split + nOccurrences;
	}

	@Override
	public void readFields(DataInput in) throws IOException {
		// nOccurrences=in.readInt();
		// pays=in.readLine();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(nOccurrences);
		out.writeChars(tag);
	}

}
