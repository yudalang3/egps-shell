package egps2.utils.common.util;

/**
 * IntervalUtil provides shared utility logic for eGPS modules and UI.
 */
public class IntervalUtil {
	
	/**
	 * Please make sure the largest value more then 0;
	 * @param largestValue
	 * @param numOfIntervals
	 * @return
	 */
	public static final int getIntalval(int largestValue, int numOfIntervals) {
		int interval = (int) Math.ceil( largestValue / (double) numOfIntervals );
		
		int quotient = interval / 10;int remainder = interval % 10;
		
		// when quotient equals to 0
		// we directly remove
		if (quotient != 0) {
			// when remainder equals to 0 
			// interval = largestValue;
			if (remainder != 0) {
				interval = (quotient + 1) * 10;
			}
		}
		
		return interval;
	}

}
