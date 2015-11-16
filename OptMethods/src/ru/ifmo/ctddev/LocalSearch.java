package ru.ifmo.ctddev;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.function.Function;

public class LocalSearch extends OptimizationMethod {
    private double step;
    private double[] init;

    public LocalSearch(double step, double[] init) {
        this.init = Arrays.copyOf(init, init.length);
        this.step = step;
    }

    @Override
    protected Point optimize(Function<double[], Double> evaluator, int arity, PrintStream out) {
        Point best = new Point(init);
        best.quality = evaluator.apply(init);
        boolean improved = true;
        while (improved) {
            improved = false;
            for (int i = 0; i < arity; i++) {
                best.x[i] += step;
                Point plusStep = new Point(best.x);
                plusStep.quality = evaluator.apply(plusStep.x);
                best.x[i] -= 2 * step;
                Point minusStep = new Point(best.x);
                minusStep.quality = evaluator.apply(minusStep.x);
                best.x[i] += step;
                if (plusStep.quality > best.quality) {
                    best = plusStep;
                    improved = true;
                }
                if (minusStep.quality > best.quality) {
                    best = minusStep;
                    improved = true;
                }
            }
        }
        return best;
    }

    @Override
    public String getName() {
        return "Local search";
    }
}
