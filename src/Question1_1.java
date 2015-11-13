
import java.io.IOException;

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

public class Question1_1 {
	private static final String TAG = Question1_1.class.getSimpleName();

	public static class MyMapper extends
			Mapper<LongWritable, Text, Text, IntWritable> {

		private Text key = new Text();
		private IntWritable value = new IntWritable(1);

		@Override
		protected void map(LongWritable k, Text line, Context context)
				throws IOException, InterruptedException {
			for (String word : line.toString().split("\\s+")) {
				key.set(word);
				context.write(key, value);
			}
		}
	}

	public static class MyReducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {

		private IntWritable value = new IntWritable();

		@Override
		protected void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable v : values) {
				sum += v.get();
			}
			value.set(sum);
			context.write(key, value);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		String input = otherArgs[0];
		String output = otherArgs[1];
		
		Job job = Job.getInstance(conf, TAG);
		job.setJarByClass(Question1_1.class);
		
		job.setMapperClass(MyMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);

		job.setCombinerClass(MyReducer.class);
		job.setNumReduceTasks(3);
		
		job.setReducerClass(MyReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		FileInputFormat.addInputPath(job, new Path(input));
		job.setInputFormatClass(TextInputFormat.class);
		
		FileOutputFormat.setOutputPath(job, new Path(output));
		job.setOutputFormatClass(TextOutputFormat.class);
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}