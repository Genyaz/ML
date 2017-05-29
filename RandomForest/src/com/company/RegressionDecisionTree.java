package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegressionDecisionTree {
	public static enum Criterion {
		MeanSquareError {
			@Override
			public double calculate(double[] differences) {
				double result = 0;
				for (double d : differences) {
					result += d * d;
				}
				return result;
			}
		};
		public abstract double calculate(double[] differences);
	}

	private int variable;
	private double value;
	private RegressionDecisionTree left, right;
	private double average;
	private final int size;

	public RegressionDecisionTree(List<double[]> data, Criterion criterion, int maxDepth) {
		size = data.size();
		average = 0;
		for (double[] d : data) {
			average += d[d.length - 1];
		}
		average /= data.size();
		if (maxDepth == 0 || data.size() < 2) {
			left = null;
			right = null;
			variable = -1;
			value = 0;
		} else {
			int xSize = data.get(0).length - 1;
			double bestQuality = Double.MAX_VALUE;
			int bestVariable = -1;
			double bestValue = 0;
			for (int var = 0; var < xSize; var++) {
				final int position = var;
				Collections.sort(data, (o1, o2) -> {
					if (o1[position] < o2[position]) {
						return -1;
					} else if (o1[position] == o2[position]) {
						return 0;
					} else {
						return 1;
					}
				});
				double[] partialSums = new double[data.size()];
				partialSums[0] = data.get(0)[xSize];
				for (int i = 1; i < data.size(); i++) {
					partialSums[i] = partialSums[i - 1] + data.get(i)[xSize];
				}
				double[] d;
				double[] differences = new double[data.size()];
				for (int sepPoint = 0; sepPoint < data.size() - 1; sepPoint++) {
					double meanLeft = partialSums[sepPoint] / (sepPoint + 1);
					double meanRight = (partialSums[data.size() - 1] - partialSums[sepPoint]) / (data.size() - sepPoint - 1);
					d = data.get(sepPoint);
					for (int i = 0; i <= sepPoint; i++) {
						differences[i] = data.get(i)[xSize] - meanLeft;
					}
					for (int i = sepPoint + 1; i < data.size(); i++) {
						differences[i] = data.get(i)[xSize] - meanRight;
					}
					double quality = criterion.calculate(differences);
					if (quality < bestQuality) {
						bestQuality = quality;
						bestVariable = var;
						bestValue = d[var];
					}
				}
			}
			variable = bestVariable;
			value = bestValue;
			List<double[]> toLeft = new ArrayList<>();
			List<double[]> toRight = new ArrayList<>();
			for (double[] d : data) {
				if (d[variable] <= value) {
					toLeft.add(d);
				} else {
					toRight.add(d);
				}
			}
			left = new RegressionDecisionTree(toLeft, criterion, maxDepth - 1);
			right = new RegressionDecisionTree(toRight, criterion, maxDepth - 1);
		}
	}

	public void naivePrune(int minSize) {
		if (variable != -1) {
			if (left.size < minSize || right.size < minSize) {
				variable = -1;
				value = 0;
				left = null;
				right = null;
			} else {
				left.naivePrune(minSize);
				right.naivePrune(minSize);
			}
		}
	}

	public double predict(double[] x) {
		if (variable == -1) {
			return average;
		} else {
			if (x[variable] <= value) {
				return left.predict(x);
			} else {
				return right.predict(x);
			}
		}
	}
}
