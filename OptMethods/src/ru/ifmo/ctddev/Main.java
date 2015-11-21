package ru.ifmo.ctddev;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Main {

    public static void main(String[] args) {
        List<Function<double[], Double>> testFunctions = new ArrayList<>();
        testFunctions.add(new Function<double[], Double>() {
            @Override
            public Double apply(double[] doubles) {
                double x = doubles[0], y = doubles[1];
                return ((1 - x) * (1 - x) + 100 * (y - x * x) * (y - x * x));
            }
        });/**/
        testFunctions.add(new Function<double[], Double>() {
            @Override
            public Double apply(double[] doubles) {
                double x = doubles[0], y = doubles[1];
                return ((x * x + y - 11) * (x * x + y - 11) + (x + y * y - 7) * (x + y * y - 7));
            }
        });/**/
        testFunctions.add(new Function<double[], Double>() {
            @Override
            public Double apply(double[] doubles) {
                double x = doubles[0], y = doubles[1];
                return (x * x + y * y);
            }
        });/**/
        testFunctions.add(new Function<double[], Double>() {
            @Override
            public Double apply(double[] doubles) {
                double x = doubles[0] * 3, y = doubles[1] * 3;
                return (Math.sin(x + y) + (x - y) * (x - y) - 1.5 * x + 2.5 * y + 1);
            }
        });/**/
        testFunctions.add(new Function<double[], Double>() {
            @Override
            public Double apply(double[] doubles) {
                double x = doubles[0] * 5, y = doubles[1] * 5;
                return (Math.pow(x, 4) + Math.pow(y, 4) - 16 * (Math.pow(x, 2) + Math.pow(y, 2)) + 5 * (x + y));
            }
        });
        List<OptimizationMethod> optimizationMethods = new ArrayList<>();
        optimizationMethods.add(new NelderMead(1, 2, -0.5, 0.5, new double[][]{{-8, 8}, {-8, -8}, {16, 0}}, 0.001, 0.05, 8));
        optimizationMethods.add(new LocalSearch(0.1, new double[]{2, 2}));
        optimizationMethods.add(new ParticleSwarm(0.6, 0.4, 1.4, new double[][]{{-8, 8}, {-8, 8}}, 4, 0.1));
        optimizationMethods.add(new GeneticAlgorithm(new double[][]{{-8, 8}, {-8, 8}}, 20, 0.1, 0.1, 0.5, 0.1, 0.1));
        optimizationMethods.add(new MemeticAlgorithm(new double[][]{{-8, 8}, {-8, 8}}, 20, 0.1, 0.1, 0.5, 0.1, 0.1, 0.5, 1, 1));
        optimizationMethods.add(new FireflyAlgorithm(new double[][]{{-8, 8}, {-8, 8}}, 20, 15, 0.1, 1, 1, 0.1));
        optimizationMethods.add(new GlowwormSwarm(new double[][]{{-8, 8}, {-8, 8}}, 20, 15, 0, 2, 8, 0.1, 5, 0.5, 2, 1, 0.1));
        for (Function<double[], Double> f : testFunctions) {
            for (OptimizationMethod om : optimizationMethods) {
                OptimizationMethod.OptimizationResult result = om.getOptimization(f, 2);
                System.out.println(om.getName());
                System.out.println(Arrays.toString(result.result.x));
                System.out.println(result.result.quality);
                System.out.println(result.totalIterations);
            }
            System.out.println();
        }
    }
}
