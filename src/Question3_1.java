import java.io.IOException;
import java.net.URLDecoder;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Question3_1 {
	private static final String TAG = Question3_1.class.getSimpleName();

	public static class MyMapper extends Mapper<LongWritable, Text, PaysTag, IntWritable> {

		public static String tagSplitter = ",";
		private PaysTag key;
		private IntWritable value = new IntWritable(1);

		@Override
		protected void map(LongWritable k, Text line, Context context) throws IOException, InterruptedException {
			String arr[] = line.toString().split("\t");
			Double lat = Double.parseDouble(arr[11]);
			Double lon = Double.parseDouble(arr[10]);
			String tags = arr[8];
			Country country = null;
			country = Country.getCountryAt(lat, lon);
			if (country != null) {
				for (String tag : tags.split(tagSplitter)) {
					@SuppressWarnings("deprecation")
					String cad = URLDecoder.decode(tag);
					if (!cad.equals("")) {
						key = new PaysTag(country.toString(), cad);
						context.write(key, value);
					}
				}
			}
		}
	}

	public static class MyReducer extends Reducer<PaysTag, IntWritable, PaysTag, IntWritable> {

		private IntWritable value = new IntWritable();

		@Override
		protected void reduce(PaysTag country, Iterable<IntWritable> tagsF, Context context)
				throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable v : tagsF) {
				sum += v.get();
			}
			value.set(sum);
			context.write(country, value);
		}
	}

	public static class MyCombiner extends Reducer<PaysTag, IntWritable, PaysTag, IntWritable> {

		private IntWritable value = new IntWritable();

		@Override
		protected void reduce(PaysTag country, Iterable<IntWritable> tags, Context context)
				throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable v : tags) {
				sum += v.get();
			}
			value.set(sum);
			System.out.println(country + " " + value.get());
			context.write(country, value);
		}

	}

	public static class MyMapper2 extends Mapper<PaysTag, IntWritable, StringAndInt2, Text> {

		@Override
		protected void map(PaysTag countryTag, IntWritable freq, Context context)
				throws IOException, InterruptedException {

			StringAndInt2 key = new StringAndInt2(countryTag.getPays().toString(), freq.get());
			Text value = countryTag.getTag();
			System.out.println(key+" "+value);
			context.write(key, value);
		}
	}

	public static class MyReducer2 extends Reducer<StringAndInt2, Text, Text, StringAndInt> {

		private Text key = new Text();
		private StringAndInt value;

		@Override
		protected void reduce(StringAndInt2 countryFreq, Iterable<Text> tags, Context context)
				throws IOException, InterruptedException {

			Configuration conf = context.getConfiguration();
			int k = Integer.parseInt(conf.get("k"));
			System.out.println(countryFreq);

			key.set(countryFreq.getStringContent());

			for (Text v : tags) {
				value = new StringAndInt(v.toString(), countryFreq.getIntContent());
				System.out.println(v.toString());
				context.write(key, value);
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
		job.setJarByClass(Question3_1.class);

		job.setMapperClass(MyMapper.class);
		job.setMapOutputKeyClass(PaysTag.class);
		job.setMapOutputValueClass(IntWritable.class);

		 //job.setGroupingComparatorClass(PaysTag.class);
		// job.setSortComparatorClass(StringAndInt.class);

		job.setCombinerClass(MyCombiner.class);
		job.setNumReduceTasks(3);

		job.setReducerClass(MyReducer.class);
		job.setOutputKeyClass(PaysTag.class);
		job.setOutputValueClass(IntWritable.class);

		FileInputFormat.addInputPath(job, new Path(input));
		job.setInputFormatClass(TextInputFormat.class);

		Path inter = new Path("inter");
		FileOutputFormat.setOutputPath(job, inter);
		job.setOutputFormatClass(SequenceFileOutputFormat.class);

		job.waitForCompletion(true);

		/* second job */

		Job job2 = Job.getInstance(conf, TAG);
		job2.setJarByClass(Question3_1.class);

		job2.setMapperClass(MyMapper2.class);
		job2.setMapOutputKeyClass(StringAndInt2.class);
		job2.setMapOutputValueClass(Text.class);

		
		job2.setGroupingComparatorClass(FrequenceComparator.class);
//		job.setSortComparatorClass(StringAndInt.class);

		job2.setReducerClass(MyReducer2.class);
		job2.setOutputKeyClass(Text.class);
		job2.setOutputValueClass(StringAndInt.class);

		FileInputFormat.addInputPath(job2, inter);
		job2.setInputFormatClass(SequenceFileInputFormat.class);

		FileOutputFormat.setOutputPath(job2, new Path(output));
		job2.setOutputFormatClass(TextOutputFormat.class);

		job2.waitForCompletion(true);

	}
}