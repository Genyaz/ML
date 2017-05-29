package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ClassificationRandomForest {
    private ClassificationDecisionTree[] forest;
    private Random r = new Random();

    public ClassificationRandomForest(List<double[]> data, ClassificationDecisionTree.Criterion criterion, int maxDepth, int minSize, int nTrees) {
        forest = new ClassificationDecisionTree[nTrees];
        int dataSize = data.size();
        for (int i = 0; i < nTrees; i++) {
            List<double[]> treeData = new ArrayList<>();
            for (int j = 0; j < dataSize; j++) {
                treeData.add(data.get(r.nextInt(dataSize)));
            }
            forest[i] = new ClassificationDecisionTree(treeData, criterion, maxDepth);
            forest[i].naivePrune(minSize);
        }
    }

    public int classify(double[] x) {
        int[] classified = new int[2];
        for (int i = 0; i < forest.length; i++) {
            if (forest[i].classify(x) < 0) {
                classified[0]++;
            } else {
                classified[1]++;
            }
        }
        if (classified[0] > classified[1]) {
            return -1;
        } else {
            return 1;
        }
    }
}
