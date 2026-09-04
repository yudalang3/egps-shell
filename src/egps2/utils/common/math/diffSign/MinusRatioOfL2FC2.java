package egps2.utils.common.math.diffSign;

/**
 * MinusRatioOfL2FC2 provides shared utility logic for eGPS modules and UI.
 */
public class MinusRatioOfL2FC2 implements Log2FoldChange {

	@Override
	public double getLog2FoldChange(double[] x, double[] y) {
		double a = mean(x);
		double b = mean(y);
		
		double fc = (a - b) / a;
		return Math.log(fc) / Math.log(2);
	}

}
