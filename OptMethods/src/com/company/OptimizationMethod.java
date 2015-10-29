package com.company;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.function.Function;

public abstract class OptimizationMethod {
    public static class OptimizationResult {
        public Point result;
        public int totalIterations;

        public OptimizationResult(Point result, int totalIterations) {
            this.result = result;
            this.totalIterations = totalIterations;
        }
    }

    public static class Point implements Comparable<Point> {
        public double[] x;
        public double quality;

        public Point(double[] x) {
            this.x = Arrays.copyOf(x, x.length);
        }

        @Override
        public int compareTo(Point o) {
            if (this.quality < o.quality) {
                return -1;
            } else if (this.quality > o.quality) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public abstract OptimizationResult optimizeMetric(Function<double[], Double> evaluator, int arity, PrintStream out);

    public OptimizationResult optimizeMetric(Function<double[], Double> evaluator, int arity) {
        return optimizeMetric(evaluator, arity, null);
    }
}
