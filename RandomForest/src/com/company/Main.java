package com.company;

import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
	    List<double[]> trainData = DataReader.read("arcene_train");
        List<double[]> validationData = DataReader.read("arcene_valid");

        /*ClassificationRandomForest rf = new ClassificationRandomForest(trainData, ClassificationDecisionTree.Criterion.InformationGain, 10, 5, 20);
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

        RegressionRandomForest forest = new RegressionRandomForest(trainData, RegressionDecisionTree.Criterion.MeanSquareError, 10, 5,  20);
        double rmse = RegressionQualifier.getRootMse(forest, trainData);
        System.out.println(String.format("Root Mean Square Error on train data: %s", rmse));
        rmse = RegressionQualifier.getRootMse(forest, validationData);
        System.out.println(String.format("Root Mean Square Error on validation data: %s", rmse));
    }
}
