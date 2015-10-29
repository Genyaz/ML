package ru.ifmo.ctddev.ml.SVM;

import com.joptimizer.functions.ConvexMultivariateRealFunction;
import com.joptimizer.functions.LinearMultivariateRealFunction;
import com.joptimizer.functions.PDQuadraticMultivariateRealFunction;
import com.joptimizer.optimizers.JOptimizer;
import com.joptimizer.optimizers.OptimizationRequest;

import java.util.Arrays;

public class SVM {
    private double[][] trainset;
    private double[] lambdas;
    private final double b;
    private final Kernel k;
    private int m, n;

    private SVM(double[][] trainset, double[] lambdas, Kernel k) {
        this.m = trainset.length;
        this.n = trainset[0].length - 1;
        this.trainset = trainset;
        this.lambdas = lambdas;
        this.k = k;
        double b = 0;
        double bPos = 0, bNeg = 0;
        int bPosCount = 0, bNegCount = 0;
        for (int i = 0; i < m; i++) {
            double m = getMetric(Arrays.copyOf(trainset[i], n));
            if (trainset[i][n] > 0) {
                bPos += m;
                bPosCount++;
            } else {
                bNeg += m;
                bNegCount++;
            }
            b += m;
        }
        //this.b = b / m;
        this.b = (bPos / bPosCount + bNeg / bNegCount);
        //System.out.println("b = " + b / m);
    }

    public static SVM train(double[][] trainset, double c, Kernel k) throws Exception {
        double[] lambdas = new double[trainset.length];
        int n = trainset[0].length - 1;
        int m = trainset.length;
        int positive = 0;
        for (int i = 0; i < m; i++) {
            if (trainset[i][n] > 0) positive++;
        }
        double posCoef = c / positive;
        double negCoef = c / (m - positive);
        //System.out.println("Positive: " + positive);
        //System.out.println("Negative: " + (m - positive));
        //System.out.println("Positive coef: " + posCoef);
        //System.out.println("Negative coef: " + negCoef);
        for (int i = 0; i < m; i++) {
            if (trainset[i][n] > 0) {
                lambdas[i] = posCoef;
            } else {
                lambdas[i] = negCoef;
            }
        }
        double[][] matrix = new double[m][];
        for (int i = 0; i < m; i++) {
            matrix[i] = new double[m];
            for (int j = 0; j < m; j++) {
                matrix[i][j] = 0.5 * trainset[i][n] * trainset[j][n] * k.product(Arrays.copyOf(trainset[i], n), Arrays.copyOf(trainset[j], n));
            }
        }
        double[] q = new double[m];
        for (int i = 0; i < m; i++) {
            q[i] = -1;
        }
        PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(matrix, q, 0);
        LinearMultivariateRealFunction[] inequalities = new LinearMultivariateRealFunction[2 * m + 2];
        for (int i = 0; i < m; i++) {
            double[] v = new double[m];
            v[i] = -1;
            inequalities[2 * i] = new LinearMultivariateRealFunction(v, -1e-3);
            v = new double[m];
            v[i] = 1;
            inequalities[2 * i + 1] = new LinearMultivariateRealFunction(v, -c - 1e-3);
        }
        double[] v = new double[m];
        for (int i = 0; i < m; i++) {
            v[i] = trainset[i][n];
        }
        inequalities[2 * m] = new LinearMultivariateRealFunction(v, -1e-3);
        v = new double[m];
        for (int i = 0; i < m; i++) {
            v[i] = -trainset[i][n];
        }
        inequalities[2 * m + 1] = new LinearMultivariateRealFunction(v, -1e-3);
        OptimizationRequest or = new OptimizationRequest();
        or.setF0(objectiveFunction);
        or.setInitialPoint(lambdas);
        or.setFi(inequalities);
        or.setToleranceFeas(1.E-3);
        or.setTolerance(1.E-3);

        //optimization
        JOptimizer opt = new JOptimizer();
        opt.setOptimizationRequest(or);
        int returnCode = opt.optimize();
        lambdas = opt.getOptimizationResponse().getSolution();
        System.out.println("Objective function = " + objectiveFunction.value(lambdas));
        System.out.println("Lambdas: " + Arrays.toString(lambdas));
        /*double res = 0;
        for (int i = 0; i < m; i++) {
            res += trainset[i][n] * lambdas[i];
        }
        System.out.println("sum(li*yi) = " + res);
        res = 0;
        q = inequalities[2 * m].getQ().toArray();
        for (int i = 0; i < m; i++) {
            if (Math.abs(q[i] - trainset[i][n]) > 1e-3) System.err.println("ERROR!");
        }
        System.out.println("Ineq = " + inequalities[2 * m].value(lambdas));
        System.out.println(inequalities[2 * m].getQ().toString());
        System.out.println("Ineq = " + inequalities[2 * m + 1].value(lambdas));
        System.out.println(inequalities[2 * m + 1].getQ().toString());/**/
        return new SVM(trainset, lambdas, k);
    }

    private double getMetric(double[] x) {
        double result = 0;
        for (int i = 0; i < m; i++) {
            result += lambdas[i] * trainset[i][n] * k.product(Arrays.copyOf(x, x.length - 1), trainset[i]);
        }
        return result;
    }

    public double classify(double[] x) {
        if (getMetric(x) < b) {
            return -1;
        } else {
            return 1;
        }
    }
}