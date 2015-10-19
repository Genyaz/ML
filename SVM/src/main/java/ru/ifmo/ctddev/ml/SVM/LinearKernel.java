package ru.ifmo.ctddev.ml.SVM;

public class LinearKernel implements Kernel {
    @Override
    public double product(double[] x, double[] y) {
        double result = 0;
        for (int i = 0; i < x.length; i++) {
            result += x[i] * y[i];
        }
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x.length; j++) {
                result += (x[i] * x[j] * y[i] * y[j]);
            }
        }
        return result;
    }
}
