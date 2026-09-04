package egps2.utils.common.math.diffSign;

import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;

/**
 * EGPSWilcoxonSignedRankTest provides shared utility logic for eGPS modules and UI.
 */
public class EGPSWilcoxonSignedRankTest implements DeffTest {
	
	/** Singleton TTest instance. */
    private final WilcoxonSignedRankTest WSR_TEST = new WilcoxonSignedRankTest();

	@Override
	public double getPvalue(double[] sample1, double[] sample2) {
		return WSR_TEST.wilcoxonSignedRankTest(sample1, sample2, false);
	}

}
