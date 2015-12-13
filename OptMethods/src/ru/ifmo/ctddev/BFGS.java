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

    public BFGS(double[] init, double eps) {
        this.init = init;
        this.eps = eps;
    }

    public BFGS() {
        this(new double[] {2, 3}, 1e-3);
    }

    @Override
    protected Point minimize(final Function<double[], Double> evaluator, int arity, PrintStream out) {
        Function<double[], Double> ev = doubles -> -evaluator.apply(doubles);
        SmoothPoint point = new SmoothPoint(init);
        point.quality = ev.apply(point.x);
        point.grad = getGradient(ev, point);
        double[] diag = new double[arity];
        int[] iflag = new int[1];
        while (true) {
            try {
                LBFGSCP.lbfgs(arity, 3, point.x, point.quality, point.grad, false, diag, new int[] {-1, 0}, 1e-3, 1e-32, iflag);
                double quality = ev.apply(point.x);
                if (quality - point.quality < eps) return point;
                point.quality = quality;
                point.grad = getGradient(ev, point);
            } catch (LBFGSCP.ExceptionWithIflag exceptionWithIflag) {
                return point;
            }
        }
    }

    @Override
    public String getName() {
        return "Berndt–Hall–Hall–Hausman";
    }
}
