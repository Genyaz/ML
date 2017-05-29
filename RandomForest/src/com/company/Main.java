package com.company;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Main {

    public static final double VALIDATION_PART = 0.2;

    public static void main(String[] args) throws IOException {
	    /*List<double[]> trainData = DataReader.read("arcene_train");
        List<double[]> validationData = DataReader.read("arcene_valid");

        ClassificationRandomForest rf = new ClassificationRandomForest(trainData, ClassificationDecisionTree.Criterion.InformationGain, 10, 5, 20);
        ClassificationQualifier.Error trainError = ClassificationQualifier.getError(rf, trainData);
        System.out.println("Train data: ");
        System.out.println("True positive: " + trainError.tp);
        System.out.println("True negative: " + trainError.tn);
        System.out.println("False positive: " + trainError.fp);
        System.out.println("False negative: " + trainError.fn);
        double precision = trainError.tp * 1.0 / (trainError.tp + trainError.fp);
        double recall = trainError.tp * 1.0 / (trainError.tp + trainError.fn);
        System.out.println("Precision: " + precision);
        System.out.println("Recall: " + recall);
        System.out.println("F1: " + (2 * recall * precision / (recall + precision)));
        ClassificationQualifier.Error validationError = ClassificationQualifier.getError(rf, validationData);
        System.out.println("\nValidation error: ");
        System.out.println("True positive: " + validationError.tp);
        System.out.println("True negative: " + validationError.tn);
        System.out.println("False positive: " + validationError.fp);
        System.out.println("False negative: " + validationError.fn);
        precision = validationError.tp * 1.0 / (validationError.tp + validationError.fp);
        recall = validationError.tp * 1.0 / (validationError.tp + validationError.fn);
        System.out.println("Precision: " + precision);
        System.out.println("Recall: " + recall);
        System.out.println("F1: " + (2 * recall * precision / (recall + precision)));
*/

        List<double[]> imdbData = ImdbDataReader.read("movie_metadata.csv");

        int validationSize = (int)(imdbData.size() * VALIDATION_PART);
        int trainSize = imdbData.size() - validationSize;
        Collections.shuffle(imdbData);
        List<double[]> trainData = imdbData.subList(0, trainSize);
        List<double[]> validationData = imdbData.subList(trainSize, imdbData.size());

        RegressionRandomForest forest = new RegressionRandomForest(trainData, RegressionDecisionTree.Criterion.MeanSquareError, 20, 10, 200);
        double rmse = RegressionQualifier.getRootMse(forest, trainData);
        System.out.println(String.format("Root Mean Square Error on train data: %s", rmse));
        rmse = RegressionQualifier.getRootMse(forest, validationData);
        System.out.println(String.format("Root Mean Square Error on validation data: %s", rmse));

        double sum = 0;
        int size = imdbData.get(0).length;
        for (int i = 0; i < imdbData.size(); i++) {
            sum += imdbData.get(i)[size - 1];
        }
        double mean = sum / imdbData.size();
        sum = 0;
        for (int i = 0; i < imdbData.size(); i++) {
            double score = imdbData.get(i)[size - 1];
            sum += (score - mean) * (score - mean);
        }
        double variation = sum / imdbData.size();
        double std = Math.sqrt(variation);
        System.out.println("Dataset: standard deviation = " + std + ", variation = " + variation);
        System.out.println("Explained variation: " + (variation - rmse * rmse) / (variation));
    }
}
