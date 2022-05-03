import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Projections;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SentimentAnalysis {


    public static void main(String[] args) throws IOException {

        Authentication auth = new Authentication();
        MongoCollection<org.bson.Document> collection = auth.mongoDBAuthentication();
        System.out.print("Connection to myMongoTweet database established.\n");

        String line;


        int count = 1;
        int outputCount = 0;
        //Read and store positive words
        List<String> positiveWords = new ArrayList<>();
        String positiveWordFile = "WordList.csv";
        FileReader posWordList = new FileReader(positiveWordFile);
        try (BufferedReader buf = new BufferedReader(posWordList)) {
            while ((line = buf.readLine()) != null) {
                positiveWords.add(line);
            }

            //Read and store negative words
            List<String> negativeWords = new ArrayList<>();
            String negativeWordFile = "Negativewords.csv";
            FileReader negWordList = new FileReader(negativeWordFile);
            try (BufferedReader buf1 = new BufferedReader(negWordList)) {
                while ((line = buf1.readLine()) != null) {
                    negativeWords.add(line);
                }

                Bson tweetFields = Projections.fields(Projections.include("data.text"), Projections.excludeId());

                FindIterable<Document> it = collection.find().projection(tweetFields);
                MongoCursor<Document> cur = it.iterator();

                while (cur.hasNext()) {
                    String regex = "\"text\" : \"(.*?)\"";
                    String tweet = cur.next().toJson();
                    Pattern p = Pattern.compile(regex);
                    Matcher m = p.matcher(tweet);

                    while (m.find()) {
//                        String currentTweet = m.group(0);
                        int positive = 0;
                        int negative = 0;
                        int finalCount = 0;
                        List<String> matches = new ArrayList<>();
                        String parsedTweet = tweet.substring(m.start() + 9, m.end() - 2);
                        String[] tweetWords = parsedTweet.split("\\s");
                        HashMap<String, Integer> wordCounter = new HashMap<>();
                        for (String word : tweetWords) {
                            if (wordCounter.containsKey(word)) {
                                wordCounter.computeIfPresent(word, (key, value) -> value + 1);
                            } else
                                wordCounter.put(word, count);
                        }
                        matches.clear();
                        for (Map.Entry<String, Integer> entry : wordCounter.entrySet()) {
                            if (entry.getKey().length() > 1) {
//                                matches = new ArrayList<>();
                                if (positiveWords.contains(entry.getKey().toLowerCase())) {
                                    matches.add(entry.getKey());
                                    positive = positive + entry.getValue();
                                } else if (negativeWords.contains(entry.getKey().toLowerCase())) {
                                    matches.add(entry.getKey());
                                    negative = negative + entry.getValue() * (-1);
                                }
                            }
                        }
                        finalCount = positive + negative;

                        if(outputCount<50) {
                            if (finalCount > 0)
                                System.out.print(parsedTweet + " :\n " + "Matches: " + matches + " " + finalCount + " Positive\n");
                            if (finalCount < 0)
                                System.out.print(parsedTweet + " :\n " + "Matches: " + matches + " " + finalCount + " Negative\n");
                            if (finalCount == 0)
                                System.out.print(parsedTweet + " :\n " + "Matches: " + matches + " " + finalCount + " Neutral\n");

                            outputCount++;
                        }
                    }
                }
            }
        }
    }
}
//            }
//        }
//    }
//}







