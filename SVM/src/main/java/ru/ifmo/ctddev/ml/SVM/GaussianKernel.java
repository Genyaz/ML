package ru.ifmo.ctddev.ml.SVM;

public class GaussianKernel implements Kernel {
    private final double sigma;

    public GaussianKernel(double sigma) {
        this.sigma = sigma;
    }

    @Override
    public double product(double[] x, double[] y) {
        double e = 0;
        for (int i = 0; i < x.length; i++) {
            e += (x[i] - y[i]) * (x[i] - y[i]);
        }
        return Math.exp(-e / (2 * sigma * sigma));
    }
}
