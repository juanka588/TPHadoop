import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.PriorityQueue;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Question2_2 {
	private static final String TAG = Question2_2.class.getSimpleName();

	public static class MyMapper extends Mapper<LongWritable, Text, Text, StringAndInt> {

		public static String tagSplitter = ",";
		private Text key = new Text();
		private StringAndInt value;

		@Override
		protected void map(LongWritable k, Text line, Context context) throws IOException, InterruptedException {
			String arr[] = line.toString().split("\t");
			Double lat = Double.parseDouble(arr[11]);
			Double lon = Double.parseDouble(arr[10]);
			String tags = arr[8];
			Country country = null;
			country = Country.getCountryAt(lat, lon);
			if (country != null) {
				key.set(country.toString());
				for (String tag : tags.split(tagSplitter)) {
					@SuppressWarnings("deprecation")
					String cad = URLDecoder.decode(tag);
					if (!cad.equals("")) {
						value = new StringAndInt(cad, 1);
						context.write(key, value);
					}
				}
			}
		}
	}

	public static class MyReducer extends Reducer<Text, StringAndInt, Text, StringAndInt> {

		private String key;
		private int freq;
		private StringAndInt value = new StringAndInt();

		@Override
		protected void reduce(Text country, Iterable<StringAndInt> tagsF, Context context)
				throws IOException, InterruptedException {

			Configuration conf = context.getConfiguration();
			int k = Integer.parseInt(conf.get("k"));
			// HashMap qui contient (tag, nbOccurencesTag)
			HashMap<String, Integer> tagAndFrequency = new HashMap<>();
			for (StringAndInt v : tagsF) {
				key = v.getStringContent();
				freq = v.getIntContent();
				if (key != null) {
					if (tagAndFrequency.containsKey(key)) {
						freq = tagAndFrequency.get(key);
						freq++;
						tagAndFrequency.put(key, freq);
					} else {
						tagAndFrequency.put(key, freq);
					}
				}
			}

			PriorityQueue<StringAndInt> order = new PriorityQueue<>();
			for (String key : tagAndFrequency.keySet()) {
				StringAndInt v = new StringAndInt(key, tagAndFrequency.get(key));
				order.add(v);
			}
			for (int i = 0; i < k; i++) {
				value = order.poll();
				if (value == null) {
					break;
				}
				context.write(country, value);
			}
		}
	}

	public static class MyCombiner extends Reducer<Text, StringAndInt, Text, StringAndInt> {

		private StringAndInt value;
		private String key;
		private int freq;

		@Override
		protected void reduce(Text country, Iterable<StringAndInt> tags, Context context)
				throws IOException, InterruptedException {
			// HashMap qui contient (tag, nbOccurencesTag)
			HashMap<String, Integer> tagAndFrequency = new HashMap<>();
			for (StringAndInt v : tags) {
				key = v.getStringContent();
				freq = v.getIntContent();
				if (key != null) {
					if (tagAndFrequency.containsKey(key)) {
						freq = tagAndFrequency.get(key);
						freq++;
						tagAndFrequency.put(key, freq);
					} else {
						tagAndFrequency.put(key, freq);
					}
				}
			}
			for (String key : tagAndFrequency.keySet()) {
				freq = tagAndFrequency.get(key);
				value = new StringAndInt(key, freq);
				System.out.println(country.toString() + " " + value);
				context.write(country, value);
			}
		}

	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		String input = otherArgs[0];
		String output = otherArgs[1];
		conf.set("k", otherArgs[2]);

		Job job = Job.getInstance(conf, TAG);
		job.setJarByClass(Question2_2.class);

		job.setMapperClass(MyMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(StringAndInt.class);

		job.setCombinerClass(MyCombiner.class);

		job.setReducerClass(MyReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(StringAndInt.class);

		FileInputFormat.addInputPath(job, new Path(input));
		job.setInputFormatClass(TextInputFormat.class);

		FileOutputFormat.setOutputPath(job, new Path(output));
		job.setOutputFormatClass(TextOutputFormat.class);

		job.waitForCompletion(true);

	}
}