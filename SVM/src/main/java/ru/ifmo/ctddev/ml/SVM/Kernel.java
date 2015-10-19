package ru.ifmo.ctddev.ml.SVM;

public interface Kernel {
    double product(double[] x, double[] y);
}
