package egps2.utils.common.math.diffSign;

/**
 * MinusRatioMinOfL2FC3 provides shared utility logic for eGPS modules and UI.
 */
public class MinusRatioMinOfL2FC3 implements Log2FoldChange {

	@Override
	public double getLog2FoldChange(double[] x, double[] y) {
		double a = mean(x);
		double b = mean(y);
		
		double fc = (a - b) / Math.min(a, b);
		return Math.log(fc) / Math.log(2);
	}

}
