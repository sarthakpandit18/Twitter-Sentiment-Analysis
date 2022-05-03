

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FiltrationEngine {

        public static void filterationEngine() throws IOException, FileNotFoundException {

        System.out.print("Twitter filtration engine begins.\n");

        String[] keywords = {"flu", "cold", "mask", "immune", "snow"};
        String[] start_time = {"2022-03-16T10:00:00Z","2022-03-17T00:00:00Z","2022-03-18T00:00:00Z","2022-03-19T00:00:00Z","2022-03-20T00:00:00Z","2022-03-22T00:00:00Z"};
        String[] end_time = {"2022-03-17T00:00:00Z","2022-03-18T00:00:00Z","2022-03-19T00:00:00Z","2022-03-20T00:00:00Z","2022-03-21T00:00:00Z","2022-03-22T07:00:00Z"};
        String inputFile;
        String outputFile;
        int countFiles = 0;

        String cleanedline = "";
        Stack<Integer> begin = new Stack<Integer>();
        List<String> cleanedOutput = new ArrayList<>();
        String cleaned_text = "", line;
        for (String s : keywords) {

            for (int i = 0; i < start_time.length; i++) {

                inputFile = "Output/ResponseOutput_" + s + "_" + start_time[i] + "_" + end_time[i] + ".json";
                outputFile = "CleanedOutput/ResponseOutput_" + s + "_" + start_time[i].replace(":", "").replace("00", "") + "_" + end_time[i] + ".json";

                //Reading the input file
                FileReader f = new FileReader(inputFile);
                List<String> output = new ArrayList<>();
                //Writing to JSON File
//                FileOutputStream fs = new FileOutputStream(outputFile); //Initializing writer operation
//                BufferedWriter w = new BufferedWriter(new OutputStreamWriter(fs, "UTF-8"));
                PrintWriter w = new PrintWriter(new FileWriter(outputFile));

                try (BufferedReader b = new BufferedReader(f)) {
                    while ((line = b.readLine()) != null) {
                        int index;
                        String regex = "\"text\":(.*?)\"[,}]";

                        Pattern p = Pattern.compile(regex);
                        Matcher m = p.matcher(line);
                        String rawline = "";
                        String newline = line;
                        int length = 0;
                        while (m.find()) {
                            length = length + newline.substring(m.start() + 8, m.end() - 2).length() -
                                    newline.substring(m.start() + 8, m.end() - 2)

                                            .replaceAll("http\\S+", "") //Removing URLs
                                            .replaceAll("[^a-zA-Z0-9 ]", "") //Removing special characters
                                            .replace("\\","")
                                            //.replaceAll("uD[a-zA-Z0-9]\\S+", "")
                                            .length();


                            cleanedline = newline.substring(m.start() + 8, m.end() - 2)

                                    .replaceAll("http\\S+", "") //Removing URLs
                                    .replaceAll("[^a-zA-Z0-9 ]", "")
                                    .replace("\\\\",""); //Removing special characters
                                    //.replaceAll("uD[a-zA-Z0-9]\\S+", "");

                            rawline = newline.substring(m.start() + 8, m.end() - 2);
                            line = line.replace(rawline, cleanedline);

                        }
                        String remove_quotes = line.replace("\"\"","\"");
                        String finalCleaning = remove_quotes

//                                .replace(",\"name\":\",",",\"name\":\"\",")
                                ;
                        w.write(line);
                    }
                    w.close();

                }

                countFiles += 1;
                System.out.print("Count of cleaned files: " + countFiles + "\n");

            }

        }
    }
}







