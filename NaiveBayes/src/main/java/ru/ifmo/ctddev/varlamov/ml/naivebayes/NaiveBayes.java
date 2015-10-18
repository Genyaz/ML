package ru.ifmo.ctddev.varlamov.ml.naivebayes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NaiveBayes {
    private static final double THRESHOLD = 0.999;
    private static final int SUBJECT_COEF = 1;
    private static final double STUB_FOR_MISSING = -45;
    private final Map<Integer, Double> logSpam = new HashMap<>(), logNonSpam = new HashMap<>();
    private final double logSpamRate;

    private NaiveBayes(Map<Integer, Double> spam, Map<Integer, Double> nonSpam,double spamRate) {
        for (Integer word: spam.keySet()) {
            logSpam.put(word, Math.log(spam.get(word)));
        }
        for (Integer word: nonSpam.keySet()) {
            logNonSpam.put(word, Math.log(nonSpam.get(word)));
        }
        this.logSpamRate = Math.log(spamRate);
    }

    public static NaiveBayes train(List<Letter> data) {
        int spamCount = 0;
        Map<Integer, Double> spam = new HashMap<>(), nonSpam = new HashMap<>();
        for (Letter letter: data) {
            if (letter.spam) spamCount++;
            Map<Integer, Double> toAdd = spam;
            if (!letter.spam) toAdd = nonSpam;
            for (Integer word: letter.subject.keySet()) {
                if (!toAdd.containsKey(word)) {
                    toAdd.put(word, 0d);
                }
                toAdd.put(word, toAdd.get(word) + letter.subject.get(word) * SUBJECT_COEF);
            }
            for (Integer word: letter.body.keySet()) {
                if (!toAdd.containsKey(word)) {
                    toAdd.put(word, 0d);
                }
                toAdd.put(word, toAdd.get(word) + letter.body.get(word));
            }
        }
        for (Integer word: spam.keySet()) {
            spam.put(word, spam.get(word) / spamCount);
        }
        for (Integer word: nonSpam.keySet()) {
            nonSpam.put(word, nonSpam.get(word) / (data.size() - spamCount));
        }
        double spamRate = spamCount * 1.0 / data.size();
        return new NaiveBayes(spam, nonSpam, spamRate);
    }

    public boolean check(Letter letter) {
        double spam = logSpamRate, nonSpam = Math.log(1 - Math.exp(logSpamRate));
        for (Integer word: letter.subject.keySet()) {
            if (logSpam.containsKey(word)) {
                spam += SUBJECT_COEF * letter.subject.get(word) * logSpam.get(word);
            } else {
                spam += SUBJECT_COEF * letter.subject.get(word) * STUB_FOR_MISSING;
            }
            if (logNonSpam.containsKey(word)) {
                nonSpam += SUBJECT_COEF * letter.subject.get(word) * logNonSpam.get(word);
            } else {
                nonSpam += SUBJECT_COEF * letter.subject.get(word) * STUB_FOR_MISSING;
            }
        }
        for (Integer word: letter.body.keySet()) {
            if (logSpam.containsKey(word)) {
                spam += letter.body.get(word) * logSpam.get(word);
            } else {
                spam += letter.body.get(word) * STUB_FOR_MISSING;
            }
            if (logNonSpam.containsKey(word)) {
                nonSpam += letter.body.get(word) * logNonSpam.get(word);
            } else {
                nonSpam += letter.body.get(word) * STUB_FOR_MISSING;
            }
        }
        double prob = Math.exp(spam - nonSpam) / (1.0 + Math.exp(spam - nonSpam));
        return prob > THRESHOLD;
    }
}
