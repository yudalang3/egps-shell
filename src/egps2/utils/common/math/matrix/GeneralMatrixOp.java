package egps2.utils.common.math.matrix;

/**
 * GeneralMatrixOp provides shared utility logic for eGPS modules and UI.
 */
public class GeneralMatrixOp {

	public final static double[][] transpose(double[][] matrix) {

		int numOfRow = matrix.length;
		int numOfCol = matrix[0].length;

		double[][] ret = new double[numOfCol][numOfRow];
		for (int i = 0; i < numOfRow; i++) {
			for (int j = 0; j < numOfCol; j++) {
				ret[j][i] = matrix[i][j];
			}
		}

		return ret;
	}

}
