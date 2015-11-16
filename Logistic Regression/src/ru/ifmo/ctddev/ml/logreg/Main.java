package ru.ifmo.ctddev.ml.logreg;

import sun.rmi.runtime.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    public static final double VAL_RATIO = 0.1;
    public static final int CV_FOLDS = 5;
    public static final double C = 1;
    public static void main(String[] args) throws Exception {
        List<double[]> data = DatasetReader.readFrom(new File("./res/chips.txt"));
        Collections.shuffle(data);
        List<double[]> validation = data.subList(0, (int)Math.ceil(VAL_RATIO * data.size()));
        List<double[]> test = data.subList((int)Math.ceil(VAL_RATIO * data.size()), data.size());
        Qualifier.Error totalError = new Qualifier.Error(0, 0, 0, 0);
        for (int i = 0; i < CV_FOLDS; i++) {
            int startPos = (test.size() / CV_FOLDS) * i, endPos = (test.size() / CV_FOLDS) * (i + 1);
            List<double[]> check = test.subList(startPos, endPos);
            List<double[]> train = new ArrayList<>(test.subList(0, startPos));
            train.addAll(test.subList(endPos, test.size()));
            double[][] array = train.toArray(new double[0][]);
            totalError = totalError.add(Qualifier.getError(LogisticRegression.train(array, 0.01, 0.01, 1), check));
        }
        System.out.println("Cross-validation:");
        System.out.println("True positive: " + totalError.tp);
        System.out.println("True negative: " + totalError.tn);
        System.out.println("False positive: " + totalError.fp);
        System.out.println("False negative: " + totalError.fn);
        double[][] array = test.toArray(new double[0][]);
        LogisticRegression svm = LogisticRegression.train(array, 0.01, 0.01, 1);
        totalError = Qualifier.getError(svm, validation);
        System.out.println("\nValidation error: ");
        System.out.println("True positive: " + totalError.tp);
        System.out.println("True negative: " + totalError.tn);
        System.out.println("False positive: " + totalError.fp);
        System.out.println("False negative: " + totalError.fn);
    }
}
