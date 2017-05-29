package com.company;

import java.util.List;

public class RegressionQualifier {
	public static double getRootMse(RegressionRandomForest regressionRandomForest, List<double[]> test) {
		double sum = 0, diff;
		for (double[] x: test) {
			diff = regressionRandomForest.predict(x) - x[x.length - 1];
			sum += diff * diff;
		}
		return Math.sqrt(sum / test.size());
	}
}
