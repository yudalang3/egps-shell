package egps2.utils.common.math.diffSign;

/**
 * 
* <p>Title: Log2FoldChange</p>  
* <p>Description: 
* 	An interface to get log2 fold change! Currently there are three fold change c
* </p>  
 */
public interface Log2FoldChange {
	
	/**
	 * Get the log2 fold change from two double array !
	 */
	double getLog2FoldChange(double[] x, double[] y);
	
	default double mean(double[] m) {
		double sum = 0;
		int len = m.length;
		for (int i = 0; i < len; i++) {
			sum += m[i];
		}
		return sum / len;
	}
}
