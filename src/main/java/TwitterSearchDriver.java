import java.io.IOException;

public class TwitterSearchDriver {

    public static void main (String[] args) throws IOException, InterruptedException {

//            Tweet extraction engine using Twitter Search v2 API
            ExtractionEngine.extractionEngine();
//            Tweet cleaning and transformation engine
            FiltrationEngine.filterationEngine();
//            Uploading cleaned tweets to myMongoTweet database hosted on cluster
            UploadFiles.updloadFiles();

    }
}
