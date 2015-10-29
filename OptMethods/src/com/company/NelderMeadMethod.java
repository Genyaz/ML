package com.company;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class NelderMeadMethod extends OptimizationMethod {

    private static double[] transform(double[] coord) {
        int n = coord.length + 1;
        double[] result = new double[n];
        double sum = 0;
        for (int i = 0; i < n - 1; i++) {
            sum += coord[i];
        }
        for (int i = 0; i < n - 1; i++) {
            result[i] = (1.0 + coord[i] * (n - 1) - (sum - coord[i])) / n;
        }
        result[n - 1] = (1.0 - sum) / n;
        return result;
    }

    private boolean transform;
    private double alpha, gamma, p, sigma, eps, diff;
    private ExecutorService executorService;

    public NelderMeadMethod(boolean transform, double alpha, double gamma, double p, double sigma, double eps, double diff) {
        this.transform = transform;
        this.alpha = alpha;
        this.gamma = gamma;
        this.p = p;
        this.sigma = sigma;
        this.eps = eps;
        this.diff = diff;
    }


    @Override
    public OptimizationResult optimizeMetric(Function<double[], Double> evaluator, int arity, PrintStream out) {
        executorService = Executors.newFixedThreadPool(arity);
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(arity + 1, () -> {});
        Point[] points = new Point[arity + 1];
        for (int i = 0; i < arity; i++) {
            double[] x = new double[arity];
            x[i] = 1;
            points[i] = new Point(x);
        }
        for (int i = 0; i < arity; i++) {
            final Point p = points[i];
            executorService.execute(() -> {
                p.quality = evaluator.apply(p.x);
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }
        double[] x = new double[arity];
        Arrays.fill(x, -1.0 / arity);
        points[arity] = new Point(x);
        points[arity].quality = evaluator.apply(x);
        try {
            cyclicBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
            return null;
        }
        int iterations = arity + 1;
        while (true) {
            Arrays.sort(points);
            //if (points[arity].quality - points[0].quality < diff) break;
            double maxDist = 0, dist;
            for (int i = 0; i < arity + 1; i++) {
                for (int j = i + 1; j < arity + 1; j++) {
                    dist = 0;
                    for (int k = 0; k < arity; k++) {
                        dist += (points[i].x[k] - points[j].x[k]) * (points[i].x[k] - points[j].x[k]);
                    }
                    if (dist > maxDist) {
                        maxDist = dist;
                    }
                }
            }
            if (maxDist < eps * eps) break;
            double[] cm = new double[arity];
            for (int i = 1; i < arity + 1; i++) {
                for (int j = 0; j < arity; j++) {
                    cm[j] += points[i].x[j];
                }
            }
            for (int j = 0; j < arity; j++) {
                cm[j] /= arity;
            }
            double[] refl = new double[arity];
            for (int j = 0; j < arity; j++) {
                refl[j] = (1 + alpha) * points[0].x[j] - alpha * cm[j];
            }
            Point reflected = new Point(refl);
            reflected.quality = evaluator.apply(refl);
            iterations++;
            if (reflected.quality > points[1].quality && reflected.quality <= points[arity].quality) {
                points[0] = reflected;
                continue;
            }
            if (reflected.quality > points[arity].quality) {
                double[] exp = new double[arity];
                for (int j = 0; j < arity; j++) {
                    exp[j] = (1 + gamma) * points[0].x[j] - gamma * cm[j];
                }
                Point expanded = new Point(exp);
                expanded.quality = evaluator.apply(exp);
                iterations++;
                if (expanded.quality > reflected.quality) {
                    points[0] = expanded;
                } else {
                    points[0] = reflected;
                }
                continue;
            }
            double[] cont = new double[arity];
            for (int j = 0; j < arity; j++) {
                cont[j] = (1 + p) * points[0].x[j] - p * cm[j];
            }
            Point contracted = new Point(cont);
            contracted.quality = evaluator.apply(cont);
            iterations++;
            if (contracted.quality > points[0].quality) {
                points[0] = contracted;
                continue;
            }
            double[] reductionCenter = points[arity].x;
            for (int i = 0; i < arity; i++) {
                for (int j = 0; j < arity; j++) {
                    points[i].x[j] = (1 - sigma) * reductionCenter[j] + sigma * points[i].x[j];
                }
            }
            for (int i = 0; i < arity; i++) {
                final Point p = points[i];
                executorService.execute(() -> {
                    p.quality = evaluator.apply(p.x);
                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                });
            }
            iterations += arity;
            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
                return null;
            }
        }
        executorService.shutdown();
        return new OptimizationResult(points[arity], iterations);
    }
}
