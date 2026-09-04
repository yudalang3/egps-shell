package demo.dockable.sigtest;

import java.util.*;

/**
 * Implementation of the Mann-Whitney U test.
 * A non-parametric test used to compare the distribution differences between two independent samples.
 * This test is suitable when the assumptions of the t-test are not met, particularly when data 
 * is not normally distributed.
 */
public class MannWhitneyUTest implements StatisticalTest {
    
    /**
     * Calculate the p-value for two groups of data using the Mann-Whitney U test
     * @param group1 First group of data
     * @param group2 Second group of data
     * @return p-value indicating statistical significance
     */
    @Override
    public double calculatePValue(double[] group1, double[] group2) {
        if (group1.length == 0 || group2.length == 0) {
            return 1.0;
        }
        
        int n1 = group1.length;
        int n2 = group2.length;
        
        // Combine data and calculate ranks
        double[] combined = new double[n1 + n2];
        System.arraycopy(group1, 0, combined, 0, n1);
        System.arraycopy(group2, 0, combined, n1, n2);
        
        double[] ranks = calculateRanks(combined);
        
        // Calculate rank sum for group1
        double R1 = 0;
        for (int i = 0; i < n1; i++) {
            R1 += ranks[i];
        }
        
        // Calculate U statistic
        double U1 = R1 - (n1 * (n1 + 1)) / 2.0;
        double U2 = n1 * n2 - U1;
        double U = Math.min(U1, U2);
        
        // Use normal approximation to calculate p-value
        double meanU = (n1 * n2) / 2.0;
        double varU = (n1 * n2 * (n1 + n2 + 1)) / 12.0;
        double z = (U - meanU) / Math.sqrt(varU);
        
        // Two-tailed test
        return 2 * (1 - standardNormalCDF(Math.abs(z)));
    }
    
    /**
     * Calculate ranks for the data
     * @param data The data to rank
     * @return Array of ranks corresponding to each data point
     */
    private double[] calculateRanks(double[] data) {
        int n = data.length;
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) {
            indices[i] = i;
        }
        
        // Sort by value
        Arrays.sort(indices, (i, j) -> Double.compare(data[i], data[j]));
        
        double[] ranks = new double[n];
        int i = 0;
        while (i < n) {
            int j = i;
            // Find range of identical values
            while (j < n && data[indices[j]] == data[indices[i]]) {
                j++;
            }
            // Calculate average rank
            double avgRank = (i + j + 1) / 2.0;
            for (int k = i; k < j; k++) {
                ranks[indices[k]] = avgRank;
            }
            i = j;
        }
        
        return ranks;
    }
    
    /**
     * Standard normal cumulative distribution function
     * @param x The value to evaluate
     * @return The cumulative probability up to x
     */
    private double standardNormalCDF(double x) {
        return 0.5 * (1 + erf(x / Math.sqrt(2)));
    }
    
    /**
     * Approximation of the error function
     * @param x The value to evaluate
     * @return The error function value
     */
    private double erf(double x) {
        // Using approximation formula
        double a1 =  0.254829592;
        double a2 = -0.284496736;
        double a3 =  1.421413741;
        double a4 = -1.453152027;
        double a5 =  1.061405429;
        double p  =  0.3275911;
        
        int sign = x < 0 ? -1 : 1;
        x = Math.abs(x);
        
        double t = 1.0 / (1.0 + p * x);
        double y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * Math.exp(-x * x);
        
        return sign * y;
    }
    
    /**
     * Get the name of this statistical method
     * @return Method name string
     */
    @Override
    public String getMethodName() {
        return "Mann_Whitney_U";
    }
}