import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Cric {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Cric");
        job.setJarByClass(Cric.class);
        job.setMapperClass(CricMapper.class);
        //job.setCombinerClass(CricReducer.class);
        job.setReducerClass(CricReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

    public static class CricMapper extends Mapper<Object, Text, Text, Text> {      //Mapper

       public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
	String line = value.toString();  //parse the user input
		String[] extracts = line.split(","); //0-batsman 1-bowler 2-predictionInfo ..split row into tokens
		double prob_zero=0.0,prob_one=0.0,prob_two=0.0,prob_three=0.0,prob_four=0.0,prob_five=0.0,prob_six=0.0,prob_seven=0.0,prob_eight=0.0;
		if(extracts[10]!="" && extracts[10]!=" ")
		{       
		prob_zero = Double.parseDouble(extracts[2])/(Double.parseDouble(extracts[12])+1);
		prob_one = Double.parseDouble(extracts[3])/(Double.parseDouble(extracts[12])+1);
		prob_two = Double.parseDouble(extracts[4])/(Double.parseDouble(extracts[12])+1);
	         prob_three = Double.parseDouble(extracts[5])/(Double.parseDouble(extracts[12])+1);
		 prob_four = Double.parseDouble(extracts[6])/(Double.parseDouble(extracts[12])+1);
		 prob_five = Double.parseDouble(extracts[7])/(Double.parseDouble(extracts[12])+1);
		 prob_six = Double.parseDouble(extracts[8])/(Double.parseDouble(extracts[12])+1);
		 prob_seven = Double.parseDouble(extracts[9])/(Double.parseDouble(extracts[12])+1);      
		 prob_eight=1/(Double.parseDouble(extracts[12])+1);
		
		}
		else
		{ prob_zero = Double.parseDouble(extracts[2])/Double.parseDouble(extracts[12]);
		 prob_one = Double.parseDouble(extracts[3])/Double.parseDouble(extracts[12]);
	         prob_two = Double.parseDouble(extracts[4])/Double.parseDouble(extracts[12]);
		 prob_three = Double.parseDouble(extracts[5])/Double.parseDouble(extracts[12]);
		 prob_four = Double.parseDouble(extracts[6])/Double.parseDouble(extracts[12]);
		 prob_five = Double.parseDouble(extracts[7])/Double.parseDouble(extracts[12]);
		 prob_six = Double.parseDouble(extracts[8])/Double.parseDouble(extracts[12]);
		 prob_seven = Double.parseDouble(extracts[9])/Double.parseDouble(extracts[12]);
		  prob_eight=0.0;
		  prob_eight=1/Double.parseDouble(extracts[12]);
		}
		
                String outval = extracts[0] + "," + extracts[1]; //batsman name,bowler name
		String probs = prob_zero+","+prob_one+","+prob_two+","+prob_three+","+prob_four+","+prob_five+","+prob_six+","+prob_seven+","+prob_eight;             
		context.write(new Text(outval), new Text(probs)); //output key: batsman name,bowler name 
									       //output value;probabilities of each type of run scored
	}
    }

    public static class CricReducer extends Reducer<Text, Text, Text, Text> { //Reducer
       //reduce method accepts the Key Value pairs from mappers, do the aggregation based on keys and produce the final out put
      public void reduce(Text key, Text probs, Context context) throws IOException, InterruptedException
      {
           
          context.write(new Text(key), new Text(probs)); //print mapper output
	  
      }
    }
}

