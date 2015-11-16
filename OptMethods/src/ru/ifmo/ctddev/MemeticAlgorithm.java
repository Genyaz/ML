package ru.ifmo.ctddev;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

public class MemeticAlgorithm extends OptimizationMethod {

    private final double[][] boundaries;
    private final double mutationCoef, mutationRate, selection, elite, diff, indOptProb, indOptStep;
    private final int populationSize, indOptIter;
    private final Random r;

    public MemeticAlgorithm(double[][] boundaries, int populationSize,
            double mutationCoef, double mutationRate, double selection,
            double elite, double diff, double indOptProb, int indOptIter,
            double indOptStep) {
        this.boundaries = boundaries;
        this.populationSize = populationSize;
        this.mutationCoef = mutationCoef;
        this.mutationRate = mutationRate;
        this.selection = selection;
        this.elite = elite;
        this.diff = diff;
        this.indOptIter = indOptIter;
        this.indOptProb = indOptProb;
        this.indOptStep = indOptStep;
        this.r = new Random(System.currentTimeMillis());
    }

    protected Point chooseParent(Point[] breeding) {
        double sumQuality = 0;
        double minQuality = Double.MAX_VALUE;
        for (Point p : breeding) {
            sumQuality += p.quality;
            minQuality = Math.min(minQuality, p.quality);
        }
        sumQuality -= minQuality * breeding.length;
        double q = r.nextDouble() * sumQuality;
        int j = 0;
        sumQuality = 0;
        while (j < breeding.length - 1 && sumQuality + (breeding[j].quality - minQuality) < q) {
            sumQuality += breeding[j].quality - minQuality;
            j++;
        }
        return breeding[j];
    }

    protected double[] crossover(double[] x, double[] y) {
        int p1 = (int)(r.nextDouble() * x.length);
        int p2 = (int)(r.nextDouble() * x.length);
        if (p1 > p2) {
            int tmp = p1;
            p1 = p2;
            p2 = tmp;
        }
        double[] result = new double[x.length];
        for (int i = 0; i < p1; i++) {
            result[i] = x[i];
        }
        for (int i = p1; i < p2; i++) {
            result[i] = y[i];
        }
        for (int i = p2; i < x.length; i++) {
            result[i] = x[i];
        }
        return result;
    }

    protected double[] mutate(double[] x) {
        for (int i = 0; i < x.length; i++) {
            if (r.nextDouble() < mutationRate) {
                x[i] += mutationCoef * 2 * (r.nextDouble() - 1);
            }
        }
        return x;
    }

    @Override
    protected Point optimize(Function<double[], Double> evaluator, int arity, PrintStream out) {
        Point[] population = new Point[populationSize];
        for (int i = 0; i < populationSize; i++) {
            double[] x = new double[arity];
            for (int j = 0; j < arity; j++) {
                x[j] = (boundaries[j][1] - boundaries[j][0]) * r.nextDouble() + boundaries[j][0];
            }
            population[i] = new Point(x);
            population[i].quality = evaluator.apply(x);
        }
        final int eliteSize = (int)(elite * populationSize);
        final int selectionSize = (int)(selection * populationSize);
        while (true) {
            Arrays.sort(population);
            if (population[populationSize - 1].quality - population[0].quality < diff) {
                return population[populationSize - 1];
            }
            Point[] breeding = Arrays.copyOfRange(population, populationSize - selectionSize, populationSize);
            Point[] nextGen = new Point[populationSize];
            for (int i = 0; i < eliteSize; i++) {
                nextGen[i] = population[populationSize - eliteSize + i];
            }
            for (int i = 0; i < populationSize - eliteSize; i++) {
                Point parent1 = chooseParent(breeding), parent2 = chooseParent(breeding);
                double[] x = mutate(crossover(parent1.x, parent2.x));
                nextGen[eliteSize + i] = new Point(x);
                nextGen[eliteSize + i].quality = evaluator.apply(x);
            }
            population = nextGen;
            for (int i = 0; i < populationSize; i++) {
                if (r.nextDouble() < indOptProb) {
                    Point best = population[i];
                    for (int t = 0; t < indOptIter; t++) {
                        for (int j = 0; j < arity; j++) {
                            best.x[j] += indOptStep;
                            Point plusStep = new Point(best.x);
                            plusStep.quality = evaluator.apply(plusStep.x);
                            best.x[j] -= 2 * indOptStep;
                            Point minusStep = new Point(best.x);
                            minusStep.quality = evaluator.apply(minusStep.x);
                            best.x[j] += indOptStep;
                            if (plusStep.quality > best.quality) {
                                best = plusStep;
                            }
                            if (minusStep.quality > best.quality) {
                                best = minusStep;
                            }
                        }
                    }
                    population[i] = best;
                }
            }
        }
    }

    @Override
    public String getName() {
        return "Memetic algorithm";
    }
}
