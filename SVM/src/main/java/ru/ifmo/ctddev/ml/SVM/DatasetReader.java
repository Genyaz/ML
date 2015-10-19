package ru.ifmo.ctddev.ml.SVM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

public class DatasetReader {
    public static List<double[]> readFrom(File file) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file.getPath()));
        in.readLine();
        String s;
        List<double[]> data = new ArrayList<double[]>();
        while ((s = in.readLine()) != null) {
            String[] ss = s.split(";");
            double[] row = new double[3];
            for (int i = 0; i < 3; i++) {
                row[i] = Double.parseDouble(ss[i]);
            }
            row[2] = 2 * row[2] - 1;
            //row[3] = row[2];
            //row[2] = Math.sqrt(row[1] * row[1] + row[2] * row[2]);
            data.add(row);
        }/**/
        in.close();
        return data;
    }
}
