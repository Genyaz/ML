package com.company;

import java.util.Arrays;
import java.util.List;

public class Qualifier {
    public static class Error {
        public int tp, tn, fp, fn;

        public Error(int tp, int tn, int fp, int fn) {
            this.tp = tp;
            this.tn = tn;
            this.fp = fp;
            this.fn = fn;
        }

        public Error add(Error error) {
            return new Error(this.tp + error.tp, this.tn + error.tn, this.fp + error.fp, this.fn + error.fn);
        }
    }
    public static Error getError(RandomForest randomForest, List<double[]> test) {
        int tp = 0, tn = 0, fn = 0, fp = 0;
        for (double[] x: test) {
            if (x[x.length - 1] < 0) {
                if (randomForest.classify(x) < 0) {
                    tn++;
                } else {
                    fp++;
                }
            } else {
                if (randomForest.classify(x) < 0) {
                    fn++;
                } else {
                    tp++;
                }
            }
        }
        return new Error(tp, tn, fp, fn);
    }
}