package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RegressionRandomForest {
	private RegressionDecisionTree[] forest;

	public RegressionRandomForest(List<double[]> data, RegressionDecisionTree.Criterion criterion, int maxDepth, int minSize, int nTrees) {
		forest = new RegressionDecisionTree[nTrees];
		int dataSize = data.size();
		Random r = new Random();
		for (int i = 0; i < nTrees; i++) {
			List<double[]> treeData = new ArrayList<>();
			for (int j = 0; j < dataSize; j++) {
				treeData.add(data.get(r.nextInt(dataSize)));
			}
			forest[i] = new RegressionDecisionTree(treeData, criterion, maxDepth);
			forest[i].naivePrune(minSize);
		}
	}

	public double predict(double[] x) {
		double sum = 0;
		for (RegressionDecisionTree tree : forest) {
			sum += tree.predict(x);
		}
		return sum / forest.length;
	}
}
