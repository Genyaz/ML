package ru.ifmo.ctddev.varlamov.ml.naivebayes;

import java.util.HashMap;
import java.util.Map;

public class Letter {
    public final Map<Integer, Integer> subject = new HashMap<>(), body = new HashMap<>();
    public final boolean spam;

    public Letter(boolean spam) {
        this.spam = spam;
    }

    public void addToSubject(int word) {
        if (!subject.containsKey(word)) {
            subject.put(word, 0);
        }
        subject.put(word, subject.get(word) + 1);
    }

    public void addToBody(int word) {
        if (!body.containsKey(word)) {
            body.put(word, 0);
        }
        body.put(word, body.get(word) + 1);
    }
}