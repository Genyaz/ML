package com.company;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class DataReader {
    public static List<double[]> read(String filename) throws IOException {
        BufferedReader dataIn = new BufferedReader(new FileReader("./res/" + filename + ".data"));
        BufferedReader labelIn = new BufferedReader(new FileReader("./res/" + filename + ".labels"));
        List<double[]> result = new ArrayList<>();
        String s;
        while ((s = dataIn.readLine()) != null) {
            StringTokenizer tokens = new StringTokenizer(s);
            int xSize = tokens.countTokens();
            double[] x = new double[xSize + 1];
            for (int i = 0; i < xSize; i++) {
                x[i] = Double.parseDouble(tokens.nextToken());
            }
            x[xSize] = Double.parseDouble(labelIn.readLine());
            result.add(x);
        }
        dataIn.close();
        labelIn.close();
        return result;
    }
}
