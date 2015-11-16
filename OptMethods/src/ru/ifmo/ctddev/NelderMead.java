package ru.ifmo.ctddev;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class NelderMead extends OptimizationMethod {
    private final double alpha, gamma, p, sigma, eps, diff;
    private final double[][] init;
    private ExecutorService executorService;
    private final int threads;

    public NelderMead(double alpha, double gamma, double p,
                      double sigma, double[][] init, double eps, double diff, int threads) {
        this.alpha = alpha;
        this.gamma = gamma;
        this.p = p;
        this.sigma = sigma;
        this.eps = eps;
        this.diff = diff;
        this.threads = threads;
        this.init = init;
    }

    @Override
    protected Point optimize(Function<double[], Double> evaluator, int arity, PrintStream out) {
        executorService = Executors.newFixedThreadPool(threads);
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(arity + 1, () -> {});
        Point[] points = new Point[arity + 1];
        for (int i = 0; i < arity + 1; i++) {
            double[] x = Arrays.copyOf(init[i], arity);
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
        points[arity].quality = evaluator.apply(points[arity].x);
        try {
            cyclicBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
            return null;
        }
        while (true) {
            Arrays.sort(points);
            if (points[arity].quality - points[0].quality < diff) break;
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
            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
                return null;
            }
        }
        executorService.shutdown();
        return points[arity];
    }

    @Override
    public String getName() {
        return "Nelder-Mead";
    }
}
