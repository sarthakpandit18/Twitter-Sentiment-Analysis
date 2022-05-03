import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Authentication {

    public static HttpResponse<String> authenticateTwitterAPI(String keyword, String start_time, String end_time) throws  IOException, InterruptedException {

            String getRequest = "https://api.twitter.com/2/tweets/search/recent?query=" + keyword + "&start_time=" + start_time + "&end_time=" + end_time + "&max_results=100"
                + "&expansions=author_id,entities.mentions.username,geo.place_id,in_reply_to_user_id,referenced_tweets.id,referenced_tweets.id.author_id";

            String token = "Bearer AAAAAAAAAAAAAAAAAAAAAG8VZwEAAAAArm4aH8X%2Bpqlr18sSR%2BA9hjpez%2BM%3DByIeSnuYGga3UkkjtK1U75VYn1ZkmSfRFvVk506JitOSt0C3iE";

            HttpClient httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            //Set headers
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(getRequest))
                    .setHeader("Authorization", token)
                    .build();

            HttpResponse<String> response = null;

            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            HttpHeaders responseHeader = response.headers();


        return response;
    }

    public static MongoCollection<Document> mongoDBAuthentication() {

        com.mongodb.client.MongoClient client = MongoClients.create( "mongodb+srv://sarthak181:Sunlozara3!@cluster0.au4fw.mongodb.net/myMongoTweet?retryWrites=true&w=majority");

        MongoDatabase database = client.getDatabase("myMongoTweet");
        MongoCollection<Document> collection = database.getCollection("Twitter-API-Research");

        return collection;
    }


}
