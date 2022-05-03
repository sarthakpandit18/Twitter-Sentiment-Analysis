import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import scala.Tuple2;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class MapReduce {

    private static JavaSparkContext sparkContext;
    private static SparkConf sc;

    private static String COLD = "cold";
    private static String FLU = "flu";
    private static String SNOW = "snow";

    public static  void main(String[] args) throws IOException {


        sc = new SparkConf().setMaster("local[1]").setAppName("Twitter-MapReduce");
        sparkContext = new JavaSparkContext(sc);


        JavaRDD<String> inputData = sparkContext.textFile("CleanedOutput/CombinedJSONOutput.json");
        JavaRDD<String> words = inputData.flatMap(lines -> Arrays.asList(lines.split("[\\s\"]")).iterator());
        JavaPairRDD result = words.mapToPair(word -> new Tuple2(word, 1)).reduceByKey((x, y) -> (int) x + (int) y);

        Map<String, Integer> finalMap = new HashMap<>();
        Map map = new HashMap<>();
        map =  result.filter(word -> word.toString().contains(COLD)).collectAsMap();
        int sum = map.values().stream().mapToInt(x -> (Integer)x).sum();
        finalMap.put(COLD,sum);
        map.clear();

        map =  result.filter(word -> word.toString().contains(FLU)).collectAsMap();
        sum = map.values().stream().mapToInt(x -> (Integer)x).sum();
        finalMap.put(FLU,sum);

        map =  result.filter(word -> word.toString().contains(SNOW)).collectAsMap();
        sum = map.values().stream().mapToInt(x -> (Integer)x).sum();
        finalMap.put(SNOW,sum);

        System.out.print(finalMap);
        File file = new File("OutputFile");
        try {

            // create new BufferedWriter for the output file
            BufferedWriter bf = new BufferedWriter( new FileWriter(file));

            // iterate map entries
            for (Map.Entry<String, Integer> entry :
                    finalMap.entrySet()) {

                // put key and value separated by a colon
                bf.write(entry.getKey() + ":"
                        + entry.getValue());

                // new line
                bf.newLine();
            }

            bf.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        }
    }


