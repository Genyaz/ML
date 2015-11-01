package ru.ifmo.ctddev;

import java.util.Arrays;
import java.util.function.Function;

public class Main {

    public static void main(String[] args) {
        Function<double[], Double> evaluator;
	    evaluator = new Function<double[], Double>() {
            @Override
            public Double apply(double[] doubles) {
                double x = doubles[0], y = doubles[1];
                return -((1 - x) * (1 - x) + 100 * (y - x * x) * (y - x * x));
            }
        };/**/
        evaluator = new Function<double[], Double>() {
            @Override
            public Double apply(double[] doubles) {
                double x = doubles[0], y = doubles[1];
                return -((x * x + y - 11) * (x * x + y - 11) + (x + y * y - 7) * (x + y * y - 7));
            }
        };/**/
        /*evaluator = new Function<double[], Double>() {
            @Override
            public Double apply(double[] doubles) {
                double x = doubles[0], y = doubles[1];
                return -(x * x + y * y);
            }
        };/**/
        /*evaluator = new Function<double[], Double>() {
            @Override
            public Double apply(double[] doubles) {
                double x = doubles[0] * 3, y = doubles[1] * 3;
                return -(Math.sin(x + y) + (x - y) * (x - y) - 1.5 * x + 2.5 * y + 1);
            }
        };/**/
        NelderMead nm = new NelderMead(1, 2, -0.5, 0.5, new double[][]{{-8, 8}, {-8, -8}, {16, 0}}, 0.001, 0.05, 8);
        OptimizationMethod.OptimizationResult result = nm.getOptimization(evaluator, 2);
        System.out.println(Arrays.toString(result.result.x));
        System.out.println(result.result.quality);
        System.out.println(result.totalIterations);
        ParticleSwarm ps = new ParticleSwarm(0.6, 0.4, 1.4, new double[][]{{-8, 8}, {-8, 8}}, 4, 0.1);
        result = ps.getOptimization(evaluator, 2);
        System.out.println(Arrays.toString(result.result.x));
        System.out.println(result.result.quality);
        System.out.println(result.totalIterations);
    }
}
