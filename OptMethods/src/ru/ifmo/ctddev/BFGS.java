package ru.ifmo.ctddev;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.function.Function;

public class BFGS extends OptimizationMethod {

    protected class SmoothPoint extends Point {

        public double[] grad;

        public SmoothPoint(double[] x) {
            super(x);
        }
    }

    private final double[] init;
    private final double eps;

    protected double[] getGradient(Function<double[], Double> evaluator, Point point) {
        int arity = point.x.length;
        double[] grad = new double[arity];
        for (int i = 0; i < arity; i++) {
            point.x[i] += eps;
            grad[i] = (evaluator.apply(point.x) - point.quality) / eps;
            point.x[i] -= eps;
        }
        return grad;
    }

    protected double[] getDirection(double[][] b, double[] grad) {
        int arity = grad.length;
        double[] result = new double[arity];
        for (int i = 0; i < arity; i++) {
            for (int j = 0; j < arity; j++) {
                result[i] -= b[i][j] * grad[j];
            }
        }
        return result;
    }

    protected SmoothPoint getNextPoint(SmoothPoint p, double[] direction, Function<double[], Double> evaluator,
            double maxStep, double tau, double c, double m) {
        double step = maxStep;
        double t = -c * m;
        int arity = direction.length;
        double[] newPoint = new double[arity];
        while (true) {
            for (int i = 0; i < arity; i++) {
                newPoint[i] = p.x[i] + maxStep * direction[i];
            }
            double quality = evaluator.apply(newPoint);
            if (p.quality - quality >= step * t) {
                SmoothPoint result = new SmoothPoint(newPoint);
                result.quality = quality;
                return result;
            } else {
                step = step * tau;
            }
        }
    }

    public BFGS(double[] init, double eps) {
        this.init = init;
        this.eps = eps;
    }

    @Override
    protected Point optimize(Function<double[], Double> evaluator, int arity, PrintStream out) {
        // B0 = I
        double[][] b = new double[arity][arity];
        for (int i = 0; i < arity; i++) {
            b[i][i] = 1;
        }
        //x0 = init
        SmoothPoint p = new SmoothPoint(Arrays.copyOf(init, arity));
        p.quality = evaluator.apply(p.x);
        p.grad = getGradient(evaluator, p);
        while (true) {
            double m = 0;
            double[] direction = getDirection(b, p.grad);
            for (int i = 0; i < arity; i++) {
                m += p.grad[i] * direction[i];
            }
            SmoothPoint newPoint = getNextPoint(p, direction, evaluator, 1, 0.5, 0.5, m);
            newPoint.grad = getGradient(evaluator, newPoint);
        }
    }

    @Override
    public String getName() {
        return "Berndt–Hall–Hall–Hausman";
    }
}
