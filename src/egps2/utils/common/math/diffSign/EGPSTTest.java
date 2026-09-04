package egps2.utils.common.math.diffSign;

import org.apache.commons.math3.stat.inference.TTest;

/**
 * EGPSTTest provides shared utility logic for eGPS modules and UI.
 */
public class EGPSTTest implements DeffTest {
	
	/** Singleton TTest instance. */
    private final TTest T_TEST = new TTest();

	@Override
	public double getPvalue(double[] sample1, double[] sample2) {
		return T_TEST.tTest(sample1, sample2);
	}

}
