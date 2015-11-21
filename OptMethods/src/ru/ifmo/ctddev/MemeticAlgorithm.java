package ru.ifmo.ctddev;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.function.Function;

public class MemeticAlgorithm extends GeneticAlgorithm {

    protected final double indOptProb, indOptStep;
    protected final int indOptIter;

    public MemeticAlgorithm(double[][] boundaries, int populationSize,
            double mutationCoef, double mutationRate, double selection,
            double elite, double diff, double indOptProb, int indOptIter,
            double indOptStep) {
        super(boundaries, populationSize, mutationCoef, mutationRate, selection, elite, diff);
        this.indOptIter = indOptIter;
        this.indOptProb = indOptProb;
        this.indOptStep = indOptStep;
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
                return population[0];
            }
            Point[] breeding = Arrays.copyOfRange(population, 0, selectionSize);
            Point[] nextGen = new Point[populationSize];
            for (int i = 0; i < eliteSize; i++) {
                nextGen[i] = population[i];
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
                            if (plusStep.quality < best.quality) {
                                best = plusStep;
                            }
                            if (minusStep.quality < best.quality) {
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
