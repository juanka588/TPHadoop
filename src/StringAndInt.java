import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class StringAndInt implements Writable, Comparable<StringAndInt> {
	private String pays;
	private int nOccurrences;

	public StringAndInt() {
		this("", 0);
	}

	public StringAndInt(String stringContent, int intContent) {
		super();
		this.pays = stringContent;
		this.nOccurrences = intContent;
	}

	public String getStringContent() {
		return pays;
	}

	public void setStringContent(String stringContent) {
		this.pays = stringContent;
	}

	public int getIntContent() {
		return nOccurrences;
	}

	public void setIntContent(int intContent) {
		this.nOccurrences = intContent;
	}

	@Override
	public int compareTo(StringAndInt o) {
		return nOccurrences - o.nOccurrences;
	}

	@Override
	public void readFields(DataInput arg0) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		// TODO Auto-generated method stub

	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return pays+" "+nOccurrences;
	}

}
