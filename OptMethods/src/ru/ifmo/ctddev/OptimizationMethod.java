package ru.ifmo.ctddev;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public abstract class OptimizationMethod {
    public static class OptimizationResult {
        public Point result;
        public int totalIterations;
        public List<double[]> log;

        protected OptimizationResult(Point result, EvaluatorProxy proxy) {
            this.result = result;
            this.totalIterations = proxy.counter;
            this.log = proxy.callsLog;
        }
    }

    protected static class EvaluatorProxy implements Function<double[], Double> {
        private final Function<double[], Double> function;
        private List<double[]> callsLog = new ArrayList<>();
        private int counter = 0;

        public EvaluatorProxy(Function<double[], Double> function) {
            this.function = function;
        }

        @Override
        public Double apply(double[] doubles) {
            synchronized (this) {
                counter++;
                callsLog.add(Arrays.copyOf(doubles, doubles.length));
            }
            return function.apply(doubles);
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

    protected abstract Point optimize(Function<double[], Double> evaluator, int arity, PrintStream out);

    public OptimizationResult getOptimization(Function<double[], Double> evaluator, int arity, PrintStream out) {
        EvaluatorProxy evaluatorProxy = new EvaluatorProxy(evaluator);
        Point point = optimize(evaluatorProxy, arity, out);
        return new OptimizationResult(point, evaluatorProxy);
    }

    public OptimizationResult getOptimization(Function<double[], Double> evaluator, int arity) {
        return getOptimization(evaluator, arity, System.out);
    }
}
