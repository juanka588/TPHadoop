import java.io.IOException;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
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

public class Question2_1 {
	private static final String TAG = Question2_1.class.getSimpleName();

	public static HashMap<String, Integer> tagf;

	public static class MyMapper extends Mapper<LongWritable, Text, Text, Text> {

		private Text key = new Text();
		private Text value = new Text();

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
				for (String tag : tags.split(",")) {
					value.set(tag);
					// System.out.println("lat " + lat + " lon " + lon + " tags
					// " + tags + " country " + key.toString());
					context.write(key, value);
				}
			}
		}
	}

	public static class MyReducer extends Reducer<Text, Text, Text, StringAndInt> {

		private StringAndInt value;

		@Override
		protected void reduce(Text country, Iterable<Text> tags, Context context)
				throws IOException, InterruptedException {

			//HashMap qui contient (tag, nbOccurencesTag)
			HashMap<String, Integer> tagAndFrequency = new HashMap<>();
//			for(Text tag : tags){
//				System.out.println("tag : "+tag.toString());
//			}
			
			for (Text v : tags) {
				if(v.toString() != null){
				if (tagAndFrequency.containsKey(v.toString())) {
					tagAndFrequency.put(v.toString(), tagAndFrequency.get(v.toString())+1);
				} else {
					tagAndFrequency.put(v.toString(), 1);
				}		
			}
			}

			tagf=tagAndFrequency;
			for (String key : tagAndFrequency.keySet()) {
				value = new StringAndInt(key, tagAndFrequency.get(key));
				context.write(country, value);
			}

		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		String input = otherArgs[0];
		String output = otherArgs[1];
		int k = Integer.parseInt(otherArgs[2]);

		Job job = Job.getInstance(conf, TAG);
		job.setJarByClass(Question2_1.class);

		job.setMapperClass(MyMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);

		// job.setCombinerClass(MyReducer.class);
		// job.setNumReduceTasks(3);

		job.setReducerClass(MyReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(StringAndInt.class);

		FileInputFormat.addInputPath(job, new Path(input));
		job.setInputFormatClass(TextInputFormat.class);

		FileOutputFormat.setOutputPath(job, new Path(output));
		job.setOutputFormatClass(TextOutputFormat.class);

		job.waitForCompletion(true);
		for (String key : tagf.keySet()) {
			int val=tagf.get(key);
			System.out.println(key+" "+val );
		}
		
	}
}