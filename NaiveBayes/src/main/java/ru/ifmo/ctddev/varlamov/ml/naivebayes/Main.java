package ru.ifmo.ctddev.varlamov.ml.naivebayes;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {

    public static final double VAL_RATIO = 0.2;
    public static final int CV_FOLDS = 5;

    public static void main(String[] args) throws IOException {
        File dir = new File("./src/main/res/pu1");
        List<Letter> letters = LetterReader.readFrom(dir);
        Collections.shuffle(letters, new Random(System.currentTimeMillis()));
        List<Letter> validation = letters.subList(0, (int)Math.ceil(VAL_RATIO * letters.size()));
        List<Letter> test = letters.subList((int)Math.ceil(VAL_RATIO * letters.size()), letters.size());
        double totalError = 0;
        for (int i = 0; i < CV_FOLDS; i++) {
            int startPos = (test.size() / CV_FOLDS) * i, endPos = (test.size() / CV_FOLDS) * (i + 1);
            List<Letter> check = test.subList(startPos, endPos);
            List<Letter> train = new ArrayList<>(test.subList(0, startPos));
            train.addAll(test.subList(endPos, test.size()));
            totalError += Qualifier.getError(NaiveBayes.train(train), check);
        }
        System.out.println("Cross-validation error: " + (totalError / CV_FOLDS));
        NaiveBayes naiveBayes = NaiveBayes.train(test);
        System.out.println("Validation error: " + Qualifier.getError(naiveBayes, validation));
    }
}
