package ru.ifmo.ctddev;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class GlowwormSwarm extends OptimizationMethod {
    protected class Glowworm extends Point {
        public double luciferin = l0;
        protected double visibility;

        public Glowworm(double[] x) {
            super(x);
            this.visibility = v0;
        }

        public double distTo(Glowworm g) {
            double sqrDist = 0;
            for (int i = 0; i < x.length; i++) {
                sqrDist += (x[i] - g.x[i]) * (x[i] - g.x[i]);
            }
            return Math.sqrt(sqrDist);
        }

        public void updateLuciferin() {
            luciferin = luciferin * (1 - p) + gamma * quality;
        }

        protected Glowworm selectNeighbour(Glowworm[] swarm) {
            List<Glowworm> neighbours = new ArrayList<>();
            double sum = 0;
            for (Glowworm g: swarm) if (g.quality < quality) {
                double d = distTo(g);
                if (d > 0 && d <= visibility) {
                    neighbours.add(g);
                    sum += quality - g.quality;
                }
            }
            double q = r.nextDouble() * sum;
            int j = 0;
            sum = 0;
            while (j < neighbours.size() - 1 && sum + quality - neighbours.get(j).quality < q) {
                sum += quality - neighbours.get(j).quality;
                j++;
            }
            updateVisibility(neighbours);
            if (neighbours.size() > 0) {
                return neighbours.get(j);
            } else {
                return null;
            }
        }

        public void moveToNeighbour(Glowworm[] swarm) {
            Glowworm g = selectNeighbour(swarm);
            if (g != null) {
                double d = distTo(g);
                for (int i = 0; i < x.length; i++) {
                    x[i] += step * (g.x[i] - x[i]) / d;
                }
            } else {
                double[] m = randomMove(x.length);
                for (int i = 0; i < x.length; i++) {
                    x[i] += m[i];
                }
            }
        }

        public void updateVisibility(List<Glowworm> neighbour) {
            visibility = Math.min(maxV, Math.max(visibility, beta * (nt - neighbour.size())));
        }
    }

    private final double l0, v0, maxV, step, nt, p, beta, gamma, diff;
    private final double[][] boundaries;
    private final int swarmSize, maxIterations;
    private Random r;

    public GlowwormSwarm(double[][] boundaries,int swarmSize, int maxIterations,
            double l0, double v0, double maxV, double step, double nt, double p,
            double beta, double gamma, double diff) {
        this.l0 = l0;
        this.v0 = v0;
        this.maxV = maxV;
        this.step = step;
        this.nt = nt;
        this.p = p;
        this.beta = beta;
        this.gamma = gamma;
        this.boundaries = boundaries;
        this.swarmSize = swarmSize;
        this.maxIterations = maxIterations;
        this.diff = diff;
    }

    protected double[] randomMove(int arity) {
        double[] result = new double[arity];
        for (int i = 0; i < arity; i++) {
            result[i] = (r.nextDouble() * 2 - 1) * step / 2;
        }
        return result;
    }

    @Override
    protected Point optimize(Function<double[], Double> evaluator, int arity, PrintStream out) {
        r = new Random(System.currentTimeMillis());
        Glowworm[] swarm = new Glowworm[swarmSize];
        for (int i = 0; i < swarmSize; i++) {
            double[] x = new double[arity];
            for (int j = 0; j < arity; j++) {
                x[j] = r.nextDouble() * (boundaries[j][1] - boundaries[j][0]) + boundaries[j][0];
            }
            swarm[i] = new Glowworm(x);
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
                swarm[i].moveToNeighbour(swarm);
            }
            for (int i = 0; i < swarmSize; i++) {
                swarm[i].quality = evaluator.apply(swarm[i].x);
                swarm[i].updateLuciferin();
            }
        }
        Arrays.sort(swarm);
        return swarm[0];
    }

    @Override
    public String getName() {
        return "Glowworm swarm";
    }
}
