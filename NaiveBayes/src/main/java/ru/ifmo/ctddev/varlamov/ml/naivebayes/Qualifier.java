package ru.ifmo.ctddev.varlamov.ml.naivebayes;

import java.util.List;

public class Qualifier {
    private static final int FALSE_POSITIVE = 30, FALSE_NEGATIVE = 1;
    public static double getError(NaiveBayes nb, List<Letter> test) {
        int error = 0;
        for (Letter letter: test) {
            if (nb.check(letter) != letter.spam) {
                if (letter.spam) {
                    error += FALSE_NEGATIVE;
                } else {
                    error += FALSE_POSITIVE;
                }
            }
        }
        return error * 1.0 / test.size();
    }
}
