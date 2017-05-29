package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author eugene
 */
public class ImdbDataReader {
	public static List<double[]> read(String filename) throws IOException {
		BufferedReader dataIn = new BufferedReader(new FileReader("./res/" + filename));
		List<double[]> result = new ArrayList<>();
		String s = dataIn.readLine();
		while ((s = dataIn.readLine()) != null) {
			StringTokenizer tokens = new StringTokenizer(s);
			int xSize = tokens.countTokens();
			double[] x = new double[xSize];
			for (int i = 0; i < xSize; i++) {
				x[i] = Double.parseDouble(tokens.nextToken());
			}
			result.add(x);
		}
		dataIn.close();
		return result;
	}
}
