package edu.pja.sri.plonca.sri3jms.helpers;

import com.jayway.jsonpath.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//    ArrayList<Double> testList = JsonPath.read(document, "$.engineTemperature.upper");

public class JsonReader {
    public static Object readJsonFile(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));

        StringBuilder sb = new StringBuilder();
        String line = br.readLine();

        while (line != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
            line = br.readLine();
        }
        br.close();
        String jsonInput = sb.toString();
        return Configuration.defaultConfiguration().jsonProvider().parse(jsonInput);

    }

}
