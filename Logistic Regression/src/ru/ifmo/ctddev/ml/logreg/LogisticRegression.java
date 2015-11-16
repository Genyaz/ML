package ru.ifmo.ctddev.ml.logreg;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class LogisticRegression {
    private final double[] theta;
    private double threshold;

    private LogisticRegression(double[] theta, double threshold) {
        this.theta = theta;
        this.threshold = threshold;
    }

    private static double sigmoid(double[] x, double[] theta) {
        double sum = 0;
        for (int i = 0; i < x.length; i++) {
            sum += x[i] * theta[i];
        }
        return 1.0 / (1 + Math.exp(-sum));
    }

    public static LogisticRegression train(double[][] data, double alpha, double eps, double lambda) {
        int n = data[0].length - 1;
        int m = data.length;
        double[] theta = new double[n], x;
        double y, sigma;
        while (true) {
            double[] grad = new double[n];
            // Regularization without bias
            for (int j = 0; j < n - 1; j++) {
                grad[j] = lambda * theta[j];
            }
            for (int i = 0; i < m; i++) {
                x = data[i];
                y = x[n];
                x = Arrays.copyOf(x, n);
                sigma = sigmoid(x, theta);
                for (int j = 0; j < n; j++) {
                    grad[j] += (sigma - y) * x[j];
                }
            }
            //System.out.println(Arrays.toString(grad));
            double module = 0;
            for (int j = 0; j < n - 1; j++) {
                module += grad[j] * grad[j];
            }
            //System.out.println(module);
            if (module < eps * eps) break;
            for (int j = 0; j < n; j++) {
                theta[j] -= alpha * grad[j];
            }
        }
        System.out.println(Arrays.toString(theta));
        return new LogisticRegression(theta, 0.5);
    }

    private static int classify(double[] x, double[] theta, double threshold) {
        if (sigmoid(x, theta) > threshold) {
            return 1;
        } else {
            return 0;
        }
    }

    public int classify(double[] x) {
        return classify(x, theta, threshold);
    }
}
