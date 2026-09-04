package demo.dockable.sigtest;

/**
 * Statistical test interface
 * Defines the common interface for statistical test methods
 * 
 * This interface provides a standard way to implement different statistical tests
 * for comparing two groups of data and calculating p-values.
 */
public interface StatisticalTest {
    
    /**
     * Calculate the p-value for two groups of data
     * @param group1 The first group of data
     * @param group2 The second group of data
     * @return The p-value indicating statistical significance
     */
    double calculatePValue(double[] group1, double[] group2);
    
    /**
     * Get the name of the statistical method
     * @return The method name as a string
     */
    String getMethodName();
}