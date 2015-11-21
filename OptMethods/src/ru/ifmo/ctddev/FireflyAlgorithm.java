package ru.ifmo.ctddev;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Function;

public class FireflyAlgorithm extends OptimizationMethod {

    protected static class Firefly extends Point {
        public Firefly(double[] x) {
            super(x);
        }

        public double distTo(Firefly f) {
            double sqrDist = 0;
            for (int i = 0; i < x.length; i++) {
                sqrDist += (x[i] - f.x[i]) * (x[i] - f.x[i]);
            }
            return Math.sqrt(sqrDist);
        }
    }

    private final double alpha, beta, gamma, diff;
    private final double[][] boundaries;
    private final int swarmSize, maxIterations;
    private Random r;

    protected double[] randomMove(int arity) {
        double[] result = new double[arity];
        for (int i = 0; i < arity; i++) {
            result[i] = (r.nextDouble() * 2 - 1) * alpha;
        }
        return result;
    }

    public FireflyAlgorithm(double[][] boundaries,int swarmSize, int maxIterations,
            double alpha, double beta, double gamma, double diff) {
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
        this.boundaries = boundaries;
        this.swarmSize = swarmSize;
        this.maxIterations = maxIterations;
        this.diff = diff;
    }

    @Override
    protected Point optimize(Function<double[], Double> evaluator, int arity, PrintStream out) {
        r = new Random(System.currentTimeMillis());
        Firefly[] swarm = new Firefly[swarmSize];
        for (int i = 0; i < swarmSize; i++) {
            double[] x = new double[arity];
            for (int j = 0; j < arity; j++) {
                x[j] = r.nextDouble() * (boundaries[j][1] - boundaries[j][0]) + boundaries[j][0];
            }
            swarm[i] = new Firefly(x);
            swarm[i].quality = evaluator.apply(x);
        }
        for (int it = 0; it < maxIterations; it++) {
            double min = swarm[0].quality, max = swarm[0].quality;
            for (int i = 1; i < swarmSize; i++) {
                min = Math.min(min, swarm[i].quality);
                max = Math.max(max, swarm[i].quality);
            }
            if (max - min < diff) break;
            for (int i = 0; i < swarmSize; i++) {
                for (int j = 0; j <= i; j++) {
                    Firefly brighter = swarm[i], darker = swarm[j];
                    if (swarm[i].quality > swarm[j].quality) {
                        brighter = swarm[j];
                        darker = swarm[i];
                    }
                    double attraction = Math.exp(-gamma * swarm[i].distTo(swarm[j])) * beta;
                    double[] move = randomMove(arity);
                    for (int k = 0; k < arity; k++) {
                        darker.x[k] += attraction * (brighter.x[k] - darker.x[k]) + move[k];
                        darker.x[k] = Math.min(boundaries[k][1], Math.max(darker.x[k], boundaries[k][0]));
                    }
                }
            }
            for (int i = 0; i < swarmSize; i++) {
                swarm[i].quality = evaluator.apply(swarm[i].x);
            }
        }
        Arrays.sort(swarm);
        return swarm[0];
    }

    @Override
    public String getName() {
        return "Firefly algorithm";
    }
}
