package ru.ifmo.ctddev.ml.logreg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatasetReader {
    public static List<double[]> readFrom(File file) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file.getPath()));
        String s;
        List<double[]> data = new ArrayList<>();
        while ((s = in.readLine()) != null) {
            String[] ss = s.split(",");
            double[] row = new double[7];
            for (int i = 0; i < 3; i++) {
                row[i] = Double.parseDouble(ss[i]);
            }
            row[6] = row[2];
            row[5] = 1;
            row[2] = row[0] * row[0];
            row[3] = row[1] * row[1];
            row[4] = row[0] * row[1];
            data.add(row);
        }
        in.close();
        return data;
    }
}
