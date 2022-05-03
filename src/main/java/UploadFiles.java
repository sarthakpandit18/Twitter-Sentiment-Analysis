import com.mongodb.MongoWriteException;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.InsertOneModel;
import org.bson.Document;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UploadFiles {

    public static void updloadFiles() throws IOException {


        Authentication auth = new Authentication();
        MongoCollection<org.bson.Document> collection =  auth.mongoDBAuthentication();
        System.out.print("Connection to myMongoTweet database established.\n");

        String[] keywords = {"immune"};
        String[] start_time = {"2022-03-16T10:00:00Z","2022-03-17T00:00:00Z","2022-03-18T00:00:00Z","2022-03-19T00:00:00Z","2022-03-20T00:00:00Z","2022-03-22T00:00:00Z"};
        String[] end_time = {"2022-03-17T00:00:00Z","2022-03-18T00:00:00Z","2022-03-19T00:00:00Z","2022-03-20T00:00:00Z","2022-03-21T00:00:00Z","2022-03-22T07:00:00Z"};

        String inputFile;
        String line;
        int countFiles = 0;
//        collection.drop();
        for (String s : keywords) {

            for (int i = 0; i < start_time.length; i++) {


                inputFile = "CleanedOutput/ResponseOutput_" + s + "_" + start_time[i].replace(":", "").replace("00", "") + "_" + end_time[i] + ".json";
//                inputFile = "CleanedOutput/ResponseOutput_immune_2022-03-15TZ_2022-03-16T00:00:00Z.json";

                    //Bulk Approach:
                    int count = 0;
                    int batch = 100;
                    List<InsertOneModel<Document>> docs = new ArrayList<>();

                    try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {

                        System.out.print("Uploading file - " + inputFile + "\n");
                        while ((line = br.readLine()) != null) {
                            docs.add(new InsertOneModel<>(Document.parse(line)));
                            count++;
                            if (count == batch) {
                                collection.bulkWrite(docs, new BulkWriteOptions().ordered(false));
                                docs.clear();
                                count = 0;
                            }
                        }
                    } catch (MongoWriteException e) {
                        System.out.println("Error");
                    }

                    if (count > 0) {
                        BulkWriteResult bulkWriteResult = collection.bulkWrite(docs, new BulkWriteOptions().ordered(false));
                        countFiles += 1;
                        System.out.print(countFiles + " documents inserted " + "\n");
                    }
            }
        }
        System.out.print("Documents uploaded.\n");
    }

}
