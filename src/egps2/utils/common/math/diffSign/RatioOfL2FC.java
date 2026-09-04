package egps2.utils.common.math.diffSign;

/**
 * RatioOfL2FC provides shared utility logic for eGPS modules and UI.
 */
public class RatioOfL2FC implements Log2FoldChange {

	@Override
	public double getLog2FoldChange(double[] x, double[] y) {
		double fc = mean(x) / mean(y);
		return Math.log(fc) / Math.log(2);
	}

}
