package egps2.utils.common.math.matrix;

import java.util.Arrays;

/**
 * MatrixTriangleOp provides shared utility logic for eGPS modules and UI.
 */
public class MatrixTriangleOp {

	public final static double[][] downTriangle2up(double[][] input) {
		int numOfRows = input.length;
		double[][] ret = new double[numOfRows][numOfRows];
		
		for (int i = 0; i < numOfRows; i++) {
			for (int j = i; j < numOfRows; j++) {
				ret[i][j] = input[j][i];
			}
		}
		
		return ret;
		
	}
	
	public final static double[][] downTriangle2full(double[][] input) {
		int numOfRows = input.length;
		double[][] ret = new double[numOfRows][numOfRows];
		
		for (int i = 0; i < numOfRows; i++) {
			for (int j = 0; j < numOfRows; j++) {
				if (i < j) {
					ret[i][j] = input[j][i];
				}else {
					ret[i][j] = input[i][j];
				}
				
			}
		}
		
		return ret;
	}
	
	public final static double[][] downTriangleNoDiag2full(double[][] input) {
		int numOfRows = input.length + 1;
		double[][] ret = new double[numOfRows][numOfRows];
		
		for (int i = 0; i < numOfRows; i++) {
			for (int j = 0; j < numOfRows; j++) {
				if (i == j) {
					ret[i][j] = 0.0;
				}else if (i < j) {
					ret[i][j] = input[j-1][i];
				}else {
					// i > j
					ret[i][j] = input[i-1][j];
				}
				
			}
		}
		
		return ret;
	}
	
	public static void main(String[] args) {
		double[][] input = new double[][] {{1.0},{2.0,3.0}};
		double[][] downTriangleNoDiag2full = downTriangleNoDiag2full(input);
		for (double[] ds : downTriangleNoDiag2full) {
			System.out.println(Arrays.toString(ds));
		}
	}
	
}
