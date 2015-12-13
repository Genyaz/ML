package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DecisionTree {
    public static enum Criterion {
        Gini {
            @Override
            public double calculate(int[] frequencies) {
                double result = 0;
                int sum = 0;
                for (int f : frequencies) {
                    sum += f;
                }
                for (int f : frequencies) {
                    double fd = f * 1.0 / sum;
                    result += fd * (1 - fd);
                }
                return result;
            }
        },
        InformationGain {
            @Override
            public double calculate(int[] frequencies) {
                double result = 0;
                double sum = 0;
                for (int f : frequencies) {
                    sum += f;
                }
                for (int f : frequencies) {
                    if (f > 0) {
                        double fd = f * 1.0 / sum;
                        result -= fd * Math.log(fd);
                    }
                }
                return result;
            }
        };
        public abstract double calculate(int[] frequencies);
    }

    private int variable;
    private double value;
    private DecisionTree left, right;
    private final int[] classified;
    private final int size;

    public DecisionTree(List<double[]> data, Criterion criterion, int maxDepth) {
        size = data.size();
        classified = new int[2];
        for (double[] d : data) {
            if (d[d.length - 1] < 0) {
                classified[0]++;
            } else {
                classified[1]++;
            }
        }
        if (maxDepth == 0 || classified[0] == 0 || classified[1] == 0) {
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
                int[] leftClass = new int[2];
                int[] rightClass = new int[]{classified[0], classified[1]};
                double[] d;
                for (int sepPoint = 0; sepPoint < data.size() - 1; sepPoint++) {
                    d = data.get(sepPoint);
                    if (d[xSize] < 0) {
                        leftClass[0]++;
                        rightClass[0]--;
                    } else {
                        leftClass[1]++;
                        rightClass[1]--;
                    }
                    double quality = criterion.calculate(leftClass) + criterion.calculate(rightClass);
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
            left = new DecisionTree(toLeft, criterion, maxDepth - 1);
            right = new DecisionTree(toRight, criterion, maxDepth - 1);
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

    public int classify(double[] x) {
        if (variable == -1) {
            if (classified[0] > classified[1]) {
                return -1;
            } else {
                return 1;
            }
        } else {
            if (x[variable] <= value) {
                return left.classify(x);
            } else {
                return right.classify(x);
            }
        }
    }
}
