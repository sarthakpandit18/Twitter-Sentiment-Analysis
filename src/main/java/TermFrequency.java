import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Projections;
import com.mongodb.gridfs.GridFSDBFile;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.print.PrinterException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TermFrequency {



    public static void main(String[] args) throws IOException, PrinterException {

        int countFile = 0;

        Authentication auth = new Authentication();
        MongoCollection<org.bson.Document> collection = auth.mongoDBAuthentication();
        System.out.print("Connection to myMongoTweet database established.\n");

        int weatherDocument = 0;
        int peopleDocument = 0;
        int conditionDocument = 0;
        int wordsPerTweet;
        float frequency = 0;
        float max= -1;
        String text;


        JFrame frame1 = new JFrame();
        JTable table1 = new JTable();

        frame1.setTitle("tf-df Table");
        DefaultTableModel tableModel1 = new DefaultTableModel();

        String columns1[] = new String[] {"Search Query", "Document containing term(df)",
                "Total Documents(N)/ number of documents term appeared (df)", "Log10(N/df)"};
        tableModel1.setColumnIdentifiers(columns1);

        table1.setModel(tableModel1);


        JFrame frame2 = new JFrame();
        JTable table2 = new JTable();

        frame2.setTitle("Term Frequency");
        DefaultTableModel tableModel2 = new DefaultTableModel();
        String columns2[] = new String[] {"Tweet", "Total Words(m)","Frequency(f)"};
        tableModel2.setColumnIdentifiers(columns2);
        table2.setModel(tableModel2);


        Bson tweetFields = Projections.fields(Projections.include("data.text"), Projections.excludeId());

        FindIterable<Document> it = collection.find().projection(tweetFields);
        MongoCursor<Document> cur = it.iterator();

        while (cur.hasNext()) {
            String regex = "\"text\" : \"(.*?)\"";
            String tweet = cur.next().toJson();
            Pattern p = Pattern.compile(regex);

            Matcher m = p.matcher(tweet);
                while (m.find()) {
                    countFile += 1;
                    text = tweet.substring(m.start() + 9, m.end());

                    if (text.contains("condition"))
                        conditionDocument += 1;
                    if (text.contains("weather"))
                        weatherDocument += 1;
                    if (text.contains("people"))
                        peopleDocument += 1;

                    if (weatherDocument > 0) {
                        String[] splitText = text.split("\\s");
                        wordsPerTweet = splitText.length;

                        int weatherCount = 0;
                        for (String s : splitText) {
                            if (s.toLowerCase().equals("weather"))
                                weatherCount += 1;
                        }
                        if(weatherCount>=1) {
                            tableModel2.addRow(new Object[]{text, wordsPerTweet, weatherCount});
                            frequency = (float) weatherCount / wordsPerTweet;
                            if(frequency>max)
                                max = frequency;
                        }

                    }


                }
            }

        tableModel1.addRow( new Object[] {"Total documents", countFile});
        tableModel1.addRow( new Object[] {"Condition", conditionDocument,
                countFile/conditionDocument, (double)Math.log(countFile/conditionDocument)}) ;
        tableModel1.addRow( new Object[] {"Weather", weatherDocument,
                countFile/weatherDocument, (double)Math.log(countFile/weatherDocument)}) ;
        tableModel1.addRow( new Object[] {"People", peopleDocument,
                countFile/peopleDocument,(double) Math.log(countFile/peopleDocument)});

        table1 = new JTable(tableModel1);
        table1.setBounds(30, 40, 200, 300);
        JScrollPane sp1 = new JScrollPane(table1);
        frame1.add(sp1);
        frame1.setSize(500, 200);
        frame1.setVisible(true);


        table2 = new JTable(tableModel2);
        table2.setBounds(30, 40, 200, 300);
        JScrollPane sp2 = new JScrollPane(table2);
        frame2.add(sp2);
        frame2.setSize(500, 200);
        frame2.setVisible(true);


        Logger log = Logger.getLogger(TermFrequency.class.getName());
        log.info("The highest relative frequency is " + max);


    }



    }




