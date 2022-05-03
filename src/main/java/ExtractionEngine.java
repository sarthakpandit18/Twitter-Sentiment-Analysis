
import java.io.*;
import java.net.http.HttpResponse;

public class ExtractionEngine {

    public static void extractionEngine() throws IOException, InterruptedException {

        System.out.print("Twitter extraction engine begins.\n");

        String[] keywords = {"flu","vaccine","cold","mask","immune","snow"};
        String[] start_time = {"2022-03-16T10:00:00Z","2022-03-17T00:00:00Z","2022-03-18T00:00:00Z","2022-03-19T00:00:00Z","2022-03-20T00:00:00Z","2022-03-22T00:00:00Z"};
        String[] end_time = {"2022-03-17T00:00:00Z","2022-03-18T00:00:00Z","2022-03-19T00:00:00Z","2022-03-20T00:00:00Z","2022-03-21T00:00:00Z","2022-03-22T07:00:00Z"};

        String newfile;
        String cleaned_text;
        int countFiles = 0;

        Authentication auth = new Authentication();
        for (String s : keywords) {
            //System.out.print(s + "\n");
            for (int i = 0; i < start_time.length; i++) {

                HttpResponse<String> response = auth.authenticateTwitterAPI(s,start_time[i],end_time[i]);

                newfile = "Output/ResponseOutput_" + s + "_" + start_time[i]  + "_" + end_time[i] + ".json";
                FileOutputStream fs = new FileOutputStream(newfile); //Initializing writer operation
                BufferedWriter w = new BufferedWriter(new OutputStreamWriter(fs, "UTF-8"));
                w.write(response.body());
                w.newLine();
                countFiles+=1;
                System.out.print("Tweets extracted in " + countFiles + " files\n");
                w.close();
            }
        }
        System.out.print("Twitter extraction engine ends.\n");
    }
}





