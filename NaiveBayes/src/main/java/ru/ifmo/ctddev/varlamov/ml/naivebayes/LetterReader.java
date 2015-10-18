package ru.ifmo.ctddev.varlamov.ml.naivebayes;

import java.io.*;
import java.util.*;

public class LetterReader {

    public static Letter readFromFile(File file) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(file.toString()));
        String name = file.getName();
        Letter letter;
        if (name.contains("spmsg")) {
            letter = new Letter(true);
        } else {
            letter = new Letter(false);
        }
        StringTokenizer tokens = new StringTokenizer(in.readLine());
        tokens.nextToken();
        while (tokens.hasMoreTokens()) {
            letter.addToSubject(Integer.parseInt(tokens.nextToken()));
        }
        in.readLine();
        String s;
        while ((s = in.readLine()) != null) {
            tokens = new StringTokenizer(s);
            while (tokens.hasMoreTokens()) {
                letter.addToBody(Integer.parseInt(tokens.nextToken()));
            }
        }
        return letter;
    }

    public static List<Letter> readFrom(File file) throws IOException {
        List<Letter> result = new ArrayList<>();
        if (file.isFile()) {
            result.add(readFromFile(file));
        } else {
            for (File inner : file.listFiles()) {
                result.addAll(readFrom(inner));
            }
        }
        return result;
    }
}
